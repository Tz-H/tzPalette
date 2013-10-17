package com.tzapps.tzpalette.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;

import com.tzapps.common.ui.view.TouchImageView;
import com.tzapps.common.ui.view.TouchImageView.OnAdvancedClickListener;
import com.tzapps.common.utils.BitmapUtils;

public class ColorImageView extends TouchImageView implements OnAdvancedClickListener
{
    private static final String TAG = "ColorImageView";
    
    public interface OnColorImageClickListener
    {
        public void onColorImageClicked(ColorImageView view, int xPos, int yPos, int color);
    }
    
    private OnColorImageClickListener mCallback;
    
    private void init()
    {
        setOnAdvancedClickListener(this);
    }
    
    public ColorImageView(Context context)
    {
        super(context);
        init();
    }

    public ColorImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }
    
    @Override
    public void onAdvancedItemClick(TouchImageView view, int xPos, int yPos)
    {
        // TODO analysis the color at the indicated location
        Bitmap bitmap = BitmapUtils.getBitmapFromView(view);
        
        int color = bitmap.getPixel(xPos, yPos);
        
        if (mCallback != null)
            mCallback.onColorImageClicked(this, xPos, yPos, color);
    }
    
    public void setOnColorImageClickListener(OnColorImageClickListener listener)
    {
        mCallback = listener;
    }

}
