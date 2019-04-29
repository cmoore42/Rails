package net.sf.rails.game.specific._1861;

import net.sf.rails.game.GameManager;
import net.sf.rails.game.Player;
import net.sf.rails.game.StartRound;
import net.sf.rails.game.state.ArrayListState;
import rails.game.action.BidStartItem;
import rails.game.action.NullAction;

/*
 * Opening round begins with auctioning the private companies, in order
 * Each private company has a minimum bid, and bids must go up by 5.
 * 
 * If all players pass and some privates have not yet been sold, the 
 * minimum bid for the next private to be sold goes down by 5.
 * 
 * If the price for a private hits 0 the last player who started a
 * full round of passes gets it free.
 * 
 * After the Warsaw-Vienna is auctioned the first minor company (N)
 * is put up for auction.  If N doesn't get sold play 2 operating rounds,
 * then try N again.
 * 
 * Once N has sold, switch to a normal stock round where players can choose
 * to auction minor companies.
 */

public class StartRound_1861 extends StartRound {
	private final ArrayListState<Player> passedPlayers = 
            ArrayListState.create(this, "passedPlayers");

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
		Player player = playerManager.getPlayerByName(playerName);
		
		passedPlayers.add(player);
        
        if (passedPlayers.size() == playerManager.getNumberOfPlayers()) {
        	// All players have passed
        	/* if any private left 
        	 *   if minBid is 0
        	 *      give it to the player
        	 *   else
        	 *     reduce price of next private
        	 *     clear passed players
        	 *     resume bidding
        	 */
        	passedPlayers.clear();
        }
        
		return false;
	}

}
