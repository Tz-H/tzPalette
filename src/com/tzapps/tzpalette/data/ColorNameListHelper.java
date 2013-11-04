package com.tzapps.tzpalette.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.tzapps.tzpalette.R;
import com.tzapps.tzpalette.data.ColorNameListHelper.ColorNameItem;

public class ColorNameListHelper
{
    private static final String TAG = "ColorNameListHelper";
    
    private static ColorNameListHelper instance;
    
    private Context mContext;
    private List<ColorNameItem> mList;
    
    public class ColorNameItem
    {
        public int color;
        public String name;
        
        public ColorNameItem(int color, String name)
        {
            this.color = color;
            this.name = name;
        }
    }
    
    private ColorNameListHelper(Context context)
    {
        mContext = context;
        mList = new ArrayList<ColorNameItem>();
        
        InputStream in = mContext.getResources().openRawResource(R.raw.color_name_list);
        String line;
        
        try
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            while ((line = br.readLine()) != null)
            {
                String []strArr = line.split("\\|");
                
                ColorNameItem item = new ColorNameItem(Color.parseColor(strArr[0]), strArr[1]);
                mList.add(item);
            }
        }
        catch (IOException e)
        {
            Log.e(TAG, "Read color name list failed!");
        }
    };
    
    public static ColorNameListHelper getInstance(Context context)
    {
        if (instance == null)
            instance = new ColorNameListHelper(context.getApplicationContext());
        
        return instance;
    }
    
    /**
     * Get the color name item at indicated position
     * 
     * @param position
     * 
     * @return the color name item at that position
     */
    public ColorNameItem get(int position)
    {
        return mList.get(position);
    }
    
    /**
     * Count the total number color name items recorded
     * 
     * @return the number of color name items
     */
    public int count()
    {
        return mList.size();
    }
    
    
    public ColorNameItem[] getAll()
    {
        return mList.toArray(new ColorNameItem[]{});
    }
}
