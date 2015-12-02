package clustertest;

import aco.ACO;
import competitivelearning.CompetitiveLearning;
import kMeans.kMeansClusterer;
import clustering.*;
import PSO.*;

public class Main
{
    // Array of every file name. Indices are the same as the datasets.
    public static String[] dataFile = {
        "data/movement_libras.csv",//0
        "data/turkiye-student-evaluation_generic.csv",//1
        "data/airfoil.data",//2
        "data/seeds.data",//3
        "data/synthetic_control.data",//4
        "data/dow_jones.data",//5
        "data/SPECTF.csv",//6
        "data/gesture.csv",//7
        "data/wholesale.csv",//8
        "data/bupa.csv",//9
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
        boolean verbose = true;  // Set to true for verbose mode (demonstrate functionality).
        
        // Initialize dataset
        double[][] dataset = DataTools.getDataFromFile(dataFile[fileIndex]);
        
        // Init cluster algorithms
        Cluster[] clusters = new Cluster[] {
//            new CompetitiveLearning(new double[]{.01, .01, 10}, verbose),
            new ACO(new double[]{10, .5}, verbose),
            new DBScan.DBScan(dataFile[fileIndex], verbose),
//            new kMeansClusterer(.001, 10, verbose),
//            new PSO(0.2, 0.2, 0.1, 75, verbose, 0.001, 10)
        };
        
        Experimenter experiment = new Experimenter(clusters, dataset, testIterations,verbose);
        
        // Run experiment
        experiment.run(dataFile[fileIndex], debugging);
    }
}
