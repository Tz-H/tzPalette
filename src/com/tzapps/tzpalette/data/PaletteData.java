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

    public void analysis()
    {
        Log.d(TAG, "palette data analysis()");
        
        KMeansProcessor proc = new KMeansProcessor(8, 10);   
        
        // TODO: async this process to avoid UI thread block
        proc.processKMean(mThumb);
        
        for (ClusterCenter center : proc.getClusterCenters())
            addColor(center.getValue());
    }
}
