package com.tzapps.tzpalette.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.tzapps.common.ui.BaseFragment;
import com.tzapps.tzpalette.R;

public class PaletteCardFragment extends BaseFragment
{
    private final String TAG = "PaletteCardFragment";
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        Log.d(TAG, "onCreateView()");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.palette_card_view, container, false);
        
        return view;
    }
}
