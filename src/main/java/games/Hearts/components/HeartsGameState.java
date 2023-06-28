package games.Hearts.components;

import core.AbstractGameState;
import core.AbstractParameters;
import core.CoreConstants;
import core.components.Component;
import core.components.Deck;
import core.components.FrenchCard;
import core.interfaces.IGamePhase;
import games.GameType;
import java.util.ArrayList;
import java.util.*;

import java.util.function.*;

/**
 * <p>The game state encapsulates all game information. It is a data-only class, with game functionality present
 * in the Forward Model or actions modifying the state of the game.</p>
 * <p>Most variables held here should be {@link Component} subclasses as much as possible.</p>
 * <p>No initialisation or game logic should be included here (not in the constructor either). This is all handled externally.</p>
 * <p>Computation may be included in functions here for ease of access, but only if this is querying the game state information.
 * Functions on the game state should never <b>change</b> the state of the game.</p>
 */
public class HeartsGameState extends AbstractGameState {
    /**
     * @param gameParameters - game parameters.
     * @param nPlayers       - number of players in the game
     */

    List<Deck<FrenchCard>> playerDecks;
    Deck<FrenchCard> drawDeck;

    List<Deck<FrenchCard>> trickDecks;

    public boolean heartsBroken;

    public int[] playerPassCounter;

    public int[] playerTricksTaken;

    public boolean firstTurn = true;

    public int currentRound;

    public List<List<FrenchCard>> pendingPasses;

    public static final int maxPossibleScore = 26;
    public static final int maxHighValueCards = 14;
    public static final int maxPossibleTricks = 13;

    private static final double MAX_HIGH_VALUE_CARD_PASS_BONUS = 1.6;

    private static final int HIGH_VALUE_THRESHOLD = 11;  // Jack or higher


    public int playerWithTwoOfClubs;

    private boolean gameEnded = false;

    // This map stores each player's chosen card
    private Map<Integer, FrenchCard> chosenCards;
    private Map<Integer, Integer> playerPoints;

    List<List<FrenchCard>> passedCards;

    public List<Map.Entry<Integer, FrenchCard>> currentRoundCards = new ArrayList<>();
    public FrenchCard.Suite firstCardSuit;


    private int currentPlayer;

    public HeartsGameState(AbstractParameters gameParameters, int nPlayers) {
        super(gameParameters, nPlayers);
        chosenCards = new HashMap<>();
        playerPoints = new HashMap<>();
        playerPassCounter = new int[nPlayers];
        playerTricksTaken = new int[nPlayers];
        trickDecks = new ArrayList<>();
        currentRound = 1;
        this.pendingPasses = new ArrayList<>(getNPlayers());
        for (int i = 0; i < getNPlayers(); i++) {
            pendingPasses.add(new ArrayList<>());
            trickDecks.add(new Deck<>("Player " + i + " deck", i, CoreConstants.VisibilityMode.VISIBLE_TO_OWNER));
        }
        this.passedCards = new ArrayList<>(getNPlayers());
        for (int i = 0; i < getNPlayers(); i++) {
            passedCards.add(new ArrayList<>());
        }
    }

    /**
     * @return the enum value corresponding to this game, declared in {@link GameType}.
     */
    @Override
    protected GameType _getGameType() {
        // TODO: replace with game-specific enum value declared in GameType
        return GameType.Hearts;
    }

    /**
     * Returns all Components used in the game and referred to by componentId from actions or rules.
     * This method is called after initialising the game state, so all components will be initialised already.
     *
     * @return - List of Components in the game.
     */
    @Override
    protected List<Component> _getAllComponents() {
        // TODO: add all components to the list
        return new ArrayList<Component>() {{
            addAll(playerDecks);
            add(drawDeck);
        }};
    }



    public enum Phase implements IGamePhase {
        PASSING,
        PLAYING
    }




    public Deck<FrenchCard> getDrawDeck() {
        return drawDeck;
    }

    public Map<Integer, FrenchCard> getChosenCards() {
        return this.chosenCards;
    }

    // Setter for chosenCards
    public void setChosenCards(Map<Integer, FrenchCard> chosenCards) {
        this.chosenCards = chosenCards;
    }


