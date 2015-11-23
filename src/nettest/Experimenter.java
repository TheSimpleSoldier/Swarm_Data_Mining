package nettest;

import training.*;
import java.util.Arrays;

/**
 * 
 * @author davej
 */
public class Experimenter {
    
    // Clustering algorithm objects to used in experiments.
    private final Cluster[] clusters;
    private final double[][] dataset;
    
    // Number of iterations through the randomly arranged dataset
    private final int TEST_ITERATIONS;
    
    /**
     * Constructor passes an array of clustering algorithm objects to iterate 
     * through during this experiment. 
     * @param in_clusters   Array of clustering algorithm (Cluster) objects.
     * @param in_dataset    Dataset to test on.
     * @param iterations    number of test runs over the dataset
     */
    public Experimenter(Cluster[] in_clusters, double[][] in_dataset, int iterations) {
        clusters = in_clusters;
        dataset = in_dataset;
        TEST_ITERATIONS = iterations;
    }
    
    /**
     * Run every clustering algorithm on the dataset.
     * @param dataName name of the dataset.
     * @param debugging set to true if you want to print the testing printout
     */
    public void run(String dataName, boolean debugging) {
        
        Results[][] results = new Results[clusters.length][TEST_ITERATIONS];
        
        // Copy the dataset
        for (int i = 0; i < TEST_ITERATIONS;) {
            // Shuffle data
            int[] indices = new int[dataset.length];
            double[][] data = DataTools.shuffleData(dataset);
            double[][] distances = DataTools.distancesTo(data);
            
            int algorithmIndex = 0;
            
            for (Cluster cluster : clusters) {
                // Get the start time
                long startTime = System.currentTimeMillis();
                
                // Label every datapoint in the dataset
                int[] labels = cluster.run(data);
                
                // Get the end time
                long end = System.currentTimeMillis();
                
                // Add a Results object to the results array
                results[algorithmIndex][i] = new Results(data, labels, 
                        end - startTime, cluster.toString(), distances, dataName);
                
                // Increment i, increment algorithm index, print out status.
                i += 1;
                algorithmIndex += 1;
                System.out.println("Completed iteration " + i + " of " + TEST_ITERATIONS);
            }
        }
        
        System.out.println();
        
        if (debugging) {
            printTest(results);
        } else {
            
        }
    }
    
    /**
     * Calculate and print the results of the experiment.
     * @param results 
     */
    public void printTest(Results[][] results) {
        for (int i = 0; i < results.length; i++) {
            for (int j = 0; j < results[i].length; j++) {
                results[i][j].testingPrintout();
                System.out.println();
            }
        }
    }
}
