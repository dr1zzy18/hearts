package games.Hearts.heuristics;

import core.AbstractGameState;
import games.Hearts.components.HeartsGameState;
import games.Hearts.components.HeartsHeuristic;

public class ScoreAndTricksHeuristic extends HeartsHeuristic {
    @Override
    public double evaluateState(AbstractGameState gs, int playerId) {
        HeartsGameState tgs = (HeartsGameState) gs;
        double scoreFactor = (maxPossibleScore - tgs.getPlayerPoints(playerId) / maxPossibleScore);

        double tricksTaken = tgs.playerTricksTaken[tgs.getCurrentPlayer()];
        double tricksFactor = (maxPossibleTricks - tricksTaken) / maxPossibleTricks;

        return scoreFactor + tricksFactor;
    }
}

