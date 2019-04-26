package net.sf.rails.game.specific._1861;

import net.sf.rails.game.GameManager;
import net.sf.rails.game.StartRound;
import rails.game.action.BidStartItem;
import rails.game.action.NullAction;

public class StartRound_1861 extends StartRound {

	protected StartRound_1861(GameManager parent, String id) {
		// hasBidding true, hasBasePrices true, hasBuying false
		super(parent, id, true, true, false);
	}

	@Override
	protected boolean bid(String playerName, BidStartItem startItem) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean pass(NullAction action, String playerName) {
		// TODO Auto-generated method stub
		return false;
	}

}
