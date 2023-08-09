package games.Hearts.heuristics;

import core.AbstractGameState;
import games.Hearts.components.HeartsGameState;
import games.Hearts.components.HeartsHeuristic;

public class ScoreFactorHeuristic extends HeartsHeuristic {

    @Override
    public double evaluateState(AbstractGameState gs, int playerId) {
        HeartsGameState tgs = (HeartsGameState) gs;
        double scoreFactor = (maxPossibleScore - tgs.getPlayerPoints(playerId) / maxPossibleScore);
        return scoreFactor;
    }
}

