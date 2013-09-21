package com.tzapps.tzpalette.data;

import android.graphics.Bitmap;
import android.util.Log;

import com.tzapps.tzpalette.algorithm.ClusterCenter;
import com.tzapps.tzpalette.algorithm.ClusterPoint;
import com.tzapps.tzpalette.algorithm.KMeansProcessor;
import com.tzapps.tzpalette.utils.BitmapUtils;
import com.tzapps.tzpalette.utils.ColorUtils;

public class PaletteDataHelper
{
    private static final String TAG = "PaletteDataHelper";
    
    private static final int THUMB_MAX_WIDTH  = 500;
    private static final int THUMB_MAX_HEIGHT = 500;
    
    public enum PaletteDataHelper_DataType
    {
        ColorToRGB,
        ColorToHSV,
        ColorToHSL
    };
    
    /**
     * Convert the color value into Cluster point values
     */
    private int[] convertColor(int color, PaletteDataHelper_DataType type)
    {
        switch(type)
        {
            case ColorToRGB:
                return ColorUtils.colorToRGB(color);
                
            case ColorToHSV:
                return ColorUtils.colorToHSV(color);
                
            case ColorToHSL:
                return ColorUtils.colorToHSL(color);
               
            default:
                int[] values = new int[1];
                values[0] = color;
                return values;
        }
    }
    
    private static PaletteDataHelper instance = new PaletteDataHelper();
    
    private PaletteDataHelper(){};
    
    public static PaletteDataHelper getInstance()
    {
        return instance;
    }
    
    public void analysis(PaletteData data, boolean reset)
    {
        analysis(data, reset, 8, 5, PaletteDataHelper_DataType.ColorToHSL);
    }
    
    /**
     * Analysis the thumb in palette data to pick up its main colors
     * automatically.
     * 
     * @param data          the PaletteData data to analysis
     * @param reset         flag to indicate if remove the existing colors
     * @param numOfColors   number of colors to pick up (default value is 10)
     * @param deviation     deviation to control how precise the analysis it is (the default value is 5)
     * @param dataType      the color type (RGB or HSV) when do the color analysis
     */
    public void analysis(PaletteData data, boolean reset, int numOfColors, int deviation, PaletteDataHelper_DataType dataType)
    {
        Log.d(TAG, "palette data analysis()");
        
        Bitmap bitmap = data.getThumb();
        
        assert(bitmap != null);
        
        if (bitmap == null)
        {
            Log.e(TAG, "palette data doesn't have a thumb!");
            return;
        }
        
        if (reset)
            data.clearColors();
        
        // initialization the pixel data
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        
        // scale the bitmap to limit the k-mean processor with a reasonable process time
        if (width > THUMB_MAX_WIDTH || height > THUMB_MAX_HEIGHT)
        {
            bitmap = BitmapUtils.resizeBitmapToFitFrame(bitmap, THUMB_MAX_WIDTH, THUMB_MAX_HEIGHT);
            width = bitmap.getWidth();
            height = bitmap.getHeight();
        }
        
        int[] inPixels = new int[width*height];
        bitmap.getPixels(inPixels, 0, width, 0, 0, width, height);
        
        ClusterPoint[] points = new ClusterPoint[inPixels.length];
        for (int i = 0; i < inPixels.length; i++)
            points[i] = new ClusterPoint(convertColor(inPixels[i], dataType));
        
        KMeansProcessor proc = new KMeansProcessor(numOfColors, deviation, /*maxRound*/99);   
        proc.processKMean(points);
        
        for (ClusterCenter center : proc.getClusterCenters())
        {
            int[] values = center.getValues();
            
            int color = 0;
            
            switch(dataType)
            {
                case ColorToRGB:
                    color = ColorUtils.rgbToColor(values);
                    break;
                
                case ColorToHSV:
                    color = ColorUtils.hsvToColor(values);
                    break;
                    
                case ColorToHSL:
                    color = ColorUtils.hslToColor(values);
                    break;
            }
            
            data.addColor(color);
        }
    }
}
