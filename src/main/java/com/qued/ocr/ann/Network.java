package com.qued.ocr.ann;

import java.util.Random;

public abstract class Network
{

    public static final double NEURON_ON = 0.90000000000000002D;
    public static final double NEURON_OFF = 0.10000000000000001D;
    protected double output[];
    protected double totalError;
    protected int inputNeuronCount;
    protected int outputNeuronCount;
    protected Random random;


    public Network()
    {
        random = new Random(System.currentTimeMillis());
    }

    public static double vectorLength(double v[])
    {
        double rtn = 0.0D;
        for(int i = 0; i < v.length; i++)
            rtn += v[i] * v[i];

        return rtn;
    }


    public abstract void learn()
        throws RuntimeException;

    public abstract void trial(double ad[]);

    public double[] getOutput()
    {
        return output;
    }

    public double dotProduct(double vec1[], double vec2[])
    {
        double rtn = 0.0D;
        int k = vec1.length;
        for(int v = 0; k-- > 0; v++)
            rtn += vec1[v] * vec2[v];

        return rtn;
    }

    public void randomizeWeights(double weight[][])
    {
        int temp = (int)(3.4641016150000001D / (2D * Math.random()));
        for(int y = 0; y < weight.length; y++)
        {
            for(int x = 0; x < weight[0].length; x++)
            {
                double r = ((double)random.nextInt(0x7fffffff) + (double)random.nextInt(0x7fffffff)) - (double)random.nextInt(0x7fffffff) - (double)random.nextInt(0x7fffffff);
                weight[y][x] = (double)temp * r;
            }

        }

    }


}
