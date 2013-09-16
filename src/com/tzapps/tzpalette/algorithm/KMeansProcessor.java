package com.tzapps.tzpalette.algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.tzapps.tzpalette.utils.ColorUtils;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.util.TimingLogger;

public class KMeansProcessor
{
    private final static String TAG = "KMeansProcessor";
    
    private List<ClusterCenter> clusterCenterList;
    private List<ClusterPoint> pointList;
    
    private int numOfCluster;
    private int deviation;
    
    public KMeansProcessor(int numOfCluster, int deviation)
    {
        this.numOfCluster = numOfCluster;
        this.deviation    = deviation;
        pointList         = new ArrayList<ClusterPoint>();
        clusterCenterList = new ArrayList<ClusterCenter>();
    }
    
    public List<ClusterCenter> getClusterCenters()
    {
        return clusterCenterList;
    }
    
    private void initKMeanProcess(int totalCol, int totalRow, int[] values)
    {
        int index = 0;
        
        pointList.clear();
        clusterCenterList.clear();
        
        // Create random points as the cluster center
        Random random = new Random();
        for (int i = 0; i < numOfCluster; i++)
        {
            int randCol = random.nextInt(totalCol);
            int randRow = random.nextInt(totalRow);
            index = randRow * totalCol + randCol;
            ClusterCenter cc = new ClusterCenter(randCol, randRow, values[index]);
            cc.setClusterIndex(i);
            clusterCenterList.add(cc);
        }
        
        // create all cluster point
        for (int row = 0; row < totalRow; row++)
        {
            for (int col = 0; col < totalCol; col++)
            {
                index = row * totalCol + col;
                int value = values[index];
                pointList.add(new ClusterPoint(col, row, value));
            }
        }
    }
    
    private void initKMeanClusters()
    {
        // initialize the clusters for each point
        double[] clusterDisValues = new double[numOfCluster];
        for (ClusterPoint point : pointList)
        {
            for (ClusterCenter center : clusterCenterList)
            {
                int cIndex = center.getClusterIndex();
                clusterDisValues[cIndex] = calculateEuclideanDistance(point, center);
            }
            point.setClusterIndex(getCloserCluster(clusterDisValues));
        }
    }
    
    public void processKMean(Bitmap src)
    {
        TimingLogger timings = new TimingLogger(TAG, "processKMean");
        
        // initialization the pixel data
        int width = src.getWidth();
        int height = src.getHeight();
        int[] inPixels = new int[width*height];
        
        src.getPixels(inPixels, 0, width, 0, 0, width, height);
        
        
        // create random cluster center
        initKMeanProcess(width, height, inPixels);
        
        timings.addSplit("initKMeanProcess");
        
        // initialize the clusters for each point
        initKMeanClusters();
        
        timings.addSplit("initKMeanClusters");
        
        // calculate the old summary
        // assign the points to cluster center
        // calculate the new cluster center
        // computing the delta value
        // stop condition
        
        int[] oldClusterCenterColors = reCalculateClusterCenters();
        int count = 1;
        timings.addSplit("reCalculateClusterCenters count " + count);
        
        while (true)
        {
            stepClusters();
            
            int[] newClusterCenterColors = reCalculateClusterCenters();
            count++;
            timings.addSplit("reCalculateClusterCenters count " + count);
            
            if (isStop(oldClusterCenterColors, newClusterCenterColors, this.deviation))
                break;
            else
                oldClusterCenterColors = newClusterCenterColors;
        }
      
        timings.addSplit("processKMean done" + count);
        timings.dumpToLog();
        Log.d(TAG, "finished");
        
        /**
        //update the result image
        dest = src.copy(Bitmap.Config.ARGB_8888, true);
        index = 0;
        int[] outPixels = new int[width*height];
        
        for (ClusterPoint point : pointList)
        {
            for (ClusterCenter center : clusterCenterList)
            {
                if (point.getClusterIndex() == center.getClusterIndex())
                {
                    int col = point.getX();
                    int row = point.getY();
                    index = row * width + col;
                    outPixels[index] = center.getValue();
                }
            }
        }
        
        // fill the pixel data
        dest.setPixels(outPixels, 0, width, 0, 0, width, height);
        return dest;
        */
    }
    
    private boolean isStop(int[] oldClusterCenterValues, int[] newClusterCenterValues, int deviation)
    {
        for (int i = 0; i < oldClusterCenterValues.length; i++)
        {
            int oldColor = oldClusterCenterValues[i];
            int newColor = newClusterCenterValues[i];
                     
            Log.d(TAG, "cluster " + i + " old " + ColorUtils.colorToString(oldColor) 
                       + ", new " + ColorUtils.colorToString(newColor));
            
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
    private void stepClusters()
    {
        // initialize the clusters for each point
        double[] clusterDisValues = new double[numOfCluster];
        
        for (ClusterPoint point: pointList)
        {
            for (ClusterCenter center : clusterCenterList)
            {
                int cIndex = center.getClusterIndex();
                clusterDisValues[cIndex] = calculateEuclideanDistance(point, center);
            }
            point.setClusterIndex(getCloserCluster(clusterDisValues));
        }
    }
    
    /**
     * using cluster value of each point to update cluster center value
     */
    private int[] reCalculateClusterCenters()
    {
        // clear the points now
        for (ClusterCenter center : clusterCenterList)
            center.setNumOfPoints(0);
        
        // recalculate the sum and total of points for each cluster
        int[] redSum   = new int[numOfCluster];
        int[] greenSum = new int[numOfCluster];
        int[] blueSum  = new int[numOfCluster];
        
        for (ClusterPoint point : pointList)
        {
            int cIndex = point.getClusterIndex();            
            clusterCenterList.get(cIndex).addPoints();
            
            int tr = Color.red(point.getValue());
            int tg = Color.green(point.getValue());
            int tb = Color.blue(point.getValue());
            
            redSum[cIndex]   += tr;
            greenSum[cIndex] += tg;
            blueSum[cIndex]  += tb;
        }
        
        int[] oldClusterCentersColors = new int[numOfCluster];
        
        for (ClusterCenter center : clusterCenterList)
        {
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
            
            Log.d(TAG, "Cluster color is " + ColorUtils.colorToString(clusterColor));
            
            center.setValue(clusterColor);
            oldClusterCentersColors[cIndex] = clusterColor;
        }
        
        return oldClusterCentersColors;
    }
    
    private int getCloserCluster(double[] clusterDisValues)
    {
        double min = clusterDisValues[0];
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
    
    private double calculateEuclideanDistance(ClusterPoint p, ClusterCenter c)
    {
        //int pa = Color.alpha(p.getValue());
        int pr = Color.red(p.getValue());
        int pg = Color.green(p.getValue());
        int pb = Color.blue(p.getValue());
        
        //int ca = Color.alpha(p.getValue());
        int cr = Color.red(c.getValue());
        int cg = Color.green(c.getValue());
        int cb = Color.blue(c.getValue());
        
        // the Euclidean distance equation
        return Math.sqrt( Math.pow((pr - cr), 2.0) + 
                          Math.pow((pg - cg), 2.0) + 
                          Math.pow((pb - cb), 2.0) );
    }
}
