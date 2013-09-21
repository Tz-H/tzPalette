package com.tzapps.tzpalette.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.graphics.Bitmap;

import com.tzapps.tzpalette.utils.BitmapUtils;

public class PaletteData
{
    private static final String TAG = "PaletteData";
       
    Bitmap mThumb;
    List<Integer> mColors;
    String mName;
    
    public PaletteData()
    {
        this(null, null);
    }
    
    public PaletteData(Bitmap thumb)
    {
        this(null, thumb);
    }

    public PaletteData(String name, Bitmap thumb)
    {
        mName = name;
        mThumb = thumb;
        mColors = new ArrayList<Integer>();
    }
    
    public void setThumb(Bitmap thumb)
    {
        if (mThumb != null)
            mThumb.recycle();
        
        if (thumb == null)
        {
            mThumb = null;
            return;
        }
        
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
    
    public void addColors(int[] colors, boolean reset)
    {
        if (colors == null)
            return;
        
        if (reset)
            mColors.clear();
        
        for (int color : colors)
            addColor(color);
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

    public void clear()
    {
        setThumb(null);
        clearColors();
    }

    public String getName()
    {
        return mName;
    }
    
    public void setName(String name)
    {
        mName = name;
    }
}
