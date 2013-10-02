package com.tzapps.tzpalette.data;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.tzapps.common.utils.BitmapUtils;
import com.tzapps.common.utils.ColorUtils;
import com.tzapps.tzpalette.R;
import com.tzapps.tzpalette.algorithm.ClusterCenter;
import com.tzapps.tzpalette.algorithm.ClusterPoint;
import com.tzapps.tzpalette.algorithm.KMeansProcessor;
import com.tzapps.tzpalette.db.PaletteDataSource;
import com.tzapps.tzpalette.ui.SettingsFragment;

public class PaletteDataHelper
{
    private static final String TAG = "PaletteDataHelper";
    
    private static final int PICTURE_ANALYSIS_MAX_WIDTH  = 500;
    private static final int PICTURE_ANALYSIS_MAX_HEIGHT = 500;
    
    private static PaletteDataHelper instance;
    
    private Context mContext;
    private PaletteDataSource mDataSource;
    
    private PaletteDataHelper(Context context)
    {
        mContext = context;
        mDataSource = new PaletteDataSource(context);
    };
    
    public static PaletteDataHelper getInstance(Context context)
    {
        if (instance == null)
            instance = new PaletteDataHelper(context);
        
        return instance;
    }
    
    public void delete(PaletteData data)
    {
        mDataSource.open(true);
        
        mDataSource.delete(data);
        
        mDataSource.close();
    }
    
    public void delete(long id)
    {
        mDataSource.open(true);
        
        mDataSource.delete(id);
        
        mDataSource.close();
    }
    
    public void add(PaletteData data)
    {
        mDataSource.open(true);
        
        mDataSource.add(data);
        
        mDataSource.close();
    }
    
    public Bitmap getThumb(long id)
    {
        Bitmap bitmap = null;
        
        mDataSource.open(false);
        
        bitmap = mDataSource.getThumb(id);
        
        mDataSource.close();
        
        return bitmap;
    }
    
    public PaletteData get(long id)
    {
        PaletteData data = null;
        
        mDataSource.open(false);
        
        data = mDataSource.get(id);
        
        mDataSource.close();
        
        return data;
    }
    
    public void update(PaletteData data, boolean updateThumb)
    {
        mDataSource.open(true);
        
        mDataSource.update(data, updateThumb);
        
        mDataSource.close();
    }
    
    public List<PaletteData> getAllData()
    {
        List<PaletteData> dataList = null;
        
        mDataSource.open(false);
        
        dataList = mDataSource.getAllPaletteData();
        
        mDataSource.close();
        
        return dataList;
    }
    
    private int getInt(int resId)
    {
        return mContext.getResources().getInteger(resId);
    }
    
    private String getString(int resId)
    {
        return mContext.getResources().getString(resId);
    }

    public void analysis(PaletteData data, boolean reset)
    {
        int numOfColors, deviation;
        PaletteDataType dataType; 
        
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        
        numOfColors = sp.getInt(SettingsFragment.KEY_PREF_COLOR_NUMBER, getInt(R.integer.pref_setColorNumber_default));
        String accuracy = sp.getString(SettingsFragment.KEY_PREF_ANALYSIS_ACCURACY, getString(R.string.pref_analysisColorAccuracy_default));
        
        if (accuracy.equalsIgnoreCase("HIGH"))
            deviation = 0;
        else if (accuracy.equalsIgnoreCase("NORMAL"))
            deviation = 5;
        else if (accuracy.equalsIgnoreCase("LOW"))
            deviation = 10;
        else
            deviation = 5;
        
        String colorType = sp.getString(SettingsFragment.KEY_PREF_COLOR_TYPE, getString(R.string.pref_analysisColorType_default));
        dataType = PaletteDataType.fromString(colorType);
        
        boolean enableKpp = sp.getBoolean(SettingsFragment.KEY_PREF_ENABLE_KPP, true);
        
        Log.d(TAG, "analysis: numOfColors=" + numOfColors + " deviation=" + deviation + " dataType=" + dataType + " enableKpp=" + enableKpp);
        
        analysis(data, reset, numOfColors, deviation, dataType, enableKpp);
    }
    
    /**
     * Analysis the thumb in palette data to pick up its main colors
     * automatically.
     * 
     * @param data          the PaletteData data to analysis
     * @param reset         flag to indicate if remove the existing colors
     * @param numOfColors   number of colors to pick up
     * @param deviation     deviation to control how precise the analysis it is
     * @param dataType      the color type (RGB or HSV) when do the color analysis
     * @param enableKpp     flag to indicate if enable the kpp process
     */
    public void analysis(PaletteData data, boolean reset, int numOfColors, int deviation, PaletteDataType dataType, boolean enableKpp)
    {
        Log.d(TAG, "palette data analysis()");
        
        Bitmap bitmap;
        
        /* we could try to fetch the original image from uri first,
         * and if it doesn't exist, then we could get it from palette 
         * data's thumb, well then the analysis accuracy would be ba
         */
        if (data.getImageUrl() != null)
            bitmap = BitmapUtils.getBitmapFromUri(mContext, Uri.parse(data.getImageUrl()));
        else
            bitmap = data.getThumb();
        
        assert(bitmap != null);
        
        if (bitmap == null)
        {
            Log.e(TAG, "cannot load a picture from palette data!");
            return;
        }
        
        if (reset)
            data.clearColors();
        
        // initialization the pixel data
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        
        // scale the bitmap to limit its size, so the k-mean processor could have a reasonable process time
        if (width > PICTURE_ANALYSIS_MAX_WIDTH || height > PICTURE_ANALYSIS_MAX_HEIGHT)
        {
            bitmap = BitmapUtils.resizeBitmapToFitFrame(bitmap, PICTURE_ANALYSIS_MAX_WIDTH, PICTURE_ANALYSIS_MAX_HEIGHT);
            width = bitmap.getWidth();
            height = bitmap.getHeight();
        }
        
        int[] inPixels = new int[width*height];
        bitmap.getPixels(inPixels, 0, width, 0, 0, width, height);
        
        ClusterPoint[] points = new ClusterPoint[inPixels.length];
        for (int i = 0; i < inPixels.length; i++)
            points[i] = new ClusterPoint(convertColor(inPixels[i], dataType));
        
        KMeansProcessor proc = new KMeansProcessor(numOfColors, deviation, /*maxRound*/99, false);   
        proc.processKMean(points);
        
        for (ClusterCenter center : proc.getClusterCenters())
        {
            /*
             * So we use the nearest point values rather than the
             * cluster center directly, in case that some times
             * there will be some weird non-existing colors caught
             * after the analysis...
             */
            int[] values = center.getNearestPoint().getValues();
            
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
    
    /**
     * Convert the color value into Cluster point values
     */
    private int[] convertColor(int color, PaletteDataType type)
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
    
}
