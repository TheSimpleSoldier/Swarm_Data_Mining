package clustertest;

import clustering.*;

public class Main
{
    // Array of every JSON file name. Indices are the same as the datasets.
    public static String[] dataFile = {
        "data/movement_libras.csv",
        "data/turkiye-student-evaluation_generic.csv",
        "data/airfoil.data",
        "data/seeds.data",
        "data/synthetic_control.data",
        "data/dow_jones.data",
        "data/SPECTF.csv",
        "data/gesture.csv",
        "data/wholesale.csv",
        "data/bupa.csv",
    };

    /**
     * To run a specific clustering algorithm, specify its parameters in its
     * respective array and select set the fileIndex variable to the index of 
     * the desired dataset.
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        int fileIndex = 3;  // Specify the file to use (see file array)
        int testIterations = 1;  // Specify the number of test iterations
        boolean debugging = false;  // Set to true if you want to print data for individual runs.
        boolean verbose = false;  // Set to true for verbose mode (demonstrate functionality).
        
        // Initialize dataset
        double[][] dataset = DataTools.getDataFromFile(dataFile[fileIndex]);
        
        // Init cluster algorithms
        Cluster[] clusters = new Cluster[] {
            //new DBScan.DBScan(dataFile[fileIndex], verbose),
            new CompetitiveLearning(new double[]{.01, .01, 10}, verbose),
        };
        
        Experimenter experiment = new Experimenter(clusters, dataset, testIterations);
        
        // Run experiment
        experiment.run(dataFile[fileIndex], debugging);
    }
    
}
