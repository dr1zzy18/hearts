package games.Hearts.heuristics;

import core.AbstractGameState;
import games.Hearts.components.HeartsHeuristic;
import games.Hearts.components.HeartsGameState;

public class HighValueCardFactorHeuristic extends HeartsHeuristic {

    @Override
    public double evaluateState(AbstractGameState gs, int playerId) {
        HeartsGameState tgs = (HeartsGameState) gs;
        double scoreFactor = (maxPossibleScore - tgs.getPlayerPoints(playerId) / maxPossibleScore);

        double highValueCardFactor = 0.0;
        int highValueCards = (int) tgs.getPlayerDecks().get(playerId).getComponents().stream()
                .filter(card -> (card).number > HIGH_VALUE_THRESHOLD)
                .count();
        if(highValueCards < maxHighValueCards) {
            highValueCardFactor = (maxHighValueCards - highValueCards) / maxHighValueCards * MAX_HIGH_VALUE_CARD_PASS_BONUS;
        }

        return scoreFactor + highValueCardFactor;
    }
}
