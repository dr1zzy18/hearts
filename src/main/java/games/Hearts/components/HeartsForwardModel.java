package games.Hearts.components;

import core.AbstractGameState;
import core.CoreConstants;
import core.StandardForwardModel;
import core.actions.AbstractAction;
import core.components.Deck;
import core.components.FrenchCard;
import games.Hearts.actions.Play;
import games.Hearts.actions.Pass;

import java.util.*;

import static core.CoreConstants.GameResult.*;

/**
 * <p>The forward model contains all the game rules and logic. It is mainly responsible for declaring rules for:</p>
 * <ol>
 *     <li>Game setup</li>
 *     <li>Actions available to players in a given game state</li>
 *     <li>Game events or rules applied after a player's action</li>
 *     <li>Game end</li>
 * </ol>
 */
public class HeartsForwardModel extends StandardForwardModel {

    @Override
    public void _setup(AbstractGameState firstState) {
        HeartsGameState hgs = (HeartsGameState) firstState;
        hgs.setGamePhase(HeartsGameState.Phase.PASSING);

        hgs.playerWithTwoOfClubs = -1;
        hgs.setFirstPlayer(0);
        hgs.pendingPasses = new ArrayList<>(hgs.getNPlayers());
        for (int i = 0; i < hgs.getNPlayers(); i++) {
            hgs.pendingPasses.add(new ArrayList<>());
        }

        FrenchCard remover = new FrenchCard(FrenchCard.FrenchCardType.Number, FrenchCard.Suite.Diamonds, 2);

        hgs.firstTurn = true;  // Initialize to true

        hgs.playerDecks = new ArrayList<>();
        //hgs.trickDecks = new ArrayList<>();
        hgs.drawDeck = FrenchCard.generateDeck("DrawDeck", CoreConstants.VisibilityMode.HIDDEN_TO_ALL);
        //hgs.drawDeck.shuffle(new Random((hgs.getGameParameters().getRandomSeed())));
        hgs.drawDeck.shuffle(new Random(System.currentTimeMillis()));

        int numOfPlayers = hgs.getNPlayers();


        // Define the cards to remove for each number of players
        Map<Integer, List<FrenchCard>> cardsToRemove = new HashMap<>();
        cardsToRemove.put(3, Arrays.asList(new FrenchCard(FrenchCard.FrenchCardType.Number, FrenchCard.Suite.Diamonds, 2)));
        cardsToRemove.put(5, Arrays.asList(new FrenchCard(FrenchCard.FrenchCardType.Number, FrenchCard.Suite.Diamonds, 2),
                new FrenchCard(FrenchCard.FrenchCardType.Number, FrenchCard.Suite.Spades, 2)));
        cardsToRemove.put(6, Arrays.asList(new FrenchCard(FrenchCard.FrenchCardType.Number, FrenchCard.Suite.Diamonds, 2),
                new FrenchCard(FrenchCard.FrenchCardType.Number, FrenchCard.Suite.Diamonds, 3),
                new FrenchCard(FrenchCard.FrenchCardType.Number, FrenchCard.Suite.Clubs, 3),
                new FrenchCard(FrenchCard.FrenchCardType.Number, FrenchCard.Suite.Clubs, 4)));
        cardsToRemove.put(7, Arrays.asList(new FrenchCard(FrenchCard.FrenchCardType.Number, FrenchCard.Suite.Diamonds, 2),
                new FrenchCard(FrenchCard.FrenchCardType.Number, FrenchCard.Suite.Diamonds, 3),
                new FrenchCard(FrenchCard.FrenchCardType.Number, FrenchCard.Suite.Clubs, 3)));

        // Remove the cards for the current number of players
        if (cardsToRemove.containsKey(numOfPlayers)) {
            for (FrenchCard removeCard : cardsToRemove.get(numOfPlayers)) {
                int removeIndex = -1;
                for (int i = 0; i < 52; i++) {
                    FrenchCard card = hgs.drawDeck.get(i);
                    if (card.suite == removeCard.suite && card.number == removeCard.number && card.type == removeCard.type) {
                        removeIndex = i;
                        break;
                    }
                }
                if (removeIndex != -1) {
                    hgs.drawDeck.remove(removeIndex);
                }
            }
        }







        boolean[] visibility = new boolean[firstState.getNPlayers()];
        Arrays.fill(visibility, true);

        for (int i = 0; i < hgs.getNPlayers(); i++){
            Deck<FrenchCard> playerDeck = new Deck<>("Player " + i + " deck", i, CoreConstants.VisibilityMode.VISIBLE_TO_OWNER);
            //Deck<FrenchCard> trickDeck = new Deck<>("Player " + i + " deck", i, CoreConstants.VisibilityMode.VISIBLE_TO_OWNER);
            hgs.playerDecks.add(playerDeck);
            int numberOfCards;
            if (hgs.getNPlayers() == 3) {
                numberOfCards = 17;
            } else if(hgs.getNPlayers()==4){
                numberOfCards = 13;
            }
            else if (hgs.getNPlayers() == 5) {
                numberOfCards = 10;
            } else if (hgs.getNPlayers() == 6) {
                numberOfCards = 8;
            } else if (hgs.getNPlayers() == 7) {
                numberOfCards = 7;
            } else {
                numberOfCards = 0;  // or set to whatever default or error value is appropriate
            }

            //hgs.trickDecks.add(hgs.trickDeck);
            for (int card = 0; card < numberOfCards; card++) {
                playerDeck.add(hgs.drawDeck.draw());
            }

        }

    }




