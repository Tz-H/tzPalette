package com.tzapps.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

import android.content.Context;
import android.net.Uri;

public class FileUtils
{
    private static final String TAG = "FileUtils";
    
    /**
     * Copy a uri file to indicated file path
     * 
     * @param context   the application context
     * @param uri       the uri file to copy
     * @param dest      the dest file 
     * @throws IOException
     */
    public static void copyUriTo(Context context, Uri uri, File dest) throws IOException
    {
        InputStream input = null;
        OutputStream output = null;
        
        try
        {
            input = context.getContentResolver().openInputStream(uri);
            output = new FileOutputStream(dest);
            
            copyFileUsingStream(input, output);
        }
        finally
        {
            input.close();
            output.close();
        }
    }
    
    /**
     * Copy a file by using the file channels, it could be 10x faster than the traditional way of
     * copying file by using the file streams.
     * 
     * @param src     the source file
     * @param dest    the destination file
     * @throws IOException
     */
    public static void copyFileUsingFileChannels(File src, File dest) throws IOException
    {
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        
        try
        {
            inputChannel = new FileInputStream(src).getChannel();
            outputChannel = new FileOutputStream(dest).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        }
        finally
        {
            inputChannel.close();
            outputChannel.close();
        }
    }
    
    
    /**
     * Copy a file by using the file streams
     * 
     * @param src     the source file
     * @param dest    the destination file
     * @throws IOException
     */
    public static void copyFileUsingFileStreams(File source, File dest) throws IOException
    {
        InputStream input = null;
        OutputStream output = null;
        
        try
        {
            input = new FileInputStream(source);
            output = new FileOutputStream(dest);
            
            copyFileUsingStream(input, output);
        }
        finally
        {
            input.close();
            output.close();
        }
    }
    
    private static void copyFileUsingStream(InputStream in, OutputStream out) throws IOException
    {
        byte[] buf = new byte[1024];
        int read;
        while ((read = in.read(buf)) != -1)
        {
            out.write(buf, 0, read);
        }
    }
    
}

