package net.sf.rails.game.specific._1861;

import net.sf.rails.common.LocalText;
import net.sf.rails.common.ReportBuffer;
import net.sf.rails.game.GameManager;
import net.sf.rails.game.Round;
import net.sf.rails.game.state.IntegerState;

public class MergerRound_1861 extends Round {
	protected final IntegerState currentMRNumber = IntegerState.create(this, "MRNUmber");

	protected MergerRound_1861(GameManager parent, String id) {
		super(parent, id);
		currentMRNumber.set(0);
	}

	public void start() {

		currentMRNumber.add(1);
        ReportBuffer.add(this, LocalText.getText("1861StartMR", currentMRNumber));
		
	}

}