    public void _afterAction(AbstractGameState gameState, AbstractAction action) {
        HeartsGameState hgs = (HeartsGameState) gameState;
        //System.out.println("Player " + gameState.getCurrentPlayer() + " performed action: " + action + " during " + hgs.getGamePhase() + " phase.");


        if (hgs.getGamePhase() == HeartsGameState.Phase.PASSING) {
            if (action instanceof Pass) {
                Pass passAction = (Pass) action;

                // Remove card from player's deck and add it to the next player's deck
                Deck<FrenchCard> playerDeck = hgs.playerDecks.get(passAction.playerID);


                playerDeck.remove(passAction.card1);
                hgs.pendingPasses.get(passAction.playerID).add(passAction.card1);

                hgs.playerPassCounter[passAction.playerID]++;

                // Check if current player has passed 3 cards
                if (hgs.playerPassCounter[passAction.playerID] == 3) {
                    // Reset player's pass counter
                    hgs.playerPassCounter[passAction.playerID] = 0;

                    // Check if all players have passed their cards
                    if (passAction.playerID == hgs.getNPlayers() - 1) {
                        hgs.setGamePhase(HeartsGameState.Phase.PLAYING);

                        // Determine the pass direction based on the current round
                        int passDirection;
                        switch (hgs.currentRound) {
                            case 1:  // To the left
                                passDirection = 1;
                                break;
                            case 2:  // To the right
                                passDirection = hgs.getNPlayers() - 1;
                                break;
                            case 3:  // Across the table (for 4 players)
                                passDirection = hgs.getNPlayers() / 2;
                                break;
                            case 4:  // No passing
                                passDirection = 0;
                                break;
                            default:
                                throw new IllegalStateException("Unexpected value: " + hgs.currentRound);
                        }

                        // Add pending passes to next player's deck
                        for (int i = 0; i < hgs.getNPlayers(); i++) {
                            Deck<FrenchCard> nextPlayerDeck = hgs.playerDecks.get((i + passDirection) % hgs.getNPlayers());
                            for (FrenchCard card : hgs.pendingPasses.get(i)) {
                                nextPlayerDeck.add(card);
                            }
                            hgs.pendingPasses.get(i).clear();  // Clear this player's pending passes
                        }
                        // Set the first player of the PLAYING phase to be the player who has the 2 of clubs
                        for (int i = 0; i < hgs.getNPlayers(); i++) {
                            Deck<FrenchCard> playerDeck1 = hgs.playerDecks.get(i);
                            for (FrenchCard card : playerDeck1.getComponents()) {
                                if (card.suite == FrenchCard.Suite.Clubs && card.number == 2) {
                                    hgs.setFirstPlayer(i);
                                    //System.out.println("First player of the PLAYING phase is =" + i);
                                    return;
                                }
                            }
                        }
                    }

                    // End player's turn here after 3 passes
                    endPlayerTurn(hgs);
                    return;
                }
            } else {
                throw new IllegalArgumentException("Invalid action type during PASSING phase.");
            }
        } else {
            if (hgs.firstTurn) {
                hgs.firstTurn = false;  // No longer the first turn
            }

            if (action instanceof Play) {
                Play play = (Play) action;
                if (hgs.currentRoundCards.isEmpty()) {
                    hgs.firstCardSuit = play.card.suite;  // Save the suit of the first card
                }

                // Store played card and its player ID
                hgs.currentRoundCards.add(new AbstractMap.SimpleEntry<>(play.playerID, play.card));

                // If a heart has been played, set heartsBroken to true
                if (play.card.suite == FrenchCard.Suite.Hearts) {
                    hgs.heartsBroken = true;
                }

                // Remove the card from the player's deck
                hgs.playerDecks.get(play.playerID).remove(play.card);
                hgs.calculatePoints(play.playerID);
            }

            // Check if all players have played a card in this round
            if (hgs.currentRoundCards.size() == hgs.getNPlayers()) {
                _endTurn(hgs);
                if (!hgs.isGameEnded()) {
                    startNewRound(hgs);
                }
                // Do not endPlayerTurn here
                return;
            }
        }

        // Only end player's turn here if it's not PASSING phase
        if (hgs.getGamePhase() != HeartsGameState.Phase.PASSING) {
            endPlayerTurn(hgs);
        }
    }



