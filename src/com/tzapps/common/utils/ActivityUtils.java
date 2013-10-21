package com.tzapps.common.utils;

import java.lang.reflect.Field;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.view.ViewConfiguration;

public class ActivityUtils
{
    /**
     * Activity util used to check if the indicated action intent is available
     * 
     * @param context
     *            the context wants to call this intent
     * @param action
     *            the intent's action name
     * @return true to indicate the intent is available, otherwise false
     */
    public static boolean isIntentAvailable(Context context, String action)
    {
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(action);
        List<ResolveInfo> list =
                packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    /**
     * A hacky way to force the app to show the overflow options on action bar no matter whether the
     * device have a physical menu button.
     * 
     * NOTE: it only works for native ActionBar introduced in Android 3.0, not ActionBarSherlock.
     * 
     * @param context
     *            The app context
     */
    public static void forceToShowOverflowOptionsOnActoinBar(Context context)
    {
        try
        {
            ViewConfiguration config = ViewConfiguration.get(context);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null)
            {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        }
        catch (Exception ex)
        {
            // Ignore
        }
    }

    /**
     * Fetch the versionName from the AndroicManifest.xml
     * 
     * @param context
     * @return the versionName
     */
    public static String getVersionName(Context context)
    {
        try
        {
            ComponentName comp = new ComponentName(context, context.getClass());
            PackageInfo pinfo = context.getPackageManager()
                    .getPackageInfo(comp.getPackageName(), 0);
            return pinfo.versionName;
        }
        catch (android.content.pm.PackageManager.NameNotFoundException e)
        {
            return null;
        }
    }
    
    /**
     * Fetch the versionCode from the AndroicManifest.xml
     * 
     * @param context
     * @return the versionCode
     */
    public static int getVersionCode(Context context)
    {
        try
        {
            ComponentName comp = new ComponentName(context, context.getClass());
            PackageInfo pinfo = context.getPackageManager()
                    .getPackageInfo(comp.getPackageName(), 0);
            return pinfo.versionCode;
        }
        catch (android.content.pm.PackageManager.NameNotFoundException e)
        {
            return 0;
        }
    }
    
    /**
     * Send an email via available mail activity
     * 
     * @param context   the app context
     * @param to        the email address send to
     * @param subject   the email subject
     * @param body      the email body
     */
    public static void sendEmail(Context context, String to, String subject, String body)
    {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", to, null));
        
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);
        
        context.startActivity(Intent.createChooser(emailIntent, null));
    }
}
