package games.Hearts.actions;

import core.AbstractGameState;
import core.actions.AbstractAction;
import core.components.Deck;
import core.components.FrenchCard;
import core.interfaces.IPrintable;
import games.Hearts.components.HeartsGameState;

import java.util.Objects;

public class Pass extends AbstractAction implements IPrintable {
    public final int playerID;
    public final FrenchCard card1;


    public Pass(int playerID, FrenchCard card1) {
        this.playerID = playerID;
        this.card1 = card1;
    }

    @Override
    public boolean execute(AbstractGameState gameState) {
        HeartsGameState hgs = (HeartsGameState) gameState;
        if (playerID >= 0 && playerID < hgs.getPlayerDecks().size()) {
            Deck<FrenchCard> playerHand = hgs.getPlayerDecks().get(playerID);
            if (playerHand.getComponents().remove(card1)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public AbstractAction copy() {
        return new Pass(playerID, card1.copy());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pass)) return false;
        Pass passCardsAction = (Pass) o;
        return playerID == passCardsAction.playerID &&
                card1.equals(passCardsAction.card1);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerID, card1);
    }

    @Override
    public void printToConsole() {
        System.out.println(toString());
    }

    @Override
    public String getString(AbstractGameState gameState) {
        return toString();
    }

    @Override
    public String toString() {
        return "Pass: " +
                card1;
    }
}
