package Support;

import com.sillysoft.lux.Board;
import com.sillysoft.lux.Country;

import java.util.HashMap;

/**
 * Created by Abhishek Pai on 31/07/2017.
 */
public class AttackPhase {

    public int ID;
    public Board board;
    public LogWriter logWriter;
    public Country[] countries;
    public HashMap<Country, Double> scores;

    public AttackPhase(int ID, Board board, LogWriter logWriter) {
        this.ID = ID;
        this.board = board;
        this.logWriter = logWriter;
    }

    /**
     * This method runs each method to produce a score for each country so that the appropriate country is chosen for
     * attack.
     */
    public void attackPhaseScore() {
        scores = new HashMap<>();
        countries = board.getCountries();
        double score;
        for (int i = 0; i < countries.length; i++) {
            score = 0.0;
            score += percentageOccupationOfContinent(countries[i]);
            score += largestArmyCountry(countries[i]);
            score += occupySmallCountry(countries[i]);
            scores.put(countries[i], score);
        }
    }

    /**
     * This method biases the choice towards countries that will enable the full occupation of a continent.
     *
     * @param country is the country being checked.
     * @return A score biased towards a specific condition.
     */
    public double percentageOccupationOfContinent(Country country) {
        double output = 0.0;
        countries = board.getCountries();
        int numberOfCountriesPerContinent = 0;
        int numberOfCountriesOwnedByPlayer = 0;
        for (int i = 0; i < countries.length; i++) {
            if (country.getContinent() == countries[i].getContinent()) {
                numberOfCountriesPerContinent++;
            }
            if (countries[i].getOwner() == ID && country.getContinent() == countries[i].getContinent()) {
                numberOfCountriesOwnedByPlayer++;
            }
        }
        double percentageOccupation = (double) numberOfCountriesOwnedByPlayer / (double) numberOfCountriesPerContinent;
        if (percentageOccupation >= 0.75) {
            output += 5.0;
        } else if (percentageOccupation >= 0.66) {
            output += 2.0;
        } else if (percentageOccupation >= 0.5) {
            output += 1.0;
        } else {
            output += 0.0;
        }
        return output;
    }

    /**
     * This method biases the choice towards the country with the largest army so that the opponent may not push to
     * attack and take over a continent.
     *
     * @param country is the country being checked.
     * @return A score biased towards a specific condition.
     */
    public double largestArmyCountry(Country country) {
        double output = 0.0;
        int largestArmyCountryCode = -1;
        int largestArmy = -1;
        countries = board.getCountries();
        for (int i = 0; i < countries.length; i++) {
            if (countries[i].getArmies() > largestArmy && countries[i].getOwner() == ID) {
                largestArmy = countries[i].getArmies();
                largestArmyCountryCode = countries[i].getCode();
            }
        }
        if (largestArmyCountryCode == country.getCode()) {
            output += 3.0;
        } else {
            output += 0.0;
        }
        return output;
    }

    /**
     * This method biases the choice towards the occupation of very small countries with just 2 bordering neighbours.
     *
     * @param country is the country being checked.
     * @return A score biased towards a specific condition.
     */
    public double occupySmallCountry(Country country) {
        double output = 0.0;
        Country[] adjacentCountries = country.getAdjoiningList();
        if (adjacentCountries.length == 2) {
            output += 3.0;
        } else {
            output += 0.0;
        }
        return output;
    }

    /**
     * This method is used to choose the largest army that is owned by the player so that it can become the attacker.
     *
     * @return An int that represents the country code.
     */
    public int largestArmy() {
        countries = board.getCountries();
        int army = -1;
        int countryCode = -1;
        for (int i = 0; i < countries.length; i++) {
            if (countries[i].getArmies() > army && countries[i].getOwner() == ID && !surroundedByOwnedCountries(countries[i].getCode())) {
                army = countries[i].getArmies();
                countryCode = countries[i].getCode();
            }
        }
        return countryCode;
    }

    /**
     * This method checks to make sure that the country chosen isn't completely surrounded by the player's own countries.
     *
     * @param country is the country being checked.
     * @return True if it is completely surrounded and false otherwise.
     */
    public Boolean surroundedByOwnedCountries(int country) {
        countries = board.getCountries();
        Country landlockedCountry = countries[country];
        Country[] adjoiningCountries = landlockedCountry.getAdjoiningList();
        Boolean[] booleanList = new Boolean[adjoiningCountries.length];
        for (int i = 0; i < adjoiningCountries.length; i++) {
            if (adjoiningCountries[i].getOwner() == ID) {
                booleanList[i] = true;
            } else {
                booleanList[i] = false;
            }
        }
        int falseBool = 0;
        for (int i = 0; i < booleanList.length; i++) {
            if (booleanList[i] == false) {
                falseBool++;
            }
        }
        if (falseBool > 0) {
            return false;
        } else {
            return true;
        }
    }
}
