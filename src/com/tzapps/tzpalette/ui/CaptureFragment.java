package com.tzapps.tzpalette.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.tzapps.tzpalette.R;
import com.tzapps.tzpalette.utils.ClipboardUtils;
import com.tzapps.tzpalette.utils.ColorUtils;

public class CaptureFragment extends BaseFragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, OnClickListener
{
    private static final String TAG = "CaptureFragment";
    
    ImageView mImageView;
    GridView mColoursGrid;
    ColorAdapter mColorsAdapter;
    View mTitleButtons;
    View mPicButtons;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        Log.d(TAG, "onCreateView()");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.capture_view, container, false);
        
        mImageView = (ImageView) view.findViewById(R.id.img_palette_picture);
        mImageView.setOnClickListener(this);
        
        
        mTitleButtons = (View) view.findViewById(R.id.title_buttons);
        mPicButtons = (View) view.findViewById(R.id.pic_buttons);
        
        mColorsAdapter = new ColorAdapter(getActivity());
        
        mColoursGrid = (GridView) view.findViewById(R.id.grid_colors);
        mColoursGrid.setAdapter(mColorsAdapter);
        mColoursGrid.setOnItemClickListener(this);
        mColoursGrid.setOnItemLongClickListener(this);
        
        return view;
    }
    
    public void updateImageView(Bitmap bitmap)
    {
        Log.d(TAG, "updateImageView");
        
        if (mImageView == null)
            return;
        
        if (bitmap != null)
        {
            mImageView.setBackgroundColor(getResources().getColor(R.color.color_transparent));
            mImageView.setImageBitmap(bitmap);
            mPicButtons.setVisibility(View.GONE);
        }
        else
        {
            mImageView.setImageBitmap(null);
            mImageView.setBackgroundColor(getResources().getColor(R.color.palette_card_pic_init_bg));
            mPicButtons.setVisibility(View.VISIBLE);
            mTitleButtons.setVisibility(View.GONE);
        }
    }

    public void updateColors(int[] colors)
    {
        Log.d(TAG, "updateColors");
        mColorsAdapter.setColors(colors);
    }
    
    public void showTitleButtons()
    {
        mTitleButtons.setVisibility(View.VISIBLE);
        
        Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in_then_out_anim);
        anim.setAnimationListener(new AnimationListener(){
            @Override
            public void onAnimationEnd(Animation animation)
            {
                mTitleButtons.setVisibility(View.GONE);
            }
            
            @Override
            public void onAnimationStart(Animation animation){}

            @Override
            public void onAnimationRepeat(Animation animation){}
        });
        
        mTitleButtons.startAnimation(anim);
    }
    
    @Override
    public void onClick(View v)
    {
        if (v == mImageView)
        {
            if (mTitleButtons.getVisibility() == View.GONE)
                showTitleButtons();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        int color = mColorsAdapter.getColor(position);
        
        Toast.makeText(getActivity(), ColorUtils.colorToHtml(color), Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
    {
        int color = mColorsAdapter.getColor(position);
        
        //TODO add strings into resources
        ClipboardUtils.setPlainText(getActivity(), "Copied color", ColorUtils.colorToHtml(color));
        Toast.makeText(getActivity(), "Color[" + ColorUtils.colorToHtml(color) + "] has been added into clipboard", Toast.LENGTH_SHORT).show();
        
        return true;
    }

    private class ColorAdapter extends BaseAdapter
    {
        private int[] colors;
        private Context mContext;

        public ColorAdapter(Context c)
        {
            mContext = c;
            colors = new int[0];
        }
        
        public int getColor(int position)
        {
            return colors[position];
        }

        public void setColors(int[] colors)
        {
            this.colors = colors;
            this.notifyDataSetChanged();
        }

        public int getCount()
        {
            return colors.length;
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
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            
            if (cellView != null)
            {
                colorView = (PaletteColorView)cellView.findViewById(R.id.item_color);
            }
            else
            {
                cellView = inflater.inflate(R.layout.color_item, parent, false);
                colorView = (PaletteColorView)cellView.findViewById(R.id.item_color);
                
                Animation fadeInAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in_anim);
                cellView.startAnimation(fadeInAnim);
            }
            
            colorView.setColor(colors[position]);
            
            return cellView;
        }
    }
}

