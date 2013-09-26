package com.tzapps.tzpalette.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.tzapps.tzpalette.R;

public class PaletteColorGrid extends GridView
{
    private final static String TAG = "PaletteColorGrid";
    
    private ColorAdapter mColorsAdapter;
    
    private void init(Context context)
    {
        mColorsAdapter = new ColorAdapter(context);
        setAdapter(mColorsAdapter);
    }

    public PaletteColorGrid(Context context)
    {
        super(context);
        init(context);
    }

    public PaletteColorGrid(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }

    public PaletteColorGrid(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init(context);
    }
    
    public void setColors(int colors[])
    {
        mColorsAdapter.clear();
        mColorsAdapter.setColors(colors);
    }
    
    public int getColor(int position)
    {
        return mColorsAdapter.getColor(position);
    }
    
    public void clear()
    {
        mColorsAdapter.clear();
    }
    
    private class ColorAdapter extends BaseAdapter
    {
        private List<Integer> mColors;
        private Context mContext;

        public ColorAdapter(Context c)
        {
            mContext = c;
            mColors = new ArrayList<Integer>();
        }
        
        public void clear()
        {
            mColors.clear();
            notifyDataSetChanged();
        }

        public int getColor(int position)
        {
            return mColors.get(position);
        }

        public void setColors(int[] colors)
        {
            for (int i = 0; i < colors.length; i++)
                mColors.add(colors[i]);
            
            notifyDataSetChanged();
        }

        public int getCount()
        {
            return mColors.size();
        }

        public Object getItem(int position)
        {
            return null;
        }

        public long getItemId(int position)
        {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent)
        {
            View cellView = convertView;
            PaletteColorView colorView;
            
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            
            if (cellView == null)
                cellView = inflater.inflate(R.layout.color_item, parent, false);
            
            colorView = (PaletteColorView)cellView.findViewById(R.id.item_color);
            colorView.setColor(mColors.get(position));
            
            Animation fadeInAnim = AnimationUtils.loadAnimation(mContext, R.anim.fade_in_anim);
            cellView.startAnimation(fadeInAnim);
            
            return cellView;
        }
    }

}
