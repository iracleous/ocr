package com.qued.ocr.ann;


public class KohonenNetwork extends Network
{

    double outputWeights[][];
    protected int learnMethod;
    protected double learnRate;
    protected double quitError;
    protected int retries;
    protected double reduction;
    protected NeuralReportable owner;
    public boolean halt;
    protected TrainingSet train;






    public KohonenNetwork(int inputCount, int outputCount, NeuralReportable owner)
    {
        super();
        learnMethod = 1;
        learnRate = 0.5D;
        quitError = 0.10000000000000001D;
        retries = 10000;
        reduction = 0.98999999999999999D;
        halt = false;
        totalError = 1.0D;
        inputNeuronCount = inputCount;
        outputNeuronCount = outputCount;
        outputWeights = new double[outputNeuronCount][inputNeuronCount + 1];
        output = new double[outputNeuronCount];
        this.owner = owner;
    }

    public void setTrainingSet(TrainingSet set)
    {
        train = set;
    }

    public static void copyWeights(KohonenNetwork dest, KohonenNetwork source)
    {
        for(int i = 0; i < source.outputWeights.length; i++)
            System.arraycopy(source.outputWeights[i], 0, dest.outputWeights[i], 0, source.outputWeights[i].length);

    }

    public void clearWeights()
    {
        totalError = 1.0D;
        for(int y = 0; y < outputWeights.length; y++)
        {
            for(int x = 0; x < outputWeights[0].length; x++)
                outputWeights[y][x] = 0.0D;

        }

    }

    private void normalizeInput(double input[], double normfac[], double synth[])
    {
        double length = vectorLength(input);
        if(length < 1.0000000000000001E-030D)
            length = 1.0000000000000001E-030D;
        normfac[0] = 1.0D / Math.sqrt(length);
        synth[0] = 0.0D;
    }

    private void normalizeWeight(double w[])
    {
        double len = vectorLength(w);
        if(len < 1.0000000000000001E-030D)
            len = 1.0000000000000001E-030D;
        len = 1.0D / Math.sqrt(len);
        for(int i = 0; i < inputNeuronCount; i++)
            w[i] *= len;

        w[inputNeuronCount] = 0.0D;
    }

    @Override
    public void trial(double input[])
    {
        double normfac[] = new double[1];
        double synth[] = new double[1];
        normalizeInput(input, normfac, synth);
        for(int i = 0; i < outputNeuronCount; i++)
        {
            double optr[] = outputWeights[i];
            output[i] = dotProduct(input, optr) * normfac[0] + synth[0] * optr[inputNeuronCount];
            output[i] = 0.5D * (output[i] + 1.0D);
            if(output[i] > 1.0D)
                output[i] = 1.0D;
            if(output[i] < 0.0D)
                output[i] = 0.0D;
        }

    }

    public int winner(double input[], double normfac[], double synth[])
    {
        int win = 0;
        normalizeInput(input, normfac, synth);
        double biggest = -1E+030D;
        for(int i = 0; i < outputNeuronCount; i++)
        {
            double optr[] = outputWeights[i];
            output[i] = dotProduct(input, optr) * normfac[0] + synth[0] * optr[inputNeuronCount];
            output[i] = 0.5D * (output[i] + 1.0D);
            if(output[i] > biggest)
            {
                biggest = output[i];
                win = i;
            }
            if(output[i] > 1.0D)
                output[i] = 1.0D;
            if(output[i] < 0.0D)
                output[i] = 0.0D;
        }

        return win;
    }

    private void evaluateErrors(double rate, int learn_method, int won[], double bigerr[], double correc[][], double work[])
        throws RuntimeException
    {
        double normfac[] = new double[1];
        double synth[] = new double[1];
        for(int y = 0; y < correc.length; y++)
        {
            for(int x = 0; x < correc[0].length; x++)
                correc[y][x] = 0.0D;

        }

        for(int i = 0; i < won.length; i++)
            won[i] = 0;

        bigerr[0] = 0.0D;
        for(int tset = 0; tset < train.getTrainingSetCount(); tset++)
        {
            double dptr[] = train.getInputSet(tset);
            int best = winner(dptr, normfac, synth);
            won[best]++;
            double wptr[] = outputWeights[best];
            double cptr[] = correc[best];
            double length = 0.0D;
            double diff;
            for(int i = 0; i < inputNeuronCount; i++)
            {
                diff = dptr[i] * normfac[0] - wptr[i];
                length += diff * diff;
                if(learn_method != 0)
                    cptr[i] += diff;
                else
                    work[i] = rate * dptr[i] * normfac[0] + wptr[i];
            }

            diff = synth[0] - wptr[inputNeuronCount];
            length += diff * diff;
            if(learn_method != 0)
                cptr[inputNeuronCount] += diff;
            else
                work[inputNeuronCount] = rate * synth[0] + wptr[inputNeuronCount];
            if(length > bigerr[0])
                bigerr[0] = length;
            if(learn_method != 0)
                continue;
            normalizeWeight(work);
            for(int i = 0; i <= inputNeuronCount; i++)
                cptr[i] += work[i] - wptr[i];

        }

        bigerr[0] = Math.sqrt(bigerr[0]);
    }

