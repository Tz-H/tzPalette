package com.tzapps.tzpalette.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

public class PaletteColorView extends ImageView
{
    int   mColor;
    Paint fillPaint = new Paint();
    
    private void init()
    {
        fillPaint = new Paint();
    }

    public PaletteColorView(Context context)
    {
        super(context);
        init();
    }

    public PaletteColorView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public PaletteColorView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }

    public void setColor(int color)
    {
        mColor = color;
        this.invalidate();
    }

    public int getColor()
    {
        return mColor;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        float w = getWidth();
        float h = getHeight();
        float r = Math.min(w/2, h/2);
        float x = w / 2;
        float y = h / 2;
        
        fillPaint.setColor(mColor);
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setAntiAlias(true);
        
        canvas.drawCircle(x, y, r, fillPaint);
    }

}
