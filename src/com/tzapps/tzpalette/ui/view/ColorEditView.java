package com.tzapps.tzpalette.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.tzapps.common.utils.ClipboardUtils;
import com.tzapps.common.utils.ColorUtils;
import com.tzapps.tzpalette.R;
import com.tzapps.tzpalette.ui.view.ColorBar.ColorBarType;
import com.tzapps.tzpalette.ui.view.ColorBar.OnColorBarChangedListener;

public class ColorEditView extends RelativeLayout implements OnColorBarChangedListener
{
    private static final String TAG = "ColorEditView";
    
    public static final int TAB_INDEX_RGB = 0;
    public static final int TAB_INDEX_HSV = 1;
    public static final String TAB_TAG_RGB = "TabTagRGB";
    public static final String TAB_TAG_HSV = "TabTagHSV";
    
    public interface OnColorEditViewChangedListener
    {
        public void onColorChanged(ColorEditView view, int oriColor, int newColor);
        public void onTabChanged(ColorEditView view, int index, String tag);
    }
    
    private int mOriColor = Color.GRAY;
    private int mNewColor = Color.GRAY;
    
    private Context mContext;
    private View mView;
    private TabHost mTabHost;
    
    private OnColorEditViewChangedListener mCallback;
    
    public ColorEditView(Context context)
    {
        super(context);
        init(context);
    }
    
    public ColorEditView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }
    
    public ColorEditView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init(context);
    }
    
    private void init(Context context)
    {
        mContext = context;
        
        mView = LayoutInflater.from(context).inflate(R.layout.color_edit_view, null);
        
        initView(mOriColor);
        addView(mView);
    }
    
    public void setColor(int oriColor, int newColor)
    {
        mOriColor = oriColor;
        mNewColor = newColor;
        
        updateColor();
    }
    
    public int getOriColor()
    {
        return mOriColor;
    }

    public int getNewColor()
    {
        return mNewColor;
    }
    
    public void setOnColorEditViewChangedListener(OnColorEditViewChangedListener listener)
    {
        mCallback = listener;
    }
    
    public void restore()
    {
        mNewColor = mOriColor;
        updateColor();
        
        if (mCallback != null)
            mCallback.onColorChanged(this, mOriColor, mNewColor);
    }
    
    @Override
    public void onColorChanged(ColorBar colorBar, int color)
    {
        mNewColor = color;
        updateColor();
        
        if (mCallback != null)
            mCallback.onColorChanged(this, mOriColor, mNewColor);
    }
    
    public void setCurrentTab(int index)
    {
        mTabHost.setCurrentTab(index);
    }
    
    public int getCurrentTab()
    {
        return mTabHost.getCurrentTab();
    }
    
    private void initTabs()
    {
        mTabHost = (TabHost)mView.findViewById(android.R.id.tabhost);
        mTabHost.setup();
        
        final TabSpec spec1 = mTabHost.newTabSpec(TAB_TAG_RGB);
        spec1.setContent(R.id.color_edit_view_tab_rgb);
        spec1.setIndicator(getString(R.string.color_edit_tab_rgb));
        
        final TabSpec spec2 = mTabHost.newTabSpec(TAB_TAG_RGB);
        spec2.setContent(R.id.color_edit_view_tab_hsv);
        spec2.setIndicator(getString(R.string.color_edit_tab_hsv));
        
        mTabHost.addTab(spec1);
        mTabHost.addTab(spec2);
        
        mTabHost.setOnTabChangedListener(new OnTabChangeListener(){
            @Override
            public void onTabChanged(String tabId)
            {
                updateColor();
                
                if (mCallback != null)
                    mCallback.onTabChanged(ColorEditView.this, 
                                           mTabHost.getCurrentTab(), 
                                           mTabHost.getCurrentTabTag());
            }
        });
    }
    
    private String getString(int resId)
    {
        return mContext.getString(resId);
    }

    private void initColorBar(int colorBarResId, ColorBarType type)
    {
        final ColorBar colorBar = (ColorBar)mView.findViewById(colorBarResId);
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
        
        final TextView htmlTv = (TextView)mView.findViewById(R.id.color_info_html);
        htmlTv.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v)
            {
                copyColorToClipboard(mOriColor);
            }
        });
        
        final TextView htmlNewTv = (TextView)mView.findViewById(R.id.color_info_html_new);
        htmlNewTv.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v)
            {
                copyColorToClipboard(mNewColor);
            }
        });
        
        final TextView restoreTv = (TextView)mView.findViewById(R.id.action_restore);
        restoreTv.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v)
            {
                restore();
            }
        });
        
        updateColor();
    }
    
    private void copyColorToClipboard(int color)
    {
        String toastText = getResources().getString(R.string.copy_color_into_clipboard);
        toastText = String.format(toastText, ColorUtils.colorToHtml(color));
        
        ClipboardUtils.setPlainText(mContext, "Copied color", ColorUtils.colorToHtml(color));
        Toast.makeText(mContext, toastText, Toast.LENGTH_SHORT).show();
    }

    private void udpateColorBar(int colorBarResId, int textViewResId, int textResId, int color, int textVaule)
    {
        final TextView title = (TextView)mView.findViewById(textViewResId);
        String titleStr = getString(textResId);
        titleStr = String.format(titleStr, textVaule);
        title.setText(titleStr);
        
        final ColorBar colorBar = (ColorBar)mView.findViewById(colorBarResId);
        colorBar.setColor(color);
    }
    
    private void updateColor()
    {
        int[] rgb = ColorUtils.colorToRGB(mNewColor);
        int[] hsv = ColorUtils.colorToHSV(mNewColor);
        
        final ImageView colorBar = (ImageView)mView.findViewById(R.id.color_edit_view_color_bar);
        final TextView htmlTv = (TextView)mView.findViewById(R.id.color_info_html);
        
        colorBar.setBackgroundColor(mOriColor);
        htmlTv.setText(ColorUtils.colorToHtml(mOriColor));
        
        final ImageView newColorBar = (ImageView)mView.findViewById(R.id.color_edit_view_new_color_bar);
        final TextView htmlNewTv = (TextView)mView.findViewById(R.id.color_info_html_new);
        
        newColorBar.setBackgroundColor(mNewColor);
        htmlNewTv.setText(ColorUtils.colorToHtml(mNewColor));
        
        udpateColorBar(R.id.color_bar_r, R.id.color_bar_r_title, R.string.color_bar_rgb_r_title, mNewColor, rgb[0]);
        udpateColorBar(R.id.color_bar_g, R.id.color_bar_g_title, R.string.color_bar_rgb_g_title, mNewColor, rgb[1]);
        udpateColorBar(R.id.color_bar_b, R.id.color_bar_b_title, R.string.color_bar_rgb_b_title, mNewColor, rgb[2]);
        udpateColorBar(R.id.color_bar_h, R.id.color_bar_h_title, R.string.color_bar_hsv_h_title, mNewColor, hsv[0]);
        udpateColorBar(R.id.color_bar_s, R.id.color_bar_s_title, R.string.color_bar_hsv_s_title, mNewColor, hsv[1]);
        udpateColorBar(R.id.color_bar_v, R.id.color_bar_v_title, R.string.color_bar_hsv_v_title, mNewColor, hsv[2]);
    }
}
