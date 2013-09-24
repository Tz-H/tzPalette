package com.tzapps.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;
import android.view.View;

public class BitmapUtils
{
    static final String TAG = "BitmapUtils";
    
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
            Log.d(TAG, "save bitmap to" + file.getPath());
        }
        
        return file;
    }
    
    public static byte[] convertBitmapToByteArray(Bitmap bitmap)
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.JPEG, 85, outputStream);
        return outputStream.toByteArray();
    }
}
