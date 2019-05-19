package net.sf.rails.game.specific._1861;

import net.sf.rails.game.PublicCompany;
import net.sf.rails.game.RailsRoot;
import net.sf.rails.game.financial.StockMarket;
import net.sf.rails.game.financial.StockSpace;

public class StockMarket_1861 extends StockMarket {

	public StockMarket_1861(RailsRoot parent, String id) {
		super(parent, id);
	}
	
	/*
	 * For 1861 minor companies can't move right of the yellow squares
	 * in the market.
	 */
	@Override
	protected void moveRightOrUp(PublicCompany company) {
		StockSpace oldsquare;
		StockSpace newsquare;
		
		if (company.getType().getId().equalsIgnoreCase("Minor")) {
			oldsquare = company.getCurrentSpace();
			int row = oldsquare.getRow();
	        int col = oldsquare.getColumn();
			if ((oldsquare.getType().getName().equalsIgnoreCase("yellow")) ||
				(oldsquare.getType().getName().equalsIgnoreCase("redyellow"))) {
				if (row > 0) {
					/* Move up */
					newsquare = getStockSpace(row-1, col);
				} else {
					/* Already at the top, can't move */
					newsquare = oldsquare;
				}
				prepareMove(company, oldsquare, newsquare);
			} else {
				/* Not on a yellow square, handle normally */
				super.moveRightOrUp(company);
			}
		} else {
			/* Not a minor company, handle normally */
			super.moveRightOrUp(company);
		}
    }

}
