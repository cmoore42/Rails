package net.sf.rails.game.specific._1861;

import net.sf.rails.game.GameDef;
import net.sf.rails.game.GameManager;
import net.sf.rails.game.OperatingRound;

public class OperatingRound_1861 extends OperatingRound {

	public OperatingRound_1861(GameManager parent, String id) {
		super(parent, id);
		
		steps = new GameDef.OrStep[] {
                GameDef.OrStep.INITIAL,
                GameDef.OrStep.TRADE_SHARES,
                GameDef.OrStep.LAY_TRACK,
                GameDef.OrStep.LAY_TOKEN,
                GameDef.OrStep.CALC_REVENUE,
                GameDef.OrStep.PAYOUT,
                GameDef.OrStep.REPAY_LOANS,
                GameDef.OrStep.BUY_TRAIN,
                GameDef.OrStep.FINAL
        };
	}
	
	@Override
    protected boolean gameSpecificNextStep (GameDef.OrStep step) {

        if (step == GameDef.OrStep.REPAY_LOANS) {

            // Has company any outstanding loans to repay?
            if (operatingCompany.value().getMaxNumberOfLoans() == 0
                || operatingCompany.value().getCurrentNumberOfLoans() == 0) {
                return false;
            // Is company required to repay loans?
            } else if (operatingCompany.value().sharesOwnedByPlayers()
                    < operatingCompany.value().getCurrentNumberOfLoans()) {
                return true;
            // Has company enough money to repay at least one loan?
            } else if (operatingCompany.value().getCash()
                    < operatingCompany.value().getValuePerLoan()) {
                return false;
            } else {
                // Loan repayment is possible but optional
                return true;
            }
        }

        return true;
    }

}
