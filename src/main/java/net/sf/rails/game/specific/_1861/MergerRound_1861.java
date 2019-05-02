package net.sf.rails.game.specific._1861;

import net.sf.rails.common.LocalText;
import net.sf.rails.common.ReportBuffer;
import net.sf.rails.game.GameManager;
import net.sf.rails.game.Round;

public class MergerRound_1861 extends Round {

	public MergerRound_1861(GameManager parent, String id) {
		super(parent, id);
	}

	public void start() {


        ReportBuffer.add(this, LocalText.getText("1861StartMR", getId()));
        
        /* This needs to do something.  For now just call the round done */
        finishRound();
		
	}

}
