package nettest;

import java.util.ArrayList;
import java.util.HashMap;
import static nettest.DataTools.distance;

/**
 *
 * @author davej
 */
public class Results {
    private final String algorithm, dataName;
    private final double[][] dataset, distances;
    private final int[] labels;
    private final long runtime;
    
    private HashMap<Integer,ArrayList<Integer>> labelsToIndices;
    private HashMap<Integer,Integer> labelsToSizes;
    
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
    
    public int getClusterSize(int cluster) {
        return labelsToSizes.getOrDefault(cluster,0);
    }
    
    public int getClusterCount() {
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
            countPts += getClusterSize(i);
        }
        return countPts;
    }
    
    public double percentClustered() {
        return 100.0 - percentNoise();
    }
    
    public long getRuntime() {
        return runtime;
    }
    
    public String getAlgorithm() {
        return algorithm;
    }
    
    public void testingPrintout() {
        System.out.format("Algorithm: %s, Dataset: %s, Runtime: %d ms%n", algorithm, dataName, runtime);
        System.out.format("Dataset size: %d, Points clustered: %d%n", dataset.length, numberOfClusteredVectors());
        System.out.format("Percent noise: %.1f%%, Percent clustered: %.1f%%%n", percentNoise(), percentClustered());
        int countPts = 0;
        for (int i = 0; i < labelsToSizes.size(); i++) {
            countPts += getClusterSize(i);
            System.out.format("Cluster %d (%d points):%n", i, getClusterSize(i));
            for (int j = 0; j < labelsToSizes.size(); j++) {
                if (j != i) {
                    System.out.format("    Separation from cluster %d: %.3f%n", j, separation(i,j));
                }
            }
            System.out.format("                     Cohesion: %.3f%n",cohesion(i));
        }
        System.out.println();
    }
    
//    public double percentCorrect() {
//        int index = Index.ACTUAL.ordinal();
//        int predictedIndex = Index.PREDICTED.ordinal();
//        double totalCorrect = 0.0;
//        for (int i = 0; i < stats[index].length; i++) {
//            if (stats[index][i] == stats[predictedIndex][i]) {
//                totalCorrect += 1.0;
//            }
//        }
//        
//        return totalCorrect / stats[index].length;
//    }
//    
//    /**
//     * Calculate the standard deviation if it has not been calculated yet.
//     * @param result
//     * @return standard deviation of the predictions.
//     */
//    public double standardDeviation(Index result) {
//        int index, size;
//        double mean, standardDeviation;
//        
//        index = result.ordinal();
//        size = stats[index].length;
//        mean = getMean(result);
//        standardDeviation = 0.0;
//        
//        for (int i = 0; i < size; i++) {
//            standardDeviation += Math.pow(stats[index][i] - mean, 2.0);
//        }
//        
//        standardDeviation = Math.sqrt(standardDeviation/(size - 1));
//        
//        return standardDeviation;
//    }
//    
//    public double[] percentErrors() {
//        int index = Index.PERCENT_ERROR.ordinal();
//        
//        if (stats[index] == null) {
//            double[] errors = getErrors();
//            double[] percentErrors = new double[errors.length];
//            double errorSum = 0.0;
//            
//            int actualIndex = Index.ACTUAL.ordinal();
//        
//            for (int i = 0; i < percentErrors.length; i++) {
//                double percentError = Math.abs(errors[i])/stats[actualIndex][i];
//                percentErrors[i] = percentError;
//            }
//            stats[index] = percentErrors;
//        }
//        
//        return stats[index];
//    }
//    
//    public double[] getErrors() {
//        
//        int errorIndex = Index.ERROR.ordinal();
//        
//        if (stats[errorIndex] == null) {
//            int actualIndex = Index.ACTUAL.ordinal();
//            int predictedIndex = Index.PREDICTED.ordinal();
//        
//            int length = stats[actualIndex].length;
//            double[] errors = new double[length];
//        
//            for (int i = 0; i < length; i++) {
//                errors[i] = stats[predictedIndex][i] - stats[actualIndex][i];
//            }
//            
//            stats[errorIndex] = errors;
//        }
//        
//        return stats[errorIndex];
//    }
//    
//    public static int indexOf(Index result) {
//        return result.ordinal();
//    }
//    
//    /**
//     * Calculate and return the mean of the specified result
//     * @param result 
//     * @return mean of the predicted values.
//     */
//    public double getMean(Index result) {
//        int index = result.ordinal();
//        double mean;
//        switch(index) {
//            case 1:
//                mean = actualMean;
//                break;
//            case 2:
//                mean = confidenceMean;
//                break;
//            case 3:
//                mean = errorMean;
//                break;
//            case 4:
//                mean = percentErrorMean;
//                break;
//            default:
//                mean = predictedMean;
//                break;  
//        }
//        
//        if (mean == 0.0) {
//            double[] results = getData(result);
//            for (int i = 0; i < results.length; i++) {
//                mean += results[i];
//            }
//            
//            mean /= results.length;
//            
//            switch(index) {
//                case 1:
//                    actualMean = mean;
//                    break;
//                case 2:
//                    confidenceMean = mean;
//                    break;
//                case 3:
//                    errorMean = mean;
//                    break;
//                case 4:
//                    percentErrorMean = mean;
//                    break;
//                default:
//                    predictedMean = mean;
//                    break;
//            }
//        }
//        return mean;
//    }
//    
//    public double[] getData(Index result) {
//        double[] data;
//        switch(result.ordinal()) {
//            case 1:
//                data = stats[1];
//                break;
//            case 2:
//                data = stats[2];
//                break;
//            case 3:
//                data = stats[3];
//                if (data == null) {
//                    data = getErrors();
//                    stats[3] = data;
//                }
//                break;
//            case 4:
//                data = stats[4];
//                if (data == null) {
//                    data = percentErrors();
//                    stats[4] = data;
//                }
//                break;
//            default:
//                data = stats[0];
//                break;
//        }
//        
//        return data;
//    }
//    
//    public void updateData(int indexA, int indexB, double data) {
//        stats[indexA][indexB] = data;
//    }
//    
//    public enum Index {
//        ACTUAL,
//        PREDICTED,
//        CONFIDENCE,
//        ERROR,
//        PERCENT_ERROR
//    }
}
