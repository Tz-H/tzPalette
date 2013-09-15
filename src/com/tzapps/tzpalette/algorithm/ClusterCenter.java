package com.tzapps.tzpalette.algorithm;

public class ClusterCenter extends ClusterPoint
{
    private int numOfPoints;
    
    public ClusterCenter(int x, int y, int value)
    {
        super(x, y, value);
    }

    public int getNumOfPoints()
    {
        return numOfPoints;
    }

    public void setNumOfPoints(int numOfPoints)
    {
        this.numOfPoints = numOfPoints;
    }

    public void addPoints()
    {
        numOfPoints++;
    }
}
