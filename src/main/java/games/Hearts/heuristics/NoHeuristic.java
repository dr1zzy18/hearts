package games.Hearts.heuristics;

import core.AbstractGameState;
import games.Hearts.components.HeartsHeuristic;

public class NoHeuristic extends HeartsHeuristic {

    @Override
    public double evaluateState(AbstractGameState gs, int playerId) {
        return 0.0;
    }
}
