package rails.game.action;

import net.sf.rails.game.PublicCompany;

public class AuctionCompany extends PossibleAction {

	private static final long serialVersionUID = 1L;
	transient protected PublicCompany company;

	public AuctionCompany() {
		super(null);
	}
	
	public AuctionCompany(PublicCompany comp) {
		super(null);
		
		company = comp;
	}

	public PublicCompany getCompany() {
		return company;
	}

}
