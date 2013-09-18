package com.tzapps.tzpalette.algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.graphics.Color;
import android.util.Log;
import android.util.TimingLogger;

import com.tzapps.tzpalette.utils.ColorUtils;

public class KMeansProcessor
{
    private final static String TAG = "KMeansProcessor";
    
    private int numOfCluster;
    private int deviation;
    private ClusterPoint[] points;
    private ClusterCenter[] centers;
    
    public KMeansProcessor(int numOfCluster, int deviation)
    {
        this.numOfCluster = numOfCluster;
        this.deviation    = deviation;
    }
    
    public ClusterCenter[] getClusterCenters()
    {
        return centers;
    }
    
    private void initKMeanProcess(int[] values, int numOfCluster)
    {
        points = new ClusterPoint[values.length];
        centers = new ClusterCenter[numOfCluster];
        
        // Create random points as the cluster center
        Random random = new Random();
        for (int i = 0; i < numOfCluster; i++)
        {
            int index = random.nextInt(values.length);
            centers[i] = new ClusterCenter(values[index]);
            centers[i].setClusterIndex(i);
        }
        
        // create all cluster point 
        for (int i = 0; i < values.length; i++)
            points[i] = new ClusterPoint(values[i]);
    }
    
    public void processKMean(int[] values)
    {
           
        TimingLogger timings = new TimingLogger(TAG, "processKMean()");
        
        // create random cluster centers and initialize the cluster points
        initKMeanProcess(values, numOfCluster);
        timings.addSplit("initKMeanProcess(): values=" + values.length);
        
        // assign the cluster points into the initial cluster centers
        updateClusters();
        timings.addSplit("init updateClusters()");
        
        // calculate the cluster center values as the first initial one
        int[] clusterCenterValues = reCalcClusterCenterValues();
        int count = 1;
        timings.addSplit("reCalculateClusterCenters() count " + count);
        
        while (true)
        {
            updateClusters();
            timings.addSplit("updateClusters() count " + count);
            
            int[] newClusterCenterValues = reCalcClusterCenterValues();
            count++;
            timings.addSplit("reCalculateClusterCenters() count " + count);
            
            if (isStop(clusterCenterValues, newClusterCenterValues, this.deviation))
                break;
            else
                clusterCenterValues = newClusterCenterValues;
        }
      
        timings.addSplit("processKMean done");
        timings.dumpToLog();
    }
    
    /**
     * check if we could stop the cluster update process
     */
    private boolean isStop(int[] oldClusterCenterValues, int[] newClusterCenterValues, int deviation)
    {
        for (int i = 0; i < oldClusterCenterValues.length; i++)
        {
            int oldColor = oldClusterCenterValues[i];
            int newColor = newClusterCenterValues[i];
                     
            Log.d(TAG, "cluster " + i + " old " + ColorUtils.colorToRGBString(oldColor) 
                       + ", new " + ColorUtils.colorToRGBString(newColor));
            
            if (deviation == 0 && oldClusterCenterValues[i] != newClusterCenterValues[i])
            {
                return false; 
            }
            else
            {
                int oRed   = Color.red(oldColor);
                int oGreen = Color.green(oldColor);
                int oBlue  = Color.blue(oldColor);
                
                int nRed   = Color.red(newColor);
                int nGreen = Color.green(newColor);
                int nBlue  = Color.blue(newColor);
                
                /* To improve the cluster convergence rate, we might
                 * allow a deviation, i.e. if the difference of the old value 
                 * and the new value is less than the indicated deviation
                 * we could treat them as "same"  
                 */
                if (Math.abs(nRed   - oRed)   > deviation ||
                    Math.abs(nGreen - oGreen) > deviation ||
                    Math.abs(nBlue  - oBlue)  > deviation)
                    return false;
            }
        }
        return true;
    }
    
    /**
     * update the cluster index by distance value
     */
    private void updateClusters()
    {
        // initialize the clusters for each point
        int[] clusterDisValues = new int[numOfCluster];
        
        for (int i = 0; i < points.length; i++)
        {
            ClusterPoint point = points[i];
            for (int cIndex = 0; cIndex < centers.length; cIndex++)
            {
                ClusterCenter center = centers[cIndex];
                clusterDisValues[cIndex] = calcEuclideanDistanceSquare(point, center);
            }
            
            point.setClusterIndex(getCloserCluster(clusterDisValues));
        }
    }
    
    /**
     * using cluster value of each point to update cluster center value
     */
    private int[] reCalcClusterCenterValues()
    {
        // clear the points now
        for (int i = 0; i < centers.length; i++)
            centers[i].setNumOfPoints(0);
        
        // recalculate the sum and total of points for each cluster
        int[] redSum   = new int[numOfCluster];
        int[] greenSum = new int[numOfCluster];
        int[] blueSum  = new int[numOfCluster];
        
        for (int i = 0; i < points.length; i++)
        {
            ClusterPoint point = points[i];
            int cIndex = point.getClusterIndex();            
            centers[cIndex].addPoints();
            
            int tr = Color.red(point.getValue());
            int tg = Color.green(point.getValue());
            int tb = Color.blue(point.getValue());
            
            redSum[cIndex]   += tr;
            greenSum[cIndex] += tg;
            blueSum[cIndex]  += tb;
        }
        
        int[] clusterCentersValues = new int[numOfCluster];
        
        for (int i = 0; i < centers.length; i++)
        {
            ClusterCenter center = centers[i];
            int sum = center.getNumOfPoints();
            int cIndex = center.getClusterIndex();
            int red, green, blue;
            
            if (sum != 0 )
            {
                red = redSum[cIndex] / sum;
                green = greenSum[cIndex] / sum;
                blue = blueSum[cIndex]/sum;
            }
            else
            {
                red = green = blue = 0; 
            }
            
            int clusterColor = Color.rgb(red, green, blue);
            
            Log.d(TAG, "Cluster color is " + ColorUtils.colorToRGBString(clusterColor));
            
            center.setValue(clusterColor);
            clusterCentersValues[cIndex] = clusterColor;
        }
        
        return clusterCentersValues;
    }
    
    private int getCloserCluster(int[] clusterDisValues)
    {
        int min = clusterDisValues[0];
        int clusterIndex = 0;
        
        for (int i = 0; i < clusterDisValues.length; i ++)
        {
            if (min > clusterDisValues[i])
            {
                min = clusterDisValues[i];
                clusterIndex = i;
            }
        }
        
        return clusterIndex;
    }
    
    private int calcEuclideanDistanceSquare(ClusterPoint p, ClusterCenter c)
    {
        int pValue = p.getValue();
        int cValue = c.getValue();
        
        int pr = Color.red(pValue);
        int pg = Color.green(pValue);
        int pb = Color.blue(pValue);
        
        int cr = Color.red(cValue);
        int cg = Color.green(cValue);
        int cb = Color.blue(cValue);
        
        return (pr-cr)*(pr-cr) + (pg-cg)*(pg-cg) + (pb-cb)*(pb-cb);
    }
}
