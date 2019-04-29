package net.sf.rails.game.specific._1861;

import net.sf.rails.game.GameManager;
import net.sf.rails.game.OperatingRound;
import net.sf.rails.game.RailsRoot;
import net.sf.rails.game.Round;
import net.sf.rails.game.financial.StockRound;
import net.sf.rails.game.state.IntegerState;

public class GameManager_1861 extends GameManager {
	protected Class<? extends Round> mergerRoundClass = MergerRound_1861.class;
    protected final IntegerState mrNumber = IntegerState.create(this, "mrNumber");


	public GameManager_1861(RailsRoot parent, String id) {
		super(parent, id);
		// TODO Auto-generated constructor stub
	}
	
	protected void startMergerRound() {
		MergerRound_1861 mr = (MergerRound_1861) createRound(mergerRoundClass, "MR_" + mrNumber.value());
        mrNumber.add(1);
        mr.start();
    }

}
