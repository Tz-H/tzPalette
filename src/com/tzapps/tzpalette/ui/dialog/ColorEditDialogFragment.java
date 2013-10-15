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
        
        initView(mColorEditView, mColor);

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
    
    private void initView(View view, int color)
    {
        final ColorBar colorBarR = (ColorBar)mColorEditView.findViewById(R.id.color_bar_r);
        colorBarR.setType(ColorBarType.RGB_R);
        colorBarR.setOnColorChangeListener(this);
        
        final ColorBar colorBarG = (ColorBar)mColorEditView.findViewById(R.id.color_bar_g);
        colorBarG.setType(ColorBarType.RGB_G);
        colorBarG.setOnColorChangeListener(this);
        
        final ColorBar colorBarB = (ColorBar)mColorEditView.findViewById(R.id.color_bar_b);
        colorBarB.setType(ColorBarType.RGB_B);
        colorBarB.setOnColorChangeListener(this);
        
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

    @Override
    public void onColorChanged(ColorBar colorBar, int color)
    {
        updateColor(color);
    }
    
    private void updateColor(int color)
    {
        mNewColor = color;
        
        int[] rgb = ColorUtils.colorToRGB(color);
        
        final ImageView colorBar = (ImageView)mColorEditView.findViewById(R.id.color_edit_view_color_bar);
        final TextView htmlTv = (TextView)mColorEditView.findViewById(R.id.color_info_html);
        
        colorBar.setBackgroundColor(color);
        htmlTv.setText(ColorUtils.colorToHtml(color));
        
        final TextView rTitle = (TextView)mColorEditView.findViewById(R.id.color_bar_r_title);
        String rTitleStr = getString(R.string.color_bar_rgb_r_title);
        rTitleStr = String.format(rTitleStr, rgb[0]);
        rTitle.setText(rTitleStr);
        
        final ColorBar colorBarR = (ColorBar)mColorEditView.findViewById(R.id.color_bar_r);
        colorBarR.setColor(color);
        
        final TextView gTitle = (TextView)mColorEditView.findViewById(R.id.color_bar_g_title);
        String gTitleStr = getString(R.string.color_bar_rgb_g_title);
        gTitleStr = String.format(gTitleStr, rgb[1]);
        gTitle.setText(gTitleStr);
        
        final ColorBar colorBarG = (ColorBar)mColorEditView.findViewById(R.id.color_bar_g);
        colorBarG.setColor(color);
        
        final TextView bTitle = (TextView)mColorEditView.findViewById(R.id.color_bar_b_title);
        String bTitleStr = getString(R.string.color_bar_rgb_b_title);
        bTitleStr = String.format(bTitleStr, rgb[2]);
        bTitle.setText(bTitleStr);
        
        final ColorBar colorBarB = (ColorBar)mColorEditView.findViewById(R.id.color_bar_b);
        colorBarB.setColor(color);
    }

}
