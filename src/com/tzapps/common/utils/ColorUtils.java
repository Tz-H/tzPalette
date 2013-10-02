package com.tzapps.common.utils;

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
    
    /**
     *  Convert a RGB Color to it corresponding HSL values.
     *
     *  @return an array containing the 3 HSL values.
     */
    public static float[] hslFromRGB(int color)
    {
        //  Get RGB values in the range 0 - 1
        float r = (float)Color.red(color) / 255.0f;
        float g = (float)Color.green(color) / 255.0f;
        float b = (float)Color.blue(color) / 255.0f;

        //  Minimum and Maximum RGB values are used in the HSL calculations
        float min = Math.min(r, Math.min(g, b));
        float max = Math.max(r, Math.max(g, b));

        //  Calculate the Hue
        float h = 0;

        if (max == min)
            h = 0;
        else if (max == r)
            h = ((60 * (g - b) / (max - min)) + 360) % 360;
        else if (max == g)
            h = (60 * (b - r) / (max - min)) + 120;
        else if (max == b)
            h = (60 * (r - g) / (max - min)) + 240;

        //  Calculate the Luminance
        float l = (max + min) / 2;

        //  Calculate the Saturation
        float s = 0;

        if (max == min)
            s = 0;
        else if (l <= .5f)
            s = (max - min) / (max + min);
        else
            s = (max - min) / (2 - max - min);

        return new float[] {h, s * 100, l * 100};
    }
    
    /**
     *  Convert RGB Color to a HSL values.
     *  H (Hue) is specified as degrees in the range 0 - 360.
     *  S (Saturation) is specified as a percentage in the range 1 - 100.
     *  L (Lumanance) is specified as a percentage in the range 1 - 100.
     *
     *  @param color the RGB color
     *  @return the HSL values
     */
    public static int[] colorToHSL(int color)
    {
        int[] hsl = new int[3];
        float[] hsl_float = hslFromRGB(color);
        
        hsl[0] = (int)Math.round(hsl_float[0]);
        hsl[1] = (int)Math.round(hsl_float[1]);
        hsl[2] = (int)Math.round(hsl_float[2]);
        
        return hsl;
    }
    
    /**
     *  Convert HSL values to a RGB Color.
     *
     *  @param h Hue is specified as degrees in the range 0 - 360.
     *  @param s Saturation is specified as a percentage in the range 1 - 100.
     *  @param l Lumanance is specified as a percentage in the range 1 - 100.
     */
    private static int hslToRGB(int hInt, int sInt, int lInt)
    {
        float h, s, l;
        
        h = (float)hInt;
        s = (float)sInt;
        l = (float)lInt;
        
        if (s < 0.0f || s > 100.0f)
        {
            String message = "Color parameter outside of expected range - Saturation";
            throw new IllegalArgumentException( message );
        }

        if (l < 0.0f || l > 100.0f)
        {
            String message = "Color parameter outside of expected range - Luminance";
            throw new IllegalArgumentException( message );
        }

        //  Formula needs all values between 0 - 1.

        h = h % 360.0f;
        h /= 360f;
        s /= 100f;
        l /= 100f;

        float q = 0;

        if (l < 0.5)
            q = l * (1 + s);
        else
            q = (l + s) - (s * l);

        float p = 2 * l - q;

        int r = (int) (Math.max(0, HueToRGB(p, q, h + (1.0f / 3.0f))) * 255);
        int g = (int) (Math.max(0, HueToRGB(p, q, h)) * 255);
        int b = (int) (Math.max(0, HueToRGB(p, q, h - (1.0f / 3.0f))) * 255);

        return Color.rgb(r, g, b);
    }
    
    private static float HueToRGB(float p, float q, float h)
    {
        if (h < 0) h += 1;

        if (h > 1 ) h -= 1;

        if (6 * h < 1)
        {
            return p + ((q - p) * 6 * h);
        }

        if (2 * h < 1 )
        {
            return  q;
        }

        if (3 * h < 2)
        {
            return p + ( (q - p) * 6 * ((2.0f / 3.0f) - h) );
        }

        return p;
    }
    
    /**
     *  Convert HSL values to a RGB Color with a default alpha value of 1.
     *  H (Hue) is specified as degrees in the range 0 - 360.
     *  S (Saturation) is specified as a percentage in the range 1 - 100.
     *  L (Lumanance) is specified as a percentage in the range 1 - 100.
     *
     *  @param hsl an array containing the 3 HSL values
     */
    public static int hslToColor(int[] hsl)
    {
        return hslToRGB(hsl[0], hsl[1], hsl[2]);
    }
}
