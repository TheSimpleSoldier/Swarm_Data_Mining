package clustering;

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
        System.out.println("Starting backprop");
        maxClusters = (int)Math.round(parameters[2]);
        FeedForwardNeuralNetwork net = new FeedForwardNeuralNetwork(1,
                new int[]{examples[0].length, 100, maxClusters}, ActivationFunction.LOGISTIC,
                ActivationFunction.LOGISTIC);
        lastDeltas = new double[net.getWeights().length];
        learningRate = parameters[0];
        momentum = parameters[1];


        int[] sizes = net.getSizes();

        int value = 1 / examples.length;
        for(int i = 0; i < value + 1; i++)
        {
            //for each example
            for(int k = 0; k < examples.length; k++)
            {
                double[] input = new double[sizes[0]];

                //separate input and output
                for(int a = 0; a < input.length; a++)
                {
                    input[a] = examples[k][a];
                }

                //run backprop on it
                backprop(input, net);
            }
        }

        int[] toReturn = new int[examples.length];

        //for each example
        for(int k = 0; k < examples.length; k++)
        {
            double[] input = new double[sizes[0]];

            //separate input and output
            for(int a = 0; a < input.length; a++)
            {
                input[a] = examples[k][a];
            }

            //run backprop on it
            double[] outputs = net.compute(input);

            int cluster = 0;
            double max = 0;
            for(int a = 0; a < maxClusters; a++)
            {
                if(outputs[a] > max)
                {
                    cluster = a;
                    max = outputs[a];
                }
            }

            toReturn[k] = cluster;
        }

        return toReturn;
    }

    /**
     * Given an example with the inputs and expected outputs, trains the network
     * @param inputs the inputs for the example
     * @param net the network to train
     */
    public void backprop(double[] inputs, FeedForwardNeuralNetwork net)
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

        expectedOutputs[cluster] = 1;

        System.out.println(cluster);
        /*for(int k = 0; k < expectedOutputs.length; k++)
        {
            System.out.print(expectedOutputs[k] + ", ");
        }
        System.out.println();*/


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
}
