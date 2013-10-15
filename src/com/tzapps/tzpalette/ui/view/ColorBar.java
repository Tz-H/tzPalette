package com.tzapps.tzpalette.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

import com.tzapps.common.utils.ColorUtils;
import com.tzapps.tzpalette.debug.MyDebug;

public class ColorBar extends ImageView implements OnTouchListener
{
    private static final String TAG = "ColorBar";
    
    public interface OnColorBarChangedListener
    {
        public void onColorChanged(ColorBar colorBar, int color);
    }
    
    public enum ColorBarType
    {
        RGB_R,
        RGB_G,
        RGB_B,
        HSL_H,
        HSL_S,
        HSL_L,
        NONE
    }
    
    private int mColor;
    private ColorBarType mType;
    private Paint mPaint;
    private OnColorBarChangedListener mCallback;
    
    private void init()
    {
        mPaint = new Paint();
        mType = ColorBarType.NONE;
        
        setOnTouchListener(this);
    }
    
    public ColorBar(Context context)
    {
        super(context);
        init();
    }

    public ColorBar(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public ColorBar(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }

    public void setColor(int color)
    {
        mColor  = color;
        update();
    }

    public int getColor()
    {
        return mColor;
    }
    
    public void setType(ColorBarType type)
    {
        mType = type;
        update();
    }
    
    public ColorBarType getType()
    {
        return mType;
    }
    
    public void setOnColorChangeListener(OnColorBarChangedListener listenr)
    {
        mCallback = listenr;
    }
    
    public void update()
    {
        invalidate();
    }
    
    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        int w, h;
        int x, y;
        
        switch(event.getAction())
        {
            case MotionEvent.ACTION_MOVE:
                w = getWidth();
                h = getHeight();
                x = (int)event.getX();
                y = (int)event.getY();
                
                if (x < 0 || x > w || y < 0 | y > h) 
                    return false;
                
                int color = getColorAt(x,y,w,h);
                setColor(color);
                
                if (mCallback != null)
                    mCallback.onColorChanged(this, color);
                
                break;
        }
        
        return false;
    }
    
    @Override
    protected void onDraw(Canvas canvas)
    {
        int w = getWidth();
        int h = getHeight();
        
        for (int i = 0; i < w; i++)
        {
            int color = getColorAt(i,h,w,h);
            
            if (color == mColor)
            {
                mPaint.setColor(Color.WHITE);
                mPaint.setStrokeWidth(3);
            }
            else
            {
                mPaint.setColor(color);
            }
                
            canvas.drawLine(i, 0, i, h, mPaint);
        }
    }
    
    private int getColorAt(int xPos, int yPos, int width, int height)
    {
        int[] rgb = ColorUtils.colorToRGB(mColor);
        int[] hsl = ColorUtils.colorToHSL(mColor);
        int color, r, g, b, h, s, l;
        
        switch(mType)
        {
            case RGB_R:
                r = xPos * 255 / width;
                g = rgb[1];
                b = rgb[2];
                color = ColorUtils.rgbToColor(r,g,b);
                break;
                
            case RGB_G:
                r = rgb[0];
                g = xPos * 255 / width;
                b = rgb[2];
                color = ColorUtils.rgbToColor(r, g, b);
                break;
                
            case RGB_B:
                r = rgb[0];
                g = rgb[1];
                b = xPos * 255 / width;
                color = ColorUtils.rgbToColor(r, g, b);
                break;
                
            case HSL_H:
                h = xPos * 360 / width;
                s = hsl[1];
                l = hsl[2];
                color = ColorUtils.hslToColor(h,s,l);
                break;
                
            case HSL_S:
                h = hsl[0];
                s = xPos * 100 / width;
                l = hsl[2];
                color = ColorUtils.hslToColor(h, s, l);
                break;
                
            case HSL_L:
                h = hsl[0];
                s = hsl[1];
                l = xPos * 100 / width;
                color = ColorUtils.hslToColor(h, s, l);
                
                break;
                
            default:
            case NONE:
                color = Color.LTGRAY;
                break;
        }
        
        return color;
    }

}
