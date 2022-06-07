import Support.AttackPhase;
import Support.LogWriter;
import Support.PickCountry;
import Support.PlaceArmies;
import com.sillysoft.lux.Board;
import com.sillysoft.lux.Card;
import com.sillysoft.lux.Country;
import com.sillysoft.lux.agent.LuxAgent;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by Abhishek Pai on 29/07/2017.
 */
public class HeuristicAI implements LuxAgent {

    public int ID;
    public Board board;
    public LogWriter logWriter;
    public Country[] countries;
    public PickCountry pickCountry;
    public PlaceArmies placeArmies;
    public AttackPhase attackPhase;

    /**
     * At the start of the game your agent will be constructed and then the setPrefs() method will be called. It will
     * tell you your ownerCode as well as give you a reference to the Board object for this game. You should store this
     * information, as you will need it later.
     *
     * @param ID
     * @param board
     */
    @Override
    public void setPrefs(int ID, Board board) {
        this.ID = ID;
        this.board = board;
        logWriter = new LogWriter();
        pickCountry = new PickCountry(ID, board, logWriter);
        placeArmies = new PlaceArmies(ID, board, logWriter);
        attackPhase = new AttackPhase(ID, board, logWriter);
    }

    /**
     * If the game's preferences are set to allow players to select the initial countries, then the pickCountry()
     * method will be called repeatadly at the beginning of a game, until all the countries have been assigned. You
     * must return th
     * e country-code of an unowned country. (Unowned countries have ownerCode's of -1).
     * <p>
     * If preferences are set to pick the initial countries randomly, then this method will never be called.
     */
    @Override
    public int pickCountry() {
        pickCountry.pickCountryScore();
        countries = board.getCountries();
        int bestChoice = -1;
        double bestScore = 0.0;
        HashMap<Country, Double> pickCountryScore = pickCountry.scores;
        for (Map.Entry<Country, Double> entry : pickCountryScore.entrySet()) {
            if (entry.getValue() > bestScore && entry.getKey().getOwner() == -1) {
                bestChoice = entry.getKey().getCode();
                bestScore = entry.getValue();
            }
        }
        return bestChoice;
    }

    /**
     * After choosing countries is done it is time to place the starting armies.
     * Since every country must have at least 1 army, the board automatically gives one army to each.
     * Then it is the agents turn to choose where to place the remaining armies it starts with.
     * Within this method, you should tell the board where you want to place your armies by calling <BR>&nbsp;&nbsp;&nbsp;
     * board.placeArmies( int numberOfArmies, int countryCode);
     * <p>
     * Currently Lux is set to have players place 4 armies at a time, but this is subject to change.
     *
     * @param numberOfArmies
     */
    @Override
    public void placeInitialArmies(int numberOfArmies) {
        placeArmies.placeArmiesScore();
        countries = board.getCountries();
        int bestChoice = -1;
        double bestScore = 0.0;
        HashMap<Country, Double> placeArmiesScore = placeArmies.scores;
        for (Map.Entry<Country, Double> entry : placeArmiesScore.entrySet()) {
            if (entry.getValue() > bestScore && entry.getKey().getOwner() == ID) {
                bestScore = entry.getValue();
                bestChoice = entry.getKey().getCode();
            }
        }
        board.placeArmies(numberOfArmies, bestChoice);
    }

    /**
     * The cardsPhase method is called at the very beginning of your agent's turn. <BR>
     * The parameter is an array with all of your cards.   <BR>
     * If your agent wants to cash a set of cards in, the agent should call    <BR>&nbsp;&nbsp;&nbsp;&nbsp;
     * board.cashCards( Card card, Card card2, Card card3 )    <BR>
     * with the parameters being references to the three cards to cash.   <BR>
     * You can call board.cashCards repeatadly if you have lots of cards.   <BR>
     * <p>
     * If your agent ever returns from the cardsPhase() method and still has more than 5 cards,    <BR>
     * enough sets will be automatically cashed to bring you to under five cards.
     *
     * @param cards
     */
    @Override
    public void cardsPhase(Card[] cards) {
        countries = board.getCountries();
        Card.getBestSet(cards, ID, countries);
        Card[] bestSet;
        if (cards.length > 3) {
            bestSet = Card.getBestSet(cards, ID, countries);
        } else {
            return;
        }
        board.cashCards(bestSet[0], bestSet[1], bestSet[2]);
    }

