package com.tzapps.tzpalette.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.tzapps.tzpalette.R;

public class CaptureFragment extends Fragment
{
    Bitmap      mImageBitmap;
    ImageView   mImageView;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.capture_view, container, false);
        
        mImageView = (ImageView)view.findViewById(R.id.img_pic);
        
        return view;
    }
    
    public void updateImageView(Bitmap bitmap)
    {
        mImageBitmap = bitmap;
        mImageView.setImageBitmap(bitmap);
    }
}
