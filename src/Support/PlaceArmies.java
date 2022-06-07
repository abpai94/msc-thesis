package Support;

import com.sillysoft.lux.Board;
import com.sillysoft.lux.Country;

import java.util.HashMap;

/**
 * Created by Abhishek Pai on 29/07/2017.
 */
public class PlaceArmies {

    public int ID;
    public Board board;
    public LogWriter logWriter;
    public HashMap<Country, Double> scores;
    public Country[] countries;

    public PlaceArmies(int ID, Board board, LogWriter logWriter) {
        this.ID = ID;
        this.board = board;
        this.logWriter = logWriter;
    }

    /**
     * This method takes in the input of the country array of the current game state and the country being
     * checked and determines a score based on the percentage occupation of the continent.
     *
     * @return a score based on the occupation of the continent.
     */
    public void placeArmiesScore() {
        scores = new HashMap<>();
        countries = board.getCountries();
        for (int i = 0; i < countries.length; i++) {
            double score = 0.0;
            score += occupiedByPlayer(countries[i]);
            score += opponentAdjacentCountry(countries[i]);
            score += armiesPerContinentScore(countries[i]);
            score += totalAdjacentEnemyArmies(countries[i]);
            score += surroundedByOwnedCountries(countries[i]);
            score += percentageOccupationOfContinent(countries[i]);
            scores.put(countries[i], score);
        }
    }

    /**
     * This method produces a score based on the number of countries occupied by the player.
     *
     * @param country The country that is being scored.
     * @return A score that represents the value of this country.
     */
    public double occupiedByPlayer(Country country) {
        double output;
        countries = board.getCountries();
        int countriesOccupiedByPlayer = 0;
        int totalCountriesInContinent = 0;
        for (int i = 0; i < countries.length; i++) {
            if (countries[i].getContinent() == country.getContinent() && countries[i].getOwner() == country.getOwner()) {
                countriesOccupiedByPlayer++;
            }
        }
        for (int j = 0; j < countries.length; j++) {
            if (country.getContinent() == countries[j].getContinent()) {
                totalCountriesInContinent++;
            }
        }
        if (totalCountriesInContinent <= 6) {
            if (countriesOccupiedByPlayer >= 4) {
                output = 3.0;
            } else if (countriesOccupiedByPlayer == 3) {
                output = 2.0;
            } else if (countriesOccupiedByPlayer == 2) {
                output = 1.0;
            } else {
                output = 0.0;
            }
        } else {
            if (countriesOccupiedByPlayer >= 4) {
                output = 2.0;
            } else if (countriesOccupiedByPlayer == 3) {
                output = 1.5;
            } else if (countriesOccupiedByPlayer == 2) {
                output = 1.0;
            } else {
                output = 0;
            }
        }
        return output;
    }

    /**
     * This method produces an array that contains the percentage occupation of each continent by the player.
     *
     * @return An array of values between 0-1 of percentage ownership.
     */
    public double percentageOccupationOfContinent(Country country) {
        countries = board.getCountries();
        double output = 0.0;
        int countriesPerContinent = 0;
        int countriesOwnedByPlayer = 0;
        double percentage = 0.0;
        for (int i = 0; i < countries.length; i++) {
            if (countries[i].getContinent() == country.getContinent()) {
                countriesPerContinent++;
            }
        }
        for (int i = 0; i < countries.length; i++) {
            if (countries[i].getOwner() == ID && countries[i].getContinent() == country.getContinent()) {
                countriesOwnedByPlayer++;
            }
        }
        if (countriesOwnedByPlayer == 0) {
            return 0.0;
        } else {
            percentage = (double) countriesOwnedByPlayer / (double) countriesPerContinent;
            if (percentage >= 2.0 / 3.0) {
                output += 3.0;
            } else {
                output += -2.0;
            }
            return output;
        }
    }

