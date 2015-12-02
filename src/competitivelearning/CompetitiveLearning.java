package competitivelearning;

import clustering.Cluster;
import feedforward.ActivationFunction;
import feedforward.FeedForwardNeuralNetwork;

/**
 * Created by joshua on 10/14/15.
 * This class teaches a given neural network with the backpropagation algorithm.
 */
public class CompetitiveLearning implements Cluster
{
    private double learningRate;
    private double momentum;
    private int maxClusters;
    private double maxIterations;
    private double[] lastDeltas;
    private double[] parameters;
    boolean verbose;

    public CompetitiveLearning(double[] parameters, boolean verbose)
    {
        this.parameters = parameters;
        this.verbose = verbose;
    }

    /**
     * This is the main runner for the algorithm, it loops through the examples,
     * clustering the network on each example
     * @param examples a 2-d array of examples. each example has a list of inputs and
     *                 expected outputs.
     * @return returns a network that is the result of running backpropagation on the examples
     */
    @Override
    public int[] run(double[][] examples)
    {
        //System.out.println("Starting backprop");
        if(verbose)
        {
            System.out.println("---------------------Begin Competitive Learning---------------------");
        }
        maxClusters = (int)Math.round(parameters[2]);
        FeedForwardNeuralNetwork net = new FeedForwardNeuralNetwork(0,
                new int[]{examples[0].length, maxClusters}, ActivationFunction.LINEAR,
                ActivationFunction.LINEAR);
        lastDeltas = new double[net.getWeights().length];
        learningRate = parameters[0];
        momentum = parameters[1];
        maxIterations = parameters[3];

        int[] sizes = net.getSizes();

        int[] last = calculateWinners(examples, net);

        if(verbose)
        {
            System.out.println("Initial weights");
            double[] weights = net.getWeights();
            for(int k = 0; k < weights.length; k++)
            {
                System.out.print(weights[k] + ", ");
            }
            System.out.println("Initial labels");
            for(int k = 0; k < maxClusters; k++)
            {
                System.out.print("Cluster " + k + ": ");
                for(int a = 0; a < last.length; a++)
                {
                    if(last[a] == k)
                    {
                        System.out.print(a + ", ");
                    }
                }
                System.out.println();
            }
            System.out.println();
        }

        int index = 0;
        for(int i = 1; i < maxIterations; i++)
        {
            double[] input = new double[sizes[0]];

            //separate input and output
            for(int a = 0; a < input.length; a++)
            {
                input[a] = examples[index][a];
            }
            index = (index + 1) % examples.length;

            //run backprop on it
            if(i % 1000 == 0 && verbose)
            {
                System.out.println("Iteration: " + i);
            }
            if(verbose)
            {
                backprop(input, net, i % 1000 == 0);
            }
            else
            {
                backprop(input, net, false);
            }

            if(i % 1000 == 0)
            {
                int[] temp = calculateWinners(examples, net);
                if(verbose)
                {
                    //System.out.println("Iteration: " + i);
                    /*System.out.println("Current labels");
                    for(int k = 0; k < last.length; k++)
                    {
                        System.out.println(k + ": " + temp[k]);
                    }
                    System.out.println();*/
                }
                boolean same = true;
                for(int j = 0; j < temp.length; j++)
                {
                    if(last != temp)
                    {
                        same = false;
                        break;
                    }
                }

                if(same)
                {
                    if(verbose)
                    {
                        System.out.println("Final weights");
                        double[] weights = net.getWeights();
                        for(int k = 0; k < weights.length; k++)
                        {
                            System.out.print(weights[k] + ", ");
                        }
                        System.out.println("Final labels");
                        for(int k = 0; k < maxClusters; k++)
                        {
                            System.out.print("Cluster " + k + ": ");
                            for(int a = 0; a < last.length; a++)
                            {
                                if(last[a] == k)
                                {
                                    System.out.print(a + ", ");
                                }
                            }
                            System.out.println();
                        }
                        System.out.println();
                        System.out.println("----------------------End Competitive Learning----------------------");
                    }
                    return temp;
                }
                else
                {
                    last = temp;
                }
            }
        }
        if(verbose)
        {
            System.out.println("Final weights");
            double[] weights = net.getWeights();
            for(int k = 0; k < weights.length; k++)
            {
                System.out.print(weights[k] + ", ");
            }
            System.out.println("Final labels");
            for(int k = 0; k < maxClusters; k++)
            {
                System.out.print("Cluster " + k + ": ");
                for(int a = 0; a < last.length; a++)
                {
                    if(last[a] == k)
                    {
                        System.out.print(a + ", ");
                    }
                }
                System.out.println();
            }
            System.out.println();
            System.out.println("----------------------End Competitive Learning----------------------");
        }

        return calculateWinners(examples, net);
    }

