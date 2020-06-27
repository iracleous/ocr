package com.qued.ocr.ann;

public class TrainingSet
{

    protected int inputCount;
    protected int outputCount;
    protected double input[][];
    protected double output[][];
    protected int trainingSetCount;

    public TrainingSet(int inputCount, int outputCount)
    {
        this.inputCount = inputCount;
        this.outputCount = outputCount;
        trainingSetCount = 0;
    }

    public int getInputCount()
    {
        return inputCount;
    }

    public int getOutputCount()
    {
        return outputCount;
    }

    public void setTrainingSetCount(int trainingSetCount)
    {
        this.trainingSetCount = trainingSetCount;
        input = new double[trainingSetCount][inputCount];
        output = new double[trainingSetCount][outputCount];
    }

    public int getTrainingSetCount()
    {
        return trainingSetCount;
    }

    public void setInput(int set, int index, double value)
        throws RuntimeException
    {
        if(set < 0 || set >= trainingSetCount)
            throw new RuntimeException((new StringBuilder()).append("Training set out of range:").append(set).toString());
        if(index < 0 || index >= inputCount)
        {
            throw new RuntimeException((new StringBuilder()).append("Training input index out of range:").append(index).toString());
        } else
        {
            input[set][index] = value;
            return;
        }
    }

    public void setOutput(int set, int index, double value)
        throws RuntimeException
    {
        if(set < 0 || set >= trainingSetCount)
            throw new RuntimeException((new StringBuilder()).append("Training set out of range:").append(set).toString());
        if(index < 0 || set >= outputCount)
        {
            throw new RuntimeException((new StringBuilder()).append("Training input index out of range:").append(index).toString());
        } else
        {
            output[set][index] = value;
            return;
        }
    }

    public double getInput(int set, int index)
        throws RuntimeException
    {
        if(set < 0 || set >= trainingSetCount)
            throw new RuntimeException((new StringBuilder()).append("Training set out of range:").append(set).toString());
        if(index < 0 || index >= inputCount)
            throw new RuntimeException((new StringBuilder()).append("Training input index out of range:").append(index).toString());
        else
            return input[set][index];
    }

    public double getOutput(int set, int index)
        throws RuntimeException
    {
        if(set < 0 || set >= trainingSetCount)
            throw new RuntimeException((new StringBuilder()).append("Training set out of range:").append(set).toString());
        if(index < 0 || set >= outputCount)
            throw new RuntimeException((new StringBuilder()).append("Training input index out of range:").append(index).toString());
        else
            return output[set][index];
    }

    public double[] getOutputSet(int set)
        throws RuntimeException
    {
        if(set < 0 || set >= trainingSetCount)
            throw new RuntimeException((new StringBuilder()).append("Training set out of range:").append(set).toString());
        else
            return output[set];
    }

    public double[] getInputSet(int set)
        throws RuntimeException
    {
        if(set < 0 || set >= trainingSetCount)
            throw new RuntimeException((new StringBuilder()).append("Training set out of range:").append(set).toString());
        else
            return input[set];
    }


}
