package com.tzapps.tzpalette.ui;

import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tzapps.common.ui.BaseFragment;
import com.tzapps.common.utils.BitmapUtils;
import com.tzapps.common.utils.MediaHelper;
import com.tzapps.tzpalette.R;
import com.tzapps.tzpalette.data.PaletteData;

public class PaletteCardFragment extends BaseFragment
{
    private static final String TAG = "PaletteCardFragment";
    
    private TextView mTitle;
    private ImageView mThumb;
    private PaletteColorGrid mColors;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        Log.d(TAG, "onCreateView()");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.palette_card_view, container, false);
        
        mTitle = (TextView)view.findViewById(R.id.palette_card_title);
        mThumb = (ImageView)view.findViewById(R.id.palette_card_thumb);
        mColors = (PaletteColorGrid)view.findViewById(R.id.palette_card_colors);
        
        return view;
    }

    /**
     * Update palette card view by indicated PaletteData
     * @param data  the PaletteData to set
     */
    public void update(PaletteData data)
    {
        assert(data != null);
        
        mTitle.setText(data.getTitle());
        mColors.setColors(data.getColors());
        
        updateThumb(data.getImageUrl());
    }
    
    private void updateThumb(String imageUrl)
    {
        assert (imageUrl != null);
        
        Bitmap bitmap    = null;
        Uri    imageUri  = Uri.parse(imageUrl);
        
        bitmap = BitmapUtils.getBitmapFromUri(getActivity(), imageUri);
        
        if (bitmap != null)
        {
            int orientation = MediaHelper.getPictureOrientation(getActivity(), imageUri);
            
            if (orientation != ExifInterface.ORIENTATION_NORMAL)
                bitmap = BitmapUtils.getRotatedBitmap(bitmap, orientation);
            
            mThumb.setImageBitmap(bitmap);
        }
    }
}
