package com.tzapps.tzpalette.algorithm;


public class ClusterPoint
{
    private int x;
    private int y;
    private int value;
    private int clusterIndex;
    
    public ClusterPoint(int x, int y, int value)
    {
        this.x = x; 
        this.y = y; 
        this.value = value;
        
        this.clusterIndex = 0;
    }

    public int getX()
    {
        return x;
    }

    public void setX(int x)
    {
        this.x = x;
    }

    public int getY()
    {
        return y;
    }

    public void setY(int y)
    {
        this.y = y;
    }

    public int getValue()
    {
        return value;
    }

    public void setValue(int value)
    {
        this.value = value;
    }

    public int getClusterIndex()
    {
        return clusterIndex;
    }

    public void setClusterIndex(int clusterIndex)
    {
        this.clusterIndex = clusterIndex;
    }
}