    /**
     * Every turn, each agent gets some armies to place on its countries.     <BR>
     * The amount is determined based on number of countries owner, continents owned, and any cards cashed.    <BR>
     * Within this method, you should tell the board where you want to place your armies by calling     <BR>&nbsp;&nbsp;&nbsp;&nbsp;
     * board.placeArmies( int numberOfArmies, int countryCode);
     *
     * @param numberOfArmies
     */
    @Override
    public void placeArmies(int numberOfArmies) {
        placeArmies.placeArmiesScore();
        countries = board.getCountries();
        int bestChoice = -1;
        double bestScore = 0.0;
        HashMap<Country, Double> placeArmiesScore = placeArmies.scores;
        for (Map.Entry<Country, Double> entry : placeArmiesScore.entrySet()) {
            if (entry.getValue() > bestScore && entry.getKey().getOwner() == ID) {
                bestScore = entry.getValue();
                bestChoice = entry.getKey().getCode();
            }
        }
        board.placeArmies(numberOfArmies, bestChoice);
    }

    /**
     * The attackPhase method is called at the start of the agent's attack-phase (duh).      <BR>
     * Attacking is done by calling one of Board's attack() methods.     <BR>
     * <p>
     * They have slightly different parameters, but you always provide     <BR>
     * 1. The country where you are attacking from (a country you own with at least 2 armies),      <BR>
     * 2. The country where you are attacking to (an enemy country that can be reached from
     * where you are attacking from),      <BR>
     * 3. The number of dice you want to attack with (1, 2, or 3 - and you must have at least (dice+1)
     * armies in the country you are attacking from).     <BR>
     * 4. Whether you want to repeat the attack until someone wins or not (a false value means just
     * one dice roll, a true value means keep attacking till someone is wiped out).
     * <p>
     * The Board's attack() method returns symbolic ints, as follows:     <BR>
     * - a negative return means that you supplied incorrect parameters.     <BR>
     * - 0 means that your single attack call has finished, with no one being totally defeated. Armies may have been lost from either country.     <BR>
     * - 7 means that the attacker has taken over the defender's country.     <BR>
     * NOTE: before returning 7, board will call moveArmiesIn() to poll you on how many armies to move into the taken over country.     <BR>
     * - 13 means that the defender has fought off the attacker (the attacking country has only 1 army left).
     */
    @Override
    public void attackPhase() {
        countries = board.getCountries();
        attackPhase.attackPhaseScore();
        while (countries[attackPhase.largestArmy()].getArmies() > 2) {
            HashMap<Country, Double> attackData = attackPhase.scores;
            Double previousValue;
            int attackerCountry = attackPhase.largestArmy();
            previousValue = -1.0;
            int defendingCountry = -1;
            Country chosenAttacker = countries[attackerCountry];
            Country[] adjacentCountry = chosenAttacker.getAdjoiningList();
            for (int i = 0; i < adjacentCountry.length; i++) {
                if (attackData.get(adjacentCountry[i]) > previousValue && adjacentCountry[i].getOwner() != ID) {
                    previousValue = attackData.get(adjacentCountry[i]);
                    defendingCountry = adjacentCountry[i].getCode();
                }
            }
            board.attack(attackerCountry, defendingCountry, true);
        }
    }

    /**
     * Whenever you take over a country, this method will be called by Lux. You must return the number of armies to move into the newly-won country.<P>
     * The minimum acceptable answer is the number of attack dice you used.    <BR>
     * The maximum acceptable value is the number of armies left in the attacking country minus one.  <P>
     * If you answer outside of these bounds it will be rounded to the nearest.
     *
     * @param countryCodeAttacker
     * @param countryCodeDefender
     */
    @Override
    public int moveArmiesIn(int countryCodeAttacker, int countryCodeDefender) {
        countries = board.getCountries();
        return countries[countryCodeAttacker].getMoveableArmies();
    }

