package com.tzapps.common.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.Environment;
import android.util.Log;
import android.view.View;

public class BitmapUtils
{
    static final String TAG = "BitmapUtils";
    
    /**
     * Get a rotated bitmap based on the indicated orientation
     * 
     * @param source        the source bitmap
     * @param orientation   the orientation code to rotate
     * @return              the rotated bitmap
     */
    public static Bitmap getRotatedBitmap(Bitmap source, int orientation)
    {
        Bitmap bitmap   = null;
        Matrix matrix   = new Matrix();
        int    rotation = 0;
        
        switch(orientation)
        {
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotation = 180;
                break;
                
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotation = 90;
                break;
                
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotation = 270;
                break;
                
            default:
                rotation = 0;
        }
        
        matrix.postRotate(rotation);
        bitmap = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
        
        return bitmap;
    }
    
    /**
     * Get a rounded corner bitmap.
     * 
     * @param bitmap the source bitmap
     * @param pixels the round radius in pixels
     * @return the bitmap with rounded corner
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels)
    {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int   color   = 0xff424242;
        final Paint paint   = new Paint();
        final Rect  rect    = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF   = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
    
    /**
     * Get the bitmap from the indicated http url address
     * 
     * @param url the http url address
     * @return the bitmap retrieved
     */
    public static Bitmap getBitmapFromInternet(String url)
    {
        final AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
        final HttpGet getRequest = new HttpGet(url);
        
        try
        {
            HttpResponse response = client.execute(getRequest);
            final int statusCode = response.getStatusLine().getStatusCode();
            
            if (statusCode != HttpStatus.SC_OK)
            {
                Log.w(TAG, "Error " + statusCode + " while retrieving bitmap from " + url);
                return null;
            }
            
            final HttpEntity entity = response.getEntity();
            if (entity != null)
            {
                InputStream inputStream = null;
                try
                {
                    inputStream = entity.getContent();
                    final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    return bitmap;
                }
                finally
                {
                    if (inputStream != null)
                        inputStream.close();
                    
                    entity.consumeContent();
                }
            }
        }
        catch (Exception e)
        {
            // Could provide a more explicit error message for IOException or IllegalStateException
            getRequest.abort();
            Log.w(TAG, "Error while retrieving bitmap from " + url);
        }
        finally
        {
            if (client != null)
                client.close();
        }
        
        return null;
    }
    
    /**
     * Get the bitmap from the indicated view
     * 
     * @param view the view to draw
     * @return the bitmap
     */
    public static Bitmap getBitmapFromView(View view)
    {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Drawable bg = view.getBackground();
        if (bg != null)
            bg.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        
        view.draw(canvas);
        
        return bitmap;
    }
    
    public static Bitmap getBitmapFromUri(Context context, Uri uri, int maxSize)
    {
        Bitmap bitmap = null;
        InputStream imageStream;
        
        try
        {
            imageStream = context.getContentResolver().openInputStream(uri);
            
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(imageStream, null, options);
            
            //Find the correct scale value. It should be the power of 2.
            int scale = 1;
            if (options.outWidth > maxSize || options.outHeight > maxSize)
            {
                scale = (int)Math.pow(2, (int)Math.round(Math.log(maxSize / 
                        (double)Math.max(options.outWidth, options.outHeight))/ Math.log(0.5)));
            }
            
            imageStream.close();
            
            //Decode with inSampleSize
            BitmapFactory.Options options2 = new BitmapFactory.Options();
            options2.inSampleSize = scale;
            
            imageStream = context.getContentResolver().openInputStream(uri);
            bitmap = BitmapFactory.decodeStream(imageStream, null, options2);
            
            imageStream.close();
        }
        catch (IOException e)
        {
            Log.e(TAG, "getBitmapFromUri failed. uri is " + uri.toString());
        }
        catch (SecurityException se)
        {
            Log.e(TAG, "getBitmapFromUri failed for security reason. uri is " + uri.toString() + " message: " + se.getLocalizedMessage());
        }
        
        return bitmap;
    }
    
    public static Bitmap resizeBitmapToFitFrame(Bitmap bitmap, int frameWidth, int frameHeight)
    {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        
        int targetW = frameWidth;
        int targetH = frameHeight;
        
        float scale = 0.0f;
        
        if (width > height)
        {
            scale = ((float) targetW) / width;
            targetH = (int)Math.round(height * scale);
        }
        else
        {
            scale = ((float) targetH) / height;
            targetW = (int)Math.round(width * scale);
        }
        
        return Bitmap.createScaledBitmap(bitmap, targetW, targetH, false);
    }
    
    public static boolean saveBitmapToFile(Bitmap bitmap, File file)
    {
        assert(bitmap != null);
        
        try
        {
            File path = file.getParentFile();
            // build the directory
            if (file.getParent() != null && !path.isDirectory())
                path.mkdirs();
            
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out);
            
            
            out.flush();
            out.close();
            
            return true;
        }
        catch (Exception e)
        {
            Log.e(TAG, "failed to save bitmap " + file.getPath());
            return false;
        }
    }
    
    public static File saveBitmapToSDCard(Bitmap bitmap, String name)
    {
        String path = Environment.getExternalStorageDirectory().toString();
        
        File file = new File(path, name + ".jpg");
        
        if(saveBitmapToFile(bitmap, file))
        { 
            Log.d(TAG, "save bitmap to " + file.getPath());
        }
        
        return file;
    }
    
    /**
     * Convert bitmap to byte array
     * 
     * @param bitmap    the bitmap to convert
     * @param quality   the bitmap convert quality (0 to 100)
     * @return the byte array conerted
     */
    public static byte[] convertBitmapToByteArray(Bitmap bitmap, int quality)
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.JPEG, quality, outputStream);
        return outputStream.toByteArray();
    }
}
