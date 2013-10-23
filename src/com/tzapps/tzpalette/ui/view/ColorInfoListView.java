package com.tzapps.tzpalette.ui.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tzapps.common.utils.ColorUtils;
import com.tzapps.tzpalette.R;

public class ColorInfoListView extends ListView
{
    private final static String TAG = "ColorInfoListView";
    
    private ColorInfoListAdapter mColorsAdapter;
    
    private void init(Context context)
    {
        mColorsAdapter = new ColorInfoListAdapter(context);
        setAdapter(mColorsAdapter);
    }

    public ColorInfoListView(Context context, AttributeSet attrs)
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
    
    public void clear()
    {
        mColorsAdapter.clear();
    }
    
    private class ColorInfoListAdapter extends BaseAdapter
    {
        private List<Integer> mColors;
        
        private Context mContext;

        public ColorInfoListAdapter(Context context)
        {
            mContext = context;
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
        
        public void addColor(int color)
        {
            mColors.add(color);
            notifyDataSetChanged();
        }
        
        public void addColor(int color, boolean selected)
        {
            mColors.add(color);
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
        
        public void removeColorAt(int position)
        {
            mColors.remove(position);
            notifyDataSetChanged();
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
            View view;
            
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.color_info_list_item, parent, false);
            udpateColorListItem(view, mColors.get(position));
            
            return view;
        }
        
        private void udpateColorListItem(View view, int color)
        {
            final ImageView colorBar = (ImageView)view.findViewById(R.id.color_info_color_bar);
            final TextView htmlTv = (TextView)view.findViewById(R.id.color_info_html);
            
            colorBar.setBackgroundColor(color);
            htmlTv.setText(ColorUtils.colorToHtml(color));
            
            int[] rgb = ColorUtils.colorToRGB(color);
            int[] hsv = ColorUtils.colorToHSV(color);
            
            updateColorTextView(view, R.id.color_info_rgb_r, R.string.color_info_rgb_r_value, rgb[0]);
            updateColorTextView(view, R.id.color_info_rgb_g, R.string.color_info_rgb_g_value, rgb[1]);
            updateColorTextView(view, R.id.color_info_rgb_b, R.string.color_info_rgb_b_value, rgb[2]);
            updateColorTextView(view, R.id.color_info_hsv_h, R.string.color_info_hsv_h_value, hsv[0]);
            updateColorTextView(view, R.id.color_info_hsv_s, R.string.color_info_hsv_s_value, hsv[1]);
            updateColorTextView(view, R.id.color_info_hsv_v, R.string.color_info_hsv_v_value, hsv[2]);
        }
        
        private void updateColorTextView(View view, int textViewResId, int textResId, int textValue)
        {
            final TextView tv = (TextView)view.findViewById(textViewResId);
            String str = mContext.getString(textResId);
            str = String.format(str, textValue);
            tv.setText(str);
        }
    }

}
