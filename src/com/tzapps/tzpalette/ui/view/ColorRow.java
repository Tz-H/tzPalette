package com.tzapps.tzpalette.ui.view;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.BaseAdapter;

import com.tzapps.common.ui.view.HorizontalListView;
import com.tzapps.tzpalette.R;

public class ColorRow extends HorizontalListView
{
    private final static String TAG = "ColorRow";
    
    private ColorAdapter mColorsAdapter;
    
    private void init(Context context)
    {
        mColorsAdapter = new ColorAdapter(context, this);
        setAdapter(mColorsAdapter);
    }

    public ColorRow(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }
    
    @Override
    public void setSelection(int position)
    {
        mColorsAdapter.setSelection(position);
    }
    
    public int getSelection()
    {
        return mColorsAdapter.getSelection();
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
    
    /**
     * Add a new color into colors bar and choose whether
     * it is selected 
     * 
     * @param color     the color to add
     * @param selected  true to set the new color selected, false not
     */
    public void addColor(int color, boolean selected)
    {
        mColorsAdapter.addColor(color, selected);
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
    
    /**
     * Update color cell's color at indicated position
     * 
     * @param position the color cell position
     * @param newColor the new color at that position
     */
    public void updateColorAt(int position, int newColor)
    {
        mColorsAdapter.updateColorAt(position, newColor);
    }
    
    public void clear()
    {
        mColorsAdapter.clear();
    }
    
    private class ColorAdapter extends BaseAdapter
    {
        /* the current selected position */
        private int mCurSel = -1;
        private int mNewlyAdded = -1;
        
        private List<Integer> mColors;
        
        private Context mContext;
        private ColorRow mColorRow;
        private boolean animFadeInViewWhenAddNew;

        public ColorAdapter(Context context, ColorRow colorRow)
        {
            mContext = context;
            mColorRow = colorRow;
            mColors = new ArrayList<Integer>();
        }

        public void updateColorAt(int position, int newColor)
        {
            mColors.set(position, newColor);
            notifyDataSetChanged();
        }

        public void setSelection(int position)
        {
            if (mCurSel == position)
                return;
            
            mCurSel = position;
            notifyDataSetChanged();
        }
        
        public int getSelection()
        {
            return mCurSel;
        }
        
        private void animToRemoveColor(final int position)
        {
            final View deleteView = mColorRow.getChildAt(position);
            List<Animator> anims = new ArrayList<Animator>();
            
            // fade out
            ObjectAnimator anim = ObjectAnimator.ofFloat(deleteView, "alpha", 1,0);
            anim.setInterpolator(new AccelerateInterpolator());
            anims.add(anim);
            
            if (position != mColorRow.getCount() - 1)
            {
                //animate the colors behind the deleted one to move forward 
                float xMoveTo = deleteView.getX();
                float yMoveTo = deleteView.getY();
                
                for (int i = position + 1; i < mColors.size(); i++)
                {
                    final View moveView = mColorRow.getChildAt(i);
                    final float x = moveView.getX();
                    final float y = moveView.getY();
                    
                    PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("x", xMoveTo);
                    PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("y", yMoveTo);
                    
                    ObjectAnimator animXY = ObjectAnimator.ofPropertyValuesHolder(moveView, pvhX, pvhY);
                    animXY.setInterpolator(new OvershootInterpolator());
                    anims.add(animXY);
                    
                    //persist the current view's X/Y values which will be used as the moveTo X/Y values
                    //for the next moveView
                    xMoveTo = x;
                    yMoveTo = y;
                }
            }
            
            AnimatorSet animSet = new AnimatorSet();
            animSet.playTogether(anims);
            animSet.addListener(new AnimatorListenerAdapter(){
                @Override
                public void onAnimationEnd(Animator animation)
                {
                    mColors.remove(position);
                    notifyDataSetChanged();
                }
            });
            animSet.start();
        }
        
        public void removeColorAt(int position)
        {
            if (position == mCurSel)
            {
                //the current selected color is deleted
                //so set the curSel to -1 (none)
                mCurSel = -1;
            }
            else if (position < mCurSel)
            {
                //a color ahead the current selected is deleted
                //so the current selected color's index need to
                //move left one unit (e.g. the old select index
                //is 4, and color in slot 2 is deleted, then the
                //selected color should be at slot 3 now)
                mCurSel--;
            }
            
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
            
            animFadeInViewWhenAddNew = true;
            notifyDataSetChanged();
        }
        
        public void addColor(int color, boolean selected)
        {
            mColors.add(color);
            
            // the newly added color is at the last position
            if (selected)
                mCurSel = mColors.size() - 1;
            
            mNewlyAdded = mColors.size() - 1;
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
            
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            cellView = inflater.inflate(R.layout.color_item, parent, false);
            
            if (animFadeInViewWhenAddNew && mNewlyAdded == position)
            {
                cellView.setAlpha(0);
                cellView.setScaleX(.2f);
                cellView.setScaleY(.2f);
                cellView.animate().setInterpolator(new AnticipateOvershootInterpolator()).alpha(1).scaleX(1).scaleY(1).start();
                
                mNewlyAdded = -1;
            }
            
            int cellSize = parent.getHeight();
            
            cellView.setMinimumHeight(cellSize);
            cellView.setMinimumWidth(cellSize);
            
            if (mCurSel == position)
            {
                cellView.setScaleX(1.2f);
                cellView.setScaleY(1.2f);
            }
            
            colorView = (ColorCell)cellView.findViewById(R.id.item_color);
            colorView.setMinimumHeight(cellSize - cellView.getPaddingTop() - cellView.getPaddingBottom());
            colorView.setMinimumWidth(cellSize - cellView.getPaddingLeft() - cellView.getPaddingRight());
            colorView.setColor(mColors.get(position));
            
            return cellView;
        }
    }

}
