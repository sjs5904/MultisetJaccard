import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NearDuplicates {
	
	final int bands;
	final int bandwidth;
	MinHashSimilarities minSim;
	double simThreshold;
	LSH lsh;
	double relaxationFactor=20;

	/**
	 * 
	 * @param folder 			Name of the folder containing documents
	 * @param numPermutations	Number of Permutations to be used for MinHash
	 * @param simThreshold		Similarity threshold s, which is a double
	 * @throws IOException 
	 */
    public NearDuplicates(String folder, int numPermutations, double simThreshold) {

		this.simThreshold=simRelaxation(simThreshold);
//		System.out.println(simRelaxation(simThreshold));
    	bandwidth=BandSizeCalculator.bestFactorR(numPermutations, this.simThreshold);
		bands = numPermutations/bandwidth;
    	minSim = new MinHashSimilarities(folder, numPermutations);
		MinHash minhash = minSim.minHash;
		lsh = new LSH(minhash.minHashMatrix(), minhash.allDocs(), bands);
    }

    public double simRelaxation(double wantedSim){
		double propose= Math.exp(relaxationFactor*Math.log(wantedSim));
		return propose>1e-5? propose: 1e-5;
	}
    
    public ArrayList<String> nearDuplicateDetector(String fileName) {
    	List<String> nearDuplicates = lsh.nearDuplicatesOf(fileName);
    	ArrayList<String> sSimilar = new ArrayList<String>();
    	
		for(String str : nearDuplicates) {
			double similarity = minSim.exactJaccard(fileName, str);
			if(similarity > simThreshold) {
				sSimilar.add(str);
			} 
		}
		
		return sSimilar;
    }
    
    
}
