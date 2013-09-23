package com.tzapps.utils;

import android.content.Context;

public class ClipboardUtils
{
    /**
     * ClipboardUitls to set a plain text into system clipboard
     * 
     * @param context  the application context
     * @param label    the User-visible label for the clip data (for android version HONEYCOMB or higher only)
     * @param text     The actual text in the clip.
     */
    public static void setPlainText(Context context, String label, String text)
    {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB)
        {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        }
        else
        {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText(label, text);
            clipboard.setPrimaryClip(clip);
        }
    }
}
