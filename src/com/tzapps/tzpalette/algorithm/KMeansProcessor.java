package com.tzapps.tzpalette.algorithm;

import java.util.Random;

import android.util.Log;
import android.util.TimingLogger;

public class KMeansProcessor
{
    private final static String TAG = "KMeansProcessor";
    
    private int numOfCluster;
    private int deviation;
    private int maxRound;
    
    private ClusterCenter[] mCenters;
    private ClusterCenter[] mNewCenters;
    
    /**
     * 
     * @param numOfCluster the number of clusters
     * @param deviation    the deviation allowed
     * @param maxRound     the max rounds to iteration
     */
    public KMeansProcessor(int numOfCluster, int deviation, int maxRound)
    {
        this.numOfCluster = numOfCluster;
        this.deviation    = deviation;
        this.maxRound     = maxRound;
    }
    
    public ClusterCenter[] getClusterCenters()
    {
        return mCenters;
    }
    
    private void initKMeanProcess(ClusterPoint[] points, int numOfCluster)
    {
        mCenters = new ClusterCenter[numOfCluster];
        mNewCenters = new ClusterCenter[numOfCluster];
        
        for (int i = 0; i < numOfCluster; i++)
        {
            mCenters[i] = new ClusterCenter();
            mNewCenters[i] = new ClusterCenter();
        }
        
        /*
         * TODO: currently I just pick up the random cluster points as 
         * the initial cluster center points, which is not a good solution
         * and will cause the cluster result unstable.
         * 
         * It should be changed to a better way to handle with. e.g.
         * the "k-means++" clustering algorithm. see
         * http://rosettacode.org/wiki/K-means%2B%2B_clustering
         * 
         */

        // create random points as the cluster centers
        Random random = new Random();
        for (int i = 0; i < numOfCluster; i++)
        {
            int index = random.nextInt(points.length);
            
            mCenters[i].setValues(points[index].getValues());
            mCenters[i].setClusterIndex(i);
        }
    }
    
    public void processKMean(ClusterPoint[] points)
    {
        TimingLogger timings = new TimingLogger(TAG, "processKMean()");
        
        // create random cluster centers
        initKMeanProcess(points, numOfCluster);
        timings.addSplit("initKMeanProcess(): values=" + points.length);
        
        int count = 1;
        
        while(count < maxRound)
        {
            updateClusters(points, mCenters);
            timings.addSplit("updateClusters() count " + count);
            
            calcClusterCenters(points, mNewCenters);
            count++;
            timings.addSplit("reCalcClusterCenters() count " + count);
            
            if (isStop(mCenters, mNewCenters, deviation))
                break;
            else
                udpateClusterCenter(mCenters, mNewCenters);
        }
      
        timings.addSplit("processKMean done");
        timings.dumpToLog();
    }
    
    private void udpateClusterCenter(ClusterCenter[] centers, ClusterCenter[] newCenters)
    {
        for (int i = 0; i < centers.length; i++)
        {
            ClusterCenter center = centers[i];
            ClusterCenter newCenter = newCenters[i];
            
            center.copy(newCenter);
        }
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
     * using cluster value of each point to update cluster center value
     */
    private void calcClusterCenters(ClusterPoint points[], ClusterCenter centers[])
    {
        // clear the points now
        for (int i = 0; i < centers.length; i++)
        {
            centers[i].setNumOfPoints(0);
            centers[i].setClusterIndex(i);
        }
        
        int numOfValues = points[0].getValuesCount();
        int[][] valuesSum = new int[numOfCluster][numOfValues];
        
        // recalculate the sum and total of points for each cluster center
        for (int i = 0; i < points.length; i++)
        {
            ClusterPoint point = points[i];
            int cIndex = point.getClusterIndex();            
            centers[cIndex].addPoints();
            
            int[] values = point.getValues();
            
            for (int j = 0; j < values.length; j++)
                valuesSum[cIndex][j] += values[j];
        }
        
        // recalculate the values in cluster center
        for (int i = 0; i < centers.length; i++)
        {
            ClusterCenter center = centers[i];
            
            int totalPoints = center.getNumOfPoints();
            int cIndex = center.getClusterIndex();
            
            int values[] = new int[numOfValues];
            
            for (int j = 0; j < numOfValues; j++)
            {
                if (totalPoints != 0)
                    values[j] = valuesSum[cIndex][j] / totalPoints;
                else
                    values[j] = 0;
            }
            
            center.setValues(values);
        }
    }
}
