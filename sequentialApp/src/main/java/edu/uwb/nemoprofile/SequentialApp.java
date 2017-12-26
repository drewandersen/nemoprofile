import edu.uwb.nemolib.*;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SequentialApp {

	private static final double P_THRESH = 0.1;
	private static final int RAND_GRAPH_COUNT = 1000;

	public static void main (String[] args) {

		String filename = args[0];
		System.out.println("filename = " + args[0]);
		int motifSize = Integer.parseInt(args[1]);
		int randGraphCount = Integer.parseInt(args[2]);
		
		if (motifSize < 3) {
		    System.err.println("Motif size must be 3 or larger");
		    System.exit(-1);
		}
		
		// parse input graph
		System.out.println("Parsing target graph...");
		Graph targetGraph = null;
		try {
			targetGraph = GraphParser.parse(filename);
		} catch (IOException e) {
			System.err.println("Could not process " + filename);
			System.err.println(e);
			System.exit(-1);
		}
		
		// analyze target graph
		System.out.println("Analyzing target graph...");
		SubgraphProfile subgraphProfile = new SubgraphProfile();
		SubgraphEnumerator esu = new ESU();
		esu.enumerate(targetGraph, motifSize, subgraphProfile);
		subgraphProfile.label();
		Map<String, Double> targetLabelRelFreqMap =
		        subgraphProfile.getRelativeFrequencies();

		// hard-code probs for now
		List<Double> probs = new LinkedList<>();
		for (int i = 0; i < motifSize - 2; i++)
		{
		    probs.add(1.0);
		}
		probs.add(1.0);
		probs.add(0.1);
		
		// analyze random graphs
		System.out.println("Analyzing random graphs...");
		RandomGraphAnalyzer rga = new RandomGraphAnalyzer(new RandESU(probs), RAND_GRAPH_COUNT);
		Map<String, List<Double>> randLabelRelFreqsMap =
		        rga.analyze(targetGraph, motifSize);
		
		System.out.println("Comparing target graph to random graphs...");
		// compare target graph to random graphs
		System.out.println("Constructing NemoProfile...");
		RelativeFrequencyAnalyzer rfa = new RelativeFrequencyAnalyzer(randLabelRelFreqsMap, targetLabelRelFreqMap);

		SubgraphProfile np = NemoProfileBuilder.build(subgraphProfile, rfa, P_THRESH);

		// output the results
		System.out.println(rfa);
		System.out.println(np);
		//System.out.println("Execution time: " + timer.getCurrentTime() + 
		//					 " milliseconds");
	}
}
