package com.tzapps.tzpalette.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tzapps.common.ui.BaseFragment;
import com.tzapps.common.utils.ActivityUtils;
import com.tzapps.tzpalette.R;

public class AboutFragment extends BaseFragment
{
    private static final String TAG = "AboutFragment";
    
    private TextView version;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.about_view, container, false);
        
        version = (TextView) view.findViewById(R.id.about_version);
        
        String versionStr = getString(R.string.about_version);
        String versionName = ActivityUtils.getVersionName(getActivity());
        versionStr = String.format(versionStr, versionName);
        
        version.setText(versionStr);
        
        return view;
    }
 
}

