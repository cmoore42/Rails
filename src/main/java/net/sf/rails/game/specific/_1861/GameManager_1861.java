package net.sf.rails.game.specific._1861;

import net.sf.rails.game.GameManager;
import net.sf.rails.game.OperatingRound;
import net.sf.rails.game.RailsRoot;
import net.sf.rails.game.Round;
import net.sf.rails.game.financial.StockRound;
import net.sf.rails.game.state.IntegerState;

public class GameManager_1861 extends GameManager {
	protected Class<? extends Round> mergerRoundClass = MergerRound_1861.class;
    protected final IntegerState mrMajorNumber = IntegerState.create(this, "mrMajorNumber", 0);
    protected final IntegerState mrMinorNumber = IntegerState.create(this,  "mrMinorNumber", 2);


	public GameManager_1861(RailsRoot parent, String id) {
		super(parent, id);
	}
	
	protected void startMergerRound() {
		if (mrMinorNumber.value() == 2) {
			mrMajorNumber.add(1);
			mrMinorNumber.set(1);
		} else {
			mrMinorNumber.add(1);
		}
		MergerRound_1861 mr = (MergerRound_1861) createRound(mergerRoundClass, 
				"MR_" + mrMajorNumber.value() + "." + mrMinorNumber.value());

        mr.start();
    }
	
	@Override
	public void nextRound(Round round) {
		if (round instanceof OperatingRound) {
            if (gameOverPending.value() && !gameEndsAfterSetOfORs) {
                finishGame();
            } else if (getRoot().getCompanyManager().getNextUnfinishedStartPacket() !=null) {
               beginStartRound();
            } else {
                if (gameOverPending.value() && gameEndsAfterSetOfORs) {
                    finishGame();
                } else if (getRoot().getPhaseManager().getCurrentPhasendex() >= 3){
                    ((OperatingRound)round).checkForeignSales();
                    startMergerRound();
                }
            }
		} else if (round instanceof MergerRound_1861) {
			if (mrMinorNumber.value() == 2) {
				startStockRound();
			} else {
				relativeORNumber.add(1);
				startOperatingRound(true);
			}
		} else {
			super.nextRound(round);
		}
	}

}