    private void adjustWeights(double rate, int learn_method, int won[], double bigcorr[], double correc[][])
    {
        bigcorr[0] = 0.0D;
        for(int i = 0; i < outputNeuronCount; i++)
        {
            if(won[i] == 0)
                continue;
            double wptr[] = outputWeights[i];
            double cptr[] = correc[i];
            double f = 1.0D / (double)won[i];
            if(learn_method != 0)
                f *= rate;
            double length = 0.0D;
            for(int j = 0; j <= inputNeuronCount; j++)
            {
                double corr = f * cptr[j];
                wptr[j] += corr;
                length += corr * corr;
            }

            if(length > bigcorr[0])
                bigcorr[0] = length;
        }

        bigcorr[0] = Math.sqrt(bigcorr[0]) / rate;
    }

    private void forceWin(int won[])
        throws RuntimeException
    {
        int which = 0;
        double normfac[] = new double[1];
        double synth[] = new double[1];
        int size = inputNeuronCount + 1;
        double dist = 1E+030D;
        int best;
        double dptr[];
        for(int tset = 0; tset < train.getTrainingSetCount(); tset++)
        {
            dptr = train.getInputSet(tset);
            best = winner(dptr, normfac, synth);
            if(output[best] < dist)
            {
                dist = output[best];
                which = tset;
            }
        }

        dptr = train.getInputSet(which);
        best = winner(dptr, normfac, synth);
        dist = -1E+030D;
        int i = outputNeuronCount;
        do
        {
            if(i-- <= 0)
                break;
            if(won[i] == 0 && output[i] > dist)
            {
                dist = output[i];
                which = i;
            }
        } while(true);
        double optr[] = outputWeights[which];
        System.arraycopy(dptr, 0, optr, 0, dptr.length);
        optr[inputNeuronCount] = synth[0] / normfac[0];
        normalizeWeight(optr);
    }

    @Override
    public void learn()
        throws RuntimeException
    {
        double bigerr[] = new double[1];
        double bigcorr[] = new double[1];
        totalError = 1.0D;
        for(int tset = 0; tset < train.getTrainingSetCount(); tset++)
        {
            double dptr[] = train.getInputSet(tset);
            if(vectorLength(dptr) < 1.0000000000000001E-030D)
                throw new RuntimeException("Multiplicative normalization has null training case");
        }

        KohonenNetwork bestnet = new KohonenNetwork(inputNeuronCount, outputNeuronCount, owner);
        int won[] = new int[outputNeuronCount];
        double correc[][] = new double[outputNeuronCount][inputNeuronCount + 1];
        double work[];
        if(learnMethod == 0)
            work = new double[inputNeuronCount + 1];
        else
            work = null;
        double rate = learnRate;
        initialize();
        double best_err = 1E+030D;
        int n_retry = 0;
        int iter = 0;
        do
        {
            evaluateErrors(rate, learnMethod, won, bigerr, correc, work);
            totalError = bigerr[0];
            if(totalError < best_err)
            {
                best_err = totalError;
                copyWeights(bestnet, this);
            }
            int winners = 0;
            for(int i = 0; i < won.length; i++)
                if(won[i] != 0)
                    winners++;

            if(bigerr[0] < quitError)
                break;
            if(winners < outputNeuronCount && winners < train.getTrainingSetCount())
            {
                forceWin(won);
            } else
            {
                adjustWeights(rate, learnMethod, won, bigcorr, correc);
                owner.update(n_retry, totalError, best_err);
                if(halt)
                {
                    owner.update(n_retry, totalError, best_err);
                    break;
                }
                Thread.yield();
                if(bigcorr[0] < 1.0000000000000001E-005D)
                {
                    if(++n_retry > retries)
                        break;
                    initialize();
                    iter = -1;
                    rate = learnRate;
                } else
                if(rate > 0.01D)
                    rate *= reduction;
            }
            iter++;
        } while(true);
        copyWeights(this, bestnet);
        for(int i = 0; i < outputNeuronCount; i++)
            normalizeWeight(outputWeights[i]);

        halt = true;
        n_retry++;
        owner.update(n_retry, totalError, best_err);
    }

    public void initialize()
    {
        clearWeights();
        randomizeWeights(outputWeights);
        for(int i = 0; i < outputNeuronCount; i++)
        {
            double optr[] = outputWeights[i];
            normalizeWeight(optr);
        }

    }


}
