package games.TG.components;

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
public class TGParameters extends TunableParameters {
    public String dataPath = "data/FrenchCards/";

    public int jackCard = 10;
    public int queenCard = 10;
    public int kingCard = 10;
    public int aceCardBelowThreshold = 1;
    public int aceCardAboveThreshold = 11;
    public int pointThreshold = 10;
    public int winScore = 21;
    public int dealerStand = 17;
    public int nDealerCardsHidden = 1;

    public TGParameters(long seed) {

        super(seed);

        addTunableParameter("jackCard", 10, Arrays.asList(5, 10, 15, 20));
        addTunableParameter("queenCard", 10, Arrays.asList(5, 10, 15, 20));
        addTunableParameter("kingCard", 10, Arrays.asList(5, 10, 15, 20));
        addTunableParameter("aceCardBelowThreshold", 1, Arrays.asList(1, 2, 3, 4));
        addTunableParameter("aceCardAboveThreshold", 11, Arrays.asList(10, 13, 15, 17, 20));
        addTunableParameter("pointThreshold", 10, Arrays.asList(7, 10, 15));
        addTunableParameter("winScore", 21, Arrays.asList(15, 21, 30, 50));
        addTunableParameter("dealerStand", 17, Arrays.asList(5, 7, 10, 13, 15, 17, 20));
        addTunableParameter("nDealerCardsHidden", 1, Arrays.asList(0,1,2,3,4,5));
        _reset();
    }

    @Override
    public void _reset() {
        jackCard = (int) getParameterValue("jackCard");
        queenCard = (int) getParameterValue("queenCard");
        kingCard = (int) getParameterValue("kingCard");
        aceCardBelowThreshold = (int) getParameterValue("aceCardBelowThreshold");
        aceCardAboveThreshold = (int) getParameterValue("aceCardAboveThreshold");
        pointThreshold = (int) getParameterValue("pointThreshold");
        winScore = (int) getParameterValue("winScore");
        dealerStand = (int) getParameterValue("dealerStand");
        nDealerCardsHidden = (int) getParameterValue("nDealerCardsHidden");
    }

    public String getDataPath(){
        return dataPath;
    }

    @Override
    protected AbstractParameters _copy() {
        TGParameters hgp = new TGParameters(System.currentTimeMillis());
        hgp.dataPath = dataPath;
        hgp.jackCard = jackCard;
        hgp.queenCard = queenCard;
        hgp.kingCard = kingCard;
        hgp.aceCardBelowThreshold = aceCardBelowThreshold;
        hgp.aceCardAboveThreshold = aceCardAboveThreshold;
        hgp.pointThreshold = pointThreshold;
        hgp.winScore = winScore;
        hgp.dealerStand = dealerStand;
        hgp.nDealerCardsHidden = nDealerCardsHidden;
        return hgp;
    }

    @Override
    public boolean _equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TGParameters)) return false;
        if (!super.equals(o)) return false;
        TGParameters that = (TGParameters) o;
        return jackCard == that.jackCard && queenCard == that.queenCard && kingCard == that.kingCard && aceCardBelowThreshold == that.aceCardBelowThreshold && aceCardAboveThreshold == that.aceCardAboveThreshold && pointThreshold == that.pointThreshold && winScore == that.winScore && dealerStand == that.dealerStand && nDealerCardsHidden == that.nDealerCardsHidden && Objects.equals(dataPath, that.dataPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), dataPath, jackCard, queenCard, kingCard, aceCardBelowThreshold, aceCardAboveThreshold, pointThreshold, winScore, dealerStand, nDealerCardsHidden);
    }

    @Override
    public Game instantiate() {
        System.out.println("New game is being instantiated");  // add this line
        return new Game(GameType.TG, new TGForwardModel(), new TGGameState(this, GameType.TG.getMinPlayers()));
    }
}
