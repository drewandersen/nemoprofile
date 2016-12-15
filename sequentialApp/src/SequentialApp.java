import nemolib.*;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SequentialApp {

	public static void main (String[] args) {

		Timer timer = new Timer();
		timer.start();
		
		// parse CLIs (where should i put this? some kind of input class?)
		String filename = args[0];
		System.out.println("filename = " + args[0]);
		int motifSize = Integer.parseInt(args[1]);
		int randGraphCount = Integer.parseInt(args[2]);
		
		if (motifSize < 3) {
		    System.err.println("Motif size must be 3 or larger");
		    System.exit(-1);
		}
		
		// hard-code probs for now
		List<Double> probs = new LinkedList<>();
		for (int i = 0; i < motifSize - 2; i++)
		{
		    probs.add(1.0);
		}
		probs.add(1.0);
		probs.add(0.1);
		
		// parse input graph
		System.out.println("Parsing target graph...");
		Graph targetGraph = parseInputGraph(filename);
		
		// analyze target graph
		System.out.println("Analyzing target graph...");
		SubgraphProfile subgraphProfile = new SubgraphProfile();
		ESU.enumerate(targetGraph, subgraphProfile, motifSize);
		subgraphProfile.label();
		Map<String, Double> targetLabelRelFreqMap =
		        subgraphProfile.getRelativeFrequencies();
		
		// analyze random graphs
		System.out.println("Analyzing random graphs...");
		Map<String, List<Double>> randLabelRelFreqsMap =
		        RandomGraphAnalysis.analyze(targetGraph, randGraphCount, 
						motifSize, probs );
		
		System.out.println("Comparing target graph to random graphs...");
		// compare target graph to random graphs
		StatisticalAnalysis statisticalAnalysis =
		        new StatisticalAnalysis(targetLabelRelFreqMap,
		                randLabelRelFreqsMap, randGraphCount);
		
		// output the results
		System.out.println(statisticalAnalysis);
		System.out.println("Execution time: " + timer.getCurrentTime());
	}

	private static Graph parseInputGraph(String filename) {
		Graph targetGraph = null;
		try {
		    targetGraph = new Graph(filename);
		} catch (IOException e) {
		    System.out.println("Unable to parse data file");
		    System.exit(-1);
		}
		return targetGraph;
	}


}
