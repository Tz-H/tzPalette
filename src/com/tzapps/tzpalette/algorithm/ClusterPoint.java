package com.tzapps.tzpalette.algorithm;


public class ClusterPoint
{
    private int value;
    private int clusterIndex;
    
    public ClusterPoint(int value)
    {
        this.value = value;
        this.clusterIndex = 0;
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