    /**
     * Given an example with the inputs and expected outputs, trains the network
     * @param inputs the inputs for the example
     * @param net the network to train
     */
    public void backprop(double[] inputs, FeedForwardNeuralNetwork net, boolean verbose)
    {
        //create variables that will be used later
        int[] sizes = net.getSizes();
        int biggestSize = 0;
        for(int k = 0; k < sizes.length; k++)
        {
            if(sizes[k] > biggestSize)
            {
                biggestSize = sizes[k];
            }
        }
        int hiddenLayers = sizes.length - 2;
        //if input or output wrong size, return
        if(inputs.length != sizes[0])
        {
            System.out.println("Invalid number of inputs");
            return;
        }

        double[][] allOutputs = new double[sizes.length][biggestSize];
        double[][] allErrors = new double[sizes.length][biggestSize];

        //fill out first layer to temp output
        int lastLayer = sizes[0];
        for(int k = 0; k < lastLayer; k++)
        {
            allOutputs[0][k] = inputs[k];
        }

        //for each layer after the input
        for(int k = 1; k < hiddenLayers + 2; k++)
        {
            //for each node in that layer
            for(int a = 0; a < sizes[k]; a++)
            {
                //get sum and get activation function result and its derivative
                double sum = 0;
                for(int t = 0; t < lastLayer; t++)
                {
                    sum += allOutputs[k - 1][t] * net.getWeight(k - 1, t, k, a);
                }
                sum += net.getBiasNum() * net.getWeight(-1, 0, k, a);
                if(k != hiddenLayers + 1)
                {
                    allOutputs[k][a] = net.applyActivationFunction(sum, net.getHiddenActivationFunction());
                    allErrors[k][a] = net.applyActivationFunctionDerivative(sum, net.getHiddenActivationFunction());
                }
                else
                {
                    allOutputs[k][a] = net.applyActivationFunction(sum, net.getOutputActivationFunction());
                    allErrors[k][a] = net.applyActivationFunctionDerivative(sum, net.getOutputActivationFunction());
                }
            }
            lastLayer = sizes[k];
        }

        if(verbose)
        {
            System.out.println("Outputs");
            for(int k = 0; k < maxClusters; k++)
            {
                System.out.print(allOutputs[hiddenLayers + 1][k] + ", ");
            }
            System.out.println();
        }
        double[] expectedOutputs = new double[maxClusters];
        int cluster = 0;
        double max = 0;
        for(int k = 0; k < maxClusters; k++)
        {
            expectedOutputs[k] = allOutputs[hiddenLayers + 1][k];
            if(allOutputs[hiddenLayers + 1][k] > max)
            {
                cluster = k;
                max = allOutputs[hiddenLayers + 1][k];
            }
        }
        if(verbose)
        {
            System.out.println("Output " + cluster + " will be set to max value");
            System.out.println();
        }

        expectedOutputs[cluster] = 4;


        //go backward from output to first hidden layer
        for(int k = hiddenLayers + 1; k > 0; k--)
        {
            //for each node in that layer
            for(int a = 0; a < sizes[k]; a++)
            {
                //compute error for not output layer
                if(k != hiddenLayers + 1)
                {
                    double temp = allErrors[k][a];
                    allErrors[k][a] = 0;
                    for(int t = 0; t < sizes[k + 1]; t++)
                    {
                        allErrors[k][a] += net.getWeight(k, t, k + 1, a) * allErrors[k + 1][t];
                    }
                    allErrors[k][a] *= temp;
                }
                //compute error for output layer
                else
                {
                    allErrors[k][a] *= (expectedOutputs[a] - allOutputs[k][a]);
                }

                //for each weight node takes as input
                for(int t = 0; t < sizes[k - 1]; t++)
                {
                    //find the delta for the weight and apply
                    int index = net.getIndex(k - 1, t, k, a);
                    double delta = learningRate * allOutputs[k - 1][t] * allErrors[k][a]
                            + momentum * lastDeltas[index];

                    net.setWeight(k - 1, t, k, a, net.getWeight(k - 1, t, k, a) + delta);
                    lastDeltas[index] = delta;
                }
            }
        }
    }

    private int[] calculateWinners(double[][] examples, FeedForwardNeuralNetwork net)
    {
        int[] outputs = new int[examples.length];
        for(int k = 0; k < examples.length; k++)
        {
            double[] input = new double[net.getSizes()[0]];

            for(int a = 0; a < input.length; a++)
            {
                input[a] = examples[k][a];
            }
            double[] output = net.compute(input);

            int max = -1;
            double maxValue = -1;
            for(int a = 0; a < output.length; a++)
            {
                if(output[a] > maxValue)
                {
                    max = a;
                    maxValue = output[a];
                }
            }

            outputs[k] = max;
        }

        return outputs;
    }
}
