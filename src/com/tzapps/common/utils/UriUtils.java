package com.tzapps.common.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.OpenableColumns;

public class UriUtils
{
    /**
     * Get a uri's user-friendly display name
     * 
     * @param context the application context
     * @param uri     the uri to query
     * 
     * @return a user-friendly display name
     */
    public static String getUriDisplayName(Context context, Uri uri)
    {
        String displayName = null;
        
        String scheme = uri.getScheme();
        
        if (scheme.startsWith("content"))
        {
            String[] proj = {OpenableColumns.DISPLAY_NAME};
            Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
            
            if (cursor != null)
            {
                int columnIndex = cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME);
                cursor.moveToFirst();
                displayName = cursor.getString(columnIndex);
                
                cursor.close();
            }
        }
        else if (scheme.startsWith("file"))
        {
            displayName = uri.getLastPathSegment();
        }
        
        return displayName;
    }
    
    /**
     * Get a uri's file path
     * 
     * @param context the application context
     * @param uri     the uri to query
     * 
     * @return the file path
     */
    public static String getUriPath(Context context, Uri uri)
    {
        String filePath = null;
        
        String scheme = uri.getScheme();
        
        if (scheme.startsWith("content"))
        {
            String[] projection = {MediaStore.Files.FileColumns.DATA};
            
            /* 
             * FIXME 2013-10-24 Tianzi Hou
             * 
             * we cannot get file path if it is from 
             *  content://com.google.android.gallery3d.provider
             * i.e. the Picasa service
             */
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
        else if (scheme.startsWith("file"))
        {
            filePath = uri.getPath();
        }
        
        return filePath;
    }
}
