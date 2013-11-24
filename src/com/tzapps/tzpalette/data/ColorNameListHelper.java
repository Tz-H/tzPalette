package com.tzapps.tzpalette.data;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;

import com.tzapps.common.utils.ColorUtils;
import com.tzapps.common.utils.StringUtils;
import com.tzapps.tzpalette.R;
import com.tzapps.tzpalette.algorithm.ClusterPoint;
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
        
        String[] names = context.getResources().getStringArray(R.array.color_name_list_names);
        String[] values = context.getResources().getStringArray(R.array.color_name_list_values);
        
        assert(names.length == values.length);
        
        for (int i = 0; i < names.length; i++)
        {
            ColorNameItem item = new ColorNameItem(Color.parseColor(values[i]), names[i]);
            mList.add(item);
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
    
    /**
     * Get all colorNameList in the order of the indicated sorter
     * @param sorter the color name item sorter
     * @return the sorted color name array
     */
    public ColorNameItem[] getAll()
    {
        return mList.toArray(new ColorNameItem[]{});
    }
    
    /**
     * Given a color name query, search for matched color name
     * 
     * @param query the query for color name
     * 
     * @return the ColorNameItem array with the color name match the query
     */
    public ColorNameItem[] search(String query)
    {
        List<ColorNameItem> list = new ArrayList<ColorNameItem>();
        
        for (int i = 0; i < mList.size(); i++)
        {
            ColorNameItem item = mList.get(i);
            
            if (StringUtils.containsIgnoreCase(item.name, query))
                list.add(item);
        }
        
        return list.toArray(new ColorNameItem[]{});
    }
    
    /**
     * Given a color, to get its similar colors from the color name list
     * 
     * @param color        the given color value
     * @param deviation    the deviation
     * @return the array with similar colors
     */
    public ColorNameItem[] getSimilar(int color, int deviation)
    {
        List<ColorNameItem> list = new ArrayList<ColorNameItem>();
        
        for (int i = 0; i < mList.size(); i++)
        {
            ColorNameItem item = mList.get(i);
            
            if (isColorSimilar(item.color, color, deviation))
                list.add(item);
        }
        
        return list.toArray(new ColorNameItem[]{});
    }
    
    private boolean isColorSimilar(int color_1, int color_2, int deviation)
    { 
        int [] hsv_1 = ColorUtils.colorToHSV(color_1);
        int [] hsv_2 = ColorUtils.colorToHSV(color_2);
        
        ClusterPoint point1 = new ClusterPoint(hsv_1);
        ClusterPoint point2 = new ClusterPoint(hsv_2);
        
        return ClusterPoint.equals(point1, point2, deviation);
    }
}
