package com.tzapps.tzpalette.ui;

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
        super.onCreateView(inflater, container, savedInstanceState);
        
        Log.d(TAG, "onCreateView()");
        
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.capture_view, container, false);
        return view;
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        
        Log.d(TAG, "onViewCreated()");
        
        mImageView = (ImageView)view.findViewById(R.id.img_pic);
        
        if (mImageBitmap != null)
            mImageView.setImageBitmap(mImageBitmap);
    }
    
    public void updateImageView(Bitmap bitmap)
    {
        Log.d(TAG, "updateImageView()");
        
        mImageBitmap = bitmap;
        
        if (mImageView != null)
            mImageView.setImageBitmap(bitmap);
    }

    public void updateColors(int[] colors)
    {
        Log.d(TAG, "updateColors");
        
        ImageView v1 = (ImageView)getView().findViewById(R.id.img_color_1);
        ImageView v2 = (ImageView)getView().findViewById(R.id.img_color_2);
        //ImageView v3 = (ImageView)getView().findViewById(R.id.img_color_3);
        //ImageView v4 = (ImageView)getView().findViewById(R.id.img_color_4);
        
        v1.setBackgroundColor(colors[0]);
        v2.setBackgroundColor(colors[1]);
        //v3.setBackgroundColor(colors[2]);
        //v4.setBackgroundColor(colors[3]);
    }
}
