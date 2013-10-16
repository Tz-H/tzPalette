package com.tzapps.tzpalette.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tzapps.common.ui.BaseFragment;
import com.tzapps.common.utils.BitmapUtils;
import com.tzapps.common.utils.ClipboardUtils;
import com.tzapps.common.utils.ColorUtils;
import com.tzapps.common.utils.MediaHelper;
import com.tzapps.tzpalette.Constants;
import com.tzapps.tzpalette.R;
import com.tzapps.tzpalette.data.PaletteData;
import com.tzapps.tzpalette.data.PaletteDataHelper;
import com.tzapps.tzpalette.debug.MyDebug;
import com.tzapps.tzpalette.ui.view.ColorEditView;

public class PaletteEditFragment extends BaseFragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, OnClickListener
{
    private static final String TAG = "PaletteEditFragment";
    
    private PaletteData mData = new PaletteData();
    
    private ImageView mImageView;
    private PaletteColorGrid mColoursGrid;
    private View mColorsBar;
    private TextView mTitle;
    private CheckBox mFavourite;
    private ColorEditView mColorEditView;
    
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
        mTitle = (TextView) view.findViewById(R.id.palette_edit_view_title);
        mFavourite = (CheckBox) view.findViewById(R.id.palette_edit_view_favourite);
        
        mColoursGrid = (PaletteColorGrid) view.findViewById(R.id.palette_edit_view_colors);
        mColoursGrid.setOnItemClickListener(this);
        mColoursGrid.setOnItemLongClickListener(this);
        
        mColorEditView = (ColorEditView) view.findViewById(R.id.palette_edit_view_color_edit_area);
        
        return view;
    }
    
    public PaletteData getData()
    {
        return mData;
    }
    
    public void refresh(boolean updatePicture)
    {
        if (mData == null)
            return;
        
        mTitle.setText(mData.getTitle());
        mFavourite.setChecked(mData.isFavourite());
        mColoursGrid.setColors(mData.getColors());
        
        if (mData.getColors().length != 0)
            showColorsBar();

        if (updatePicture)
        {
            Bitmap bitmap    = null;
            
            bitmap = PaletteDataHelper.getInstance(getActivity()).getThumb(mData.getId());
            
            if (bitmap == null)
            {
                String imagePath = mData.getImageUrl();
                Uri    imageUri  = imagePath == null ? null : Uri.parse(imagePath);
                
                bitmap = BitmapUtils.getBitmapFromUri(getActivity(), imageUri, Constants.THUMB_MAX_SIZE);
                
                if (bitmap != null)
                {
                    int orientation;
                    
                    /*
                     * This is a quick fix on picture orientation for the picture taken
                     * from the camera, as it will be always rotated to landscape 
                     * incorrectly even if we take it in portrait mode...
                     */
                    orientation = MediaHelper.getPictureOrientation(getActivity(), imageUri);
                    bitmap = BitmapUtils.getRotatedBitmap(bitmap, orientation);
                } 
            }
            
            updateImageView(bitmap);
        }
    }
    
    public void updateData(PaletteData data, boolean updatePicture)
    {
        mData.copy(data);
        refresh(updatePicture);
    }
    
    public void updateImageView(Bitmap bitmap)
    {
        Log.d(TAG, "updateImageView");
        
        if (mImageView == null)
            return;
        
        mImageView.setImageBitmap(bitmap);
    }

    public void updateColors(int[] colors)
    {
        if (MyDebug.LOG)
            Log.d(TAG, "updateColors");
        
        mData.addColors(colors, false);
        
        if (colors.length != 0)
            showColorsBar();
        
        refresh(false);
    }
    
    public void updateTitle(String title)
    {
        if (MyDebug.LOG)
            Log.d(TAG, "update title " + title);
        
        mData.setTitle(title);
        showTitleBar();
    }
    
    public void updateFavourite(boolean favourite)
    {
        if (MyDebug.LOG)
            Log.d(TAG, "update favourite" + favourite);
        
        mData.setFavourite(favourite);
        refresh(false);
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
        
        mColorEditView.setColor(color);
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

