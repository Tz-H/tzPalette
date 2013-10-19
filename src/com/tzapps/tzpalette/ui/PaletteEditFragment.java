package com.tzapps.tzpalette.ui;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.TextView;

import com.tzapps.common.ui.BaseFragment;
import com.tzapps.common.utils.BitmapUtils;
import com.tzapps.common.utils.ColorUtils;
import com.tzapps.common.utils.MediaHelper;
import com.tzapps.tzpalette.Constants;
import com.tzapps.tzpalette.R;
import com.tzapps.tzpalette.data.PaletteData;
import com.tzapps.tzpalette.data.PaletteDataHelper;
import com.tzapps.tzpalette.debug.MyDebug;
import com.tzapps.tzpalette.ui.view.ColorEditView;
import com.tzapps.tzpalette.ui.view.ColorImageView;
import com.tzapps.tzpalette.ui.view.ColorRow;

public class PaletteEditFragment extends BaseFragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, 
                                        ColorImageView.OnColorImageClickListener, ColorEditView.OnColorEditViewChangedListener
{
    private static final String TAG = "PaletteEditFragment";
    
    private PaletteData mData;
    
    private View mView;
    private ColorImageView mImageView;
    private ColorRow mColorsRow;
    private TextView mTitle;
    private CheckBox mFavourite;
    private ColorEditView mColorEditView;
    
    private int mSelColor;
    private int mSelColorPosition;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreateView()");
        
        setRetainInstance(true);
        
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.palette_edit_view, container, false);
        
        mImageView = (ColorImageView) mView.findViewById(R.id.palette_edit_view_picture);
        mImageView.setOnColorImageClickListener(this);
        
        mTitle = (TextView) mView.findViewById(R.id.palette_edit_view_title);
        mFavourite = (CheckBox) mView.findViewById(R.id.palette_edit_view_favourite);
        
        mColorsRow = (ColorRow) mView.findViewById(R.id.palette_edit_view_colors);
        mColorsRow.setOnItemClickListener(this);
        mColorsRow.setOnItemLongClickListener(this);
        
        mColorEditView = (ColorEditView) mView.findViewById(R.id.palette_edit_view_color_edit_area);
        mColorEditView.setOnColorEditViewChangedListener(this);
        
        refresh(true);
        
        return mView;
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
        mColorsRow.setColors(mData.getColors());

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
        mData = data;
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
        
        refresh(false);
    }
    
    public void updateTitle(String title)
    {
        if (MyDebug.LOG)
            Log.d(TAG, "update title " + title);
        
        mData.setTitle(title);
        refresh(false);
    }
    
    public void updateFavourite(boolean favourite)
    {
        if (MyDebug.LOG)
            Log.d(TAG, "update favourite" + favourite);
        
        mData.setFavourite(favourite);
        refresh(false);
    }
    
    public void addNewColorIntoColorsBar(int color, boolean selected)
    {
        mData.addColor(color);
        mColorsRow.addColor(color, selected);
    }
    
    @Override
    public void onColorImageClicked(ColorImageView view, int xPos, int yPos, int color)
    {
        if (MyDebug.LOG)
            Log.d(TAG, "image clicked at x=" + xPos + " y=" + yPos + " color=" + ColorUtils.colorToHtml(color));
        
        mColorEditView.setColor(color);
        
        // Add user's picking color into mColoursRow directly if
        if (mColorsRow.getColorCount() < Constants.COLOR_SLOT_MAX_SIZE)
            addNewColorIntoColorsBar(color, /*selected*/true);
    }
    
    @Override
    public void onColorChanged(ColorEditView view, int oldColor, int newColor)
    {
        if (MyDebug.LOG)
            Log.d(TAG, "update color: color=" + ColorUtils.colorToHtml(mSelColor) + " newColor=" + ColorUtils.colorToHtml(newColor) +
                    " selected color slot=" + mSelColorPosition);
        
        if (mSelColorPosition != -1)
        {
            mData.replaceAt(mSelColorPosition, newColor);
            mColorsRow.updateColorAt(mSelColorPosition, newColor);
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        int color = mColorsRow.getColor(position);
        
        if (MyDebug.LOG)
            Log.d(TAG, "select a color position= " + position + " Color=" + ColorUtils.colorToHtml(color));
        
        mSelColor = color;
        mSelColorPosition = position;
        
        mColorEditView.setColor(color);
        mColorsRow.setSelection(position);
    }
    
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
    {
        //long click to remove this color from palette data and colors bar
        int color = mColorsRow.getColor(position);
        
        mColorsRow.removeColorAt(position);
        mData.removeColor(color);
        
        return true;
    }

}

