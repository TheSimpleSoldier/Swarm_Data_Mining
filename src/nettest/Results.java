package nettest;

import java.util.HashMap;

/**
 *
 * @author davej
 */
public class Results {
    private String algorithm;
    private double[][] dataset;
    private int[] labels;
    private long runtime;
    private double predictedMean,
            actualMean,
            confidenceMean,
            errorMean,
            percentErrorMean;
    
    /**
     * 
     * 
     * @param in_dataset
     * @param in_labels
     * @param in_runtime
     * @param in_cluster
     */
    public Results(double[][] in_dataset, int[] in_labels, long in_runtime, String in_algorithm) {
        dataset = in_dataset;
        labels = in_labels;
        runtime = in_runtime;
        algorithm = in_algorithm;
    }
    
    public long runtime() {
        return runtime;
    }
    
    public String algorithm() {
        return algorithm;
    }
    
    public void temporaryResultsPrintout() {
        HashMap<Integer,Integer> clusterLabels = new HashMap<>();
        
        for(int i = 0; i < labels.length; i++) {
            int currentCluster = labels[i];
            if (currentCluster >= 0) {
                
                int value = clusterLabels.getOrDefault(currentCluster, Integer.valueOf(0));
                value += 1;
                clusterLabels.put(currentCluster, value);
            }
        }
        
        System.out.println("Dataset size: " + dataset.length);
        int countPts = 0;
        for (int i = 0; i < clusterLabels.size(); i++) {
            countPts += clusterLabels.get(i);
            System.out.format("Cluster: %d, size: %d%n", i, clusterLabels.get(i));
        }
        System.out.println("Points in clusters: " + countPts);
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
