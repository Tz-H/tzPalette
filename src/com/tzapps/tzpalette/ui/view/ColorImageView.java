package com.tzapps.tzpalette.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import com.tzapps.common.ui.view.TouchImageView;
import com.tzapps.common.ui.view.TouchImageView.OnAdvancedClickListener;

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
        this.setOnAdvancedClickListener(this);
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
        // TODO analyais the color at the indicated location
        
        int color = Color.BLACK;
        
        if (mCallback != null)
            mCallback.onColorImageClicked(this, xPos, yPos, color);
    }
    
    public void setOnColorImageClickListener(OnColorImageClickListener listener)
    {
        mCallback = listener;
    }

}
