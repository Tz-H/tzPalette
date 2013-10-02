package com.tzapps.tzpalette.ui;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.tzapps.tzpalette.R;

public class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener
{
    private static final String TAG = "SettingsFragment";
    
    public static final String KEY_PREF_COLOR_TYPE = "pref_analysisColorType";
    public static final String KEY_PREF_COLOR_NUMBER = "pref_setColorNumber";
    public static final String KEY_PREF_ANALYSIS_ACCURACY = "pref_analysisAccuracy";
    public static final String KEY_PREF_ENABLE_KPP = "pref_enableKpp";
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        
        // Initialize the prefs title/summary label
        loadPrefs();
    }
    
    private void loadPrefs()
    {
        SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
        
        String colorType = sp.getString(KEY_PREF_COLOR_TYPE, getString(R.string.pref_analysisColorType_default));
        Preference colorTypePref = findPreference(KEY_PREF_COLOR_TYPE);
        
        String strColorTypeSumm = getString(R.string.pref_analysisColorType);
        strColorTypeSumm = String.format(strColorTypeSumm, colorType);
        colorTypePref.setTitle(strColorTypeSumm);
        
        int colurNumber = sp.getInt(KEY_PREF_COLOR_NUMBER, getResources().getInteger(R.integer.pref_setColorNumber_default));
        Preference colorNumberPref = findPreference(KEY_PREF_COLOR_NUMBER);
        
        String strColorNumberSumm = getString(R.string.pref_setColorNumber);
        strColorNumberSumm = String.format(strColorNumberSumm, colurNumber);
        colorNumberPref.setTitle(strColorNumberSumm);
        
        String accuracy = sp.getString(KEY_PREF_ANALYSIS_ACCURACY, getString(R.string.pref_analysisColorAccuracy_default));
        Preference accuracyPref = findPreference(KEY_PREF_ANALYSIS_ACCURACY);
        
        String strAccuracySumm = getString(R.string.pref_analysisColorAccuracy);
        strAccuracySumm = String.format(strAccuracySumm, accuracy);
        accuracyPref.setTitle(strAccuracySumm);
        
        boolean enableKpp = sp.getBoolean(KEY_PREF_ENABLE_KPP, false);
        CheckBoxPreference enableKppPref = (CheckBoxPreference)findPreference(KEY_PREF_ENABLE_KPP);
        
        enableKppPref.setChecked(enableKpp);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        // Just simply reload all preferences to refresh them...
        loadPrefs();
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
