package com.tzapps.tzpalette.ui.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.tzapps.common.utils.ColorUtils;
import com.tzapps.tzpalette.R;
import com.tzapps.tzpalette.ui.view.ColorBar;
import com.tzapps.tzpalette.ui.view.ColorBar.ColorBarType;
import com.tzapps.tzpalette.ui.view.ColorBar.OnColorBarChangedListener;

public class ColorEditDialogFragment extends DialogFragment implements OnColorBarChangedListener
{
    private static final String TAG = "ColorInfoDialogFragment";
    
    public interface OnColorEditDialogClosedListener
    {
        public void onSaveColorChange(ColorEditDialogFragment fragment, int oldColor, int newColor);
    }
    
    private int  mColor;
    private int  mNewColor;
    private View mColorEditView;
    private OnColorEditDialogClosedListener mCallback;
    
    public static ColorEditDialogFragment newInstance(String title, int color)
    {
        ColorEditDialogFragment frag = new ColorEditDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putInt("color", color);
        frag.setArguments(args);
        return frag;
    }
    
    public void setOnColorEditDialogClosedListener(OnColorEditDialogClosedListener listener)
    {
        mCallback = listener;
    }
    
    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        String title = getArguments().getString("title");
        
        LayoutInflater inflater = getActivity().getLayoutInflater();
        
        mColor = getArguments().getInt("color");
        mColorEditView = inflater.inflate(R.layout.color_edit_view, null);
        
        initView(mColor);

        AlertDialog.Builder builder = new Builder(getActivity());
        //builder.setTitle(title);
        builder.setView(mColorEditView);
        builder.setPositiveButton(R.string.action_save, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (mCallback != null)
                    mCallback.onSaveColorChange(ColorEditDialogFragment.this, mColor, mNewColor);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);

        return dialog;
    }
    
    @Override
    public void onColorChanged(ColorBar colorBar, int color)
    {
        updateColor(color);
    }
    
    private void initTabs()
    {
        final TabHost tabHost = (TabHost)mColorEditView.findViewById(android.R.id.tabhost);
        tabHost.setup();
        
        final TabSpec spec1 = tabHost.newTabSpec("TAB 1 - RGB");
        spec1.setContent(R.id.color_edit_view_tab_rgb);
        spec1.setIndicator(getString(R.string.color_edit_tab_rgb));
        
        final TabSpec spec2 = tabHost.newTabSpec("TAB 2 - HSV");
        spec2.setContent(R.id.color_edit_view_tab_hsv);
        spec2.setIndicator(getString(R.string.color_edit_tab_hsv));
        
        tabHost.addTab(spec1);
        tabHost.addTab(spec2);
        
        tabHost.setOnTabChangedListener(new OnTabChangeListener(){
            @Override
            public void onTabChanged(String tabId)
            {
                updateColor(mNewColor);
            }
            
        });
    }
    
    private void initColorBar(int colorBarResId, ColorBarType type)
    {
        final ColorBar colorBar = (ColorBar)mColorEditView.findViewById(colorBarResId);
        colorBar.setType(type);
        colorBar.setOnColorChangeListener(this);
    }
    
    private void initView(int color)
    {
        initTabs();
        
        initColorBar(R.id.color_bar_r, ColorBarType.RGB_R);
        initColorBar(R.id.color_bar_g, ColorBarType.RGB_G);
        initColorBar(R.id.color_bar_b, ColorBarType.RGB_B);
        initColorBar(R.id.color_bar_h, ColorBarType.HSV_H);
        initColorBar(R.id.color_bar_s, ColorBarType.HSV_S);
        initColorBar(R.id.color_bar_v, ColorBarType.HSV_V);
        
        final TextView restoreTV = (TextView)mColorEditView.findViewById(R.id.action_restore);
        restoreTV.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v)
            {
                updateColor(mColor);
            }
            
        });
        
        updateColor(color);
    }
    
    public void udpateColorBar(int colorBarResId, int textViewResId, int textResId, int color, int textVaule)
    {
        final TextView title = (TextView)mColorEditView.findViewById(textViewResId);
        String titleStr = getString(textResId);
        titleStr = String.format(titleStr, textVaule);
        title.setText(titleStr);
        
        final ColorBar colorBar = (ColorBar)mColorEditView.findViewById(colorBarResId);
        colorBar.setColor(color);
    }
    
    private void updateColor(int color)
    {
        mNewColor = color;
        
        int[] rgb = ColorUtils.colorToRGB(color);
        int[] hsv = ColorUtils.colorToHSV(color);
        
        final ImageView colorBar = (ImageView)mColorEditView.findViewById(R.id.color_edit_view_color_bar);
        final TextView htmlTv = (TextView)mColorEditView.findViewById(R.id.color_info_html);
        
        colorBar.setBackgroundColor(color);
        htmlTv.setText(ColorUtils.colorToHtml(color));
        
        udpateColorBar(R.id.color_bar_r, R.id.color_bar_r_title, R.string.color_bar_rgb_r_title, color, rgb[0]);
        udpateColorBar(R.id.color_bar_g, R.id.color_bar_g_title, R.string.color_bar_rgb_g_title, color, rgb[1]);
        udpateColorBar(R.id.color_bar_b, R.id.color_bar_b_title, R.string.color_bar_rgb_b_title, color, rgb[2]);
        udpateColorBar(R.id.color_bar_h, R.id.color_bar_h_title, R.string.color_bar_hsv_h_title, color, hsv[0]);
        udpateColorBar(R.id.color_bar_s, R.id.color_bar_s_title, R.string.color_bar_hsv_s_title, color, hsv[1]);
        udpateColorBar(R.id.color_bar_v, R.id.color_bar_v_title, R.string.color_bar_hsv_v_title, color, hsv[2]);
    }

}
