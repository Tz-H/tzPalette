package com.tzapps.tzpalette.ui.task;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.tzapps.tzpalette.R;
import com.tzapps.tzpalette.data.PaletteDataHelper;

public class PaletteThumbWorkerTask extends AsyncTask<Long, Void, Bitmap>
{
    private final WeakReference<ImageView> imageViewReference;
    
    public enum ThumbSize
    {
        Large,
        Small;
    };
    
    public long data = 0;
    
    private Context mContext;
    private boolean enableAnim;
    private ThumbSize thumbSize;
    
    public PaletteThumbWorkerTask(Context context, ImageView imageView)
    {
        mContext = context;
        // Use a WeakReference to ensure the ImageView can be garbage collected
        imageViewReference = new WeakReference<ImageView>(imageView);
        thumbSize = ThumbSize.Large;
        enableAnim = false;
    }
    
    /**
     * (En|dis)able the fade in animation effect when set the thumb on image view
     * 
     * @param enabled
     */
    public void setAnimEnabled(boolean enabled)
    {
        enableAnim = enabled;
    }
    
    /**
     * Set the thumb size to fetch
     * 
     * @param size
     */
    public void setThumbSize(ThumbSize size)
    {
        thumbSize = size;
    }
    
    // Decode image in background
    @Override
    protected Bitmap doInBackground(Long...params)
    {
        data = params[0];
        
        switch (thumbSize)
        {
            case Small:
                return PaletteDataHelper.getInstance(mContext).getThumbSmall(data);
            
            default:
            case Large:
                return PaletteDataHelper.getInstance(mContext).getThumb(data);
        }
    }
    
    // Once complete, see if ImageView is still around and set bitmap
    @Override
    protected void onPostExecute(Bitmap bitmap)
    {
        if (isCancelled())
            bitmap = null;
        
        if (imageViewReference != null && bitmap != null)
        {
            final ImageView imageView = imageViewReference.get();
            final PaletteThumbWorkerTask workerTask = getPaletteThumbWorkerTask(imageView);
            
            if (this == workerTask && imageView != null)
            {
                imageView.setImageBitmap(bitmap);
                
                if (enableAnim)
                {
                    Animation fadeInAnim = AnimationUtils.loadAnimation(mContext, R.anim.fade_in_anim);
                    imageView.startAnimation(fadeInAnim);
                }
            }
        }
    }
    
    private static PaletteThumbWorkerTask getPaletteThumbWorkerTask(ImageView imageView) 
    {
        if (imageView != null) 
        {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable)
            {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getPaletteThumbWorkerTask();
            }
         }
         return null;
    }
    
    public static boolean cancelPotentialWork(long data, ImageView imageView)
    {
        final PaletteThumbWorkerTask workTask = getPaletteThumbWorkerTask(imageView);
        
        if (workTask != null)
        {
            final long thumbData = workTask.data;
            
            if (thumbData != data)
            {
                // Cancel previous task
                workTask.cancel(true);
            }
            else
            {
                // The same work is already in process
                return false;
            }
        }
        
        // No task associate with the ImageView, or an existing task was cancelled
        return true;
    }
    
    public static class AsyncDrawable extends BitmapDrawable
    {
        private final WeakReference<PaletteThumbWorkerTask> workTaskReference;
        
        public AsyncDrawable(Resources res, Bitmap bitmap, PaletteThumbWorkerTask workerTask)
        {
            super(res, bitmap);
            workTaskReference = new WeakReference<PaletteThumbWorkerTask>(workerTask);
        }
        
        public PaletteThumbWorkerTask getPaletteThumbWorkerTask()
        {
            return workTaskReference.get();
        }
    }
}
