package com.tzapps.tzpalette.algorithm;

public class ClusterCenter extends ClusterPoint
{
    private static final String TAG = "ClusterCenter";
    
    private int numOfPoints;
    
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
        int[] values_c1 = c1.getValues();
        int[] values_c2 = c2.getValues();
        
        assert(values_c1.length == values_c2.length);
        
        /* To improve the cluster convergence rate, we might
         * allow a deviation, i.e. if the difference of the old value 
         * and the new value is less than the indicated deviation
         * we could treat them as the "same"  
         */
        for (int i = 0; i < values_c1.length; i++)
        {
            if (Math.abs(values_c1[i] - values_c2[i]) > deviation)
                return false;
        }
        
        return true;
    }
}