    public List<Deck<FrenchCard>> getPlayerDecks() {
        return playerDecks;
    }




    public void calculatePoints(int playerId) {
        // Get the trick deck for the player
        Deck<FrenchCard> trickDeck = trickDecks.get(playerId);
        if (trickDeck != null) {
            int points = 0;

            // Iterate over all cards in the trick deck
            for (FrenchCard card : trickDeck.getComponents()) {
                if (card.suite == FrenchCard.Suite.Hearts) {
                    points += 1;
                }
                // The queen of spades is worth 13 points
                else if (card.suite == FrenchCard.Suite.Spades && card.number == 12) {
                    points += 13;
                }
            }


            // Check if the playerID exists in the map
            if (playerPoints.containsKey(playerId)) {
                // If it does, add the points
                playerPoints.put(playerId, playerPoints.get(playerId) + points);
            } else {
                // If not, add the playerID to the map with the calculated points
                playerPoints.put(playerId, points);
            }

            //System.out.println("player points:" + playerPoints);

            // Clear the trick deck after its points have been added
            trickDeck.clear();
        }
    }




    public int getPlayerPoints(int playerID) {
        int points = playerPoints.getOrDefault(playerID,0); //put this into gamescore
        return points;
    }

    public Map<Integer, Integer> getPlayerPointsMap() {
        return playerPoints;
    }







    /**
         * <p>Create a deep copy of the game state containing only those components the given player can observe.</p>
         * <p>If the playerID is NOT -1 and If any components are not visible to the given player (e.g. cards in the hands
         * of other players or a face-down deck), then these components should instead be randomized (in the previous examples,
         * the cards in other players' hands would be combined with the face-down deck, shuffled together, and then new cards drawn
         * for the other players).</p>
         * <p>If the playerID passed is -1, then full observability is assumed and the state should be faithfully deep-copied.</p>
         *
         * <p>Make sure the return type matches the class type, and is not AbstractGameState.</p>
         *
         * @param playerId - player observing this game state.
         */
    @Override
    protected AbstractGameState _copy(int playerId) {
        HeartsGameState copy = new HeartsGameState(gameParameters.copy(), getNPlayers());

        // Copy player decks
        copy.playerDecks = new ArrayList<>();
        for (Deck<FrenchCard> d : playerDecks) {
            copy.playerDecks.add(d.copy());
        }

        // Copy draw deck
        copy.drawDeck = drawDeck.copy();

        if (getCoreGameParameters().partialObservable && playerId != -1) {
            for (int i = 0; i < getNPlayers(); i++) {
                if (i != playerId) {
                    int originalSize = playerDecks.get(i).getSize();  // Store the original size of the deck
                    copy.drawDeck.add(copy.playerDecks.get(i));
                    copy.playerDecks.get(i).clear();

                    for (int j = 0; j < originalSize; j++) {
                        copy.playerDecks.get(i).add(copy.drawDeck.draw());
                    }
                }
            }
        }

        // Copy heartsBroken
        copy.heartsBroken = heartsBroken;

        copy.currentRound = currentRound;

        // Copy playerPassCounter
        copy.playerPassCounter = Arrays.copyOf(playerPassCounter, playerPassCounter.length);

        copy.playerTricksTaken = Arrays.copyOf(playerTricksTaken, playerTricksTaken.length);

        // Copy firstTurn
        copy.firstTurn = firstTurn;

        // Copy pendingPasses
        copy.pendingPasses = new ArrayList<>();
        for (List<FrenchCard> list : pendingPasses) {
            copy.pendingPasses.add(new ArrayList<>(list));
        }

        // Copy playerWithTwoOfClubs
        copy.playerWithTwoOfClubs = playerWithTwoOfClubs;

        // Copy gameEnded
        copy.gameEnded = gameEnded;

        // Copy chosenCards
        copy.chosenCards = new HashMap<>();
        for (Map.Entry<Integer, FrenchCard> entry : chosenCards.entrySet()) {
            copy.chosenCards.put(entry.getKey(), entry.getValue().copy());
        }

        // Copy playerPoints
        copy.playerPoints = new HashMap<>(playerPoints);

        // Copy passedCards
        copy.passedCards = new ArrayList<>();
        for (List<FrenchCard> list : passedCards) {
            copy.passedCards.add(new ArrayList<>(list));
        }

        // Copy currentRoundCards
        copy.currentRoundCards = new ArrayList<>();
        for (Map.Entry<Integer, FrenchCard> entry : currentRoundCards) {
            copy.currentRoundCards.add(new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue().copy()));
        }

