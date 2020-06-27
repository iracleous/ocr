package com.qued.ocr;
import com.qued.ocr.ann.Entry;
import com.qued.ocr.ann.KohonenNetwork;
import com.qued.ocr.ann.NeuralReportable;
import com.qued.ocr.ann.TrainingSet;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class MainEntry extends JFrame implements Runnable, NeuralReportable
{
    static final int DOWNSAMPLE_WIDTH = 5;
    static final int DOWNSAMPLE_HEIGHT = 7;
    private Entry entry;
    private Sample sample;
    private DefaultListModel letterListModel;
    private KohonenNetwork net;
    private Thread trainThread;
    private JLabel JLabel1;
    private JLabel JLabel2;
    private JButton downSample;
    private JButton add;
    private JButton clear;
    private JButton recognize;
    private JScrollPane JScrollPane1;
    private JList letters;
    private JButton del;
    private JButton load;
    private JButton save;
    private JButton train;
    private JLabel JLabel3;
    private JLabel JLabel4;
    private JLabel tries;
    private JLabel lastError;
    private JLabel bestError;
    private JLabel JLabel8;
    private JLabel JLabel5;



    public MainEntry()
    {
        letterListModel = new DefaultListModel();
        trainThread = null;
        JLabel1 = new JLabel();
        JLabel2 = new JLabel();
        downSample = new JButton();
        add = new JButton();
        clear = new JButton();
        recognize = new JButton();
        JScrollPane1 = new JScrollPane();
        letters = new JList();
        del = new JButton();
        load = new JButton();
        save = new JButton();
        train = new JButton();
        JLabel3 = new JLabel();
        JLabel4 = new JLabel();
        tries = new JLabel();
        lastError = new JLabel();
        bestError = new JLabel();
        JLabel8 = new JLabel();
        JLabel5 = new JLabel();
        getContentPane().setLayout(null);
        entry = new Entry();
        entry.setBounds(168, 25, 200, 128);
        getContentPane().add(entry);
        sample = new Sample(5, 7);
        sample.setBounds(307, 210, 65, 70);
        entry.setSample(sample);
        getContentPane().add(sample);
        setTitle("Java Neural Network");
        getContentPane().setLayout(null);
        setSize(405, 382);
        setVisible(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JLabel1.setText("Letters Known");
        getContentPane().add(JLabel1);
        JLabel1.setBounds(12, 12, 84, 12);
        JLabel2.setText("Tries:");
        getContentPane().add(JLabel2);
        JLabel2.setBounds(12, 264, 72, 24);
        downSample.setText("Down Sample");
        downSample.setActionCommand("Down Sample");
        getContentPane().add(downSample);
        downSample.setBounds(252, 180, 120, 24);
        add.setText("Add");
        add.setActionCommand("Add");
        getContentPane().add(add);
        add.setBounds(168, 156, 84, 24);
        clear.setText("Clear");
        clear.setActionCommand("Clear");
        getContentPane().add(clear);
        clear.setBounds(168, 180, 84, 24);
        recognize.setText("Recognize");
        recognize.setActionCommand("Recognize");
        getContentPane().add(recognize);
        recognize.setBounds(252, 156, 120, 24);
        JScrollPane1.setVerticalScrollBarPolicy(22);
        JScrollPane1.setOpaque(true);
        getContentPane().add(JScrollPane1);
        JScrollPane1.setBounds(12, 24, 144, 132);
        JScrollPane1.getViewport().add(letters);
        letters.setBounds(0, 0, 126, 129);
        del.setText("Delete");
        del.setActionCommand("Delete");
        getContentPane().add(del);
        del.setBounds(12, 156, 144, 24);
        load.setText("Load");
        load.setActionCommand("Load");
        getContentPane().add(load);
        load.setBounds(12, 180, 72, 24);
        save.setText("Save");
        save.setActionCommand("Save");
        getContentPane().add(save);
        save.setBounds(84, 180, 72, 24);
        train.setText("Begin Training");
        train.setActionCommand("Begin Training");
        getContentPane().add(train);
        train.setBounds(12, 204, 144, 24);
        JLabel3.setText("Last Error:");
        getContentPane().add(JLabel3);
        JLabel3.setBounds(12, 288, 72, 24);
        JLabel4.setText("Best Error:");
        getContentPane().add(JLabel4);
        JLabel4.setBounds(12, 312, 72, 24);
        tries.setText("0");
        getContentPane().add(tries);
        tries.setBounds(96, 264, 72, 24);
        lastError.setText("0");
        getContentPane().add(lastError);
        lastError.setBounds(96, 288, 72, 24);
        bestError.setText("0");
        getContentPane().add(bestError);
        bestError.setBounds(96, 312, 72, 24);
        JLabel8.setHorizontalTextPosition(0);
        JLabel8.setHorizontalAlignment(0);
        JLabel8.setText("Training Results");
        getContentPane().add(JLabel8);
        JLabel8.setFont(new Font("Dialog", 1, 14));
        JLabel8.setBounds(12, 240, 120, 24);
        JLabel5.setText("Draw Letters Here");
        getContentPane().add(JLabel5);
        JLabel5.setBounds(204, 12, 144, 12);
        SymAction lSymAction = new SymAction();
        downSample.addActionListener(lSymAction);
        clear.addActionListener(lSymAction);
        add.addActionListener(lSymAction);
        del.addActionListener(lSymAction);
        SymListSelection lSymListSelection = new SymListSelection();
        letters.addListSelectionListener(lSymListSelection);
        load.addActionListener(lSymAction);
        save.addActionListener(lSymAction);
        train.addActionListener(lSymAction);
        recognize.addActionListener(lSymAction);
        letters.setModel(letterListModel);
    }





    public static void main(String args[])
    {
        (new MainEntry()).setVisible(true);
    }




//**1
    class UpdateStats  implements Runnable
    {
        long _tries;
        double _lastError;
        double _bestError;
        final MainEntry this$0;
        public UpdateStats()
        {
            super();
            this$0 = MainEntry.this;
        }
        public void run()
        {
            tries.setText((new StringBuilder()).append("").append(_tries).toString());
            lastError.setText((new StringBuilder()).append("").append(_lastError).toString());
            bestError.setText((new StringBuilder()).append("").append(_bestError).toString());
        }
    }

    //**2
    class SymListSelection   implements ListSelectionListener
    {
        final MainEntry this$0;
        public SymListSelection()
        {
            super();
            this$0 = MainEntry.this;
        }

        public void valueChanged(ListSelectionEvent event)
        {
            Object object = event.getSource();
            if(object == letters)
                letters_valueChanged(event);
        }
    }
    //**3
    class SymAction  implements ActionListener
    {
        final MainEntry this$0;
        public SymAction()
        {
            super();
            this$0 = MainEntry.this;
        }
        public void actionPerformed(ActionEvent event)
        {
            Object object = event.getSource();
            if(object == downSample)
                downSample_actionPerformed(event);
            else
            if(object == clear)
                clear_actionPerformed(event);
            else
            if(object == add)
                add_actionPerformed(event);
            else
            if(object == del)
                del_actionPerformed(event);
            else
            if(object == load)
                load_actionPerformed(event);
            else
            if(object == save)
                save_actionPerformed(event);
            else
            if(object == train)
                train_actionPerformed(event);
            else
            if(object == recognize)
                recognize_actionPerformed(event);
        }
    }


    public void downSample_actionPerformed(ActionEvent event)
    {
        entry.downSample();
    }

    public void clear_actionPerformed(ActionEvent event)
    {
        entry.clear();
        sample.getData().clear();
        sample.repaint();
    }

    public void add_actionPerformed(ActionEvent event)
    {
        String letter = JOptionPane.showInputDialog("Please enter a letter you would like to assign this sample to.");
        if(letter == null)
            return;
        if(letter.length() > 1)
        {
            JOptionPane.showMessageDialog(this, "Please enter only a single letter.", "Error", 0);
            return;
        }
        entry.downSample();
        SampleData sampleData = (SampleData)sample.getData().clone();
        sampleData.setLetter(letter.charAt(0));
        int i;
        for(i = 0; i < letterListModel.size(); i++)
        {
            Comparable str = (Comparable)letterListModel.getElementAt(i);
            if(str.equals(letter))
            {
                JOptionPane.showMessageDialog(this, "That letter is already defined, delete it first!", "Error", 0);
                return;
            }
            if(str.compareTo(sampleData) > 0)
            {
                letterListModel.add(i, sampleData);
                return;
            }
        }

        letterListModel.add(letterListModel.size(), sampleData);
        letters.setSelectedIndex(i);
        entry.clear();
        sample.repaint();
    }

    private void del_actionPerformed(ActionEvent event)
    {
        int i = letters.getSelectedIndex();
        if(i == -1)
        {
            JOptionPane.showMessageDialog(this, "Please select a letter to delete.", "Error", 0);
            return;
        } else
        {
            letterListModel.remove(i);
            return;
        }
    }

    private void letters_valueChanged(ListSelectionEvent event)
    {
        if(letters.getSelectedIndex() == -1)
        {
            return;
        } else
        {
            SampleData selected = (SampleData)letterListModel.getElementAt(letters.getSelectedIndex());
            sample.setData((SampleData)selected.clone());
            sample.repaint();
            entry.clear();
            return;
        }
    }

    private void load_actionPerformed(ActionEvent event)
    {
        try
        {
            FileReader f = new FileReader(new File("./sample.dat"));
            BufferedReader r = new BufferedReader(f);
            int i = 0;
            letterListModel.clear();
            String line;
            while((line = r.readLine()) != null) 
            {
                SampleData ds = new SampleData(line.charAt(0), 5, 7);
                letterListModel.add(i++, ds);
                int idx = 2;
                int y = 0;
                while(y < ds.getHeight()) 
                {
                    for(int x = 0; x < ds.getWidth(); x++)
                        ds.setData(x, y, line.charAt(idx++) == '1');

                    y++;
                }
            }
            r.close();
            f.close();
            clear_actionPerformed(null);
            JOptionPane.showMessageDialog(this, "Loaded from 'sample.dat'.", "Training", -1);
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(this, (new StringBuilder()).append("Error: ").append(e).toString(), "Training", 0);
        }
    }

    private void save_actionPerformed(ActionEvent event)
    {
        try
        {
            OutputStream os = new FileOutputStream("./sample.dat", false);
            PrintStream ps = new PrintStream(os);
            for(int i = 0; i < letterListModel.size(); i++)
            {
                SampleData ds = (SampleData)letterListModel.elementAt(i);
                ps.print((new StringBuilder()).append(ds.getLetter()).append(":").toString());
                for(int y = 0; y < ds.getHeight(); y++)
                {
                    for(int x = 0; x < ds.getWidth(); x++)
                        ps.print(ds.getData(x, y) ? "1" : "0");

                }

                ps.println("");
            }

            ps.close();
            os.close();
            clear_actionPerformed(null);
            JOptionPane.showMessageDialog(this, "Saved to 'sample.dat'.", "Training", -1);
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(this, (new StringBuilder()).append("Error: ").append(e).toString(), "Training", 0);
        }
    }

    public void run()
    {
        try
        {
            int inputNeuron = 35;
            int outputNeuron = letterListModel.size();
            TrainingSet set = new TrainingSet(inputNeuron, outputNeuron);
            set.setTrainingSetCount(letterListModel.size());
            for(int t = 0; t < letterListModel.size(); t++)
            {
                int idx = 0;
                SampleData ds = (SampleData)letterListModel.getElementAt(t);
                for(int y = 0; y < ds.getHeight(); y++)
                {
                    for(int x = 0; x < ds.getWidth(); x++)
                        set.setInput(t, idx++, ds.getData(x, y) ? 0.5D : -0.5D);

                }

            }

            net = new KohonenNetwork(inputNeuron, outputNeuron, this);
            net.setTrainingSet(set);
            net.learn();
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(this, (new StringBuilder()).append("Error: ").append(e).toString(), "Training", 0);
        }
    }

    public void update(int retry, double totalError, double bestError)
    {
        if((retry % 100 != 0 || retry == 10) && !net.halt)
            return;
        if(net.halt)
        {
            trainThread = null;
            train.setText("Begin Training");
            JOptionPane.showMessageDialog(this, "Training has completed.", "Training", -1);
        }
        UpdateStats stats = new UpdateStats();
        stats._tries = retry;
        stats._lastError = totalError;
        stats._bestError = bestError;
        try
        {
            SwingUtilities.invokeAndWait(stats);
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(this, (new StringBuilder()).append("Error: ").append(e).toString(), "Training", 0);
        }
    }

    private void train_actionPerformed(ActionEvent event)
    {
        if(trainThread == null)
        {
            train.setText("Stop Training");
            train.repaint();
            trainThread = new Thread(this);
            trainThread.start();
        } else
        {
            net.halt = true;
        }
    }

    private void recognize_actionPerformed(ActionEvent event)
    {
        if(net == null)
        {
            JOptionPane.showMessageDialog(this, "I need to be trained first!", "Error", 0);
            return;
        }
        entry.downSample();
        double input[] = new double[35];
        int idx = 0;
        SampleData ds = sample.getData();
        for(int y = 0; y < ds.getHeight(); y++)
        {
            for(int x = 0; x < ds.getWidth(); x++)
                input[idx++] = ds.getData(x, y) ? 0.5D : -0.5D;

        }

        double normfac[] = new double[1];
        double synth[] = new double[1];
        int best = net.winner(input, normfac, synth);
        char map[] = mapNeurons();
        JOptionPane.showMessageDialog(this, (new StringBuilder()).append("  ").append(map[best]).append("   (Neuron #").append(best).append(" fired)").toString(), "That Letter Is", -1);
        clear_actionPerformed(null);
    }

    private char[] mapNeurons()
    {
        char map[] = new char[letterListModel.size()];
        double normfac[] = new double[1];
        double synth[] = new double[1];
        for(int i = 0; i < map.length; i++)
            map[i] = '?';

        for(int i = 0; i < letterListModel.size(); i++)
        {
            double input[] = new double[35];
            int idx = 0;
            SampleData ds = (SampleData)letterListModel.getElementAt(i);
            for(int y = 0; y < ds.getHeight(); y++)
            {
                for(int x = 0; x < ds.getWidth(); x++)
                    input[idx++] = ds.getData(x, y) ? 0.5D : -0.5D;

            }

            int best = net.winner(input, normfac, synth);
            map[best] = ds.getLetter();
        }

        return map;
    }


}
