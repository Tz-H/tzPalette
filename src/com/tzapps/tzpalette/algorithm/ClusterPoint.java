package com.tzapps.tzpalette.algorithm;

import android.graphics.Color;


public class ClusterPoint
{
    private static final String TAG = "ClusterPoint";
    
    private int   clusterIndex;
    private int[] values;
    
    public ClusterPoint(int...values)
    {
        this.values = values;
        this.clusterIndex = 0;
    }

    public int[] getValues()
    {
        return values;
    }

    public void setValues(int...values)
    {
        this.values = values;
    }

    public int getClusterIndex()
    {
        return clusterIndex;
    }

    public void setClusterIndex(int clusterIndex)
    {
        this.clusterIndex = clusterIndex;
    }
    
    public static int calcEuclideanDistanceSquare(ClusterPoint p1, ClusterPoint p2)
    {
        int dist = 0;
        
        assert(p1.values.length == p2.values.length);
        
        for (int i = 0; i < p1.values.length; i++)
            dist += (p1.values[i] - p2.values[i]) * (p1.values[i] - p2.values[i]);
        
        return dist;
    }

    public int getValuesCount()
    {
        return values.length;
    }
    
    public String toString()
    {
        StringBuffer buffer = new StringBuffer();
        
        buffer.append("[ ");
        
        for (int i = 0; i < values.length; i++)
            buffer.append(values[i]).append(" ");
        
        buffer.append("]");
        
        return buffer.toString();
    }
}