        // Copy firstCardSuit
        copy.firstCardSuit = firstCardSuit;

        // Copy currentPlayer
        copy.currentPlayer = currentPlayer;

        return copy;
    }



    /**
     * @param playerId - player observing the state.
     * @return a score for the given player approximating how well they are doing (e.g. how close they are to winning
     * the game); a value between 0 and 1 is preferred, where 0 means the game was lost, and 1 means the game was won.
     */
    @Override
    protected double _getHeuristicScore(int playerId) {
        return new HeartsHeuristic().evaluateState(this, playerId);
    }



    /**
     * @param playerId - player observing the state.
     * @return the true score for the player, according to the game rules. May be 0 if there is no score in the game.
     */
    @Override
    public double getGameScore(int playerId) {
        return playerPoints.getOrDefault(playerId,0);
    }



    @Override
    protected ArrayList<Integer> _getUnknownComponentsIds(int playerId) {
        return new ArrayList<Integer>() {{
            add(drawDeck.getComponentID());
            for (Component c : drawDeck.getComponents()) {
                add(c.getComponentID());
            }
        }};
    }

    @Override
    public boolean _equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HeartsGameState)) return false;
        if (!super.equals(o)) return false;
        HeartsGameState that = (HeartsGameState) o;
        return heartsBroken == that.heartsBroken &&
                currentRound == that.currentRound &&
                firstTurn == that.firstTurn &&
                playerWithTwoOfClubs == that.playerWithTwoOfClubs &&
                gameEnded == that.gameEnded &&
                currentPlayer == that.currentPlayer &&
                Objects.equals(playerDecks, that.playerDecks) &&
                Objects.equals(drawDeck, that.drawDeck) &&
                Objects.equals(trickDecks, that.trickDecks) &&
                Objects.equals(playerPassCounter, that.playerPassCounter) &&
                Objects.equals(playerTricksTaken, that.playerTricksTaken) &&
                Objects.equals(pendingPasses, that.pendingPasses) &&
                Objects.equals(chosenCards, that.chosenCards) &&
                Objects.equals(playerPoints, that.playerPoints) &&
                Objects.equals(passedCards, that.passedCards) &&
                Objects.equals(currentRoundCards, that.currentRoundCards) &&
                Objects.equals(firstCardSuit, that.firstCardSuit);
    }










    public List<Deck<FrenchCard>> getPlayerTrickDecks() {
        return trickDecks;
    }

    public void setGameEnded(boolean gameEnded) {
        this.gameEnded = gameEnded;
    }

    public boolean isGameEnded() {
        return this.gameEnded;
    }

    public void setCurrentPlayer(int currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public void setCurrentRound(int currentRound) {
        this.currentRound = currentRound;
    }

    public void setFirstTurn(boolean value){
        this.firstTurn = value;
    }

    public void setPlayerWithTwoOfClubs(int player){
        this.playerWithTwoOfClubs = player;
    }

    public int getPlayerWithTwoOfClubs() {
        return this.playerWithTwoOfClubs;
    }


    @Override
    public int getOrdinalPosition(int playerId, Function<Integer, Double> scoreFunction, BiFunction<Integer, Integer, Double> tiebreakFunction) {
        int ordinal = 1;
        double playerScore = scoreFunction.apply(playerId);
        for (int i = 0, n = getNPlayers(); i < n; i++) {
            double otherScore = scoreFunction.apply(i);
            if (otherScore < playerScore) // Changed to < because lower score is better
                ordinal++;
            else if (otherScore == playerScore && tiebreakFunction != null && tiebreakFunction.apply(i, 1) != Double.MAX_VALUE) {
                if (getOrdinalPositionTiebreak(i, tiebreakFunction, 1) > getOrdinalPositionTiebreak(playerId, tiebreakFunction, 1))
                    ordinal++;
            }
        }
        return ordinal;
    }





    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), playerDecks, drawDeck);
    }}








