package clustertest;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author davej
 */
public class Results {
    private final String algorithm, dataName;
    private final double[][] dataset, distances;
    private final int[] labels;
    private final long runtime;
    
    private final HashMap<Integer,ArrayList<Integer>> labelsToIndices;
    private final HashMap<Integer,Integer> labelsToSizes;
    
    /**
     * 
     * 
     * @param in_dataset
     * @param in_labels
     * @param in_runtime
     * @param in_algorithm
     * @param in_distances
     * @param in_indices
     */
    public Results(double[][] in_dataset, int[] in_labels, long in_runtime, String in_algorithm, double[][] in_distances, String in_dataName) {
        dataset = in_dataset;
        dataName = in_dataName;
        labels = in_labels;
        runtime = in_runtime;
        distances = in_distances;
        
        int atIndex = in_algorithm.indexOf("@");
        int dotIndex = in_algorithm.indexOf(".");
        if (atIndex > 0 && dotIndex >= 0) {
            algorithm = in_algorithm.substring(dotIndex + 1, atIndex);
        } else if (atIndex > 0) {
            algorithm = in_algorithm.substring(0, atIndex);
        } else {
            algorithm = in_algorithm;
        }
        
        labelsToIndices = new HashMap<>();
        labelsToSizes = new HashMap<>();
        
        for (int i = 0; i < labels.length; i++) {
            int label = labels[i];
            if (label >= 0) {
                int size = labelsToSizes.getOrDefault(label, Integer.valueOf(0));
                size += 1;
                labelsToSizes.put(label, size);
                
                ArrayList<Integer> cluster = labelsToIndices.getOrDefault(label, new ArrayList<>());
                cluster.add(i);
                labelsToIndices.put(label, cluster);
            }
        }
    }
    
    
    public double separation(int clusterA, int clusterB) {
        double separation = 0.0;
        ArrayList<Integer> clusterListA = labelsToIndices.get(clusterA);
        ArrayList<Integer> clusterListB = labelsToIndices.get(clusterB);
        int lengthA = clusterListA.size();
        int lengthB = clusterListB.size();
        double edgeCount = 0.0;
        
        for (int i = 0; i < lengthA; i++) {
            int indexA = clusterListA.get(i);
            for (int j = 0; j < lengthB; j++) {
                edgeCount += 1.0;
                int indexB = clusterListB.get(j);
                separation += distances[indexA][indexB];
            }
        }
        separation /= edgeCount;
        
        return separation;
    }
    
    public double cohesion(int cluster) {
        double cohesion = 0.0;
        ArrayList<Integer> clusterList = labelsToIndices.get(cluster);
        int length = clusterList.size();
        double edgeCount = 0.0;
        for (int i = 0; i < length; i++) {
            int indexA = clusterList.get(i);
            for (int j = i + 1; j < length; j++) {
                edgeCount += 1.0;
                int indexB = clusterList.get(j);
                cohesion += distances[indexA][indexB];
            }
        }
        cohesion /= edgeCount;
        
        return cohesion;
    }
    
    public double shortestDistanceBetweenClusters(int clusterA, int clusterB) {
        double distance = Double.MAX_VALUE;
        for (int i = 0; i < labelsToSizes.get(clusterA); i++) {
            int indexA = labelsToIndices.get(clusterA).get(i);
            for (int j = 0; j < labelsToSizes.get(clusterB); j++) {
                int indexB = labelsToIndices.get(clusterB).get(j);
                double nextDistance = DataTools.distance(dataset[indexA], dataset[indexB]);
                
                if(nextDistance < distance) {
                    distance = nextDistance;
                }
            }
        }
        
        return distance;
    }
    
    public int clusterSize(int cluster) {
        return labelsToSizes.getOrDefault(cluster,0);
    }
    
    public int clusterCount() {
        return labelsToSizes.size();
    }
    
    public double percentNoise() {
        double noise = 0.0;
        for (int i = 0; i < labels.length; i++) {
            if (labels[i] < 0) {
                noise += 1.0;
            }
        }
        noise /= (double) labels.length;
        return noise * 100;
    }
    
    public int numberOfClusteredVectors() {
        int countPts = 0;
        for (int i = 0; i < labelsToSizes.size(); i++) {
            countPts += clusterSize(i);
        }
        return countPts;
    }
    
    public double averageClusterSize() {
        double clusterSizes = 0.0;
        for (int i = 0; i < clusterCount(); i++) {
            clusterSizes += clusterSize(i);
        }
        return clusterSizes / (double)clusterCount();
    }
    
    public double averageSeparation() {
        double separation = 0.0;
        double edges = 0.0;
        for (int i = 0; i < clusterCount(); i++) {
            for (int j = i + 1; j < clusterCount(); j++) {
                edges += 1;
                separation += separation(i, j);
            }
        }
        return separation / edges;
    }
    
    public double averageCohesion() {
        double cohesion = 0.0;
        for (int i = 0; i < clusterCount(); i++) {
            cohesion += cohesion(i);
        }
        return cohesion / (double)clusterCount();
    }
    
    public double percentClustered() {
        return 100.0 - percentNoise();
    }
    
    public long runtime() {
        return runtime;
    }
    
    public String algorithm() {
        return algorithm;
    }
    
    public String datasetName() {
        return dataName;
    }
    
    public int datasetSize() {
        return dataset.length;
    }
    
    public int numberOfAttributes() {
        return dataset[0].length;
    }
    
    public void testingPrintout() {
        System.out.format("Algorithm: %s, Dataset: %s, Runtime: %d ms%n", algorithm(), datasetName(), runtime());
        System.out.format("Dataset size: %d, Points clustered: %d%n", datasetSize(), numberOfClusteredVectors());
        System.out.format("Percent noise: %.1f%%, Percent clustered: %.1f%%%n", percentNoise(), percentClustered());
        int countPts = 0;
        for (int i = 0; i < clusterCount(); i++) {
            countPts += clusterSize(i);
            System.out.format("Cluster %d (%d points):%n", i, clusterSize(i));
            for (int j = 0; j < clusterCount(); j++) {
                if (j != i) {
                    System.out.format("    Separation from cluster %d: %.3f%n", j, separation(i,j));
                }
            }
            System.out.format("                     Cohesion: %.3f%n",cohesion(i));
        }
        System.out.println();
    }
}
