package com.tzapps.tzpalette.ui.dialog;

import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import com.tzapps.common.utils.ClipboardUtils;
import com.tzapps.common.utils.ColorUtils;
import com.tzapps.tzpalette.Constants;
import com.tzapps.tzpalette.R;
import com.tzapps.tzpalette.data.ColorNameListHelper;
import com.tzapps.tzpalette.data.ColorNameListHelper.ColorNameItem;
import com.tzapps.tzpalette.ui.view.ColorNameListView;

public class ColorInfoDialogFragment extends DialogFragment implements OnClickListener
{
    private static final String TAG = "ColorInfoDialogFragment";
    
    public static final String TAB_TAG_INFO    = "TabTagInfo";
    public static final String TAB_TAG_SIMILAR = "TabTagSimilar";
    
    private int mColor;
    private View mView;
    private TabHost mTabHost;
    
    public static ColorInfoDialogFragment newInstance(int color)
    {
        ColorInfoDialogFragment frag = new ColorInfoDialogFragment();
        Bundle args = new Bundle();
        args.putInt("color", color);
        frag.setArguments(args);
        return frag;
    }
    
    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        mColor = getArguments().getInt("color");
        
        LayoutInflater inflater = getActivity().getLayoutInflater();
        mView = inflater.inflate(R.layout.color_info_view, null);
        
        initView();

        AlertDialog.Builder builder = new Builder(getActivity());
        builder.setView(mView);
        builder.setNegativeButton(android.R.string.cancel, null);
        
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);

        return dialog;
    }
    

    private void initTabs()
    {
        mTabHost = (TabHost)mView.findViewById(android.R.id.tabhost);
        mTabHost.setup();
        
        final TabSpec spec1 = mTabHost.newTabSpec(TAB_TAG_INFO);
        spec1.setContent(R.id.color_info_view_tab_info);
        spec1.setIndicator(getString(R.string.color_info_tab_info));
        
        final TabSpec spec2 = mTabHost.newTabSpec(TAB_TAG_SIMILAR);
        spec2.setContent(R.id.color_info_view_tab_similar);
        spec2.setIndicator(getString(R.string.color_info_tab_similar));
        
        mTabHost.addTab(spec1);
        mTabHost.addTab(spec2);
    }
    
    private void initView()
    {
        initTabs();
        
        final ImageView colorBar = (ImageView)mView.findViewById(R.id.color_info_view_color_bar);
        final TextView htmlTv = (TextView)mView.findViewById(R.id.color_info_html);
        final TextView moreTv = (TextView)mView.findViewById(R.id.color_info_tab_action_more);
        final TextView rgbTv = (TextView)mView.findViewById(R.id.color_info_rgb);
        final TextView hsvTv = (TextView)mView.findViewById(R.id.color_info_hsv);
        final TextView hslTv = (TextView)mView.findViewById(R.id.color_info_hsl);
        final TextView labTv = (TextView)mView.findViewById(R.id.color_info_lab);
        final TextView cmykTv = (TextView)mView.findViewById(R.id.color_info_cmyk);
        final ColorNameListView colorNameList = (ColorNameListView)mView.findViewById(R.id.color_name_list);
        
        moreTv.setOnClickListener(this);
        
        colorBar.setBackgroundColor(mColor);
        htmlTv.setText(ColorUtils.colorToHtml(mColor));
        htmlTv.setOnClickListener(this);
        
        int rgb[] = ColorUtils.colorToRGB(mColor);
        String rgbStr = getString(R.string.color_info_rgb_value);
        rgbStr = String.format(rgbStr, rgb[0], rgb[1], rgb[2]);
        rgbTv.setText(rgbStr);
        
        int hsv[] = ColorUtils.colorToHSV(mColor);
        String hsvStr = getString(R.string.color_info_hsv_value);
        hsvStr = String.format(hsvStr, hsv[0], hsv[1], hsv[2]);
        hsvTv.setText(hsvStr);
        
        int hsl[] = ColorUtils.colorToHSL(mColor);
        String hslStr = getString(R.string.color_info_hsl_value);
        hslStr = String.format(hslStr, hsl[0], hsl[1], hsl[2]);
        hslTv.setText(hslStr);
        
        int lab[] = ColorUtils.colorToLAB(mColor);
        String labStr = getString(R.string.color_info_lab_value);
        labStr = String.format(labStr, lab[0], lab[1], lab[2]);
        labTv.setText(labStr);
        
        int cmyk[] = ColorUtils.colorToCMYK(mColor);
        String cmykStr = getString(R.string.color_info_cmyk_value);
        cmykStr = String.format(cmykStr, cmyk[0], cmyk[1], cmyk[2], cmyk[3]);
        cmykTv.setText(cmykStr);
        
        ColorNameListHelper helper = ColorNameListHelper.getInstance(getActivity());
        ColorNameItem similarColors[] = helper.getSimilar(mColor, Constants.COLOR_INFO_SIMILAR_DEVIATION);
        
        if (similarColors.length != 0)
        {
            colorNameList.setColors(similarColors);
        }
        else
        {
            // if there is no similar color found, we should hide the "similar" tab...
            mTabHost.getTabWidget().getChildTabViewAt(1).setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.color_info_html:
                copyColorToClipboard(getActivity(), mColor);
                break;
                
            case R.id.color_info_tab_action_more:
                openColorInfoWebPage(mColor);
                break;
        }
    }
    
    private void copyColorToClipboard(Context context, int color)
    {
        String toastText = getResources().getString(R.string.copy_color_into_clipboard);
        toastText = String.format(toastText, ColorUtils.colorToHtml(color));
        
        ClipboardUtils.setPlainText(context, "Copied color", ColorUtils.colorToHtml(color));
        Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
    }
    
    private void openColorInfoWebPage(int color)
    {
        String webUrl = Constants.COLOR_INFO_MORE_WEBSITE;
        webUrl += ColorUtils.colorToHtml(color).replace("#", "").toLowerCase(Locale.getDefault());
        
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(webUrl));
        startActivity(intent);
    }
    
}
