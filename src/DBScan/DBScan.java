package DBScan;

import java.util.ArrayList;
import java.util.HashMap;
import nettest.DataTools;
import training.Cluster;

/**
 * 
 * @author David Bell
 */
public class DBScan implements Cluster {
    
    private double deviationsFromMean;
    private double regionSize;
    private int minPoints;
    private boolean verbose;
    private double[][] distances;
    
    /**
     * Initialize all parameters based on dataset.
     */
    public DBScan() {
        minPoints = -1;
        regionSize = -1;
        deviationsFromMean = 2;
        distances = null;
    }
    
    /**
     * Use the deviations to find parameters.
     * @param deviations 
     */
    public DBScan(double deviations) {
        minPoints = -1;
        regionSize = -1;
        deviationsFromMean = deviations;
        distances = null;
    }
    
    /**
     * Set only minPoints.
     * @param minPoints
     */
    public DBScan(int minPoints) {
        this.minPoints = minPoints;
        regionSize = -1;
        deviationsFromMean = 2;
        distances = null;
    }
    
    /**
     * Use the deviations to find a good regionSize, while setting minPoints.
     * @param deviations
     * @param minPoints 
     */
    public DBScan(int minPoints, double deviations) {
        this.minPoints = minPoints;
        regionSize = -1;
        deviationsFromMean = deviations;
        distances = null;
    }
    
    /**
     * Use this constructor once you have found good parameters for regionSize
     * (epsilon) and minPoints.
     * @param regionSize
     * @param minPoints 
     */
    public DBScan(double regionSize, int minPoints) {
        this.minPoints = minPoints;
        this.regionSize = regionSize;
        distances = null;
        deviationsFromMean = 0;
    }
    
    public DBScan(String dataset, boolean verbosity) {
        verbose = verbosity;
        distances = null;
        regionSize = -1;
        deviationsFromMean = 2;
        minPoints = -1;
        
        switch(dataset) {
            case "data/movement_libras.csv":
                minPoints = 8;
                deviationsFromMean = 2.15;
                break;
            case "data/turkiye-student-evaluation_generic.csv":
                minPoints = 8;
                deviationsFromMean = .7;
                break;
            case "data/airfoil.data":
                minPoints = 11;
                deviationsFromMean = 4.14;
                break;
            case "data/seeds.data":
                minPoints = 8;
                deviationsFromMean = 2.15;
                break;
            case "data/synthetic_control.data":
                minPoints = 20;
                break;
            case "data/dow_jones.data":
                // Defaults work well
                break;
            case "data/SPECTF.csv":// Consider replacing specf dataset
                minPoints = 10;
                deviationsFromMean = -.1;
                break;
            case "data/gesture.csv":
                minPoints = 25;
                deviationsFromMean = 5.85;
                break;
            case "data/wholesale.csv":
                // Defaults work well
                break;
            default: // data/bupa.csv
                minPoints = 7;
                deviationsFromMean = -0.17;
                break;
        }
    }
    
    
    @Override
    public int[] run(double[][] dataset) {
        distances = DataTools.distancesTo(dataset);
        // Set regionSize if regionSize not known
        if (regionSize < 0) {
            regionSize = setEpsilon(dataset);
        }
        // Set minimum points for core labels as the number of attributes if not set
        if (minPoints < 0) {
            minPoints = Math.min(dataset.length/5, dataset[0].length + (int) deviationsFromMean);
        }
        
        if (verbose) {
            System.out.println("----------------Begin DB-Scan----------------");
            System.out.format("Epsilon/max distance of neighbors: %f%nMinimum vectors in a neighborhood: %d%n%n", regionSize,minPoints);
        }
        
        // Get the neighbors of every node
        Integer[][] neighbors = getNeighbors(dataset);
        // Initialize labels array (0 = noise, 1 = core, -1 = border)
        int[] labels = new int[neighbors.length];
        int currentCluster = 0;
        int noiseVectors;
        visit_neighbors:
        // Visit every vector
        for (int i = 0; i < neighbors.length; i++) {
            // Get the size of vector i's neighborhood
            int neighborhoodSize = neighbors[i].length;
            noiseVectors = 0;
            // Find a core vector that has not been visited
            while(labels[i] != 0 || neighborhoodSize < minPoints) {
                i += 1;
                if (i >= neighbors.length) {
                    break visit_neighbors;
                }
                neighborhoodSize = neighbors[i].length;
                noiseVectors++;
            }
            
            // Increment cluster label and mark current vector as its first member
            currentCluster += 1;
            labels[i] = currentCluster;
            
            if (verbose) {
                System.out.println("Found " + noiseVectors + " noise vectors.");
                System.out.println();
                System.out.println("Creating cluster " + currentCluster);
                System.out.format("    Initial vector %d has %d neighbors.%n",
                                i,
                                neighbors[i].length);
            }
            
            int mergedPts = 0;
            
            // Go through all of the vectors in the current neighborhood
            for (int j = 0; j < neighborhoodSize; j++) {
                
                // Get the index of neighbor j
                int Jth_neighbor = neighbors[i][j];
                
                // Get the label of the Jth neighbor's cluster
                int neighborCluster = labels[Jth_neighbor];
                
                // If Jth neighbor is not in a cluster, add it to this cluster
                if (neighborCluster != currentCluster) {
                    labels[Jth_neighbor] = currentCluster;
                    // if Jth neighbor is a core point, add its neighborhood to this cluster
                    if (neighbors[Jth_neighbor].length >= minPoints) {
                        mergedPts += mergeClusters(labels,neighbors,Jth_neighbor,currentCluster);
                    }
                }
            }
            
            if (verbose) {
                System.out.println("    Merged " + mergedPts + " neighborhoods of core vectors into cluster " + currentCluster + ".");
                System.out.println();
            }
        }
        
        // Merge clusters sharing core points in their neighborhoods
        ArrayList<Integer> keys = new ArrayList<>();
        for (int i = 0; i < labels.length; i++) {
            int cluster = labels[i];
            if (cluster == 0) {
                labels[i] = -1;
            } else if (!keys.contains(cluster)) {
                keys.add(cluster);
                int index = keys.indexOf(cluster);
                if (verbose) {
                    System.out.println("Applying new label " + index + " to cluster " + cluster);
                }
            }
        }
        for (int i = 0; i < labels.length; i++) {
            if (labels[i] > 0) {
                labels[i] = keys.indexOf(labels[i]);
            }
        }
        
        if (verbose) {
            System.out.println();
            System.out.println("-----------------End DB-Scan-----------------");
            System.out.println();
            System.out.println();
        }
        
        return labels;
    }
    
