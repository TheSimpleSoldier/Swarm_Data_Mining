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
        for (int i = 0; i < TEST_ITERATIONS; i++) {
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
                //i += 1;
                algorithmIndex += 1;
                System.out.println("Completed iteration " + i + " of " + TEST_ITERATIONS);
            }
        }
        
        System.out.println();
        
        if (debugging) {
            printTest(results);
            printResults(results);
        } else {
            printResults(results);
        }
    }
    
    /**
     * Calculate and print the results of the experiment, verbose.
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
    
    /**
     * Calculate and print the results of the experiment.
     * @param results 
     */
    public void printResults(Results[][] results) {
        
        // Arrays to store data for each algorithm averaged across iterations
        // Arrays without 'Err' at the end store the mean, & with 'Err' store the error
        
        // Percent of the dataset that made it into a cluster
        double[] percentClusteredAves = new double[results.length];
        double[] percentClusteredErrs = new double[results.length];
            
        // Average sizes of clusters
        double[] clusterSizeAves = new double[results.length];
        double[] clusterSizeErrs = new double[results.length];
            
        // Average numbers of clusters
        double[] clusterCountAves = new double[results.length];
        double[] clusterCountErrs = new double[results.length];
            
        // Average separation between clusters
        double[] separationAves = new double[results.length];
        double[] separationErrs = new double[results.length];
            
        // Average cohesion of clusters
        double[] cohesionAves = new double[results.length];
        double[] cohesionErrs = new double[results.length];
        
        for (int i = 0; i < results.length; i++) {
            // Metadata
            System.out.format("Algorithm: %s, iterations: %d, dataset: %s, size: %d, attributes: %d%n", 
                    results[0][0].algorithm(),
                    results[0].length,
                    results[0][0].datasetName(),
                    results[0][0].datasetSize(),
                    results[0][0].numberOfAttributes());
            
            double percentClustered = 0.0;
            double[] percentsClustered = new double[results[0].length];
            double percentClusteredErr = 0.0;
            
            double clusterSize = 0.0;
            double[] clusterSizes = new double[results[0].length];
            double clusterSizeErr = 0.0;
            
            double clusterCount = 0;
            double[] clusterCounts = new double[results[0].length];
            double clusterCountErr = 0;
            
            double separation = 0.0;
            double[] separations = new double[results[0].length];
            double separationErr = 0.0;
            
            double cohesion = 0.0;
            double[] cohesions = new double[results[0].length];
            double cohesionErr = 0.0;
            
            
            for (int j = 0; j < results[i].length; j++) {
                Results result = results[i][j];
                
                
                percentsClustered[j] = result.percentClustered();
                clusterSizes[j] = result.averageClusterSize();
                clusterCounts[j] = result.clusterCount();
                separations[j] = result.averageSeparation();
                cohesions[j] = result.averageCohesion();
                
                percentClustered += percentsClustered[j];
                clusterSize += clusterSizes[j];
                clusterCount += clusterCounts[j];
                separation += separations[j];
                cohesion += cohesions[j];
            }
            
            percentClustered /= results[i].length;
            clusterSize /= results[i].length;
            clusterCount /= results[i].length;
            separation /= results[i].length;
            cohesion /= results[i].length;
            
            for (int j = 0; j < results[i].length; j++) {
                
                percentClusteredErr += Math.abs(percentsClustered[j] - percentClustered)/percentClustered;
                clusterSizeErr += Math.abs(clusterSizes[j] - clusterSize)/clusterSize;
                clusterCountErr += Math.abs(clusterCounts[j] - clusterCount)/clusterCount;
                separationErr += Math.abs(separations[j] - separation)/separation;
                cohesionErr += Math.abs(cohesions[j] - cohesion)/cohesion;
            }
            
            percentClusteredErr /= results[i].length;
            clusterSizeErr /= results[i].length;
            clusterCountErr /= results[i].length;
            separationErr /= results[i].length;
            cohesionErr /= results[i].length;
            
            percentClusteredAves[i] = percentClustered;
            percentClusteredErrs[i] = 100.0 * percentClusteredErr;
            
            clusterSizeAves[i] = clusterSize;
            clusterSizeErrs[i] = 100.0 * clusterSizeErr;
            
            clusterCountAves[i] = clusterCount;
            clusterCountErrs[i] = 100.0 * clusterCountErr;
            
            separationAves[i] = separation;
            separationErrs[i] = 100.0 * separationErr;
            
            cohesionAves[i] = cohesion;
            cohesionErrs[i] = 100.0 * cohesionErr;
        }
        System.out.println("----------------------------------------Averages----------------------------------------");
        System.out.println("Algorithm     | Percent Clustered | Cluster Size | Cluster Count | Separation | Cohesion");
        for (int i = 0; i < results.length; i++) {
            System.out.format("%-14s| %-18.3f| %-13.3f| %-14.3f| %-11.3f| %.3f%n",
                    results[i][0].algorithm(),
                    percentClusteredAves[i],
                    clusterSizeAves[i],
                    clusterCountAves[i],
                    separationAves[i],
                    cohesionAves[i]);
        }
        
        System.out.println("-----------------------------------------Errors-----------------------------------------");
        System.out.println("Algorithm     | Percent Clustered | Cluster Size | Cluster Count | Separation | Cohesion");
        for (int i = 0; i < results.length; i++) {
        System.out.format("%-14s| %-18.3f| %-13.3f| %-14.3f| %-11.3f| %.3f%n",
                    results[i][0].algorithm(),
                    percentClusteredErrs[i],
                    clusterSizeErrs[i],
                    clusterCountErrs[i],
                    separationErrs[i],
                    cohesionErrs[i]);
        }
        System.out.println();
    }
}
