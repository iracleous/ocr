package com.qued.ocr;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;

public class Sample extends JPanel
{
    private SampleData data;

    public Sample(int width, int height)
    {
        data = new SampleData(' ', width, height);
    }

    public SampleData getData()
    {
        return data;
    }

    public void setData(SampleData data)
    {
        this.data = data;
    }

    public void paint(Graphics g)
    {
        if(data == null)
            return;
        int vcell = getHeight() / data.getHeight();
        int hcell = getWidth() / data.getWidth();
        g.setColor(Color.white);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.black);
        for(int y = 0; y < data.getHeight(); y++)
            g.drawLine(0, y * vcell, getWidth(), y * vcell);

        for(int x = 0; x < data.getWidth(); x++)
            g.drawLine(x * hcell, 0, x * hcell, getHeight());

        for(int y = 0; y < data.getHeight(); y++)
        {
            for(int x = 0; x < data.getWidth(); x++)
                if(data.getData(x, y))
                    g.fillRect(x * hcell, y * vcell, hcell, vcell);

        }

        g.setColor(Color.black);
        g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
    }


}
