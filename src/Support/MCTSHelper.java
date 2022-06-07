package Support;

import com.sillysoft.lux.Country;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by Abhishek Pai on 13/07/2017.
 */
public class MCTSHelper {

    public BoardState boardState;
    public int numberOfSimulations;
    public Random random;
    public Country[] editableGameState;
    public int initialChoice;
    public int choice;
    public ArrayList<Country> dynamicSimulationGameState;
    public HashMap<Integer, Integer> winningChoices;
    public ArrayList<Country> choicesSoFar;

    public MCTSHelper(BoardState boardState, int numberOfSimulations, Random random) {
        this.boardState = boardState;
        this.numberOfSimulations = numberOfSimulations;
        this.random = random;
    }

    /**
     * This method initiates the selection of the initial country so that simulations can be run later on.
     */
    public void pickCountry() {
        winningChoices = new HashMap<>();
        choicesSoFar = new ArrayList<>();
        for (int simulation = 0; simulation < numberOfSimulations; simulation++) {
            editableGameState = new Country[]{};
            editableGameState = boardState.getCountries();
            dynamicSimulationGameState = new ArrayList<>();
            for (int i = 0; i < editableGameState.length; i++) {
                if (editableGameState[i].getOwner() == -1) {
                    dynamicSimulationGameState.add(editableGameState[i]);
                }
            }
            dynamicSimulationGameState.addAll(choicesSoFar);
            initialChoice = Math.abs(random.nextInt(dynamicSimulationGameState.size()));
            Country chosenCountry = dynamicSimulationGameState.get(initialChoice);
            int chosenCountryCode = chosenCountry.getCode();
            for (int i = 0; i < editableGameState.length; i++) {
                if (editableGameState[i].getCode() == chosenCountryCode) {
                    editableGameState[i].setOwner(boardState.ID, boardState.passkey);
                    break;
                }
            }
            while (boardState.checkUnOccupiedCountry(editableGameState)) {
                pickCountrySimulation();
            }
            if (boardState.ID == boardState.pickCountryHighestScore(editableGameState)) {
                if (winningChoices.containsKey(chosenCountryCode)) {
                    int value = winningChoices.get(chosenCountryCode);
                    value++;
                    winningChoices.put(chosenCountryCode, value);
                    choicesSoFar.add(chosenCountry);
                } else {
                    winningChoices.putIfAbsent(chosenCountryCode, 1);
                    choicesSoFar.add(chosenCountry);
                }
            }
        }
    }

    /**
     * This method runs the simulations for pick country phase.
     */
    public void pickCountrySimulation() {
        dynamicSimulationGameState = new ArrayList<>();
        for (int i = 0; i < editableGameState.length; i++) {
            if (editableGameState[i].getOwner() == -1) {
                dynamicSimulationGameState.add(editableGameState[i]);
            }
        }
        pickCountryOpponent();
        dynamicSimulationGameState = new ArrayList<>();
        for (int i = 0; i < editableGameState.length; i++) {
            if (editableGameState[i].getOwner() == -1) {
                dynamicSimulationGameState.add(editableGameState[i]);
            }
        }
        if (dynamicSimulationGameState.size() == 0) {
            return;
        }
        for (int i = 0; i < choicesSoFar.size(); i++) {
            if (choicesSoFar.get(i).getOwner() == -1) {
                dynamicSimulationGameState.add(choicesSoFar.get(i));
            }
        }
        choice = Math.abs(random.nextInt(dynamicSimulationGameState.size()));
        Country chosenCountry = dynamicSimulationGameState.get(choice);
        int chosenCountryCode = chosenCountry.getCode();
        for (int i = 0; i < editableGameState.length; i++) {
            if (editableGameState[i].getCode() == chosenCountryCode) {
                editableGameState[i].setOwner(boardState.ID, boardState.passkey);
                break;
            }
        }
    }

    /**
     * This method chooses the country for the opponents within the game during the pick country phase.
     */
    public void pickCountryOpponent() {
        for (int i = 0; i < boardState.board.getNumberOfPlayers(); i++) {
            if (i == boardState.ID) {
                continue;
            } else if (dynamicSimulationGameState.size() == 1) {
                Country opponentChosenCountry = dynamicSimulationGameState.get(0);
                int chosenOpponentCountryCode = opponentChosenCountry.getCode();
                for (int j = 0; j < editableGameState.length; j++) {
                    if (editableGameState[j].getCode() == chosenOpponentCountryCode) {
                        editableGameState[j].setOwner(i, boardState.passkey);
                        break;
                    }
                }
            } else {
                dynamicSimulationGameState = new ArrayList<>();
                for (int j = 0; j < editableGameState.length; j++) {
                    if (editableGameState[j].getOwner() == -1) {
                        dynamicSimulationGameState.add(editableGameState[j]);
                    }
                }
                choice = Math.abs(random.nextInt(dynamicSimulationGameState.size()));
                Country opponentChosenCountry = dynamicSimulationGameState.get(choice);
                int chosenOpponentCountryCode = opponentChosenCountry.getCode();
                for (int j = 0; j < editableGameState.length; j++) {
                    if (editableGameState[j].getCode() == chosenOpponentCountryCode) {
                        editableGameState[j].setOwner(i, boardState.passkey);
                        break;
                    }
                }
            }
        }
    }
}