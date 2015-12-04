package aco;

import DBScan.DBScan;
import clustering.Cluster;
import clustertest.DataTools;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by joshua on 11/29/15.
 * Runs ACO cemetery algorithm
 */
public class ACO implements Cluster
{
    boolean verbose;
    double neighborhood;
    double gamma;

    public ACO(double[] parameters, boolean verbose)
    {
        this.verbose = verbose;
        neighborhood = parameters[0];
        gamma = parameters[1];
    }

    @Override
    public int[] run(double[][] dataset)
    {
        Random rand = new Random();
        int size = (int)Math.round(Math.sqrt(dataset.length)) * 2;
        int numAnts = dataset.length / 2;
        neighborhood = 5;
        Ant[][] ants = new Ant[size][size];
        DataPoint[][] points = new DataPoint[size][size];
        Ant[] allAnts = new Ant[numAnts];
        DataPoint[] allPoints = new DataPoint[dataset.length];

        //place ants
        for(int k = 0; k < numAnts; k++)
        {
            while(true)
            {
                int x = Math.abs(rand.nextInt()) % size;
                int y = Math.abs(rand.nextInt()) % size;

                if(ants[x][y] == null)
                {
                    ants[x][y] = new Ant(x, y, size - 1, size - 1, gamma);
                    allAnts[k] = ants[x][y];
                    break;
                }
            }
        }

        //place points
        for(int k = 0; k < dataset.length; k++)
        {

            while(true)
            {
                int x = Math.abs(rand.nextInt()) % size;
                int y = Math.abs(rand.nextInt()) % size;

                if(points[x][y] == null)
                {
                    points[x][y] = new DataPoint(x, y, size - 1, size - 1, dataset[k]);
                    allPoints[k] = points[x][y];
                    break;
                }
            }
        }

        if(verbose)
        {
            System.out.println("Starting data points");
            for(int k = 0; k < size; k++)
            {
                for(int a = 0; a < size; a++)
                {
                    if(points[k][a] != null)
                    {
                        System.out.print("1");
                    }
                    else
                    {
                        System.out.print("0");
                    }
                }
                System.out.println();
            }
            System.out.println();
            System.out.println("Starting ant positions");
            for(int k = 0; k < size; k++)
            {
                for(int a = 0; a < size; a++)
                {
                    if(ants[k][a] != null)
                    {
                        System.out.print("1");
                    }
                    else
                    {
                        System.out.print("0");
                    }
                }
                System.out.println();
            }
        }

        //for each iteration
        int iterations = 10000;
        //set up time when ants can only drop
        int maxAntPickUp = 9000;
        for(int k = 0; k < iterations; k++)
        {
            if(k % 1000 == 0 && verbose)
            {
                System.out.println("Iteration: " + k);
            }
            //for each ant
            for(int a = 0; a < numAnts; a++)
            {
                int[] loc = allAnts[a].getLocation();
                //if its holding
                if(allAnts[a].isHolding())
                {
                    //and there is nothing there
                    if(points[loc[0]][loc[1]] == null)
                    {
                        //drop according to environment
                        if(allAnts[a].shouldDrop(getNeighborhood(points, loc[0], loc[1])))
                        {
                            points[loc[0]][loc[1]] = allAnts[a].drop();
                            if(verbose)
                            {
                                System.out.println("Ant dropped at (" + loc[0] + ", " + loc[1] + ")");
                            }
                        }
                    }
                }
                else
                {
                    //if holding nothing and data point there
                    if(points[loc[0]][loc[1]] != null && k < maxAntPickUp)
                    {
                        //pick up according to environment
                        if(allAnts[a].shouldPickUp(points[loc[0]][loc[1]], getNeighborhood(points, loc[0], loc[1])))
                        {
                            if(allAnts[a].pickUp(points[loc[0]][loc[1]]))
                            {
                                points[loc[0]][loc[1]] = null;
                                if(verbose)
                                {
                                    System.out.println("Ant picked up at (" + loc[0] + ", " + loc[1] + ")");
                                }
                            }
                        }
                    }
                }

                //code to move ant
                int x = rand.nextInt() % 2;
                int y = rand.nextInt() % 2;
                int[] tempLoc = allAnts[a].getLocation();
                if(tempLoc[0] + x < 0 || tempLoc[0] + x >= size)
                {
                    x = 0;
                }
                if(tempLoc[1] + y < 0 || tempLoc[1] + y >= size)
                {
                    y = 0;
                }
                boolean done = false;
                int i = 0;
                while(ants[tempLoc[0] + x][tempLoc[1] + y] != null && !done)
                {
                    x = rand.nextInt() % 2;
                    y = rand.nextInt() % 2;
                    if(tempLoc[0] + x < 0 || tempLoc[0] + x >= size)
                    {
                        x = 0;
                    }
                    if(tempLoc[1] + y < 0 || tempLoc[1] + y >= size)
                    {
                        y = 0;
                    }
                    if(i >= 10)
                    {
                        done = true;
                    }
                    i++;
                }
                allAnts[a].move(x, y);
                ants[loc[0]][loc[1]] = null;
                loc = allAnts[a].getLocation();
                ants[loc[0]][loc[1]] = allAnts[a];
            }
        }

        //all ants drop their points wherever they are
        for(int k = 0; k < allAnts.length; k++)
        {
            if(allAnts[k].isHolding())
            {
                int[] loc = allAnts[k].getLocation();
                while(points[loc[0]][loc[1]] != null)
                {
                    int x = rand.nextInt() % 2;
                    int y = rand.nextInt() % 2;
                    loc = allAnts[k].getLocation();
                    if(loc[0] + x < 0 || loc[0] + x >= size)
                    {
                        x = 0;
                    }
                    if(loc[1] + y < 0 || loc[1] + y >= size)
                    {
                        y = 0;
                    }
                    while(ants[loc[0] + x][loc[1] + y] != null)
                    {
                        x = rand.nextInt() % 2;
                        y = rand.nextInt() % 2;
                        if(loc[0] + x < 0 || loc[0] + x >= size)
                        {
                            x = 0;
                        }
                        if(loc[1] + y < 0 || loc[1] + y >= size)
                        {
                            y = 0;
                        }
                    }
                    allAnts[k].move(x, y);
                    ants[loc[0]][loc[1]] = null;
                    loc = allAnts[k].getLocation();
                    ants[loc[0]][loc[1]] = allAnts[k];
                }
                points[loc[0]][loc[1]] = allAnts[k].drop();
            }
        }

        if(verbose)
        {
            System.out.println("Ending data points");
            for(int k = 0; k < size; k++)
            {
                for(int a = 0; a < size; a++)
                {
                    if(points[k][a] != null)
                    {
                        System.out.print("1");
                    }
                    else
                    {
                        System.out.print("0");
                    }
                }
                System.out.println();
            }
            System.out.println();
            System.out.println("Ending ant positions");
            for(int k = 0; k < size; k++)
            {
                for(int a = 0; a < size; a++)
                {
                    if(ants[k][a] != null)
                    {
                        System.out.print("1");
                    }
                    else
                    {
                        System.out.print("0");
                    }
                }
                System.out.println();
            }
        }

        double[][] data = new double[dataset.length][2];

        for(int k = 0; k < data.length; k++)
        {
            int[] loc = allPoints[k].getLocation();
            data[k][0] = loc[0];
            data[k][1] = loc[1];
        }

        data = DataTools.normalizeData(data);

        return new DBScan().run(data);
    }

    //finds all the points in the neighborhood around a point
    private DataPoint[] getNeighborhood(DataPoint[][] points, int x, int y)
    {
        ArrayList<DataPoint> dataPoints = new ArrayList<>();
        for(int k = 0; k < points.length; k++)
        {
            for(int a = 0; a < points[0].length; a++)
            {
                if(points[k][a] != null)
                {
                    if(Math.sqrt(Math.pow(x - k, 2) + Math.pow(y - a, 2)) < neighborhood)
                    {
                        dataPoints.add(points[k][a]);
                    }
                }
            }
        }

        return dataPoints.toArray(new DataPoint[dataPoints.size()]);
    }
}
