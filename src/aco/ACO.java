package aco;

import DBScan.DBScan;
import clustering.Cluster;
import clustertest.DataTools;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by joshua on 11/29/15.
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
        int size = dataset.length / 4;
        int numAnts = dataset.length / 2;
        neighborhood = size / 8;
        Ant[][] ants = new Ant[size][size];
        DataPoint[][] points = new DataPoint[size][size];
        Ant[] allAnts = new Ant[numAnts];
        DataPoint[] allPoints = new DataPoint[dataset.length];

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

        /*for(int k = 0; k < size; k++)
        {
            for(int a = 0; a < size; a++)
            {
                if(points[k][a] == null)
                {
                    System.out.print("0");
                }
                else
                {
                    System.out.print("1");
                }
            }
            System.out.println();
        }
        System.out.println();
        System.out.println();*/

        int iterations = 100000;
        int maxAntPickUp = 99000;
        for(int k = 0; k < iterations; k++)
        {
            //System.out.println("Iteration: " + k);
            for(int a = 0; a < numAnts; a++)
            {
                int[] loc = allAnts[a].getLocation();
                if(allAnts[a].isHolding())
                {
                    if(points[loc[0]][loc[1]] == null)
                    {
                        if(allAnts[a].shouldDrop(getNeighborhood(points, loc[0], loc[1])))
                        {
                            points[loc[0]][loc[1]] = allAnts[a].drop();
                        }
                    }
                }
                else
                {
                    if(points[loc[0]][loc[1]] != null && k < maxAntPickUp)
                    {
                        if(allAnts[a].shouldPickUp(points[loc[0]][loc[1]], getNeighborhood(points, loc[0], loc[1])))
                        {
                            if(allAnts[a].pickUp(points[loc[0]][loc[1]]))
                            {
                                points[loc[0]][loc[1]] = null;
                            }
                        }
                    }
                }

                int x = rand.nextInt() % 2;
                int y = rand.nextInt() % 2;
                allAnts[a].move(x, y);
            }
        }

        //System.out.println("dropping");
        for(int k = 0; k < allAnts.length; k++)
        {
            if(allAnts[k].isHolding())
            {
                int[] loc = allAnts[k].getLocation();
                while(points[loc[0]][loc[1]] != null)
                {
                    loc = allAnts[k].getLocation();
                    int x = rand.nextInt() % 2;
                    int y = rand.nextInt() % 2;
                    allAnts[k].move(x, y);
                }
                points[loc[0]][loc[1]] = allAnts[k].drop();
            }
        }

        //System.out.println("creating data");

        double[][] data = new double[dataset.length][2];

        for(int k = 0; k < data.length; k++)
        {
            int[] loc = allPoints[k].getLocation();
            data[k][0] = loc[0];
            data[k][1] = loc[1];
        }

        data = DataTools.normalizeData(data);

        /*for(int k = 0; k < data.length; k++)
        {
            System.out.println(data[k][0] + ", " + data[k][1]);
        }*/

        return new DBScan().run(data);
    }

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
