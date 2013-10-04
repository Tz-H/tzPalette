package com.tzapps.tzpalette.ui;

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
import android.widget.ImageView;
import android.widget.Toast;

import com.tzapps.common.ui.BaseFragment;
import com.tzapps.common.utils.ClipboardUtils;
import com.tzapps.common.utils.ColorUtils;
import com.tzapps.tzpalette.R;

public class CaptureFragment extends BaseFragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, OnClickListener
{
    private static final String TAG = "CaptureFragment";
    
    ImageView mImageView;
    PaletteColorGrid mColoursGrid;
    View mBottomButtons;
    View mPicButtons;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        Log.d(TAG, "onCreateView()");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.capture_view, container, false);
        
        mImageView = (ImageView) view.findViewById(R.id.capture_view_picture);
        mImageView.setOnClickListener(this);
        
        
        mBottomButtons = (View) view.findViewById(R.id.capture_view_bottom_bar);
        mPicButtons = (View) view.findViewById(R.id.pic_buttons);
        
        
        mColoursGrid = (PaletteColorGrid) view.findViewById(R.id.palette_card_colors);
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
            mImageView.setImageBitmap(bitmap);
            mPicButtons.setVisibility(View.GONE);
        }
        else
        {
            mImageView.setImageBitmap(null);
            mPicButtons.setVisibility(View.VISIBLE);
            //mBottomButtons.setVisibility(View.GONE);
        }
    }

    public void updateColors(int[] colors)
    {
        Log.d(TAG, "updateColors");
        mColoursGrid.setColors(colors);
    }
    
    public void clear()
    {
        updateImageView(null);
        mColoursGrid.clear();
    }
    
    public void showTitleButtons()
    {
        mBottomButtons.setVisibility(View.VISIBLE);
        
        Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in_then_out_anim);
        anim.setAnimationListener(new AnimationListener(){
            @Override
            public void onAnimationEnd(Animation animation)
            {
                mBottomButtons.setVisibility(View.GONE);
            }
            
            @Override
            public void onAnimationStart(Animation animation){}

            @Override
            public void onAnimationRepeat(Animation animation){}
        });
        
        mBottomButtons.startAnimation(anim);
    }
    
    @Override
    public void onClick(View v)
    {
        if (v == mImageView)
        {
            //if (mBottomButtons.getVisibility() == View.GONE)
            //    showTitleButtons();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        int color = mColoursGrid.getColor(position);
        
        Toast.makeText(getActivity(), ColorUtils.colorToHtml(color), Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
    {
        int color = mColoursGrid.getColor(position);
        
        //TODO add strings into resources
        ClipboardUtils.setPlainText(getActivity(), "Copied color", ColorUtils.colorToHtml(color));
        Toast.makeText(getActivity(), "Color[" + ColorUtils.colorToHtml(color) + "] has been added into clipboard", Toast.LENGTH_SHORT).show();
        
        return true;
    }
}

