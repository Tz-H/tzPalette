package com.tzapps.tzpalette.debug;

import com.tzapps.common.utils.ActivityUtils;
import com.tzapps.tzpalette.R;

import android.content.Context;

public final class MyDebug
{
    public static final boolean LOG = true;
    
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
                                       info.appVersion);
        String body    = String.format(feedbackStr,
                                        info.appVersion,
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
