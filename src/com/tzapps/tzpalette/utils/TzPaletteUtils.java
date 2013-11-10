package com.tzapps.tzpalette.utils;

import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.tzapps.common.utils.ActivityUtils;
import com.tzapps.common.utils.ClipboardUtils;
import com.tzapps.common.utils.ColorUtils;
import com.tzapps.tzpalette.R;
import com.tzapps.tzpalette.debug.DebugInfo;

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
    
    /**
     * Open the google play store market with this app
     * 
     * @param context the application context
     */
    public static void openMarket(Context context)
    {
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        
        try
        {
            context.startActivity(goToMarket);
        }
        catch (ActivityNotFoundException e) 
        {
            Toast.makeText(context, "Couldn't launch the market", Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Open a webpage with indicated http url
     * 
     * @param context   the application context
     * @param httpUrl   the http url to open
     */
    public static void openWebPage(Context context, String httpUrl)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(httpUrl));
        context.startActivity(intent);
    }
    
    /**
     * Send a feedback email with debug information
     * 
     * @param context the app context
     */
    public static void sendFeedback(Context context)
    {
        DebugInfo info = DebugInfo.getDebugInfo(context);
        
        String feedbackStr = context.getString(R.string.app_feedback_debugInfo);
        String subjectStr = context.getString(R.string.feedback_subject);
        
        String to      = context.getString(R.string.app_contact);
        String subject = String.format(subjectStr, 
                                       context.getString(R.string.app_name),
                                       info.appVersionName);
        String body    = String.format(feedbackStr,
                                       info.appVersionName,
                                       info.appVersionCode,
                                       info.apiLevel,
                                       info.osVersion,
                                       info.deviceModel,
                                       info.locale,
                                       info.screenDensity,
                                       info.screenWidth,
                                       info.screenHeight,
                                       info.deviceWidth,
                                       info.deviceHeight);
        
        ActivityUtils.sendEmail(context, to, subject, body);
    }
}
