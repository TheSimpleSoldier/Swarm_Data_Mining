package nettest;

/**
 *
 * @author David
 */
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataTools {
    
    private static final java.util.Random rand = new java.util.Random();
    
    /**
     * Set the specified column as the class attribute, making it the last column.
     * @param dataset
     * @param classColumn
     * @return the modified dataset
     */
    private static double[][] setClass(double[][] dataset, int classColumn) {
        if (dataset != null) {
            double[][] newDataset = new double[dataset.length][dataset[0].length];
            
            for (int i = 0; i < dataset.length; i++) {
                int newColumnIndex = 0;
                for (int j = 0; j < dataset[i].length; j++) {
                    if (j == classColumn) {
                        newDataset[i][dataset[i].length - 1] = dataset[i][j];
                    } else {
                        newDataset[i][newColumnIndex] = dataset[i][j];
                        newColumnIndex++;
                    }
                }
            }
            
            return newDataset;
        } else {
            return null;
        }
    }
    
    /**
     * Randomly shuffle all tuples in the dataset
     * @param dataset
     * @param indices
     * @return dataset with tuples rearranged;
     */
    public static double[][] shuffleData(double[][] dataset) {
        ArrayList<Double[]> shuffleData = new ArrayList<>();
        
        int tupleSize = dataset[0].length;
        int tupleCount = dataset.length;
        
        for (int i = 0; i < tupleCount; i++) {
            Double[] tuple = new Double[tupleSize];
            for (int j = 0; j < tupleSize; j++) {
                tuple[j] = dataset[i][j];
            }
            shuffleData.add(tuple);
        }
        
        double[][] newDataset = new double[tupleCount][tupleSize];
        int size = tupleCount;
        for (int i = 0; i < tupleCount; i++) {
            int index = rand.nextInt(size);
            Double[] tuple = shuffleData.remove(index);
            size--;
            
            for (int j = 0; j < tupleSize; j++) {
                newDataset[i][j] = tuple[j];
            }
        }
        
        return newDataset;
    }

    /**
     * Remove the specified column
     * @param dataset
     * @param columnIndex
     * @return 2D double array, dataset without the specified column
     */
    public static double[][] removeColumn(double[][] dataset, int columnIndex) {
        if (dataset != null) {
            double[][] newDataset = new double[dataset.length][dataset[0].length - 1];
            
            for (int i = 0; i < dataset.length; i++) {
                int newColumnIndex = 0;
                for (int j = 0; j < dataset[i].length; j++) {
                    if (j == columnIndex) {
                        j++;
                        if (j >= dataset[i].length) {
                            break;
                        }
                    }
                        newDataset[i][newColumnIndex] = dataset[i][j];
                        newColumnIndex++;
                }
            }
            
            return newDataset;
        } else {
            return null;
        }
    }
    
    public static double[][] normalizeData(double[][] dataset) {
        double[] min = new double[dataset[0].length];
        double[] max = new double[dataset[0].length];
        // Initialize the minimum and maximum values
        for (int i = 0; i < max.length; i++) {
            max[i] = Double.MIN_VALUE;
            min[i] = Double.MAX_VALUE;
        }
        // Find the min and max of each attribute in the dataset
        for (int i = 0; i < dataset.length; i++) {
            for (int j = 0; j < dataset[i].length; j++) {
                if (max[j] < dataset[i][j]) {
                    max[j] = dataset[i][j];
                }
                if (min[j] > dataset[i][j]) {
                    min[j] = dataset[i][j];
                }
            }
        }
        
        // Normalize the data relative to its min and max
        for (int i = 0; i < dataset.length; i++) {
            for (int j = 0; j < dataset[i].length; j++) {
                dataset[i][j] = (dataset[i][j]-min[j])/(max[j]-min[j]);
            }
        }
        return dataset;
    }
    
    /**
     * Get a dataset from a CSV.
     * @param file_name
     * @return 2D array data[i][j]
     */
    public static double[][] getDataFromFile(String file_name) {
        // Relative path. Must create a directory "~/data/"  
        File file = new File(file_name);
        ArrayList<ArrayList<Double>> relation = new ArrayList();

        HashMap<String, Double> replace = new HashMap();
        
        double newValue = 1.0;
        
        boolean csv = file_name.contains("csv");
        
        try {
            
            Scanner scan = new Scanner(new File(file_name));
            
            String comma = ",";
            
            while(scan.hasNext()) {
                ArrayList<Double> tuple = new ArrayList();
                
                String line = scan.nextLine();
                line = line.trim();
                
                line = replaceSpaces(line);
                
                while (line.contains(comma)) {
                    
                    int index = line.indexOf(comma);
                    String value = line.substring(0,index);
                    
                    double parseValue = 0.0;
                    
                    try {
                        parseValue += Double.parseDouble(value);
                    } catch(NumberFormatException x) {
                        if (replace.containsKey(value)) {
                            parseValue = replace.get(value);
                        } else {
                            replace.put(value, newValue);
                            parseValue = newValue;
                            newValue += 1.0;
                        }
                    }
                    
                    tuple.add(parseValue);
                    
                    line = line.substring(index + 1);
                }
                
                if (line.length() > 0) {
                    double parseValue = 0.0;
                    
                    try {
                        parseValue += Double.parseDouble(line);
                    } catch(NumberFormatException x) {
                        if (replace.containsKey(line)) {
                            parseValue = replace.get(line);
                        } else {
                            replace.put(line, newValue);
                            parseValue = newValue;
                            newValue += 1.0;
                        }
                    }
                    
                    tuple.add(parseValue);
                }
                
                relation.add(tuple);
            }
        } catch (IOException e) {
            System.out.println("Dataset does not exist.");
            return null;
        }

        double[][] data = new double[relation.size()][relation.get(0).size()];
        
        for (int i = 0; i < relation.size(); i++) {
            ArrayList<Double> tuple = relation.get(i);
            for (int j = 0; j < tuple.size(); j++) {
                data[i][j] = tuple.get(j);
            }
        }
        
        switch(file_name) {
            case "data/bupa.csv":
                // Remove selector attribute
                data = removeColumn(data, data[0].length - 1);
                break;
            case "data/wholesale.csv":
                // Remove flags for region
//                data = removeColumn(data, 0);
                // Remove flags for channel
//                data = removeColumn(data, 0);
                break;
            case "data/gesture.csv":
                // Remove timestamp
                data = removeColumn(data, data[0].length - 2);
                break;
            case "data/dow_jones.data":
                // Remove fiscal quarter
                data = removeColumn(data, 0);
                // Remove timestamp
                data = removeColumn(data, 1);
                break;
            case "data/SPECTF.csv":
                // Remove only binary attribute (class attribute)
//                data = removeColumn(data, 0);
                break;
            case "data/turkiye-student-evaluation_generic.csv":
                // Keep everything, values of class attribute(s) are similar to
                // the values of other attributes.
                break;
            case "data/synthetic_control.data":
                // Keep everything.
                break;
            case "data/seeds.data":
                // Keep everything.
                break;
            case "data/airfoil.data":
                // Keep everything.
                break;
            case "data/movement_libras.csv":
                // Keep everything.
                break;
        }
        
        data = normalizeData(data);
        
        return data;
    }
    
    /**
     * Randomly partition the dataset into two equal subsets, one training and
     * one testing
     * @param dataset data to be partitioned
     * @return two random halves of the data set
     */
    public static double[][][] partitionData(double[][] dataset) {
        
        int numInputs = dataset[0].length;
        int size = dataset.length;
        
        // Two halves of the dataset
        double[][] trainingData = new double[size/2][numInputs];
        double[][] testingData = new double[size/2][numInputs];
        
        int trainingIndex = 0;
        int testingIndex = 0;
        int dataIndex = 0;
        int half = size/2;
        
        while(trainingIndex < half && testingIndex < half) {
            // Flip a coin...
            if (rand.nextBoolean()) {
                // If heads, add the next instance of the dataset to the training set
                for (int i = 0; i < numInputs; i++) {
                    trainingData[trainingIndex][i] = dataset[dataIndex][i];
                }
                
                trainingIndex++;
                dataIndex++;
            } else {
                // If tails, add the next instance of the dataset to the testing set
                for (int i = 0; i < numInputs; i++) {
                    testingData[testingIndex][i] = dataset[dataIndex][i];
                }
                
                testingIndex++;
                dataIndex++;
            }
        }
        
        // If the training index is still not full, give it the remaining data
        while(trainingIndex < half) {
            for (int i = 0; i < numInputs; i++) {
                trainingData[trainingIndex][i] = dataset[dataIndex][i];
            }
                
            trainingIndex++;
            dataIndex++;
        }
        
        // If the testing index is still not full, give it the remaining data
        while(testingIndex < half) {
            for (int i = 0; i < numInputs; i++) {
                testingData[testingIndex][i] = dataset[dataIndex][i];
            }
            
            testingIndex++;
            dataIndex++;
        }
        
        return new double[][][] { trainingData, testingData };
    }
    
    public static double[][] incrementalClasses(double[][] dataset) {
        ArrayList<Double> values = new ArrayList<>();
        double counter = 0.0;
        for (int i = 0; i < dataset.length; i++) {
            int index = dataset[i].length - 1;
            if (!values.contains(dataset[i][index])) {
                values.add(dataset[i][index]);
                dataset[i][index] = (double) values.indexOf(dataset[i][index]);
            } else {
                dataset[i][index] = (double) values.indexOf(dataset[i][index]);
            }
        }
        
        return dataset;
    }
    
    public static void printData(double[][] data) {
        
        for(int i = 0; i < data.length; i++) {
            System.out.println(data[i][data[i].length - 1]);
        }
    }
    
    public static double[][] distancesTo(double[][] dataset) {
        int length = dataset.length;
        double[][] distances = new double[length][length];
        for (int i = 0; i < length; i++) {
            for (int j = i + 1; j < length; j++) {
                distances[i][j] = distance(dataset[i], dataset[j]);
                distances[j][i] = distances[i][j];
            }
        }
        return distances;
    }
    
    /**
     * Find the Euclidean distance between a and b.
     * 
     * @param a
     * @param b
     * @return 
     */
    public static double distance(double[] a, double[] b) {
        double distance = 0.0;
        for (int i = 0; i < a.length; i++) {
            double difference = a[i] - b[i];
            distance += difference * difference;
        }
        return Math.sqrt(distance);
    }
    
    public static String replaceSpaces(String line) {
        Pattern find = Pattern.compile("(\\s+)");
        String replace = ",";
        Matcher matcher = find.matcher(line);
        return matcher.replaceAll(replace);
    }
    
    /**
     * Randomly add to or subtract from the input a fixed percentage of the input
     * @param output output of the Rosenbrock function
     * @return output plus or minus a fixed percentage.
     */
    public static double plusOrMinus10(double output) {
        double newVal = output + output * 1 * Math.pow(-1, (double)(rand.nextInt(2) + 1)); //Math.pow(-1, (double)(rand.nextInt(2) + 1)) * 2 * output * Math.random() + output;
        return newVal;
    }
}
