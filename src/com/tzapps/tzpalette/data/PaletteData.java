package com.tzapps.tzpalette.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.graphics.Bitmap;
import android.util.Log;

import com.tzapps.tzpalette.algorithm.ClusterCenter;
import com.tzapps.tzpalette.algorithm.KMeansProcessor;

public class PaletteData
{
    private static final String TAG = "PaletteData";
    
    Bitmap mThumb;
    List<Integer> mColors;
    
    public PaletteData()
    {
        mThumb  = null;
        mColors = new ArrayList<Integer>();
    }
    
    public PaletteData(Bitmap thumb)
    {
        mThumb  = thumb;
        mColors = new ArrayList<Integer>();
    }
    
    public void setThumb(Bitmap thumb)
    {
        if (mThumb != null)
            mThumb.recycle();
        
        mThumb = thumb;
    }
    
    public Bitmap getThumb()
    {
        return mThumb;
    }
    
    public void addColor(int color)
    {
        mColors.add(color);
    }
    
    public int[] getColors()
    {
        int numOfColors = mColors.size();
        int[] colors = new int[mColors.size()];
        
        for (int i = 0; i < numOfColors; i++)
        {
            colors[i] = mColors.get(i);
        }
        
        Arrays.sort(colors);
        
        return colors;
    }
    
    public void clearColors()
    {
        mColors.clear();
    }

    /**
     * Analysis the thumb in palette data to pick up
     * its main colors
     * 
     * @param reset  true to clean up all existing colors
     *               false to keep them and append new colors
     *               in to colors list
     */
    public void analysis(boolean reset)
    {
        Log.d(TAG, "palette data analysis()");
        
        assert(mThumb != null);
        
        if (mThumb == null)
        {
            Log.e(TAG, "palette data doesn't have a thumb!");
            return;
        }
        
        if (reset)
            clearColors();
        
        KMeansProcessor proc = new KMeansProcessor(8, 10);   
        
        proc.processKMean(mThumb);
        
        for (ClusterCenter center : proc.getClusterCenters())
            addColor(center.getValue());
    }
}
