package com.tzapps.tzpalette.ui.view;

import java.util.ArrayList;
import java.util.List;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.tzapps.tzpalette.R;

public class ColorRow extends GridView
{
    private final static String TAG = "ColorRow";
    
    private ColorAdapter mColorsAdapter;
    
    private void init(Context context)
    {
        mColorsAdapter = new ColorAdapter(context, this);
        setAdapter(mColorsAdapter);
    }

    public ColorRow(Context context)
    {
        super(context);
        init(context);
    }

    public ColorRow(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }

    public ColorRow(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init(context);
    }
    
    public void setColors(int colors[])
    {
        mColorsAdapter.clear();
        mColorsAdapter.setColors(colors);
    }
    
    public void addColor(int color)
    {
        mColorsAdapter.addColor(color);
    }
    
    public int getColor(int position)
    {
        return mColorsAdapter.getColor(position);
    }
    
    public void removeColor(int color)
    {
        mColorsAdapter.removeColor(color);
    }
    
    public void removeColorAt(int position)
    {
        mColorsAdapter.removeColorAt(position);
    }
    
    public void clear()
    {
        mColorsAdapter.clear();
    }
    
    private class ColorAdapter extends BaseAdapter
    {
        private List<Integer> mColors;
        
        private GridView mGridView;
        private Context mContext;

        public ColorAdapter(Context context, GridView gridView)
        {
            mContext = context;
            mGridView = gridView;
            mColors = new ArrayList<Integer>();
        }
        
        private void animToRemoveColor(int position)
        {
            final int index = position;
            final View deleteView = mGridView.getChildAt(position);
            
            //fade out
            deleteView.animate().alpha(0).setListener(new AnimatorListenerAdapter(){
                @Override
                public void onAnimationEnd(Animator animation)
                {
                    deleteView.clearAnimation();
                    deleteView.setAlpha(1);
                    mColors.remove(index);
                    notifyDataSetChanged();
                }
            });
            
            if (position != getCount() - 1)
            {
                //animate the colors behind the deleted one to move forward 
                float xMoveTo = deleteView.getX();
                float yMoveTo = deleteView.getY();
                
                for (int i = position + 1; i < mGridView.getCount(); i++)
                {
                    final View moveView = mGridView.getChildAt(i);
                    
                    final float x = moveView.getX();
                    final float y = moveView.getY();
                    
                    moveView.animate().x(xMoveTo).y(yMoveTo).setListener(new AnimatorListenerAdapter(){
                        @Override
                        public void onAnimationEnd(Animator animation)
                        {
                            moveView.clearAnimation();
                            moveView.setX(x);
                            moveView.setY(y);
                        }
                    });
                    
                    //persist the current view's X/Y values which will be used as the moveTo X/Y values
                    //for the next moveView
                    xMoveTo = x;
                    yMoveTo = y;
                }
            }
        }
        
        public void removeColorAt(int position)
        {
            animToRemoveColor(position);
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
        
        public void addColor(int color)
        {
            mColors.add(color);
            //Collections.sort(mColors, ColorUtils.colorSorter);
            
            notifyDataSetChanged();
        }
        
        public void removeColor(int color)
        {
            int index = -1;
            
            for (int i = 0; i < mColors.size(); i++)
            {
                if (color == mColors.get(i))
                {
                    index = i;
                    break;
                }
            }
            
            if (index != -1)
                removeColorAt(index);
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
            return mColors.get(position);
        }

        public long getItemId(int position)
        {
            return position;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent)
        {
            View cellView = convertView;
            ColorCell colorView;
            
            if (cellView == null)
            {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                cellView = inflater.inflate(R.layout.color_item, parent, false);
            }
            
            colorView = (ColorCell)cellView.findViewById(R.id.item_color);
            colorView.setColor(mColors.get(position));
            
            return cellView;
        }
    }

}
