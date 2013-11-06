package com.tzapps.tzpalette.algorithm;

public class ClusterCenter extends ClusterPoint
{
    private static final String TAG = "ClusterCenter";
    
    /** The total number of points in this cluster */
    protected int numOfPoints;
    /** The nearest point to the cluster center */
    protected ClusterPoint nearestPoint;
    
    public ClusterCenter(int...values)
    {
        super(values);
    }

    public int getNumOfPoints()
    {
        return numOfPoints;
    }

    public void setNumOfPoints(int numOfPoints)
    {
        this.numOfPoints = numOfPoints;
    }
    
    public void setNearestPoint(ClusterPoint point)
    {
        this.nearestPoint = point;
    }
    
    public ClusterPoint getNearestPoint()
    {
        return nearestPoint;
    }

    public void addPoints()
    {
        numOfPoints++;
    }

    public void copy(ClusterCenter c)
    {
        this.numOfPoints  = c.numOfPoints;
        this.values       = c.values;
        this.clusterIndex = c.clusterIndex;
        this.nearestPoint = c.nearestPoint;
    }
}
