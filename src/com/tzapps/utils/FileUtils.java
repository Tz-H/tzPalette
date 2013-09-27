package com.tzapps.utils;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.net.Uri;
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
    
}

