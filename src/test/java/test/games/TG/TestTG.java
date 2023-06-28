package test.games.TG;

import core.AbstractParameters;
import core.AbstractPlayer;
import core.CoreConstants;
import core.Game;
import core.actions.AbstractAction;
import core.components.Deck;
import java.util.ArrayList;
import java.util.List;
import games.GameType;
import games.TG.components.TGForwardModel;
import games.TG.components.TGGameState;
import games.TG.components.TGParameters;
import games.TG.actions.Pass;
import games.TG.actions.Play;
import org.junit.*;
import players.simple.RandomPlayer;
import core.components.FrenchCard;

import static games.TG.components.TGGameState.Phase.PLAYING;
import static org.junit.Assert.*;

public class TestTG {

    private TGForwardModel forwardModel;
    private TGGameState gameState;

    AbstractParameters gameParameters = new TGParameters(System.currentTimeMillis());

    List<AbstractAction> actions;


    @Before
    public void setUp() {
        forwardModel = new TGForwardModel();
        // Assuming the constructor TGGameState(int nPlayers) exists.
        gameState = new TGGameState(gameParameters, 3);
        forwardModel._setup(gameState);

    }

    @Test
    public void testSetup() {
        // Assert that game phase is set to PASSING
        assertEquals(TGGameState.Phase.PASSING, gameState.getGamePhase());
        // Assert that all players have a deck with 17 cards (assuming a 3 player game)
        gameState.getPlayerDecks().forEach(deck -> assertEquals(17, deck.getSize()));
        // Add more assertions based on your game logic
    }

    @Test
    public void testAfterAction() {
        // Setup a Pass action
        FrenchCard cardToPass = gameState.getPlayerDecks().get(0).get(0); // Get first card of first player
        Pass passAction = new Pass(0, cardToPass);
        forwardModel._afterAction(gameState, passAction);

        // Assert that the card has been removed from the player's deck
        assertFalse(gameState.getPlayerDecks().get(0).contains(cardToPass));

        // Assert that the card has been added to the pending passes of the player
        assertTrue(gameState.pendingPasses.get(0).contains(cardToPass));


    }

    @Test
    public void testSetupPhaseIsPassing() {
        assertEquals(TGGameState.Phase.PASSING, gameState.getGamePhase());
    }

    //@Test
    //public void testPendingPasses() {
        //assertEquals(3, gameState.pendingPasses.size());
        //assertTrue(gameState.pendingPasses.stream().allMatch(ArrayList::isEmpty));
    //}

    @Test
    public void testPlayerDecksSize() {
        assertEquals(3, gameState.getPlayerDecks().size());
        for (Deck<FrenchCard> deck : gameState.getPlayerDecks()) {
            assertEquals(17, deck.getSize());
        }
    }

    @Test
    public void testDrawDeckSize() {
        assertEquals(52 - (17*3)-1, gameState.getDrawDeck().getSize());
    }

    @Test
    public void testRemovedCard() {
        boolean containsRemovedCard = gameState.getDrawDeck().getComponents().stream().anyMatch(c ->
                c.suite == FrenchCard.Suite.Diamonds &&
                        c.number == 2 &&
                        c.type == FrenchCard.FrenchCardType.Number
        );
        assertFalse(containsRemovedCard);
    }

    @Test
    public void testAfterActionPass() {
        int initialDeckSize = gameState.getPlayerDecks().get(0).getSize();
        FrenchCard card = gameState.getPlayerDecks().get(0).get(0);
        Pass passAction = new Pass(0, card);
        forwardModel._afterAction(gameState, passAction);

        // Check that the card has been removed from the player's deck
        assertEquals(initialDeckSize - 1, gameState.getPlayerDecks().get(0).getSize());
        assertFalse(gameState.getPlayerDecks().get(0).getComponents().contains(card));

        // Check that the card has been added to the pendingPasses
        assertTrue(gameState.pendingPasses.get(0).contains(card));
    }

}








