package com.tzapps.tzpalette.ui;

import com.tzapps.tzpalette.R;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;

public class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener
{
    private static final String TAG = "SettingsFragment";
    
    public static final String KEY_PREF_COLOR_TYPE = "pref_analysisColorType";
    public static final String KEY_PREF_COLOR_NUMBER = "pref_setColorNumber";
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        if (key.equals(KEY_PREF_COLOR_TYPE))
        {
            Preference colorTypePref = findPreference(key);
            String defaultColorType = getResources().getString(R.string.pref_analysisColorType_default);
            String colorType = sharedPreferences.getString(key, defaultColorType);
            colorTypePref.setSummary(colorType);
        }
        else if (key.equals(KEY_PREF_COLOR_NUMBER))
        {
            Preference colorNumberPref = findPreference(key);
            int defaultColorNumber = getResources().getInteger(R.integer.pref_setColorNumber_default);
            int colorNumber = sharedPreferences.getInt(key, defaultColorNumber);
            colorNumberPref.setSummary(colorNumber + "");
        }
    }
    
    @Override
    public void onResume()
    {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }
    
    @Override
    public void onPause()
    {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
