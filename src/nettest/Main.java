package nettest;

import java.io.File;
import training.*;
import org.json.JSONObject;
import org.apache.commons.io.FileUtils;
import feedforward.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Main
{
    // Array of every JSON file name. Indices are the same as the datasets.
    public static String[] dataFile = {
        "data/movement_libras.csv",
        "data/turkiye-student-evaluation_generic.JSON",
        "data/airfoil.data",
        "data/seeds.data",
        "data/synthetic_control.data",
        "data/dow_jones.data",
        "data/SPECTF.JSON",
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
        int fileIndex = 3;      // Specify the file to use (see file array)
        int testIterations = 2; // Specify the number of test iterations
        
        // Initialize dataset
        double[][] dataset = DataTools.getDataFromFile(dataFile[fileIndex]);
        
        // Init cluster algorithms
        Cluster[] clusters = new Cluster[] {
            new DBScan.DBScan(dataFile[fileIndex]),
        };
        
        Experimenter experiment = new Experimenter(clusters, dataset, testIterations);
        
        // Run experiment (temporary printout of cluster population for immediate testing)
        System.out.println("Testing on " + dataFile[fileIndex]);
        experiment.run();
    }
    
}
