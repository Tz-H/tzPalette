package com.tzapps.common.utils;

import java.lang.reflect.Field;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.ViewConfiguration;

public class ActivityUtils
{
    /**
     * Activity util used to check if the indicated action 
     * intent is available
     * 
     * @param context the context wants to call this intent
     * @param action  the intent's action name
     * @return true to indicate the intent is available,
     *         otherwise false
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
     * A hacky way to force the app to show the overflow options on action bar
     * no matter whether the device have a physical menu button.
     * 
     * NOTE: it only works for native ActionBar introduced in Android 3.0, not
     * ActionBarSherlock.
     * 
     * @param context The app context
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
}
