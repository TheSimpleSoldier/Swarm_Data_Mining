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
    
    private final double deviationsFromMean;
    private double regionSize;
    private int minPoints;
    
    /**
     * Initialize all parameters based on dataset.
     */
    public DBScan() {
        minPoints = -1;
        regionSize = -1;
        deviationsFromMean = 2;
    }
    
    /**
     * Use the deviations to find parameters.
     * @param deviations 
     */
    public DBScan(double deviations) {
        minPoints = -1;
        regionSize = -1;
        deviationsFromMean = deviations;
    }
    
    /**
     * Set only minPoints.
     * @param minPoints
     */
    public DBScan(int minPoints) {
        this.minPoints = minPoints;
        regionSize = -1;
        deviationsFromMean = 2;
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
        deviationsFromMean = 0;
    }
    
    public DBScan(String dataset) {
        switch(dataset) {
            case "data/wholesale.csv":
                minPoints = 10;
                regionSize = -1;
                deviationsFromMean = 0.1;
                break;
            case "data/gesture.csv":
                minPoints = -1;
                regionSize = -1;
                deviationsFromMean = 2;
                break;
            case "data/dow_jones.data":
                minPoints = -1;
                regionSize = -1;
                deviationsFromMean = 2;
                break;
            case "data/SPECTF.csv":
                minPoints = -1;
                regionSize = -1;
                deviationsFromMean = 2;
                break;
            case "data/turkiye-student-evaluation_generic.csv":
                minPoints = -1;
                regionSize = -1;
                deviationsFromMean = 2;
                break;
            case "data/synthetic_control.data":
                minPoints = -1;
                regionSize = -1;
                deviationsFromMean = 2;
                break;
            case "data/seeds.data":
                minPoints = 14;
                regionSize = -1;
                deviationsFromMean = 4;
                break;
            case "data/airfoil.data":
                minPoints = 14;
                regionSize = -1;
                deviationsFromMean = 5;
                break;
            case "data/movement_libras.csv":
                minPoints = -1;
                regionSize = -1;
                deviationsFromMean = 2;
                break;
            default: // data/bupa.csv
                minPoints = 15;
                regionSize = -1;
                deviationsFromMean = 0.2;
                break;
        }
    }
    
    
    @Override
    public int[] run(double[][] dataset) {
        // Set regionSize if regionSize not known
        if (regionSize < 0) {
            regionSize = setEpsilon(dataset);
        }
        // Set minimum points for core labels as the number of attributes if not set
        if (minPoints < 0) {
            minPoints = Math.min(dataset.length/5, dataset[0].length + 2 + (int)deviationsFromMean);
        }
        
        // Get the neighbors of every node
        Integer[][] neighbors = getNeighbors(dataset, regionSize);
        // Initialize labels array (0 = noise, 1 = core, -1 = border)
        int[] labels = new int[neighbors.length];
        int currentCluster = 0;
        
        visit_neighbors:
        // Visit every vector
        for (int i = 0; i < neighbors.length; i++) {
            // Get the size of vector i's neighborhood
            int neighborhoodSize = neighbors[i].length;
            
            // Find a core vector that has not been visited
            while(labels[i] != 0 || neighborhoodSize < minPoints) {
                i += 1;
                if (i >= neighbors.length) {
                    break visit_neighbors;
                }
                neighborhoodSize = neighbors[i].length;
            }
            
            
            // Increment cluster label and mark current vector as its first member
            currentCluster += 1;
            labels[i] = currentCluster;
            
            // Go through all of the vectors in the current neighborhood
            for (int j = 0; j < neighborhoodSize; j++) {
                
                // Get the index of neighbor j
                int Jth_neighbor = neighbors[i][j];
                
                // Get the label of the Jth neighbor's cluster
                int neighborCluster = labels[Jth_neighbor];
                
                // If Jth neighbor is not in a cluster, add it to this cluster
                if (neighborCluster == 0) {
                    labels[Jth_neighbor] = currentCluster;
                    // if Jth neighbor is a core point, add its neighborhood to this cluster
                    if (neighbors[Jth_neighbor].length >= minPoints) {
                        mergeClusters(labels,neighbors,Jth_neighbor,currentCluster);
                    }
                // Else if Jth neighbor is in a cluster and a core vector, flag for merge
                } else if (neighbors[Jth_neighbor].length >= minPoints) {
                    for (int k = 0; k < labels.length; k++) {
                        if (labels[k] == neighborCluster) {
                            labels[k] = currentCluster;
                        }
                    }
                } else if (neighborCluster != currentCluster){
                    labels[Jth_neighbor] = currentCluster;
                }
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
            }
        }
        
        for (int i = 0; i < labels.length; i++) {
            if (labels[i] > 0) {
                labels[i] = keys.indexOf(labels[i]);
            }
        }
        
        return labels;
    }
    
    public void mergeClusters(int[] labels, Integer[][] neighbors, int corePoint, int cluster) {
        if (labels[corePoint] == 0) {
            for (int k = 0; k < neighbors[corePoint].length; k++) {
                int neighbor_of_corePoint = neighbors[corePoint][k];
                labels[neighbor_of_corePoint] = cluster;
                if (neighbors[neighbor_of_corePoint].length >= minPoints) {
                    mergeClusters(labels,neighbors,neighbor_of_corePoint,cluster);
                }
            }
        }
    }
    
    public Integer[][] getNeighbors(double[][] dataset, double regionSize) {
        int size = dataset.length;
        Integer[][] neighbors = new Integer[size][];
        
        // For each vector in the dataset
        for (int i = 0; i < size; i++) {
            // Find all indices of the vector's neighbors
            neighbors[i] = findNeighbors(i, dataset, regionSize);
//            java.util.Arrays.sort(neighbors[i]);
        }
        
        return neighbors;
    }
    
    public Integer[] findNeighbors(int pointIndex, double[][] dataset, double regionSize) {
        ArrayList<Integer> foundNeighbors = new ArrayList<>();
        
        double[][] distances = DataTools.distancesTo(dataset);
        
        for (int i = 0; i < dataset.length; i++) {
            //
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
