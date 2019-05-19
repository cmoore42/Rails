package net.sf.rails.game.specific._1861;

import java.util.List;

import com.google.common.collect.ImmutableSortedSet;

import net.sf.rails.common.LocalText;
import net.sf.rails.common.ReportBuffer;
import net.sf.rails.game.GameManager;
import net.sf.rails.game.Phase;
import net.sf.rails.game.Player;
import net.sf.rails.game.PublicCompany;
import net.sf.rails.game.financial.PublicCertificate;
import net.sf.rails.game.financial.StockRound;
import net.sf.rails.game.financial.StockSpace;
import net.sf.rails.game.model.PortfolioModel;
import net.sf.rails.game.state.Currency;
import net.sf.rails.game.state.MoneyOwner;
import rails.game.action.AuctionCompany;
import rails.game.action.BuyCertificate;
import rails.game.action.PossibleAction;
import rails.game.action.StartCompany;


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
 *   In Phases 3 and 4, 6 additional minors are available.
 *   Minimal initial bid is R100
 *   Bid increment is R5
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
	public void start() {
		super.start();
		raiseIfSoldOut = false;
	}
	
    @Override
    public void setBuyableCerts() {

        super.setBuyableCerts();
        
        /*
        PortfolioModel ipo = bank.getIpo().getPortfolioModel();
        PublicCompany firstMinor = getRoot().getCompanyManager().getPublicCompany("N");
        
        // TODO:  Set the price
        int price = 150;
        
        if (!firstMinor.hasStarted()) {
        	possibleActions.add(new BuyCertificate(firstMinor,
                    1,
                    ipo.getParent(), price, 1));
        }
        */
		
    }
    
    /*
     * The game-wide limit on share ownership doesn't apply to minor
     * companies.  A Player can own all or nothing in a minor company.
     * So as long as the minor hasn't started yet, a player can buy
     * the single 100% share.
     */
    /*
    @Override
    public int maxAllowedNumberOfSharesToBuy(Player player,
            PublicCompany company,
            int shareSize) {
    	
    	if (company.getType().getId().equalsIgnoreCase("Minor")) {
    		if (!company.hasStarted()) {
    			return 1;
    		}
    	}
    	
    	return super.maxAllowedNumberOfSharesToBuy(player, company, shareSize);
    }
    */

	@Override
	protected void setGameSpecificActions() {
		PublicCompany firstMinor = getRoot().getCompanyManager().getPublicCompany("N");
		int price = 150;
		int prices[] = {100, 105, 110, 115, 120, 125, 130, 135, 140, 145, 150, 155, 160,
				165, 170, 175, 180, 185, 190, 195, 200, 205, 210, 215, 220, 225, 230, 235,
				240, 245, 250, 255, 260, 265, 270};


		/* If N hasn't started yet that's the only one that can start */
		if (!firstMinor.hasStarted()) {
			possibleActions.add(new StartCompany(firstMinor, prices));
			return;
		}
		
		for (PublicCompany company : companyManager.getAllPublicCompanies()) {
            if (company.getType().getId().equalsIgnoreCase("Minor")) {
            	if (!company.hasStarted()) {
            		possibleActions.add(new StartCompany(company, prices));
            	}
            }
		}
		
		/*
		List<PublicCompany> allCompanies = getRoot().getCompanyManager().getAllPublicCompanies();
		for (PublicCompany c : allCompanies) {
			if ((c instanceof MinorCompany_1861) && (!c.hasStarted())) {
				possibleActions.add(new AuctionCompany(c));
			}
		}
		*/
    }

	@Override
	public boolean startCompany(String playerName, StartCompany action) {
		PublicCompany company = action.getCompany();
		int pricePaid = action.getPrice();
		
		int startStockPrice = pricePaid / 2;
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
		
		/* Start and float the company */
		company.start(startSpace);
		company.setFloated();
		
		/* Transfer money from the purchaser to the company */
		String costText = Currency.wire(action.getPlayer(), pricePaid, company);
		
		/* Transfer the certificate to the player */
		PublicCertificate cert = ipo.findCertificate(company, true);
		cert.moveTo(currentPlayer);

		ReportBuffer.add(this, LocalText.getText("PriceIsPaidTo",
				costText,
				company.getId() ));
		ReportBuffer.add(this, LocalText.getText("START_COMPANY_LOG",
                playerName,
                company.getId(),
                bank.getCurrency().format(pricePaid), // TODO: Do this nicer
                costText,
                1,
                cert.getShare(),
                company.getId() ));

        companyBoughtThisTurnWrapper.set(company);
        hasActed.set(true);
        setPriority();
		
		return true;
		
	}

	
	protected boolean oldProcessGameSpecificAction(PossibleAction action) {
		if (action instanceof AuctionCompany) {
			AuctionCompany auctionAction = (AuctionCompany) action;
			PublicCompany company = auctionAction.getCompany();
			PortfolioModel ipo = bank.getIpo().getPortfolioModel();
			PublicCertificate cert = ipo.findCertificate(company,  false);
					
			if (cert == null) {
	            log.error("Cannot find cert");
	            return(false);
	        } 
			
			/* This needs to implement the auction of a minor company. 
			 * For testing purposes we'll just sell the company to the 
			 * current player for R150
			 */
			Player purchaser = currentPlayer;
			int purchasePrice = 150;
			
			cert.moveTo(purchaser);
			
			/* 
			 * Determine starting stock price
			 * Stock price must be in the range of 50 to 135, and
			 * must be a valid start space.
			 * 
			 * Stock price is the purchase price / 2 rounded down
			 * to the nearest start space.
			 */
			int startStockPrice = purchasePrice / 2;
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
			
			/* Start and float the company */
			company.start(startSpace);
			company.setFloated();
			
			/* Transfer money from the purchaser to the company */
			String costText = Currency.wire(purchaser, purchasePrice, company);

			ReportBuffer.add(this, LocalText.getText("PriceIsPaidTo",
					costText,
					company.getId() ));

	        companyBoughtThisTurnWrapper.set(company);
	        hasActed.set(true);
	        setPriority();
			
			return true;
		}

        return false;
    }
}
