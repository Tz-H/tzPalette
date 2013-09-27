package com.tzapps.utils;

import java.io.IOException;

import android.content.Context;
import android.database.Cursor;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

public class MediaHelper
{
    private static final String TAG = "MediaHelper";
    
    public static int getPictureOrientation(String filename)
    {
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
        String filePath = getUriPath(context, uri);
        
        return filePath == null ? -1 : getPictureOrientation(filePath);
    }

    private static String getUriPath(Context context, Uri uri)
    {
        String filePath = null;
        
        if (uri.getScheme().toString().compareTo("content") == 0)
        {
            String[] projection = {MediaStore.Images.Media.DATA};
            
            filePath = null;
            Cursor cursor = context.getContentResolver().query(uri, projection, null, null,null);
            
            if (cursor != null)
            {
                int column_index = cursor.getColumnIndexOrThrow(projection[0]);
                cursor.moveToFirst();
                filePath = cursor.getString(column_index);
                cursor.close();
            }
        }
        else if (uri.getScheme().compareTo("file") == 0)
        {
            filePath = uri.getPath();
        }
        
        return filePath;
    }
}