    /**
     * The last phase of the turn is for fortifying your armies into neighboring countries.    <p>
     * Each Country has a moveableArmies variable. Right before the board calls your fortifyPhase method, it will set
     * each Country's moveableArmies equal to that Country's number of armies. Every time you fortify from a country the
     * movableArmies will be decremented, to a minimum of 0. <P>
     * Within this method you should invoke     <BR>&nbsp;&nbsp;&nbsp;&nbsp;
     * board.fortifyArmies( int numberOfArmies, int countryCodeOrigin, int countryCodeDestination);    <BR>
     * to do the actual moving.
     */
    @Override
    public void fortifyPhase() {
        Random random = new Random();
        countries = board.getCountries();
        int bestChoiceSoFar = -1;
        int numberOfEnemyNeighboursTemp = 0;
        int numberOfEnemyNeighbours = 0;
        for (int i = 0; i < countries.length; i++) {
            if (countries[i].getOwner() == ID && countries[i].getMoveableArmies() > 0) {
                Country[] neighboursOfChosenCountry = countries[i].getAdjoiningList();
                for (int j = 0; j < neighboursOfChosenCountry.length; j++) {
                    if (neighboursOfChosenCountry[j].getOwner() == ID) {
                        numberOfEnemyNeighbours = neighboursOfChosenCountry[j].getNumberEnemyNeighbors();
                        if (numberOfEnemyNeighbours > numberOfEnemyNeighboursTemp) {
                            bestChoiceSoFar = neighboursOfChosenCountry[j].getCode();
                            numberOfEnemyNeighboursTemp = numberOfEnemyNeighbours;
                        }
                    }
                }
                numberOfEnemyNeighbours = countries[i].getNumberEnemyNeighbors();
                if (numberOfEnemyNeighboursTemp > numberOfEnemyNeighbours) {
                    board.fortifyArmies(countries[i].getMoveableArmies(), i, bestChoiceSoFar);
                } else if (numberOfEnemyNeighbours == 0) {
                    int randomCountryChoice = Math.abs(random.nextInt(neighboursOfChosenCountry.length));
                    board.fortifyArmies(countries[i].getMoveableArmies(), i, neighboursOfChosenCountry[randomCountryChoice].getCode());
                }
            }
        }
    }

    /**
     * This is the name of your agent. It will identify you in the info window and record books.
     */
    @Override
    public String name() {
        return "HeuristicAI";
    }

    /**
     * The version of your agent. It is used by the plugin manager to notify the user when new versions are made available.
     */
    @Override
    public float version() {
        return 1.0f;
    }





    /**
     * A description of your agent.
     */
    @Override
    public String description() {
        return null;
    }

    /**
     * If your agent wins the game then this method will be called.		<BR>
     * Whatever you return will be displayed in big letters across the screen.
     * <p>
     * If you think that you will win a lot feel free to provide many different answers for variety.
     */
    @Override
    public String youWon() {
        return "I am so simple minded and yet I have defeated you.";
    }

    /**
     * This method is used to send some notifications to the LuxAgent. You can safely ignore it if you like. Currently 2
     * message types are sent:
     * <p>
     * 1. "youLose" will be sent when the agent gets eliminated from the game. It's data object is an Integer with the
     * ID of the conquering player.
     * 2. "attackNotice" gets sent every time an attack order is made against one of your agent's countries. An order
     * could be a single attack round or it could be an attack-till-death order. The data object is a List containing
     * Integer's of the attacking and defending country codes.
     * <p>
     * The Angry agent has a sample implementation that you can use to recieve these events. It's possible that more
     * will be added in the future.
     *
     * @param message
     * @param data
     */
    @Override
    public String message(String message, Object data) {
        return null;
    }
}
