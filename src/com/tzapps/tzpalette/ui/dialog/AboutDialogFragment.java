package com.tzapps.tzpalette.ui.dialog;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.tzapps.common.utils.ActivityUtils;
import com.tzapps.tzpalette.R;

public class AboutDialogFragment extends DialogFragment
{
    private static final String TAG = "AboutDialogFragment";
    
    public static AboutDialogFragment newInstance(String title)
    {
        AboutDialogFragment frag = new AboutDialogFragment();
        return frag;
    }
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.about_view, null);
        
        initView(view);
           
        AlertDialog.Builder builder = new Builder(getActivity());
        builder.setView(view);
        
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);

        return dialog;
    }
    
    private void initView(View view)
    {
        final TextView version = (TextView) view.findViewById(R.id.about_version);
        
        String versionStr = getString(R.string.about_version);
        String versionName = ActivityUtils.getVersionName(getActivity());
        versionStr = String.format(versionStr, versionName);
        
        version.setText(versionStr);
    }

}
