package com.tzapps.tzpalette.ui;

import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tzapps.common.ui.BaseFragment;
import com.tzapps.common.utils.BitmapUtils;
import com.tzapps.common.utils.ClipboardUtils;
import com.tzapps.common.utils.ColorUtils;
import com.tzapps.common.utils.MediaHelper;
import com.tzapps.tzpalette.R;
import com.tzapps.tzpalette.data.PaletteData;
import com.tzapps.tzpalette.data.PaletteDataHelper;

public class PaletteCardFragment extends BaseFragment implements AdapterView.OnItemLongClickListener
{
    private static final String TAG = "PaletteCardFragment";
    
    private TextView mTitle;
    private ImageView mThumb;
    private PaletteColorGrid mColors;
    private PaletteData mData;
    
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
        mColors.setOnItemLongClickListener(this);
        
        refresh();
        
        return view;
    }
    
    public void setData(PaletteData data)
    {
        mData = data;
        refresh();
    }
    
    private void refresh()
    {
        if (mData == null)
            return;
        
        if (mTitle != null)
            mTitle.setText(mData.getTitle());
        
        if (mColors != null)
            mColors.setColors(mData.getColors());
        
        if (mThumb != null)
            updateThumb();
    }
    
    private void updateThumb()
    {
        String imageUrl = mData.getImageUrl();
        
        assert (imageUrl != null);
        
        Bitmap bitmap    = null;
        Uri    imageUri  = Uri.parse(imageUrl);
        
        /* try to get the original image first, if it doesn't exist then
         * to use the thumbnail stored in local db instead 
         */
        
        /*
         * FIXME: it cannot handle with the imageUri correctly if its
         * content is "content://com.google.android.gallery3d.provider/picasa"
         * i.e. the picture from picasa app, it refers to the uri permission
         * grant logic, and might be worse in previous android version (3.0, 4.0, 
         * 4.1). I will need to test it and evaluate this bug on other lower 
         * android version.
         * 
         * A useful reference: http://dimitar.me/how-to-get-picasa-images-using-the-image-picker-on-android-devices-running-any-os-version/
         */
        
        bitmap = BitmapUtils.getBitmapFromUri(getActivity(), imageUri);
        
        if (bitmap != null)
        {
            int orientation = MediaHelper.getPictureOrientation(getActivity(), imageUri);
            
            if (orientation != ExifInterface.ORIENTATION_NORMAL)
                bitmap = BitmapUtils.getRotatedBitmap(bitmap, orientation);
            
            mThumb.setImageBitmap(bitmap);
        }
        else
        {
            bitmap = PaletteDataHelper.getInstance(getActivity()).getThumb(mData.getId());
            mThumb.setImageBitmap(bitmap);
        }
    }
    
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
    {
        int color = mColors.getColor(position);
        
        String toastText = getResources().getString(R.string.copy_color_into_clipboard);
        toastText = String.format(toastText, ColorUtils.colorToHtml(color));
        
        ClipboardUtils.setPlainText(getActivity(), "Copied color", ColorUtils.colorToHtml(color));
        Toast.makeText(getActivity(), toastText, Toast.LENGTH_SHORT).show();
        
        return true;
    }

}
