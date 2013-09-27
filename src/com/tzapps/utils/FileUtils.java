package com.tzapps.utils;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

public class FileUtils
{
    private static final String TAG = "FileUtils";

    public static File getTempFile(Context context, String url)
    {
        File file = null;
        try
        {
            String fileName = Uri.parse(url).getLastPathSegment();
            file = File.createTempFile(fileName, null, context.getCacheDir());
        }
        catch (IOException e)
        {
            Log.e(TAG, "getTempFile failed");
        }

        return file;
    }
    
    public static String getFileNameByUri(Context context, Uri uri)
    {
        String fileName = null;
        Uri filePathUri = uri;
        
        if (uri.getScheme().toString().compareTo("content") == 0)
        {      
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            if (cursor.moveToFirst())
            {
                //Instead of "MediaStore.Images.Media.DATA" can be used "_data"
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                filePathUri = Uri.parse(cursor.getString(column_index));
                fileName = filePathUri.getLastPathSegment().toString();
            }
            
            cursor.close();
        }
        else if (uri.getScheme().compareTo("file") == 0)
        {
            fileName = filePathUri.getLastPathSegment().toString();
        }

        return fileName;
    }
}
