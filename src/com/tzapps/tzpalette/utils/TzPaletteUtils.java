package com.tzapps.tzpalette.utils;

import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.tzapps.common.utils.ClipboardUtils;
import com.tzapps.common.utils.ColorUtils;
import com.tzapps.tzpalette.Constants;
import com.tzapps.tzpalette.R;

public class TzPaletteUtils
{
    /**
     * Copy an indicated color value into clipboard
     * 
     * @param context the application context
     * @param color   the color to copy
     */
    public static void copyColorToClipboard(Context context, int color)
    {
        String toastText = context.getResources().getString(R.string.copy_color_into_clipboard);
        toastText = String.format(toastText, ColorUtils.colorToHtml(color));
        
        ClipboardUtils.setPlainText(context, "Copied color", ColorUtils.colorToHtml(color));
        Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
    }
    
    /**
     * Open an indicated color within a color enquire web page
     * 
     * @param context   the application context
     * @param color     the color to enquiry
     */
    public static void openColorInfoWebPage(Context context, int color)
    {
        String webUrl = Constants.COLOR_INFO_MORE_WEBSITE;
        webUrl += ColorUtils.colorToHtml(color).replace("#", "").toLowerCase(Locale.getDefault());
        
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(webUrl));
        context.startActivity(intent);
    }
}
