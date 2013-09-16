package com.tzapps.tzpalette.utils;

import android.graphics.Color;

public class ColorUtils
{

    public static String colorToRGBString(int color)
    {
        StringBuffer buffer = new StringBuffer();
        
        buffer.append("[r,g,b]:[")
        .append(Color.red(color)).append(",")
        .append(Color.green(color)).append(",")
        .append(Color.blue(color)).append("]");
        
        return buffer.toString();
    }
    
    public static String colorToHtml(int color)
    {
        return String.format("#%06X", (0xFFFFFF & color));
    }
}
