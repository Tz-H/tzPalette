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
    
    public static int[] colorToRGB(int color)
    {
        int[] rgb = new int[3];
        
        rgb[0] = Color.red(color);
        rgb[1] = Color.green(color);
        rgb[2] = Color.blue(color);
        
        return rgb;
    }
    
    public static int[] colorToHSV(int color)
    {
        int[] hsv = new int[3];
        float[] hsv_float = new float[3];
        
        Color.colorToHSV(color, hsv_float);
        
        hsv[0] = (int)hsv_float[0];
        hsv[1] = (int)(hsv_float[1] * 100);
        hsv[2] = (int)(hsv_float[2] * 100);
        
        return hsv;
    }
    
    public static int hsvToColor(int[] hsv)
    {
        assert(hsv.length == 3);
        
        float[] hsv_float = new float[3];
        
        hsv_float[0] = hsv[0];
        hsv_float[1] = (float)hsv[1] / 100;
        hsv_float[2] = (float)hsv[2] / 100;
        
        return Color.HSVToColor(hsv_float);
    }

    public static int rgbToColor(int[] rgb)
    {
        assert(rgb.length == 3);
        
        return Color.rgb(rgb[0], rgb[1], rgb[2]);
    }
}