    public int mergeClusters(int[] labels, Integer[][] neighbors, int corePoint, int cluster) {
        int merged = 1;
//        if (labels[corePoint] != cluster) {
            for (int k = 0; k < neighbors[corePoint].length; k++) {
                int neighbor_of_corePoint = neighbors[corePoint][k];
                int neighborLabel = labels[neighbor_of_corePoint];
                labels[neighbor_of_corePoint] = cluster;
                if (neighborLabel != cluster && neighbors[neighbor_of_corePoint].length >= minPoints) {
                    merged += mergeClusters(labels,neighbors,neighbor_of_corePoint,cluster) + 1;
                }
            }
//        }
        return merged;
    }
    
    public Integer[][] getNeighbors(double[][] dataset) {
        int size = dataset.length;
        Integer[][] neighbors = new Integer[size][];
        
        // For each vector in the dataset
        for (int i = 0; i < size; i++) {
            // Find all indices of the vector's neighbors
            neighbors[i] = findNeighbors(i, dataset);
//            java.util.Arrays.sort(neighbors[i]);
        }
        
        return neighbors;
    }
    
    public Integer[] findNeighbors(int pointIndex, double[][] dataset) {
        ArrayList<Integer> foundNeighbors = new ArrayList<>();
        
        if (distances == null) {
            distances = DataTools.distancesTo(dataset);
        }
        
        for (int i = 0; i < dataset.length; i++) {
            
            if (distances[pointIndex][i] < regionSize && distances[pointIndex][i] > 0) {
                foundNeighbors.add(i);
            }
        }
        
        Integer[] output = new Integer[foundNeighbors.size()];
        
        for (int i = 0; i < output.length; i++) {
            output[i] = foundNeighbors.get(i);
        }
        
        return output;
    }
    
    /**
     * Find the Euclidean distance between a and b.
     * @param a
     * @param b
     * @return 
     */
    public double distance(double[] a, double[] b) {
        double distance = 0.0;
        for (int i = 0; i < a.length; i++) {
            double difference = a[i] - b[i];
            distance += difference * difference;
        }
        return Math.sqrt(distance);
    }
    
    /**
     * Set epsilon, the max distance of any node's neighbors
     * @param dataset
     * @return 
     */
    public double setEpsilon(double[][] dataset) {
        double[][] distances = DataTools.distancesTo(dataset);
        double averageSmall = 0.0;
        double[] smalls = new double[distances.length];
        
        for(int i = 0; i < distances.length; i++) {
            double small = 999999999.0;
            for (int j = 0; j < distances.length; j++) {
                double nextDistance = distances[i][j];
                if (nextDistance < small && nextDistance > 0) {
                    small = nextDistance;
                }
            }
            smalls[i] = small;
            averageSmall += small;
        }
        
        averageSmall /= (double) smalls.length;
        
        double stdDeviation = 0.0;
        
        for (int i = 0; i < smalls.length; i++) {
            double difference = averageSmall - smalls[i];
            stdDeviation += difference*difference;
        }
        
        stdDeviation /= smalls.length;
        stdDeviation = Math.sqrt(stdDeviation);
        
        return averageSmall + stdDeviation * deviationsFromMean;
    }
}
