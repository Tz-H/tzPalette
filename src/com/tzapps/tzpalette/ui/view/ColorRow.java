package com.tzapps.tzpalette.ui.view;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.BaseAdapter;

import com.tzapps.common.ui.view.HorizontalListView;
import com.tzapps.tzpalette.R;

public class ColorRow extends HorizontalListView
{
    private final static String TAG = "ColorRow";
    
    private ColorAdapter mColorsAdapter;
    
    private void init(Context context)
    {
        mColorsAdapter = new ColorAdapter(context);
        setAdapter(mColorsAdapter);
    }

    public ColorRow(Context context, AttributeSet attrs)
    {
        super(context, attrs);
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
    
    public int getColorCount()
    {
        return mColorsAdapter.getCount();
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
        private LinkedList<View> mViews;
        
        private Context mContext;
        private boolean animFadeInViewWhenAddNew;

        public ColorAdapter(Context context)
        {
            mContext = context;
            mColors = new ArrayList<Integer>();
            mViews = new LinkedList<View>();
        }
        
        private void animToRemoveColor(final int position)
        {
            final View deleteView = mViews.get(position);
            
            //fade out
            deleteView.animate().alpha(0).scaleX(0).scaleY(0).setInterpolator(new AccelerateInterpolator())
                .setListener(new AnimatorListenerAdapter(){
                @Override
                public void onAnimationEnd(Animator animation)
                {
                    deleteView.clearAnimation();
                    deleteView.setScaleX(1);
                    deleteView.setScaleY(1);
                    deleteView.setAlpha(1);
                    mColors.remove(position);
                    mViews.remove(position);
                    notifyDataSetChanged();
                }
            });
            
            if (position != getCount() - 1)
            {
                //animate the colors behind the deleted one to move forward 
                float xMoveTo = deleteView.getX();
                float yMoveTo = deleteView.getY();
                
                for (int i = position + 1; i < mViews.size(); i++)
                {
                    final View moveView = mViews.get(i);
                    
                    final float x = moveView.getX();
                    final float y = moveView.getY();
                    
                    moveView.animate().x(xMoveTo).y(yMoveTo).setInterpolator(new AnticipateInterpolator())
                        .setListener(new AnimatorListenerAdapter(){
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
            mViews.clear();
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
            animFadeInViewWhenAddNew = true;
            
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
            
            animFadeInViewWhenAddNew = false;
            
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
            View cellView;
            ColorCell colorView;
            
            if (position < mViews.size())
            {
                cellView = mViews.get(position);
            }
            else
            {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                cellView = inflater.inflate(R.layout.color_item, parent, false);
                mViews.add(cellView);
                
                if (animFadeInViewWhenAddNew)
                {
                    cellView.setAlpha(0);
                    cellView.setScaleX(.2f);
                    cellView.setScaleY(.2f);
                    cellView.animate().setInterpolator(new AnticipateOvershootInterpolator()).alpha(1).scaleX(1).scaleY(1).start();
                }
            }
            
            int cellSize = parent.getHeight();
            
            cellView.setMinimumHeight(cellSize);
            cellView.setMinimumWidth(cellSize);
            
            colorView = (ColorCell)cellView.findViewById(R.id.item_color);
            colorView.setMinimumHeight(cellSize - cellView.getPaddingTop() - cellView.getPaddingBottom());
            colorView.setMinimumWidth(cellSize - cellView.getPaddingLeft() - cellView.getPaddingRight());
            colorView.setColor(mColors.get(position));
            
            return cellView;
        }
    }

}
