package com.tzapps.common.utils;

import java.io.IOException;

import android.content.Context;
import android.media.ExifInterface;
import android.net.Uri;
import android.util.Log;

public class MediaHelper
{
    private static final String TAG = "MediaHelper";
    
    public static int getPictureOrientation(String filename)
    {
        if (filename == null)
            return -1;
        
        int orientation = ExifInterface.ORIENTATION_NORMAL;

        try
        {
            ExifInterface exif = new ExifInterface(filename);
            orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
        }
        catch (IOException ioe)
        {
            Log.e(TAG, "get picture orientation failed");
        }
        
        return orientation;
    }
    
    public static int getPictureOrientation(Context context, Uri uri)
    {
        String filePath = UriUtils.getUriPath(context, uri);
        
        return filePath == null ? -1 : getPictureOrientation(filePath);
    }

}
