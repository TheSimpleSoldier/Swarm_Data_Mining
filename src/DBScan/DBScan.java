package DBScan;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author David Bell
 */
public class DBScan {
    
    public DBScan() {
        
    }
    
    public Integer[][] DBScan(double[][] dataset, double regionSize, int minPoints) {
        // Get the neighbors of every node
        Integer[][] neighbors = getNeighbors(dataset, regionSize, minPoints);
        // Initialize labels array (0 = noise, 1 = core, -1 = border)
        int[] labels = new int[neighbors.length];
        int currentCluster = 0;
        
        ArrayList<Integer> keys = new ArrayList<>();
        HashMap<Integer,ArrayList<Integer>> mergeClusters = new HashMap<>();
        
        // Visit every vector
        for (int i = 0; i < neighbors.length; i++) {
            // Get the size of vector i's neighborhood
            int neighborhoodSize = neighbors[i].length;
            
            // Find a core vector that has not been visited
            while(labels[i] > 0 || neighborhoodSize < regionSize) {
                i += 1;
                neighborhoodSize = neighbors[i].length;
            }
            
            // Increment cluster label and mark current vector as its first member
            currentCluster += 1;
            labels[i] = currentCluster;
            
            // Go through all of the vectors in the current neighborhood
            for (int j = 0; j < neighborhoodSize; j++) {
                // Get the index of neighbor j
                int index = neighbors[i][j];
                // Get the cluster label of this neighbor vector
                int neighborCluster = labels[index];
                if (neighborCluster == 0) {
                    // If neighbor j is not in a cluster, add it to this cluster
                    labels[index] = currentCluster;
                } else if (neighbors[j].length >= regionSize) {
                    // If j is in a cluster and a core vector, flag for merge
                    if (keys.contains(neighborCluster)) {
                        // Neighbor cluster is a key, add to existing merge list
                        mergeClusters.get(neighborCluster).add(currentCluster);
                    } else {
                        boolean added = false;
                        // Find the cluster that neighborCluster is merging with
                        for (int key : keys) {
                            if (mergeClusters.get(key).contains(neighborCluster)) {
                                // Found neighborCluster, add current to this key
                                mergeClusters.get(key).add(currentCluster);
                                added = true;
                                break;
                            }
                        }
                        if (!added) {
                            // Create a new list of clusters to merge with neighbor
                            ArrayList<Integer> mergeList = new ArrayList<>();
                            // Add current cluster to the list
                            mergeList.add(currentCluster);
                            // Make neighborCluster a key for the new list
                            keys.add(neighborCluster);
                            mergeClusters.put(neighborCluster, mergeList);
                        }
                    }
                }
            }
        }
        
        // Merge clusters sharing core points in their neighborhoods
        int numberOfClusters = keys.size();
        ArrayList<ArrayList<Integer>> clusters = new ArrayList<>();
        for (int i = 0; i < numberOfClusters; i++) {
            clusters.add(new ArrayList<>());
        }
        
        for (int i = 0; i < labels.length; i++) {
            int cluster = labels[i];
            if (keys.contains(cluster)) {
                int finalCluster = keys.indexOf(cluster);
                clusters.get(finalCluster).add(i);
            } else {
                for (int key : keys) {
                    if (mergeClusters.get(key).contains(cluster)) {
                        int finalCluster = keys.indexOf(key);
                        clusters.get(finalCluster).add(i);
                        break;
                    }
                }
            }
        }
        
        Integer[][] output = new Integer[clusters.size()][];
        
        for (int i = 0; i < clusters.size(); i++) {
            ArrayList<Integer> newCluster = clusters.get(i);
            output[i] = new Integer[newCluster.size()];
            for (int j = 0; j < output[i].length; j++) {
                output[i][j] = newCluster.get(j);
            }
        }
        
        return output;
    }
    
    public void mergeClusters(int[] clusterLabels, Integer[][] neighbors, int newCluster, int center, int regionSize) {
        // For every neighbor of center vector
        for (int i = 0; i < neighbors[center].length; i++) {
            // Get the neighbor's index
            int neighborIndex = neighbors[center][i];
            // If the neighbor is a core 
            if (neighbors[neighborIndex].length >= regionSize &&
                    clusterLabels[neighborIndex] != newCluster) {
                
                clusterLabels[neighborIndex] = newCluster;
                mergeClusters(clusterLabels, neighbors, newCluster, neighborIndex, regionSize);
                
            } else {
                
                clusterLabels[neighborIndex] = newCluster;
                
            }
        }
    }
    
    public Integer[][] getNeighbors(double[][] dataset, double regionSize, int minPoints) {
        int size = dataset.length;
        Integer[][] neighbors = new Integer[size][];
        
        // For each vector in the dataset
        for (int i = 0; i < size; i++) {
            // Find all indices of the vector's neighbors
            neighbors[i] = findNeighbors(i, dataset, regionSize);
        }
        
        return neighbors;
    }
    
    public Integer[] findNeighbors(int pointIndex, double[][] dataset, double regionSize) {
        ArrayList<Integer> foundNeighbors = new ArrayList<>();
        
        for (int i = 0; i < dataset.length; i++) {
            
            // Find the distance from the central point to the current point
            double distance = distance(dataset[pointIndex],dataset[i]);
            // If the distance is in the neighborhood, add it to the list
            if (i != pointIndex && distance < regionSize) {
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
     * 
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
    /*
    1: function DB-Scan(D) 
    2:      currClustLbl ← 1 
    3:      for all p ∈ Core do do 
    4:          if clustLbl[p] = “Unknown” then 
    5:              currClustLbl ← currClustLbl + 1 
    6:              clustLbl[p] ← currClustLbl 
    7:          end if 
    8:          for all p0 ∈ θ-neighborhood do 
    9:              if clustLbl[p0] = “Unknown” then 
    10:                 clustLbl[p0] ← currClustLbl 
    11:             end if 
    12:         end for 
    13:     end for 
    14:     return clustLbl 
    15: end function
    */
    
}
