package com.tzapps.tzpalette.algorithm;

import java.util.Random;

import com.tzapps.tzpalette.debug.MyDebug;

import android.util.Log;
import android.util.TimingLogger;

public class KMeansProcessor
{
    private final static String TAG = "KMeansProcessor";
    
    private int numOfCluster;
    private int deviation;
    private int maxRound;
    private boolean enableKpp;
    
    private ClusterCenter[] mCenters;
    private ClusterCenter[] mNewCenters;
    
    /**
     * 
     * @param numOfCluster the number of clusters
     * @param deviation    the deviation allowed
     * @param maxRound     the max rounds to iteration
     * @param enableKpp    flag to indicate if enable the kpp process
     */
    public KMeansProcessor(int numOfCluster, int deviation, int maxRound, boolean enableKpp)
    {
        this.numOfCluster = numOfCluster;
        this.deviation    = deviation;
        this.maxRound     = maxRound;
        this.enableKpp    = enableKpp;
    }
    
    public ClusterCenter[] getClusterCenters()
    {
        return mCenters;
    }
    
    private void kpp(ClusterPoint[] points, ClusterCenter[] centers)
    {
        Random random = new Random();
        
        centers[0].setValues(points[random.nextInt(points.length)].getValues());
        centers[0].setClusterIndex(0);
        
        int[] d = new int[points.length];
        
        for (int i = 0; i < centers.length; i++)
        {
            int sum = 0;
            for (int j = 0; j < points.length; j++)
            {
                d[j] = nearest_cluster_center(points[j], centers, i);
                sum += d[j];
            }
            
            sum = (int)((float)sum * random.nextFloat());
            
            for (int j = 0; j < d.length; j++)
            {
                sum -= d[j];
                if (sum > 0)
                    continue;
                
                centers[i].setValues(points[j].getValues());
                centers[i].setClusterIndex(i);
                break;
            }
        }
    }
    
    private int nearest_cluster_center(ClusterPoint point, ClusterCenter[] centers, int stepTo)
    {
        int min_dist  = Integer.MAX_VALUE;
        
        for (int i = 0; i < stepTo; i++)
        {
            int dist = ClusterPoint.calcEuclideanDistanceSquare(point,  centers[i]);
            if (min_dist > dist)
            {
                min_dist = dist;
                point.setClusterIndex(i);
            }
        }
        
        return min_dist;
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
        

        if (enableKpp)
        {        
            /*
             * TODO: currently I just pick up the random cluster points as 
             * the default process to get the initial cluster center points, 
             * which is not a good solution and will cause the cluster result 
             * unstable.
             * 
             * It should be changed to a better way to handle with. e.g.
             * the "k-means++" clustering algorithm. see
             * http://rosettacode.org/wiki/K-means%2B%2B_clustering
             *
             * However, the kpp() algorithm is still not quite stable, so
             * just make it optional for now...
             */
            kpp(points, mCenters);
        }
        else
        {
            // create random points as the cluster centers
            Random random = new Random();
            for (int i = 0; i < numOfCluster; i++)
            {
                int index = random.nextInt(points.length);
                
                mCenters[i].setValues(points[index].getValues());
                mCenters[i].setClusterIndex(i);
            }
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
        
        // find out the nearest center point for each cluster
        findNearestPoint(points, mCenters);
        timings.addSplit("findNearestPoint()");
      
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
     * Find out the nearest point for each cluster center
     */
    private void findNearestPoint(ClusterPoint[] points, ClusterCenter[] centers)
    {
        int dist[] = new int[centers.length];
        int nearestPointIndex[] = new int[centers.length];
        
        for (int i = 0; i < dist.length; i ++)
            dist[i] = Integer.MAX_VALUE;
        
        for (int i = 0; i < points.length; i++)
        {
            ClusterPoint point = points[i];
            int cIndex = point.clusterIndex;
            
            ClusterCenter center = centers[cIndex];
            
            if (dist[cIndex] > ClusterPoint.calcEuclideanDistanceSquare(center, point))
            {
                dist[cIndex] = ClusterPoint.calcEuclideanDistanceSquare(center, point);
                nearestPointIndex[cIndex] = i;
            }
        }
        
        for (int i = 0; i < centers.length; i++)
        {
            ClusterCenter center = centers[i];
            center.setNearestPoint(points[nearestPointIndex[i]]);
        }
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
            
            if (MyDebug.LOG)
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
    private void calcClusterCenters(ClusterPoint[] points, ClusterCenter[] centers)
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
