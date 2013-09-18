package com.tzapps.tzpalette.algorithm;

import java.util.Random;

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
            
            centers[i] = new ClusterCenter(ColorUtils.colorToHSV(values[index]));
            centers[i].setClusterIndex(i);
        }
        
        // create all cluster point 
        for (int i = 0; i < values.length; i++)
            points[i] = new ClusterPoint(ColorUtils.colorToHSV(values[i]));
    }
    
    public void processKMean(int[] values)
    {
        TimingLogger timings = new TimingLogger(TAG, "processKMean()");
        
        // create random cluster centers and initialize the cluster points
        initKMeanProcess(values, numOfCluster);
        timings.addSplit("initKMeanProcess(): values=" + values.length);
        
        // assign the cluster points into the initial cluster centers
        updateClusters(points, centers);
        timings.addSplit("init updateClusters()");
        
        // calculate the cluster center values as the first initial one
        centers = reCalcClusterCenters(points);
        int count = 1;
        timings.addSplit("reCalcClusterCenters() count " + count);
        
        while (true)
        {
            updateClusters(points, centers);
            timings.addSplit("updateClusters() count " + count);
            
            ClusterCenter[] newCenters = reCalcClusterCenters(points);
            count++;
            timings.addSplit("reCalcClusterCenters() count " + count);
            
            if (isStop(centers, newCenters, this.deviation))
                break;
            else
                centers = newCenters;
        }
      
        timings.addSplit("processKMean done");
        timings.dumpToLog();
    }
    
    /**
     * check if we could stop the cluster update process
     */
    private boolean isStop(ClusterCenter[] oldCenters, ClusterCenter[] newCenters, int deviation)
    {
        assert(oldCenters.length == newCenters.length);
        
        for (int i = 0; i < oldCenters.length; i++)
        {
            ClusterCenter oldCenter = oldCenters[i];
            ClusterCenter newCenter = newCenters[i];
                     
            Log.d(TAG, "cluster " + i + " old " + oldCenter.toString()
                       + ", new " + newCenter.toString());
            
            if (!ClusterCenter.equals(oldCenter, newCenter, deviation))
                return false;
        }
        return true;
    }
    
    /**
     * update the cluster index by distance value
     */
    private void updateClusters(ClusterPoint[] points, ClusterCenter[] centers)
    {
        // initialize the clusters for each point
        int[] clusterDisValues = new int[numOfCluster];
        
        for (int i = 0; i < points.length; i++)
        {
            ClusterPoint point = points[i];
            for (int cIndex = 0; cIndex < centers.length; cIndex++)
            {
                ClusterCenter center = centers[cIndex];
                clusterDisValues[cIndex] = ClusterPoint.calcEuclideanDistanceSquare(point, center);
            }
            
            point.setClusterIndex(getCloserCluster(clusterDisValues));
        }
    }
    
    /**
     * using cluster value of each point to update cluster center value
     */
    private ClusterCenter[] reCalcClusterCenters(ClusterPoint points[])
    {
        ClusterCenter[] newCenters = new ClusterCenter[numOfCluster];
        
        // clear the points now
        for (int i = 0; i < newCenters.length; i++)
        {
            newCenters[i] = new ClusterCenter();
            newCenters[i].setClusterIndex(i);
        }
        
        int numOfValues = points[0].getValuesCount();
        int[][] valuesSum = new int[numOfCluster][numOfValues];
        
        // recalculate the sum and total of points for each cluster center
        for (int i = 0; i < points.length; i++)
        {
            ClusterPoint point = points[i];
            int cIndex = point.getClusterIndex();            
            newCenters[cIndex].addPoints();
            
            int[] values = point.getValues();
            
            for (int j = 0; j < values.length; j++)
                valuesSum[cIndex][j] += values[j];
        }
        
        // recalculate the values in cluster center
        for (int i = 0; i < newCenters.length; i++)
        {
            ClusterCenter center = newCenters[i];
            
            int sum = center.getNumOfPoints();
            int cIndex = center.getClusterIndex();
            
            int values[] = new int[numOfValues];
            
            for (int j = 0; j < numOfValues; j++)
            {
                if (sum != 0)
                    values[j] = valuesSum[cIndex][j] / sum;
                else
                    values[j] = 0;
            }
            
            center.setValues(values);
        }
        
        return newCenters;
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
    
}