    @Override
    protected void endGame(AbstractGameState gs){
        double minScore = 100;
        gs.setGameStatus(GAME_END);
        List<Integer> winningPlayers = new ArrayList<>();

        for (int i = 0; i < gs.getNPlayers(); i++) {

            double playerScore = gs.getGameScore(i);

            if (playerScore < minScore) {
                minScore = playerScore;
                winningPlayers.clear();
                winningPlayers.add(i);
            } else if (playerScore == minScore) {
                winningPlayers.add(i);
            }

        }


        if (winningPlayers.size() == 1) {
            gs.setPlayerResult(WIN_GAME,winningPlayers.get(0));

        } else {
            for (Integer playerID : winningPlayers) {
                gs.setPlayerResult(DRAW_GAME, playerID);
                // Debug: print the result of setPlayerResult
            }
        }




        // Set result for the losing players
        for(int playerID = 0; playerID < gs.getNPlayers(); playerID++) {
            if (!winningPlayers.contains(playerID)) {
                gs.setPlayerResult(LOSE_GAME, playerID);
            }
        }





    }




    @Override
    public List<AbstractAction> _computeAvailableActions(AbstractGameState gameState) {
        HeartsGameState hgs = (HeartsGameState) gameState;
        ArrayList<AbstractAction> actions = new ArrayList<>();
        int player = hgs.getCurrentPlayer();
        Deck<FrenchCard> playerHand = hgs.playerDecks.get(player);

        if (hgs.getGamePhase() == HeartsGameState.Phase.PASSING) {
            // Generate Pass action for each card in the player's hand
            List<FrenchCard> cards = playerHand.getComponents();
            for (FrenchCard card : cards) {
                actions.add(new Pass(player, card));
            }
        }else {

            if (hgs.firstTurn) {
                // First turn of the game, the player with 2 of clubs must play it
                for (FrenchCard card : playerHand.getComponents()) {
                    if (card.suite == FrenchCard.Suite.Clubs && card.number == 2) {
                        actions.add(new Play(player, card));
                        return actions;  // Return immediately, no other actions available
                    }
                }
            }

            boolean hasLeadSuit = playerHand.getComponents().stream().anyMatch(card -> card.suite.equals(hgs.firstCardSuit));

            if (hasLeadSuit) {
                // Player can only play cards of the lead suit
                for (FrenchCard card : playerHand.getComponents()) {
                    if (card.suite.equals(hgs.firstCardSuit)) {
                        actions.add(new Play(player, card));
                    }
                }
            } else {
                // Player can play cards of any other suit, but only play a heart if hearts have been broken or their hand only contains hearts
                boolean onlyHasHearts = playerHand.getComponents().stream().allMatch(card -> card.suite == FrenchCard.Suite.Hearts);
                for (FrenchCard card : playerHand.getComponents()) {
                    if (card.suite != FrenchCard.Suite.Hearts || hgs.heartsBroken || onlyHasHearts) {
                        actions.add(new Play(player, card));
                    }
                }
            }
        }
        //System.out.println("Player " + player + " available actions: " + actions);

        return actions;
    }








    private void _endTurn(HeartsGameState hgs) {
        HeartsParameters params = (HeartsParameters) hgs.getGameParameters();
        Map<Integer, Integer> pointsMap = hgs.getPlayerPointsMap();

        int highestCardValue = -1;
        int winningPlayerID = -1;
        for (Map.Entry<Integer, FrenchCard> entry : hgs.currentRoundCards) {
            FrenchCard card = entry.getValue();
            if (card.suite.equals(hgs.firstCardSuit) && card.number > highestCardValue) {
                highestCardValue = card.number;
                winningPlayerID = entry.getKey();
                //System.out.println("Winning Player ID: " + winningPlayerID);
            }
        }

        // Add all cards from this round to the winner's trick deck
        if (winningPlayerID != -1) {
            for (Map.Entry<Integer, FrenchCard> entry : hgs.currentRoundCards) {
                //System.out.println("Player " + entry.getKey() + " played " + entry.getValue());
                hgs.trickDecks.get(winningPlayerID).add(entry.getValue());
                //System.out.println("Added " + entry.getValue() + " to Player " + winningPlayerID + "'s trick deck.");
            }
            hgs.playerTricksTaken[winningPlayerID]++;
            //System.out.println("Winning trick deck: " + hgs.trickDecks);
            hgs.setFirstPlayer(winningPlayerID);
            startNewRound(hgs);
        }

        // Check if all cards from player hands have been played
        if (hgs.playerDecks.stream().allMatch(deck -> deck.getSize() == 0)) {
            boolean scoreAbove100 = hgs.getPlayerPointsMap().values().stream().anyMatch(score -> score >= 100);



            // If any player has reached 100 points or more, end the game
            if (scoreAbove100) {

                endGame(hgs);

            } else {
                // If no player has reached 100 points yet, reshuffle and deal new hands
                if (hgs.currentRound == 4) {
                    hgs.currentRound = 1;
                    _setup(hgs);
                } else {
                    hgs.currentRound++;
                    _setup(hgs);
                }
            }
        }
    }

    private void startNewRound(HeartsGameState hgs) {
        hgs.currentRoundCards.clear();
        hgs.firstCardSuit = null;
        hgs.setCurrentPlayer(hgs.getFirstPlayer());
        //System.out.println("A new round has started. The first player of this round is Player " + hgs.getCurrentPlayer());
    }





}