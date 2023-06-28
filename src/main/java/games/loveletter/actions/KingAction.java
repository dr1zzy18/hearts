package games.loveletter.actions;

import core.AbstractGameState;
import core.CoreConstants;
import core.CoreConstants.VisibilityMode;
import core.components.Deck;
import core.interfaces.IPrintable;
import games.loveletter.LoveLetterGameState;
import games.loveletter.cards.LoveLetterCard;

import java.util.ArrayList;
import java.util.List;

/**
 * The King lets two players swap their hand cards.
 */
public class KingAction extends PlayCard implements IPrintable {

    public KingAction(int playerID, int opponentID, boolean canExecuteEffect) {
        super(LoveLetterCard.CardType.King, playerID, opponentID, null, null, canExecuteEffect);
    }

    @Override
    protected boolean _execute(LoveLetterGameState llgs) {
        Deck<LoveLetterCard> playerDeck = llgs.getPlayerHandCards().get(playerID);
        Deck<LoveLetterCard> opponentDeck = llgs.getPlayerHandCards().get(targetPlayer);

        // create a temporary deck to store cards in and then swap cards accordingly
        Deck<LoveLetterCard> tmpDeck = new Deck<>("tmp", VisibilityMode.HIDDEN_TO_ALL);
        while (opponentDeck.getSize() > 0)
            tmpDeck.add(opponentDeck.draw());
        while (playerDeck.getSize() > 0)
            opponentDeck.add(playerDeck.draw());
        while (tmpDeck.getSize() > 0)
            playerDeck.add(tmpDeck.draw());

        return true;
    }

    @Override
    public String _toString(){
        return "King (" + playerID + " trades hands with " + targetPlayer + ")";
    }

    @Override
    public String getString(AbstractGameState gameState) {
        return toString();
    }

    @Override
    public void printToConsole(AbstractGameState gameState) {
        System.out.println(this);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof KingAction && super.equals(o);
    }

    @Override
    public KingAction copy() {
        return this;
    }

    public static List<? extends PlayCard> generateActions(LoveLetterGameState gs, int playerID) {
        List<PlayCard> cardActions = new ArrayList<>();
        for (int targetPlayer = 0; targetPlayer < gs.getNPlayers(); targetPlayer++) {
            if (targetPlayer == playerID || gs.getPlayerResults()[targetPlayer] == CoreConstants.GameResult.LOSE_ROUND || gs.isProtected(targetPlayer))
                continue;
            cardActions.add(new KingAction(playerID, targetPlayer, true));
        }
        if (cardActions.size() == 0) cardActions.add(new KingAction(playerID, -1, false));
        return cardActions;
    }
}
