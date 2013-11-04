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
import com.tzapps.tzpalette.data.ColorNameListHelper.ColorNameItem;

public class ColorNameListView extends ListView
{
    private final static String TAG = "ColorNameListView";
    
    private ColorNameListAdapter mAdapter;
    
    private void init(Context context)
    {
        mAdapter = new ColorNameListAdapter(context);
        setAdapter(mAdapter);
    }

    public ColorNameListView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }
    
    public void setColors(ColorNameItem items[])
    {
        mAdapter.setColors(items);
    }
    
    public int getColor(int position)
    {
        return mAdapter.getColor(position);
    }
    
    public int getColorCount()
    {
        return mAdapter.getCount();
    }
    
    public void removeColor(int color)
    {
        mAdapter.removeColor(color);
    }
    
    public void removeColorAt(int position)
    {
        mAdapter.removeColorAt(position);
    }
    
    public void clear()
    {
        mAdapter.clear();
    }
    
    private class ColorNameListAdapter extends BaseAdapter
    {
        private List<ColorNameItem> mList;
        
        private Context mContext;

        public ColorNameListAdapter(Context context)
        {
            mContext = context;
            mList = new ArrayList<ColorNameItem>();
        }

        public int getColor(int position)
        {
            return mList.get(position).color;
        }

        public void clear()
        {
            mList.clear();
            notifyDataSetChanged();
        }

        public void addItem(ColorNameItem item)
        {
            mList.add(item);
            notifyDataSetChanged();
        }
        
        public void removeColor(int color)
        {
            int index = -1;
            
            for (int i = 0; i < mList.size(); i++)
            {
                if (color == mList.get(i).color)
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
            mList.remove(position);
            notifyDataSetChanged();
        }

        public void setColors(ColorNameItem[] items)
        {
            mList.clear();
            
            for (int i = 0; i < items.length; i++)
                mList.add(items[i]);
            
            notifyDataSetChanged();
        }

        public int getCount()
        {
            return mList.size();
        }

        public Object getItem(int position)
        {
            return mList.get(position);
        }

        public long getItemId(int position)
        {
            return position;
        }
        
        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent)
        {
            View view = convertView;
            
            if (view == null)
            {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.color_name_list_item, parent, false);
            }
            
            udpateColorListItem(view, mList.get(position));
            
            return view;
        }
        
        private void udpateColorListItem(View view, ColorNameItem item)
        {
            final ImageView colorBar = (ImageView)view.findViewById(R.id.color_info_color_bar);
            final TextView htmlTv = (TextView)view.findViewById(R.id.color_info_html);
            final TextView nameTv = (TextView)view.findViewById(R.id.color_info_name);
            
            nameTv.setText(item.name);
            
            int color = item.color;
            
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