    /**
     * This method checks to see how many opponent countries are surrounding the country.
     *
     * @param country The country being checked.
     * @return Score based on the number of opponent countries.
     */
    public double opponentAdjacentCountry(Country country) {
        double output = 0.0;
        countries = board.getCountries();
        Country[] adjacentCountry = country.getAdjoiningList();
        int opponentCountries = 0;
        for (int i = 0; i < adjacentCountry.length; i++) {
            if (adjacentCountry[i].getOwner() != ID) {
                opponentCountries++;
            }
        }
        if (opponentCountries == 0) {
            output = -1.0;
        } else if (adjacentCountry.length - opponentCountries >= 3) {
            output = 5.0;
        } else if (adjacentCountry.length - opponentCountries == 2) {
            output = 1.0;
        } else if (adjacentCountry.length - opponentCountries == 1) {
            output = 0.5;
        } else if (adjacentCountry.length == opponentCountries) {
            output = -2.0;
        }
        return output;
    }

    /**
     * This method produces a score based on the number of opponent armies there are per continent.
     *
     * @param country central country being checked.
     * @return A score based on the number of armies.
     */
    public double armiesPerContinentScore(Country country) {
        double output = 0.0;
        int enemyArmiesPerContinent = 0;
        int playerArmiesPerContinent = 0;
        countries = board.getCountries();
        for (int i = 0; i < countries.length; i++) {
            if (countries[i].getContinent() == country.getContinent() && countries[i].getOwner() != country.getOwner()) {
                enemyArmiesPerContinent += countries[i].getArmies();
            }
        }
        for (int i = 0; i < countries.length; i++) {
            if (countries[i].getContinent() == country.getContinent() && countries[i].getOwner() == country.getOwner()) {
                playerArmiesPerContinent += countries[i].getArmies();
            }
        }
        if (enemyArmiesPerContinent - playerArmiesPerContinent > 5) {
            output = 3.0;
        } else if (enemyArmiesPerContinent - playerArmiesPerContinent > 1) {
            output = 1.0;
        } else if (enemyArmiesPerContinent - playerArmiesPerContinent == 0) {
            output = 0.5;
        } else if (enemyArmiesPerContinent - playerArmiesPerContinent < 5) {
            output = -1.0;
        } else if (enemyArmiesPerContinent - playerArmiesPerContinent < 2) {
            output = -0.5;
        } else if (enemyArmiesPerContinent - playerArmiesPerContinent < 0) {
            output = -0.25;
        }
        return output;
    }

    /**
     * This method sums all the enemy armies in adjacent countries.
     *
     * @param country is the central country that is being checked.
     * @return total armies surrounding the country.
     */
    public double totalAdjacentEnemyArmies(Country country) {
        double output = 0.0;
        countries = board.getCountries();
        int totalArmies = 0;
        Country[] adjoiningCountries = country.getAdjoiningList();
        for (int i = 0; i < adjoiningCountries.length; i++) {
            if (adjoiningCountries[i].getOwner() != country.getOwner() && adjoiningCountries[i].getArmies() != 1) {
                totalArmies += adjoiningCountries[i].getArmies();
            }
        }
        if (country.getArmies() - totalArmies > 5) {
            output += -1.0;
        } else if (country.getArmies() - totalArmies > 1) {
            output += -0.5;
        } else if (country.getArmies() - totalArmies < -5) {
            output += 3.0;
        } else if (country.getArmies() - totalArmies < -1) {
            output += 1.0;
        }
        return output;
    }

    /**
     * This method checks the adjoining country's to the input country to determine if it is completely
     * surrounded by the player.
     *
     * @param country is checked to determine whether it is surrounded by the player.
     * @return True if the country is surrounded by the player and false otherwise.
     */
    public double surroundedByOwnedCountries(Country country) {
        double output = 0.0;
        countries = board.getCountries();
        Country landlockedCountry = country;
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
            output += 1.0;
        } else {
            output += -2.0;
        }
        return output;
    }
}
