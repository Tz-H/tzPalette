package com.tzapps.tzpalette.utils;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.tzapps.common.utils.ClipboardUtils;
import com.tzapps.common.utils.ColorUtils;
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
     * Search the indicated color info online with google search engine
     * 
     * @param context   the applciation context
     * @param color     the color to search
     */
    public static void searchColorInfoOnWeb(Context context, int color)
    {
        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
        intent.putExtra(SearchManager.QUERY, ColorUtils.colorToHtml(color));
        context.startActivity(intent);
    }
}
