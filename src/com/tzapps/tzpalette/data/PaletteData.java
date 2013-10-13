package com.tzapps.tzpalette.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.tzapps.tzpalette.debug.MyDebug;

public class PaletteData implements Parcelable
{
    private static final String TAG = "PaletteData";
       
    private long   id;
    private long   mUpdated;
    private String mTitle;
    private String mImageUrl;
    private List<Integer> mColors;
    private boolean isFavourite;
    
    public static final Parcelable.Creator<PaletteData> CREATOR = 
        new Parcelable.Creator<PaletteData>()
        {
            public PaletteData createFromParcel(Parcel source)
            {
                return new PaletteData(source);
            }
            
            public PaletteData[] newArray(int size)
            {
                return new PaletteData[size];
            }
        };
    
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
        mColors = new ArrayList<Integer>();
    }
    
    /**
     * This will be used only by the MyCreator
     * @param source
     */
    public PaletteData(Parcel source)
    {
        /*
         * Reconstruct from the Parcel
         */
        if (MyDebug.LOG)
            Log.v(TAG, "ParcelData(Parcel source): put back parcel data");
        
        id = source.readLong();
        mUpdated = source.readLong();
        mTitle = source.readString();
        mImageUrl = source.readString();
        source.readList(mColors, Integer.class.getClassLoader());
        
        isFavourite = (source.readByte() != 0); // isFavourite == true if byte != 0
    }
    
    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        if (MyDebug.LOG)
            Log.v(TAG, "writeToParcel..." + flags);
        
        dest.writeLong(id);
        dest.writeLong(mUpdated);
        dest.writeString(mTitle);
        dest.writeString(mImageUrl);
        dest.writeList(mColors);
        
        dest.writeByte((byte)(isFavourite ? 1 : 0));
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
        id        = -1;
        mUpdated  = -1;
        mTitle    = null;
        mImageUrl = null;
        isFavourite = false;
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
    
    public boolean isFavourite()
    {
        return isFavourite;
    }
    
    public void setFavourite(boolean favourite)
    {
        isFavourite = favourite;
    }
    
    /**
     * Compares the given palette data with the data, and returns true 
     * if they represent the same data
     *  
     * @param data the palette data to compare
     * 
     * @return true if they are the same data, otherwise false
     */
    public boolean equals(PaletteData data)
    {
        if (data == null)
            return false;
        
        return (this.id == data.id                    &&
                this.mUpdated == data.mUpdated        &&
                this.mTitle.equals(data.mTitle)       &&
                this.mImageUrl.equals(data.mImageUrl) &&
                this.isFavourite == data.isFavourite  &&
                this.mColors.equals(data.mColors));
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
              .append("isFavourite=").append(isFavourite)
              .append(" ]");
        
        return buffer.toString();
    }

    /**
     * Copy palette data values from an indicated one
     * 
     * @param data the palette data to copy
     */
    public void copy(PaletteData data)
    {
        this.id          = data.id;
        this.mColors     = data.mColors;
        this.mTitle      = data.mTitle;
        this.mImageUrl   = data.mImageUrl;
        this.mUpdated    = data.mUpdated;
        this.isFavourite = data.isFavourite;
    }
}
