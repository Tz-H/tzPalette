package com.tzapps.tzpalette.debug;

import java.util.Locale;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

import com.tzapps.common.utils.ActivityUtils;

public final class DebugInfo
{
    public String appVersionName;
    public String osVersion;
    public String deviceModel;
    public String locale;
    public int apiLevel;
    public int appVersionCode;
    public int screenDensity;
    public int screenWidth;
    public int screenHeight;
    public int deviceWidth;
    public int deviceHeight;
    
    public static DebugInfo getDebugInfo(Context context)
    {
        DebugInfo info = new DebugInfo();
        
        info.appVersionName  = ActivityUtils.getVersionName(context);
        info.appVersionCode = ActivityUtils.getVersionCode(context);
        info.osVersion   = android.os.Build.VERSION.RELEASE;
        info.deviceModel = android.os.Build.MODEL;
        info.locale      = Locale.getDefault().toString();
        
        info.apiLevel = android.os.Build.VERSION.SDK_INT;
        info.screenDensity = context.getResources().getDisplayMetrics().densityDpi;
        info.deviceWidth   = context.getResources().getDisplayMetrics().widthPixels;
        info.deviceHeight  = context.getResources().getDisplayMetrics().heightPixels;
        
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        info.screenWidth = size.x;
        info.screenHeight = size.y;
        
        info.deviceModel = android.os.Build.MODEL;
        
        return info;
    }
}
