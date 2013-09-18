package com.tzapps.tzpalette.data;

import android.graphics.Bitmap;
import android.util.Log;

import com.tzapps.tzpalette.algorithm.ClusterCenter;
import com.tzapps.tzpalette.algorithm.KMeansProcessor;
import com.tzapps.tzpalette.algorithm.KMeansProcessor.KMeansProcessor_DataType;
import com.tzapps.tzpalette.utils.ColorUtils;

public class PaletteDataHelper
{
    private static final String TAG = "PaletteDataHelper";
    
    private static PaletteDataHelper instance = new PaletteDataHelper();
    
    private PaletteDataHelper(){};
    
    public static PaletteDataHelper getInstance()
    {
        return instance;
    }
    
    public void analysis(PaletteData data, boolean reset)
    {
        analysis(data, reset, 10, 5, KMeansProcessor_DataType.ColorToRGB);
    }
    
    /**
     * Analysis the thumb in palette data to pick up its main colors
     * automatically.
     * 
     * @param data          the PaletteData data to analysis
     * @param reset         flag to indicate if remove the existing colors
     * @param numOfColors   number of colors to pick up (default value is 10)
     * @param deviation     deviation to control how precise the analysis it (default value is 5)
     * @param dataType      the color type (RGB or HSV) when do the color analysis
     */
    public void analysis(PaletteData data, boolean reset, int numOfColors, int deviation, KMeansProcessor_DataType dataType)
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
        
        KMeansProcessor proc = new KMeansProcessor(numOfColors, deviation, dataType);   
        
        // initialization the pixel data
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] inPixels = new int[width*height];
        bitmap.getPixels(inPixels, 0, width, 0, 0, width, height);
        
        proc.processKMean(inPixels);
        
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
            }
            
            data.addColor(color);
        }
    }
}
