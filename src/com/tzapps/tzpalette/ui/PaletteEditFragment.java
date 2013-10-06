package com.tzapps.tzpalette.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tzapps.common.ui.BaseFragment;
import com.tzapps.common.utils.ClipboardUtils;
import com.tzapps.common.utils.ColorUtils;
import com.tzapps.tzpalette.R;

public class PaletteEditFragment extends BaseFragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, OnClickListener
{
    private static final String TAG = "PaletteEditFragment";
    
    private ImageView mImageView;
    private PaletteColorGrid mColoursGrid;
    private View mBottomBar;
    private View mColorsBar;
    private TextView mTitle;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        Log.d(TAG, "onCreateView()");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.palette_edit_view, container, false);
        
        mImageView = (ImageView) view.findViewById(R.id.palette_edit_view_picture);
        mImageView.setOnClickListener(this);
        
        mColorsBar = (View) view.findViewById(R.id.palette_edit_view_colors_bar);
        mBottomBar = (View) view.findViewById(R.id.palette_edit_view_bottom_bar);
        mTitle = (TextView) view.findViewById(R.id.palette_edit_view_title);
        
        mColoursGrid = (PaletteColorGrid) view.findViewById(R.id.palette_edit_view_colors);
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
            mBottomBar.setVisibility(View.VISIBLE);
        }
        else
        {
            mImageView.setImageBitmap(null);
            mBottomBar.setVisibility(View.INVISIBLE);
            mColorsBar.setVisibility(View.INVISIBLE);
            mTitle.setVisibility(View.INVISIBLE);
        }
    }

    public void updateColors(int[] colors)
    {
        Log.d(TAG, "updateColors");
        mColoursGrid.setColors(colors);
        
        if (colors.length != 0)
            showColorsBar();
    }
    
    public void udpateTitle(String title)
    {
        Log.d(TAG, "update title" + title);
        mTitle.setText(title);
        
        showTitleBar();
    }
    
    public void clear()
    {
        updateImageView(null);
        mColoursGrid.clear();
    }
    
    private void showColorsBar()
    {
        if (mColorsBar.getVisibility() != View.VISIBLE)
        {
            mColorsBar.setVisibility(View.VISIBLE);
            
            Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in_anim);
            mColorsBar.startAnimation(anim);
        }
    }
    
    private void showTitleBar()
    {
        if (mTitle.getVisibility() != View.VISIBLE)
        {
            mTitle.setVisibility(View.VISIBLE);
            
            Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in_anim);
            mTitle.startAnimation(anim);
        }
    }
    
    @Override
    public void onClick(View v)
    {
        if (v == mImageView)
        {
            //TODO: pick the color on image view
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
        
        String toastText = getResources().getString(R.string.copy_color_into_clipboard);
        toastText = String.format(toastText, ColorUtils.colorToHtml(color));
        
        ClipboardUtils.setPlainText(getActivity(), "Copied color", ColorUtils.colorToHtml(color));
        Toast.makeText(getActivity(), toastText, Toast.LENGTH_SHORT).show();
        
        return true;
    }
}

