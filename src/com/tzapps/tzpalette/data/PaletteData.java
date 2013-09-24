package com.tzapps.tzpalette.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.graphics.Bitmap;

public class PaletteData
{
    private static final String TAG = "PaletteData";
       
    private long   id;
    private long   mUpdated;
    private Bitmap mThumb;
    private String mTitle;
    private String mImageUrl;
    private List<Integer> mColors;
    
    public PaletteData()
    {
        this(null, null);
    }
    
    public PaletteData(Bitmap thumb)
    {
        this(null, thumb);
    }

    public PaletteData(String title, Bitmap thumb)
    {
        id     = -1;
        mTitle = title;
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
    
    public void removeColor(int color)
    {
        int index = -1;
        
        for (int i = 0; i < mColors.size(); i++)
        {
            if (mColors.get(i) == color)
            {
                index = i;
                break;
            }
        }
        
        if (index != -1)
            mColors.remove(index);
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

    public String getTitle()
    {
        return mTitle;
    }

    public void setTitle(String title)
    {
       mTitle = title;
    }

    public long getId()
    {
        return id;
    }
    
    public void setId(long id)
    {
        this.id = id;
    }
    
    public long getUpdated()
    {
        return mUpdated;
    }
    
    public void setUpdated(long updated)
    {
        mUpdated = updated;
    }
    
    public String getImageUrl()
    {
        return mImageUrl;
    }
    
    public void setImageUrl(String imageUrl)
    {
        mImageUrl = imageUrl;
    }
    
    @Override
    public String toString()
    {
        StringBuffer buffer = new StringBuffer();
        
        buffer.append("[ ")
              .append("id=").append(id).append(",")
              .append("title=").append(mTitle).append(",")
              .append("colors=").append(Arrays.toString(getColors())).append(",")
              .append("imageUrl=").append(mImageUrl).append(",")
              .append("thumb=").append(mThumb)
              .append(" ]");
        
        return buffer.toString();
    }

}
