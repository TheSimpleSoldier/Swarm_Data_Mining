package kMeans;

public class kMeansClusterer
{
    private double minDistance;
    private int numbOfClusters;

    public kMeansClusterer(double minDistance, int numbOfClusters)
    {
        this.minDistance = minDistance;
        this.numbOfClusters = numbOfClusters;
    }

    public static void main(String[] args)
    {
        double[] array = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18};

        kMeansClusterer kmeansClusterer = new kMeansClusterer(1,2);

        System.out.println(kmeansClusterer.run(array));
    }

    public double[][] run(double[] inputs)
    {
        double[][] clusteredData;
        double distance;
        double[] clusters = pickInitialClusterPoints(inputs);

        do {
            clusteredData = assignInputsToClusters(inputs, clusters);
            double[] newClusters = pickNewClusters(clusteredData);
            distance = avgClusterMovement(clusters, newClusters);
            clusters = newClusters;
        } while (distance > this.minDistance);


        return clusteredData;
    }


    public double distanceFunc(double x1, double x2)
    {
        return Math.abs(x1 - x2);
    }

    public double[] pickInitialClusterPoints(double[] inputs)
    {
        int numbOfClusters = 0;
        int size = this.numbOfClusters;
        double[] clusters = new double[size];
        int index = 0;

        while (numbOfClusters < size)
        {
            if (Math.random() < 1/inputs.length)
            {
                clusters[numbOfClusters] = inputs[index % inputs.length];
            }
        }

        return clusters;
    }

    public double[][] assignInputsToClusters(double[] inputs, double[] clusters)
    {
        double[][] clusteredData = new double[clusters.length][];
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
            inputsClusters[i] = closestIndex;
            numbOfNodesInCluster[closestIndex]++;
        }

        for (int i = 0; i < clusters.length; i++)
        {
            clusteredData[i] = new double[numbOfNodesInCluster[i]];
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

    public double getClusterCenter(double[] cluster)
    {
        double total = 0.0;
        for (int i = 0; i < cluster.length; i++)
        {
            total += cluster[i];
        }

        return total/cluster.length;
    }

    public double[] pickNewClusters(double[][] clusteredData)
    {
        double[] newCluster = new double[clusteredData.length];

        for (int i = 0; i < newCluster.length; i++)
        {
            newCluster[i] = getClusterCenter(clusteredData[i]);
        }

        return newCluster;
    }

    public double avgClusterMovement(double[] oldClusters, double[] newClusters)
    {
        double totalDist = 0.0;

        for (int i = 0; i < oldClusters.length; i++)
        {
            totalDist = distanceFunc(oldClusters[i], newClusters[i]);
        }

        return totalDist / oldClusters.length;

    }

}
