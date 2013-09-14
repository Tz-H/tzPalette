package com.tzapps.tzpalette.utils;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

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
}
