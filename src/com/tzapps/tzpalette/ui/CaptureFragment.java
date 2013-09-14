package com.tzapps.tzpalette.ui;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.tzapps.tzpalette.R;

public class CaptureFragment extends Fragment
{
    private static final String TAG = "CaptureFragment";
    
    Bitmap      mImageBitmap;
    ImageView   mImageView;
    
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.i(TAG, "onCreateView()");
        
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.capture_view, container, false);
        
        mImageView = (ImageView)view.findViewById(R.id.img_pic);
        
        if (mImageBitmap != null)
            mImageView.setImageBitmap(mImageBitmap);
        
        return view;
    }
    
    public void updateImageView(Bitmap bitmap)
    {
        Log.i(TAG, "updateImageView()");
        
        mImageBitmap = bitmap;
        
        if (mImageView != null)
            mImageView.setImageBitmap(bitmap);
    }
}
