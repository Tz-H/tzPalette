package com.tzapps.tzpalette.algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

public class KMeansProcessor
{
    private final static String TAG = "KMeansProcessor";
    
    private List<ClusterCenter> clusterCenterList;
    private List<ClusterPoint> pointList;
    
    private int numOfCluster;
    
    public KMeansProcessor(int clusters)
    {
        this.numOfCluster = clusters;
        pointList = new ArrayList<ClusterPoint>();
        clusterCenterList = new ArrayList<ClusterCenter>();
    }
    
    public Bitmap filter(Bitmap src, Bitmap dest)
    {
        // initialization the pixel data
        int width = src.getWidth();
        int height = src.getHeight();
        int[] inPixels = new int[width*height];
        int index = 0;
        
        src.getPixels(inPixels, 0, width, 0, 0, width, height);
        
        // Crate random points as the cluster center
        Random random = new Random();
        for (int i = 0; i < numOfCluster; i++)
        {
            int randCol = random.nextInt(width);
            int randRow = random.nextInt(height);
            index = randRow * width + randCol;
            ClusterCenter cc = new ClusterCenter(randCol, randRow, inPixels[index]);
            cc.setClusterIndex(i);
            clusterCenterList.add(cc);
        }
        
        // create all cluster point
        for (int row = 0; row < height; row++)
        {
            for (int col = 0; col < width; col++)
            {
                index = row * width + col;
                int color = inPixels[index];
                pointList.add(new ClusterPoint(col, row, color));
            }
        }
        
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
        
        // calculate the old summary
        // assign the points to cluster center
        // calculate the new cluster center
        // computing the delta value
        // stop condition
        int[] oldClusterCenterColors = reCalculateClusterCenters();
        while (true)
        {
            stepClusters();
            
            int[] newClusterCenterColors = reCalculateClusterCenters();
            
            if (isStop(oldClusterCenterColors, newClusterCenterColors))
                break;
            else
                oldClusterCenterColors = newClusterCenterColors;
        }
        
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
    }
    
    private boolean isStop(int[] oldClusterCenterValues, int[] newClusterCenterValues)
    {
        for (int i = 0; i < oldClusterCenterValues.length; i++)
        {
            int oldColor = oldClusterCenterValues[i];
            int newColor = newClusterCenterValues[i];
            
            int oRed = Color.red(oldColor);
            int oGreen = Color.green(oldColor);
            int oBlue = Color.blue(oldColor);
            
            int nRed = Color.red(newColor);
            int nGreen = Color.green(newColor);
            int nBlue = Color.blue(newColor);
            
            StringBuffer buffer = new StringBuffer();
            
            buffer.append("old r,g,b:")
                  .append(oRed).append(",")
                  .append(oGreen).append(",")
                  .append(oBlue).append(",")
                  .append("new r,g,b:")
                  .append(nRed).append(",")
                  .append(nGreen).append(",")
                  .append(nBlue);
            
            Log.i(TAG, "cluster " + i + " " + buffer.toString());
            
            if (oldClusterCenterValues[i] != newClusterCenterValues[i])
                return false;
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
            
            Log.i(TAG, "red = " + red + " gree = " + green + " blue = " + blue);
            
            int clusterColor = Color.rgb(red, green, blue);
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
