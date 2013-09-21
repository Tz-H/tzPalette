package com.tzapps.tzpalette.algorithm;

public class ClusterCenter extends ClusterPoint
{
    private static final String TAG = "ClusterCenter";
    
    protected int numOfPoints;
    
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

    public void addPoints()
    {
        numOfPoints++;
    }

    public static boolean equals(ClusterCenter c1, ClusterCenter c2, int deviation)
    {
        int dist = ClusterPoint.calcEuclideanDistanceSquare(c1, c2);
        
        /* To improve the cluster convergence rate, we might
         * allow a deviation, i.e. if the distance of the cluster center
         * is less than the indicated deviation we could treat them as 
         * "equals"  
         */
        return dist <= deviation*deviation;
    }

    public void copy(ClusterCenter c)
    {
        this.numOfPoints  = c.numOfPoints;
        this.values       = c.values;
        this.clusterIndex = c.clusterIndex;
    }
}
