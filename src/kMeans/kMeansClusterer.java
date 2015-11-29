package kMeans;

import clustering.Cluster;

public class kMeansClusterer implements Cluster
{
    private double minDistance;
    private int numbOfClusters;
    private boolean verbose;

    public kMeansClusterer(double minDistance, int numbOfClusters, boolean verbose)
    {
        this.minDistance = minDistance;
        this.numbOfClusters = numbOfClusters;
        this.verbose = verbose;
    }

    public void println(String msg)
    {
        if (verbose)
        {
            System.out.println(msg);
        }
    }

    public void print(String msg)
    {
        if (verbose)
        {
            System.out.print(msg);
        }
    }

    public int[] run(double[][] inputs)
    {
        println("Starting k-Means Clustering");
        double[][][] clusteredData;
        double distance;
        double[][] clusters = pickInitialClusterPoints(inputs);

        do {
            println("Starting another round of clustering");
            clusteredData = assignInputsToClusters(inputs, clusters);
            double[][] newClusters = pickNewClusters(clusteredData);
            distance = avgClusterMovement(clusters, newClusters);
            clusters = newClusters;
        } while (distance > this.minDistance);

        return findInputsIndex(inputs, clusteredData);
    }

    public int[] findInputsIndex(double[][] inputs, double[][][] clusteredData)
    {
        int[] indexes = new int[inputs.length];

        for (int i = 0; i < clusteredData.length; i++)
        {
            for (int j = 0; j < clusteredData[i].length; j++)
            {
                for (int k = 0; k < inputs.length; k++)
                {
                    if (inputs[k] == clusteredData[i][j])
                    {
                        indexes[k] = i;
                        break;
                    }
                }
            }
        }
        return indexes;
    }


    public double distanceFunc(double[] x1, double[] x2)
    {
        double dist = 0;

        for (int i = 0; i < x1.length; i++)
        {
            dist += (x1[i] - x2[i]) * (x1[i] - x2[i]);
        }

        return Math.sqrt(dist);
    }

    public double[][] pickInitialClusterPoints(double[][] inputs)
    {
        int numbOfClusters = 0;
        int size = this.numbOfClusters;
        double[][] clusters = new double[size][];
        int index = 0;

        while (numbOfClusters < size)
        {
            if (Math.random() < 0.1)
            {
                println("Selecting point: " + (index % inputs.length) + " To be an initial cluster");
                clusters[numbOfClusters] = inputs[index % inputs.length];
                numbOfClusters++;
            }
            index++;
        }

        return clusters;
    }

    public double[][][] assignInputsToClusters(double[][] inputs, double[][] clusters)
    {
        println("Assigning inputs to Clusters");
        double[][][] clusteredData = new double[clusters.length][][];
        int[] inputsClusters = new int[inputs.length];
        int[] numbOfNodesInCluster = new int[clusters.length];
        for (int i = 0; i < inputs.length; i++)
        {
            double closestDist = distanceFunc(inputs[i], clusters[0]);
            int closestIndex = 0;
            for (int j = 1; j < clusters.length; j++)
            {
                double currentDist = distanceFunc(inputs[i], clusters[j]);
                if (currentDist < closestDist)
                {
                    closestDist = currentDist;
                    closestIndex = j;
                }
            }
            println("Assigning input: " + i + " to cluster: " + closestIndex);
            inputsClusters[i] = closestIndex;
            numbOfNodesInCluster[closestIndex]++;
        }

        for (int i = 0; i < clusters.length; i++)
        {
            clusteredData[i] = new double[numbOfNodesInCluster[i]][];
        }

        int[] currentIndex = new int[clusters.length];

        for (int i = 0; i < inputs.length; i++)
        {
            int clusterIndex = inputsClusters[i];
            clusteredData[clusterIndex][currentIndex[clusterIndex]] = inputs[i];
            currentIndex[clusterIndex]++;
        }

        return clusteredData;
    }

    public double[] getClusterCenter(double[][] cluster)
    {
        double[] total = new double[cluster[0].length];
        for (int i = 0; i < cluster.length; i++)
        {
            for (int j = 0; j < cluster[i].length; j++)
            {
                total[j] += cluster[i][j];
            }
        }

        for (int i = 0; i < total.length; i++)
        {
            total[i] /= cluster.length;
        }

        return total;
    }

    public double[][] pickNewClusters(double[][][] clusteredData)
    {
        double[][] newClusters = new double[clusteredData.length][];

        for (int i = 0; i < newClusters.length; i++)
        {
            newClusters[i] = getClusterCenter(clusteredData[i]);
        }

        return newClusters;
    }

    public double avgClusterMovement(double[][] oldClusters, double[][] newClusters)
    {
        double totalDist = 0.0;

        for (int i = 0; i < oldClusters.length; i++)
        {
            totalDist = distanceFunc(oldClusters[i], newClusters[i]);
        }

        return totalDist / oldClusters.length;
    }
}
