package com.tzapps.tzpalette.utils;

import java.io.File;
import java.io.FileOutputStream;

import android.graphics.Bitmap;
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
    
    private static boolean saveBitmapToFile(Bitmap bitmap, File file)
    {
        try
        {
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
    
    public static void saveBitmapToSDCard(Bitmap bitmap, String name)
    {
        String path = Environment.getExternalStorageDirectory().toString();
        
        File file = new File(path, name + ".jpg");
        
        if(saveBitmapToFile(bitmap, file))
        { 
            Log.d(TAG, "save bitmap to" + file.getPath());
        }
    }
}
