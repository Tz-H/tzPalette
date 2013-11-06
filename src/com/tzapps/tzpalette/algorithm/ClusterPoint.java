package com.tzapps.tzpalette.algorithm;


public class ClusterPoint
{
    private static final String TAG = "ClusterPoint";
    
    protected int   clusterIndex;
    protected int[] values;
    
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
    
    public static int calcEuclideanDistanceSquare(ClusterPoint p1, ClusterPoint p2)
    {
        int dist = 0;
        
        assert(p1.values.length == p2.values.length);
        
        for (int i = 0; i < p1.values.length; i++)
            dist += (p1.values[i] - p2.values[i]) * (p1.values[i] - p2.values[i]);
        
        return dist;
    }
    
    /**
     * Check whether the given points are equals based on the indicated deviation
     *  
     * @param p1        point 1
     * @param p2        point 2
     * @param deviation the deviation
     * 
     * @return true if p1 equals p2, otherwise false
     */
    public static boolean equals(ClusterPoint p1, ClusterPoint p2, int deviation)
    {
        int dist = ClusterPoint.calcEuclideanDistanceSquare(p1, p2);
        
        /* To improve the cluster convergence rate, we might
         * allow a deviation, i.e. if the distance of the cluster center
         * is less than the indicated deviation we could treat them as 
         * "equals"  
         */
        return dist <= deviation*deviation;
    }
}
