package games.Hearts.components;

import core.AbstractGameState;
import core.AbstractParameters;
import core.Game;
import evaluation.TunableParameters;
import games.GameType;

import java.util.Arrays;
import java.util.Objects;

/**
 * <p>This class should hold a series of variables representing game parameters (e.g. number of cards dealt to players,
 * maximum number of rounds in the game etc.). These parameters should be used everywhere in the code instead of
 * local variables or hard-coded numbers, by accessing these parameters from the game state via {@link AbstractGameState#getGameParameters()}.</p>
 *
 * <p>It should then implement appropriate {@link #_copy()}, {@link #_equals(Object)} and {@link #hashCode()} functions.</p>
 *
 * <p>The class can optionally extend from {@link evaluation.TunableParameters} instead, which allows to use
 * automatic game parameter optimisation tools in the framework.</p>
 */
public class HeartsParameters extends TunableParameters {
    public String dataPath = "data/FrenchCards/";



    public HeartsParameters(long seed) {

        super(seed);

        _reset();
    }

    @Override
    public void _reset() {

    }

    public String getDataPath(){
        return dataPath;
    }

    @Override
    protected AbstractParameters _copy() {
        HeartsParameters hgp = new HeartsParameters(System.currentTimeMillis());
        hgp.dataPath = dataPath;
        return hgp;
    }

    @Override
    public boolean _equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HeartsParameters)) return false;
        if (!super.equals(o)) return false;
        HeartsParameters that = (HeartsParameters) o;
        return Objects.equals(dataPath, that.dataPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), dataPath);
    }

    @Override
    public Game instantiate() {
        System.out.println("New game is being instantiated");  // add this line
        return new Game(GameType.Hearts, new HeartsForwardModel(), new HeartsGameState(this, GameType.Hearts.getMinPlayers()));
    }
}
