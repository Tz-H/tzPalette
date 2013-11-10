package com.tzapps.tzpalette.ui.dialog;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.tzapps.common.utils.ActivityUtils;
import com.tzapps.tzpalette.R;
import com.tzapps.tzpalette.utils.TzPaletteUtils;

public class AboutDialogFragment extends DialogFragment implements OnClickListener
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
        
        final View about_author = (View) view.findViewById(R.id.about_author);
        about_author.setOnClickListener(this);
        
        final View about_creative = (View) view.findViewById(R.id.about_creative);
        about_creative.setOnClickListener(this);
        
        final View about_feedback = (View) view.findViewById(R.id.about_feedback);
        about_feedback.setOnClickListener(this);
        
        final View about_rate = (View) view.findViewById(R.id.about_rate);
        about_rate.setOnClickListener(this);
        
        final View about_project = (View) view.findViewById(R.id.about_project);
        about_project.setOnClickListener(this);
    }
    
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.about_feedback:
                TzPaletteUtils.sendFeedback(getActivity());
                break;
                
            case R.id.about_author:
                TzPaletteUtils.openWebPage(getActivity(), getString(R.string.app_author_webpage));
                break;
                
            case R.id.about_creative:
                TzPaletteUtils.openWebPage(getActivity(), getString(R.string.app_creative_webpage));
                break;
                
            case R.id.about_project:
                TzPaletteUtils.openWebPage(getActivity(), getString(R.string.app_project_website));
                break;
                
            case R.id.about_rate:
                TzPaletteUtils.openMarket(getActivity());
                break;
        }
    }

}
