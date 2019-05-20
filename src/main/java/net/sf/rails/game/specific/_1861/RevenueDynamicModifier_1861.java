package net.sf.rails.game.specific._1861;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.rails.algorithms.NetworkTrain;
import net.sf.rails.algorithms.NetworkVertex;
import net.sf.rails.algorithms.RevenueAdapter;
import net.sf.rails.algorithms.RevenueDynamicModifier;
import net.sf.rails.algorithms.RevenueManager;
import net.sf.rails.algorithms.RevenueTrainRun;

public class RevenueDynamicModifier_1861 implements RevenueDynamicModifier {
	protected static Logger log =
	        LoggerFactory.getLogger(RevenueDynamicModifier_1861.class);
	private int maxValue;

	@Override
	public boolean prepareModifier(RevenueAdapter revenueAdapter) {
		log.debug("1861 Revenue Modifier activated");
		maxValue = maximumMajorValue(revenueAdapter.getVertices());
		return true;
	}

	@Override
	public int predictionValue(List<RevenueTrainRun> runs) {
		log.debug("predictionValue");
		for (RevenueTrainRun run : runs) {
			log.debug(run.prettyPrint(true));;
		}
		return maxValue;
	}

	@Override
	public int evaluationValue(List<RevenueTrainRun> runs, boolean optimalRuns) {
		log.debug("evaluationValue(" + optimalRuns + ")");
		int value = 0;
		
		for (RevenueTrainRun run : runs) {
			log.debug(run.prettyPrint(true));
			int runMajors = 0;
			int runMinors = 0;
			for (NetworkVertex vertex : run.getRunVertices()) {
				if (vertex.isMajor()) {
					++runMajors;
				}
				if (vertex.isMinor()) {
					++runMinors;
				}
			}
			log.debug("Run has " + runMajors + " cities and " + runMinors + " towns.");
			
			NetworkTrain train = run.getTrain();
			int trainLength = train.getRailsTrainType().getMajorStops();
			
			int minorsToSkip;
			if (runMajors >= trainLength) {
				/* Using only majors, skip all the minors */
				minorsToSkip = runMinors;
			} else {
				/* Count at least some minors */
				minorsToSkip = runMinors - (trainLength - runMajors);
			}
			if (minorsToSkip < 0) {
				minorsToSkip = 0;
			}
			log.debug("minorsToSkip is " + minorsToSkip);
			value -= (10 * minorsToSkip);
		}
		log.debug("Returning " + value);
		return value;
	}

	@Override
	public void adjustOptimalRun(List<RevenueTrainRun> optimalRuns) {
		log.debug("adjustOptimalRun");
		for (RevenueTrainRun run : optimalRuns) {
			log.debug(run.prettyPrint(true));
			
			NetworkTrain train = run.getTrain();
			int trainLength = train.getRailsTrainType().getMajorStops();
			int runMajors = 0;
			int runMinors = 0;
			for (NetworkVertex vertex : run.getRunVertices()) {
				if (vertex.isMajor()) {
					++runMajors;
				}
				if (vertex.isMinor()) {
					++runMinors;
				}
			}
			int minorsToTrim = runMajors + runMinors - trainLength;
			log.debug("Need to remove " + minorsToTrim + " minors");
			List<NetworkVertex> verticesToDelete = new ArrayList<>();
			List<NetworkVertex> runVertices = run.getRunVertices();
			for (NetworkVertex vertex : runVertices) {
				if (minorsToTrim <= 0) {
					break;
				}
				if (vertex.isMinor()) {
					verticesToDelete.add(vertex);
					--minorsToTrim;
				}
			}
			for (NetworkVertex vertex : verticesToDelete) {
				log.debug("Removing " + vertex.toString());
				runVertices.remove(vertex);
			}		
		}		
	}

	@Override
	public String prettyPrint(RevenueAdapter revenueAdapter) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private int maximumMajorValue(Collection<NetworkVertex> vertices) {
        int maximum = 0;
        for (NetworkVertex vertex:vertices) {
            if (!vertex.isMajor()) continue;
            maximum= Math.max(maximum, vertex.getValue());
        }
        return maximum;
    }

	
}
