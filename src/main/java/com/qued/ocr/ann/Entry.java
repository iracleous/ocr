package com.qued.ocr.ann;
import com.qued.ocr.Sample;
import com.qued.ocr.SampleData;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.PixelGrabber;
import javax.swing.JPanel;

public class Entry extends JPanel
{
    protected Image entryImage;
    protected Graphics entryGraphics;
    protected int lastX;
    protected int lastY;
    protected Sample sample;
    protected int downSampleLeft;
    protected int downSampleRight;
    protected int downSampleTop;
    protected int downSampleBottom;
    protected double ratioX;
    protected double ratioY;
    protected int pixelMap[];

    public Entry()
    {
        lastX = -1;
        lastY = -1;
        enableEvents(49L);
    }

    protected void initImage()
    {
        entryImage = createImage(getWidth(), getHeight());
        entryGraphics = entryImage.getGraphics();
        entryGraphics.setColor(Color.white);
        entryGraphics.fillRect(0, 0, getWidth(), getHeight());
    }

    public void paint(Graphics g)
    {
        if(entryImage == null)
            initImage();
        g.drawImage(entryImage, 0, 0, this);
        g.setColor(Color.black);
        g.drawRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.red);
        g.drawRect(downSampleLeft, downSampleTop, downSampleRight - downSampleLeft, downSampleBottom - downSampleTop);
    }

    protected void processMouseEvent(MouseEvent e)
    {
        if(e.getID() != 501)
        {
            return;
        } else
        {
            lastX = e.getX();
            lastY = e.getY();
            return;
        }
    }

    protected void processMouseMotionEvent(MouseEvent e)
    {
        if(e.getID() != 506)
        {
            return;
        } else
        {
            entryGraphics.setColor(Color.black);
            entryGraphics.drawLine(lastX, lastY, e.getX(), e.getY());
            getGraphics().drawImage(entryImage, 0, 0, this);
            lastX = e.getX();
            lastY = e.getY();
            return;
        }
    }

    public void setSample(Sample s)
    {
        sample = s;
    }

    public Sample getSample()
    {
        return sample;
    }

    protected boolean hLineClear(int y)
    {
        int w = entryImage.getWidth(this);
        for(int i = 0; i < w; i++)
            if(pixelMap[y * w + i] != -1)
                return false;

        return true;
    }

    protected boolean vLineClear(int x)
    {
        int w = entryImage.getWidth(this);
        int h = entryImage.getHeight(this);
        for(int i = 0; i < h; i++)
            if(pixelMap[i * w + x] != -1)
                return false;

        return true;
    }

    protected void findBounds(int w, int h)
    {
        int y = 0;
        do
        {
            if(y >= h)
                break;
            if(!hLineClear(y))
            {
                downSampleTop = y;
                break;
            }
            y++;
        } while(true);
        y = h - 1;
        do
        {
            if(y < 0)
                break;
            if(!hLineClear(y))
            {
                downSampleBottom = y;
                break;
            }
            y--;
        } while(true);
        int x = 0;
        do
        {
            if(x >= w)
                break;
            if(!vLineClear(x))
            {
                downSampleLeft = x;
                break;
            }
            x++;
        } while(true);
        x = w - 1;
        do
        {
            if(x < 0)
                break;
            if(!vLineClear(x))
            {
                downSampleRight = x;
                break;
            }
            x--;
        } while(true);
    }

    protected boolean downSampleQuadrant(int x, int y)
    {
        int w = entryImage.getWidth(this);
        int startX = (int)((double)downSampleLeft + (double)x * ratioX);
        int startY = (int)((double)downSampleTop + (double)y * ratioY);
        int endX = (int)((double)startX + ratioX);
        int endY = (int)((double)startY + ratioY);
        for(int yy = startY; yy <= endY; yy++)
        {
            for(int xx = startX; xx <= endX; xx++)
            {
                int loc = xx + yy * w;
                if(pixelMap[loc] != -1)
                    return true;
            }

        }

        return false;
    }

    public void downSample()
    {
        int w = entryImage.getWidth(this);
        int h = entryImage.getHeight(this);
        PixelGrabber grabber = new PixelGrabber(entryImage, 0, 0, w, h, true);
        try
        {
            grabber.grabPixels();
            pixelMap = (int[])(int[])grabber.getPixels();
            findBounds(w, h);
            SampleData data = sample.getData();
            ratioX = (double)(downSampleRight - downSampleLeft) / (double)data.getWidth();
            ratioY = (double)(downSampleBottom - downSampleTop) / (double)data.getHeight();
            for(int y = 0; y < data.getHeight(); y++)
            {
                for(int x = 0; x < data.getWidth(); x++)
                    if(downSampleQuadrant(x, y))
                        data.setData(x, y, true);
                    else
                        data.setData(x, y, false);

            }

            sample.repaint();
            repaint();
        }
        catch(InterruptedException e) { }
    }

    public void clear()
    {
        entryGraphics.setColor(Color.white);
        entryGraphics.fillRect(0, 0, getWidth(), getHeight());
        downSampleBottom = downSampleTop = downSampleLeft = downSampleRight = 0;
        repaint();
    }


}
