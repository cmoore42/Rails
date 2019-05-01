package net.sf.rails.game.specific._1861;

import java.util.List;

import com.google.common.collect.ImmutableSortedSet;

import net.sf.rails.common.LocalText;
import net.sf.rails.common.ReportBuffer;
import net.sf.rails.game.GameManager;
import net.sf.rails.game.Phase;
import net.sf.rails.game.PublicCompany;
import net.sf.rails.game.financial.PublicCertificate;
import net.sf.rails.game.financial.StockRound;
import net.sf.rails.game.financial.StockSpace;
import net.sf.rails.game.model.PortfolioModel;
import net.sf.rails.game.state.Currency;
import net.sf.rails.game.state.MoneyOwner;
import rails.game.action.AuctionCompany;
import rails.game.action.PossibleAction;


/**
 * The 1861 Stock Round has the following features:
 * - A player may:
 *   - Sell any number of certificates (in Public Companies), then
 *   - Buy one share certificate (in a Public Company) OR
 *   - Auction a Minor Company (Prior to Phase 5)
 *   
 *   Starting in Phase 4, the "Buy One Share" can buy the Director's Share
 *   of a Public Company that hasn't started yet.
 *   
 *   Auction a minor:
 *   In Phase 2, 10 minors are available to be auctioned.
 *   In Pases 3 and 4, 6 additional minors are available.
 *   Minimal initial bid is R100
 *   Bid increment is R5
 *   The amount paid is for the Director's share, so for 20% ownership
 *   Stock price set at price paid / 2
 *   
 *   Note:  The first minor (N) MUST be auctioned first.  After N is 
 *   auctioned a player can choose any other minor to auction.
 */
public class StockRound_1861 extends StockRound {

	public StockRound_1861(GameManager parent, String id) {
		super(parent, id);
	}
	
	@Override
	protected void setGameSpecificActions() {
		PublicCompany firstMinor = getRoot().getCompanyManager().getPublicCompany("N");
		
		/* If N hasn't been sold yet then it has to be sold first */
		if (!firstMinor.hasStarted()) {
			possibleActions.add(new AuctionCompany(firstMinor));;
			return;
		}
		
		/* If N has been sold then the other minors can be auctioned */	
		List<PublicCompany> allCompanies = getRoot().getCompanyManager().getAllPublicCompanies();
		for (PublicCompany c : allCompanies) {
			if ((c instanceof MinorCompany_1861) && (!c.hasStarted())) {
				possibleActions.add(new AuctionCompany(c));
			}
		}
    }
	
	@Override
	protected boolean processGameSpecificAction(PossibleAction action) {
		if (action instanceof AuctionCompany) {
			/* This needs to implement the auction of a minor company. 
			 * For testing purposes we'll just sell the company to the 
			 * current player for R150
			 */
			AuctionCompany auctionAction = (AuctionCompany) action;
			PublicCompany company = auctionAction.getCompany();
			PortfolioModel ipo = bank.getIpo().getPortfolioModel();
			PublicCertificate cert = ipo.findCertificate(company,  false);
			
			if (cert == null) {
	            log.error("Cannot find cert");
	        } else {
	        	cert.moveTo(currentPlayer);
	        }
			
			int cost=150;
			MoneyOwner priceRecipient = company;
			PortfolioModel from = ipo;
			
			/* Determine start space */
			int startStockPrice = cost / 2;
			if (startStockPrice > 135) {
				startStockPrice = 135;
			}

			ImmutableSortedSet<StockSpace> stockSpaces = getRoot().getStockMarket().getStartSpaces();
			StockSpace startSpace = null;
			StockSpace previous = null;
			for (StockSpace s : stockSpaces) {
				if (s.getPrice() == startStockPrice) {
					startSpace = s;
					break;
				}
				if (s.getPrice() > startStockPrice) {
					startSpace = previous;
					break;
				}
				previous = s;
			}
			
			company.start(startSpace);
			company.setFloated();
			
			String costText = Currency.wire(currentPlayer, cost, priceRecipient);
	        if (priceRecipient != from.getMoneyOwner()) {
	            ReportBuffer.add(this, LocalText.getText("PriceIsPaidTo",
	                    costText,
	                    priceRecipient.getId() ));
	        }

	        companyBoughtThisTurnWrapper.set(company);
	        hasActed.set(true);
	        setPriority();
			
			return true;
		}

        return false;
    }
}
