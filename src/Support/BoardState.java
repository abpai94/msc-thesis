package Support;

import com.sillysoft.lux.Board;
import com.sillysoft.lux.Country;

import java.util.Random;

/**
 * Created by Abhishek Pai on 05/07/2017.
 */
public class BoardState {

    public int ID;
    public Board board;
    public LogWriter logWriter;
    public MCTSHelper mctsHelper;
    public Country[] countries;
    public int numberOfSimulations;
    public Random random;
    public Object passkey = "passkey";

    public BoardState(int ID, Board board, LogWriter logWriter, int numberOfSimulations, Random random) {
        this.ID = ID;
        this.board = board;
        this.logWriter = logWriter;
        this.numberOfSimulations = numberOfSimulations;
        this.random = random;
        mctsHelper = new MCTSHelper(this, numberOfSimulations, random);
        countries = getCountries();
    }

    /**
     * This method creates a clone of the getCountries method by making it easier to edit by using a custom passkey Object
     *
     * @return Country[] containing the current state of the game.
     */
    public Country[] getCountries() {
        Country[] output = new Country[42];
        Country[] copy = board.getCountries();
        for (int i = 0; i < copy.length; i++) {
            Country newCountry = new Country(copy[i].getCode(), copy[i].getContinent(), passkey);
            output[i] = newCountry;
            output[i].setName(copy[i].getName(), passkey);
            output[i].setOwner(copy[i].getOwner(), passkey);
            output[i].setArmies(copy[i].getArmies(), passkey);
        }
        return output;
    }

    /**
     * This methods is used to check if there are any unoccupied countries available in the game for
     * the player to pick their initial set of countries.
     *
     * @param checkingArray The array of countries that needs to be checked.
     * @return true if an unoccupied country exist and false if an occupied country exists.
     */
    public Boolean checkUnOccupiedCountry(Country[] checkingArray) {
        Boolean check = false;
        for (int i = 0; i < checkingArray.length; i++) {
            if (checkingArray[i].getOwner() == -1) {
                check = true;
                break;
            }
        }
        return check;
    }

    /**
     * This method produces a score for the countries already occuppied by the player.
     *
     * @param checkingArray The array that is being used to produce the score.
     * @param playerID      The ID of the player that you want to get the score for.
     * @return A score that can be used to choose the correct country on the map.
     */
    public double pickCountryScore(Country[] checkingArray, int playerID) {
        double score = 0.0;
        int continentCountries;
        double[] percentageContinentOccupation = percentageOccupationOfContinent();
        for (int i = 0; i < board.getNumberOfContinents(); i++) {
            continentCountries = 0;
            for (int j = 0; j < checkingArray.length; j++) {
                if (i == checkingArray[j].getContinent()) {
                    continentCountries++;
                }
            }
            for (int j = 0; j < checkingArray.length; j++) {
                if (i == checkingArray[j].getContinent() && checkingArray[j].getOwner() == playerID) {
                    score += ((double) board.getContinentBonus(checkingArray[j].getContinent())) / continentCountries;
                    if (percentageContinentOccupation[checkingArray[j].getContinent()] >= 2.0 / 3.0 || percentageContinentOccupation[checkingArray[j].getContinent()] <= 1.0 / 3.0) {
                        score += 4.0;
                    }
                }
            }
        }
        return score;
    }

    /**
     * This method looks through the array of countries and determines which player has the highest score.
     *
     * @param checkingArray The array of countries that needs to be checked
     * @return An Integer that represents the playerID.
     */
    public int pickCountryHighestScore(Country[] checkingArray) {
        int player = -1;
        double score = 0.0;
        for (int i = 0; i < board.getNumberOfPlayers(); i++) {
            double checkingScore = pickCountryScore(checkingArray, i);
            if (checkingScore > score) {
                player = i;
                score = checkingScore;
            }
        }
        return player;
    }

    /**
     * This method produces an array that contains the percentage occupation of each continent by the player.
     *
     * @return An array of values between 0-1 of percentage ownership.
     */
    public double[] percentageOccupationOfContinent() {
        Country[] checkingArray = getCountries();
        double[] output = new double[6];
        int countriesPerContinent;
        int countriesOwnedByPlayer;
        int numberOfContinents = board.getNumberOfContinents();
        for (int i = 0; i < numberOfContinents; i++) {
            countriesOwnedByPlayer = 0;
            countriesPerContinent = 0;
            for (int j = 0; j < checkingArray.length; j++) {
                if (checkingArray[j].getContinent() == i) {
                    countriesPerContinent++;
                }
            }
            for (int j = 0; j < checkingArray.length; j++) {
                if (checkingArray[j].getOwner() == ID && checkingArray[j].getContinent() == i) {
                    countriesOwnedByPlayer += 1;
                }
            }
            if (countriesOwnedByPlayer == 0) {
                output[i] = 0.0;
            } else {
                output[i] = (double) countriesOwnedByPlayer / (double) countriesPerContinent;
            }
        }
        return output;
    }
}