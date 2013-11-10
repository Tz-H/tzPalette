package com.tzapps.tzpalette.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.tzapps.common.utils.ActivityUtils;
import com.tzapps.tzpalette.R;
import com.tzapps.tzpalette.data.PaletteDataHelper;
import com.tzapps.tzpalette.debug.MyDebug;
import com.tzapps.tzpalette.ui.dialog.AboutDialogFragment;

public class SettingsFragment extends PreferenceFragment implements 
            OnSharedPreferenceChangeListener, OnPreferenceClickListener
{
    private static final String TAG = "SettingsFragment";

    public static final String KEY_PREF_COLOR_TYPE = "pref_analysisColorType";
    public static final String KEY_PREF_COLOR_NUMBER = "pref_setColorNumber";
    public static final String KEY_PREF_ANALYSIS_ACCURACY = "pref_analysisAccuracy";
    public static final String KEY_PREF_ENABLE_KPP = "pref_enableKpp";
    public static final String KEY_PREF_SYSTEM_VERSION = "pref_system_version";
    public static final String KEY_PREF_SYSTEM_FEEDBACK = "pref_system_feedback";
    public static final String KEY_PREF_SYSTEM_ABOUT = "pref_system_about";
    public static final String KEY_PREF_CACHE_THUMB_QUALITY = "pref_cache_thumbQuality";
    public static final String KEY_PREF_CACHE_CLEARALL = "pref_cache_clearAll";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        
        Preference feedbackPref = (Preference) findPreference(KEY_PREF_SYSTEM_FEEDBACK);
        feedbackPref.setOnPreferenceClickListener(this);
        
        Preference cacheClearPref = (Preference) findPreference(KEY_PREF_CACHE_CLEARALL);
        cacheClearPref.setOnPreferenceClickListener(this);
        
        Preference aboutPref = (Preference) findPreference(KEY_PREF_SYSTEM_ABOUT);
        aboutPref.setOnPreferenceClickListener(this);

        // Initialize the prefs title/summary label
        loadPrefs();
    }
    
    private void removeAllData()
    {
        PaletteDataHelper.getInstance(getActivity()).deleteAll();
    }
    
    private void sendFeedback()
    {
        MyDebug.sendFeedback(getActivity());
    }
    
    private void openAboutDialog()
    {
        // Show about dialog
        AboutDialogFragment dialogFrag = AboutDialogFragment.newInstance(getString(R.string.title_about_view));
        dialogFrag.show(getFragmentManager(), "aboutDialog");
    }

    private void loadPrefs()
    {
        SharedPreferences sp = getPreferenceScreen().getSharedPreferences();

        ListPreference colorTypePref = (ListPreference)findPreference(KEY_PREF_COLOR_TYPE);
        String colorType = colorTypePref.getEntry().toString();

        String strColorType = getString(R.string.pref_analysisColorType);
        strColorType = String.format(strColorType, colorType);
        colorTypePref.setTitle(strColorType);

        int colurNumber = sp.getInt(KEY_PREF_COLOR_NUMBER,
                getResources().getInteger(R.integer.pref_setColorNumber_default));
        Preference colorNumberPref = findPreference(KEY_PREF_COLOR_NUMBER);

        String strColorNumberSumm = getString(R.string.pref_setColorNumber);
        strColorNumberSumm = String.format(strColorNumberSumm, colurNumber);
        colorNumberPref.setTitle(strColorNumberSumm);

        ListPreference accuracyPref = (ListPreference)findPreference(KEY_PREF_ANALYSIS_ACCURACY);
        String accuracy = accuracyPref.getEntry().toString();
        
        String strAccuracySumm = getString(R.string.pref_analysisColorAccuracy);
        strAccuracySumm = String.format(strAccuracySumm, accuracy);
        accuracyPref.setTitle(strAccuracySumm);
        
        boolean enableKpp = sp.getBoolean(KEY_PREF_ENABLE_KPP, false);
        CheckBoxPreference enableKppPref = (CheckBoxPreference) findPreference(KEY_PREF_ENABLE_KPP);
        enableKppPref.setChecked(enableKpp);

        String versionSumm = ActivityUtils.getVersionName(getActivity());
        Preference versionPref = (Preference) findPreference(KEY_PREF_SYSTEM_VERSION);
        versionPref.setSummary(versionSumm);
        
        ListPreference thumbQualityPref = (ListPreference)findPreference(KEY_PREF_CACHE_THUMB_QUALITY);
        String strThumbQualitySumm = thumbQualityPref.getEntry().toString();
        thumbQualityPref.setSummary(strThumbQualitySumm);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        // Just simply reload all preferences to refresh them...
        loadPrefs();
    }
    
    @Override
    public boolean onPreferenceClick(Preference preference)
    {
        String key = preference.getKey();
        
        if (key.equals(KEY_PREF_SYSTEM_FEEDBACK))
        {
            sendFeedback();
        }
        else if (key.equals(KEY_PREF_SYSTEM_ABOUT))
        {
            openAboutDialog();
        }
        else if (key.equals(KEY_PREF_CACHE_CLEARALL))
        {
            // alert dialog to force user confirm this operation
            new AlertDialog.Builder(getActivity())
                .setTitle(R.string.pref_cache_clearAll_dialogTitle)
                .setMessage(R.string.pref_cache_clearAll_dialogContent)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) { 
                        removeAllData();
                    }
                 })
                .setNegativeButton(android.R.string.no, null)
                .show();
        }
        
        return false;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        getPreferenceManager().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(
                this);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference)
    {
        super.onPreferenceTreeClick(preferenceScreen, preference);

        // If the user has clicked on a preference screen, set up the action bar
        if (preference instanceof PreferenceScreen)
            initializeActionBar((PreferenceScreen) preference);

        return false;
    }

    /** Sets up the action bar for an {@link PreferenceScreen} */
    public static void initializeActionBar(PreferenceScreen preferenceScreen)
    {
        final Dialog dialog = preferenceScreen.getDialog();

        if (dialog != null)
        {
            // Initialize the action bar
            dialog.getActionBar().setDisplayHomeAsUpEnabled(true);

            // Apply custom home button area click listener to close the PreferenceScreen because
            // PreferenceScreens are dialogs which swallow
            // events instead of passing to the activity
            // Related Issue: https://code.google.com/p/android/issues/detail?id=4611
            View homeBtn = dialog.findViewById(android.R.id.home);

            if (homeBtn != null)
            {
                OnClickListener dismissDialogClickListener = new OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        dialog.dismiss();
                    }
                };

                // Prepare yourselves for some hacky programming
                ViewParent homeBtnContainer = homeBtn.getParent();

                // The home button is an ImageView inside a FrameLayout
                if (homeBtnContainer instanceof FrameLayout)
                {
                    ViewGroup containerParent = (ViewGroup) homeBtnContainer.getParent();

                    if (containerParent instanceof LinearLayout)
                    {
                        // This view also contains the title text, set the whole view as clickable
                        ((LinearLayout) containerParent)
                                .setOnClickListener(dismissDialogClickListener);
                    }
                    else
                    {
                        // Just set it on the home button
                        ((FrameLayout) homeBtnContainer)
                                .setOnClickListener(dismissDialogClickListener);
                    }
                }
                else
                {
                    // The 'If all else fails' default case
                    homeBtn.setOnClickListener(dismissDialogClickListener);
                }
            }
        }
    }
}
