package com.tzapps.tzpalette.ui;

import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tzapps.common.ui.BaseFragment;
import com.tzapps.common.utils.BitmapUtils;
import com.tzapps.common.utils.MediaHelper;
import com.tzapps.tzpalette.Constants;
import com.tzapps.tzpalette.R;
import com.tzapps.tzpalette.data.ColorNameListHelper;
import com.tzapps.tzpalette.data.PaletteData;
import com.tzapps.tzpalette.data.PaletteDataHelper;
import com.tzapps.tzpalette.ui.view.ColorNameListView;
import com.tzapps.tzpalette.ui.view.ColorRow;

public class ColorNameListFragment extends BaseFragment
{
    private static final String TAG = "ColorNameListFragment";
    
    private ColorNameListView mColorNameList;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.color_name_list_view, container, false);
        
        mColorNameList = (ColorNameListView) view.findViewById(R.id.color_name_list);
        
        mColorNameList.setColors(ColorNameListHelper.getInstance(getActivity()).getAll());
        
        return view;
    }    
}
