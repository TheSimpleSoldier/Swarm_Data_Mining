package PSO;

import clustering.Cluster;

public class PSO implements Cluster
{
    private double localMax;
    private double globalMax;
    private double randomMax;
    private int numbOfUpdates;
    private boolean verbose;
    private double momentum;
    private Particle[] swarm;
    private int numbOfCentroids;

    public PSO(double localMax, double globalMax, double randomMax, int numbOfUpdates, boolean verbose, double momentum, int numbOfCentroids)
    {
        this.localMax = localMax;
        this.globalMax = globalMax;
        this.randomMax = randomMax;
        this.numbOfUpdates = numbOfUpdates;
        this.verbose = verbose;
        this.momentum = momentum;
        this.numbOfCentroids = numbOfCentroids;
    }

    public void println(String msg)
    {
        if (this.verbose)
        {
            System.out.println(msg);
        }
    }

    public void print(String msg)
    {
        if (this.verbose)
        {
            System.out.print(msg);
        }
    }


    public int[] run(double[][] inputs)
    {
        println("Starting PSO");
        int[] assignments = new int[inputs.length];

        swarm = swarmInitialization(inputs);
        double[] globalBest = new double[inputs[0].length];
        double globalBestScore = 99999999;


        for (int i = 0; i < numbOfUpdates; i++)
        {
            println("Running PSO generation: " + i);

            // assign all inputs to a cluster point
            for (int j = 0; j < inputs.length; j++)
            {
                double shortestDist = 99999999;
                int shortestIndex = -1;
                for (int k = 0; k < swarm.length; k++)
                {
                    double currentDist = swarm[k].getDistanceTo(inputs[j]);

                    if (currentDist < shortestDist)
                    {
                        shortestDist = currentDist;
                        shortestIndex = k;
                    }
                }

                swarm[shortestIndex].addInput(inputs[j]);
            }

            boolean updatedGlobalBest = false;
            // get scores for all of the clusters to update global best
            for (int j = 0; j < swarm.length; j++)
            {
                double score = swarm[j].getScore();
                if (score < globalBestScore)
                {
                    println("Updated global best score to: " + score);
                    globalBestScore = score;
                    globalBest = swarm[j].getLocation();
                    updatedGlobalBest = true;
                }
            }

            if (updatedGlobalBest)
            {
                println("Giving particles new global best");
                // give the global best to the entire swarm
                for (int j = 0; j < swarm.length; j++)
                {
                    swarm[j].updateGlobalBest(globalBest);
                }
            }


            println("Updating particle positions");
            // have all of the particles update
            for (int j = 0; j < swarm.length; j++)
            {
                swarm[j].updatePosition();
                swarm[j].update();
            }

        }

        println("");
        // find cluster indexs for all points
        for (int j = 0; j < inputs.length; j++)
        {
            double shortestDist = 99999999;
            int shortestIndex = -1;
            for (int k = 0; k < swarm.length; k++)
            {
                double currentDist = swarm[k].getDistanceTo(inputs[j]);

                if (currentDist < shortestDist)
                {
                    shortestDist = currentDist;
                    shortestIndex = k;
                }
            }

            assignments[j] = shortestIndex;
            print("" + shortestIndex + ", ");
        }
        println("");

        return assignments;
    }

    public Particle[] swarmInitialization(double[][] inputs)
    {
        Particle[] newSwarm = new Particle[numbOfCentroids];

        for (int i = 0; i < this.numbOfCentroids; i++)
        {
            int index = (int) (Math.random() * inputs.length);
            newSwarm[i] = new Particle(inputs[index], this.localMax, this.globalMax, this.randomMax, this.momentum);
        }

        return newSwarm;
    }

    private class Particle
    {
        private double localMax;
        private double globalMax;
        private double randomMax;
        private double currentScore;
        private double[] globalBest;
        private double[] localBest;
        private int numbOfMembers;
        private double[] location;
        private double momentum;
        private double[] currentVelocity;
        private double bestLocalScore;

        public Particle(double[] startingLocation, double localMax, double globalMax, double randomMax, double momentum)
        {
            this.location = startingLocation;
            this.localMax = localMax;
            this.globalMax = globalMax;
            this.randomMax = randomMax;
            this.momentum = momentum;
            this.initialize();
        }

        public void initialize()
        {
            this.currentVelocity = new double[this.location.length];
            this.localBest = new double[this.location.length];
            this.bestLocalScore = 999999999;

            for (int i = 0; i < this.currentVelocity.length; i++)
            {
                this.currentVelocity[i] = 0.0;
                this.localBest[i] = this.location[i];
            }
        }

        public void update()
        {
            double currentScore = getScore();

            if (currentScore < this.bestLocalScore)
            {
                this.bestLocalScore = currentScore;
                this.localBest = this.location;
            }
        }

        public void updateGlobalBest(double[] globalBest)
        {
            this.globalBest = globalBest;
        }

        public double getScore()
        {
            if (numbOfMembers == 0)
            {
                return 9999999;
            }
            return currentScore / numbOfMembers;
        }

        public void addInput(double[] input)
        {
            numbOfMembers++;

            currentScore += distanceFunction(input, location);
        }

        public double distanceFunction(double[] x1, double[] x2)
        {
            double dist = 0;

            for (int i = 0; i < x1.length; i++)
            {
                dist += (x1[i] - x2[i]) * (x1[i] - x2[i]);
            }

            return Math.sqrt(dist);
        }

        public void updatePosition()
        {
            numbOfMembers = 0;
            currentScore = 0;

            for (int i = 0; i < location.length; i++)
            {
                currentVelocity[i] = momentum * currentVelocity[i] + Math.random() * globalMax * pointDiff(location[i], globalBest[i]) + Math.random() * localMax * pointDiff(location[i], localBest[i]);
                location[i] += currentVelocity[i];
            }
        }

        public double getDistanceTo(double[] x1)
        {
            return distanceFunction(x1, this.location);
        }

        public double pointDiff(double x1, double x2)
        {
            return x1 - x2;
        }

        public double[] getLocation()
        {
            return this.location;
        }
    }
}
