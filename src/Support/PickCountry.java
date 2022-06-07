package Support;

import com.sillysoft.lux.Board;
import com.sillysoft.lux.Country;

import java.util.HashMap;

/**
 * Created by Abhishek Pai on 29/07/2017.
 */
public class PickCountry {

    public int ID;
    public Board board;
    public LogWriter logWriter;
    public HashMap<Country, Double> scores;
    public Country[] countries;

    public PickCountry(int ID, Board board, LogWriter logWriter) {
        this.ID = ID;
        this.board = board;
        this.logWriter = logWriter;
    }

    /**
     * This method produces a score for the countries already occupied by the player.
     *
     * @return A score that can be used to choose the correct country on the map.
     */
    public void pickCountryScore() {
        scores = new HashMap<>();
        countries = board.getCountries();
        for (int i = 0; i < countries.length; i++) {
            double score = 0.0;
            score += adjacentOwnedCountries(countries[i]);
            score += unoccupiedCountries(countries[i]);
            score += continentBonus(countries[i]);
            score += continentSizeBias(countries[i]);
            score += preventEnemyCaptureOfContinent(countries[i]);
            score += fullyOccupyContinent(countries[i]);
            scores.put(countries[i], score);
        }
    }

    /**
     * This method biases the score towards unoccupied countries.
     *
     * @param country is the country being checked.
     * @return A score biased towards a specific condition.
     */
    public double unoccupiedCountries(Country country) {
        double output = 0.0;
        if (country.getOwner() == -1) {
            output += 2.0;
        } else {
            output += -2.0;
        }
        return output;
    }

    /**
     * This method pushes the choice towards the continent with the highest bonus.
     *
     * @param country is the country being checked.
     * @return A score biased towards a specific condition.
     */
    public double continentBonus(Country country) {
        double output = 0.0;
        if (board.getContinentBonus(country.getContinent()) == 7) {
            output = 2.0;
        }
        if (board.getContinentBonus(country.getContinent()) == 5) {
            output = 1.5;
        }
        if (board.getContinentBonus(country.getContinent()) == 3) {
            output = 1.0;
        }
        if (board.getContinentBonus(country.getContinent()) == 2) {
            output = 0.5;
        }
        return output;
    }

    /**
     * This method biases the choice towards continents that are small in size so they can be occupied more quickly.
     *
     * @param country is the country being checked.
     * @return A score biased towards a specific condition.
     */
    public double continentSizeBias(Country country) {
        double output = 0.0;
        countries = board.getCountries();
        int countriesPerContinent = 0;
        for (int i = 0; i < countries.length; i++) {
            if (country.getContinent() == countries[i].getContinent()) {
                countriesPerContinent++;
            }
        }
        if (countriesPerContinent == 4) {
            output = 3.0;
        } else if (countriesPerContinent == 6) {
            output = 2.5;
        } else if (countriesPerContinent == 7) {
            output = 2.0;
        } else if (countriesPerContinent == 9) {
            output = 1.0;
        } else if (countriesPerContinent == 12) {
            output = 0.5;
        }
        return output;
    }

    /**
     * This method biases the choice to prevent an opponent completely occupying a continent and getting the bonus.
     *
     * @param country is the country being checked.
     * @return A score biased towards a specific condition.
     */
    public double preventEnemyCaptureOfContinent(Country country) {
        double output = 0.0;
        countries = board.getCountries();
        int highestNumberOfCountriesOccupied = 0;
        int countriesPerContinent = 0;
        int countriesOwnedBySpecificPlayer = 0;
        for (int i = 0; i < board.getNumberOfPlayers(); i++) {
            countriesPerContinent = 0;
            countriesOwnedBySpecificPlayer = 0;
            for (int j = 0; j < countries.length; j++) {
                if (country.getContinent() == countries[j].getContinent()) {
                    countriesPerContinent++;
                }
                if (country.getContinent() == countries[j].getContinent() && countries[j].getOwner() == i) {
                    countriesOwnedBySpecificPlayer++;
                }
            }
            if (countriesOwnedBySpecificPlayer > highestNumberOfCountriesOccupied) {
                highestNumberOfCountriesOccupied = countriesOwnedBySpecificPlayer;
            }
        }
        if (countriesPerContinent - highestNumberOfCountriesOccupied == 1) {
            output += 4.0;
        } else if (countriesPerContinent - highestNumberOfCountriesOccupied == 2) {
            output += 2.0;
        } else if (countriesPerContinent - highestNumberOfCountriesOccupied == 3) {
            output += 1.0;
        } else {
            output += 0.0;
        }
        return output;
    }

    /**
     * This method biases the choice so that a continent that is almost fully occupied is chosen.
     *
     * @param country is the country being checked.
     * @return A score biased towards a specific condition.
     */
    public double fullyOccupyContinent(Country country) {
        double output = 0.0;
        countries = board.getCountries();
        int countriesPerContinent = 0;
        int countriesOwedByPlayer = 0;
        for (int i = 0; i < countries.length; i++) {
            if (country.getContinent() == countries[i].getContinent()) {
                countriesPerContinent++;
            }
        }
        for (int i = 0; i < countries.length; i++) {
            if (country.getContinent() == countries[i].getContinent() && countries[i].getOwner() == ID) {
                countriesOwedByPlayer++;
            }
        }
        if (countriesPerContinent - countriesOwedByPlayer == 1) {
            output += 3.0;
        } else if (countriesPerContinent - countriesOwedByPlayer == 2) {
            output += 1.0;
        } else if (countriesPerContinent - countriesOwedByPlayer == 3) {
            output += 0.5;
        }
        return output;
    }

    /**
     * This method biases the choice towards a country adjacent to a country that has already been chosen by the agent.
     *
     * @param country is the country being checked.
     * @return A score biased towards a specific condition.
     */
    public double adjacentOwnedCountries(Country country) {
        countries = board.getCountries();
        double output = 0.0;
        Country[] adjacentCountry = country.getAdjoiningList();
        int countriesOwnedByPlayer = 0;
        for (int i = 0; i < adjacentCountry.length; i++) {
            if (adjacentCountry[i].getOwner() == ID) {
                countriesOwnedByPlayer++;
            }
        }
        if (countriesOwnedByPlayer == adjacentCountry.length) {
            output += 3.0;
        } else if (countriesOwnedByPlayer == 3) {
            output += 3.0;
        } else if (countriesOwnedByPlayer == 2) {
            output += 2.0;
        } else if (countriesOwnedByPlayer == 1) {
            output += 1.0;
        } else {
            output += -2.0;
        }
        return output;
    }
}
