package com.tzapps.tzpalette.ui;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
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
import com.tzapps.tzpalette.debug.MyDebug;

public class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener
{
    private static final String TAG = "SettingsFragment";

    public static final String KEY_PREF_COLOR_TYPE = "pref_analysisColorType";
    public static final String KEY_PREF_COLOR_NUMBER = "pref_setColorNumber";
    public static final String KEY_PREF_ANALYSIS_ACCURACY = "pref_analysisAccuracy";
    public static final String KEY_PREF_ENABLE_KPP = "pref_enableKpp";
    public static final String KEY_PREF_SYSTEM_VERSION = "pref_system_version";
    public static final String KEY_PREF_SYSTEM_FEEDBACK = "pref_system_feedback";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        
        Preference feedbackPref = (Preference) findPreference(KEY_PREF_SYSTEM_FEEDBACK);
        feedbackPref.setOnPreferenceClickListener(new OnPreferenceClickListener(){
                @Override
                public boolean onPreferenceClick(Preference preference)
                {
                    sendFeedback();
                    return false;
                }
            }
        );

        // Initialize the prefs title/summary label
        loadPrefs();
    }
    
    private void sendFeedback()
    {
        MyDebug.sendFeedback(getActivity());
    }

    private void loadPrefs()
    {
        SharedPreferences sp = getPreferenceScreen().getSharedPreferences();

        String colorType = sp.getString(KEY_PREF_COLOR_TYPE,
                getString(R.string.pref_analysisColorType_default));
        Preference colorTypePref = findPreference(KEY_PREF_COLOR_TYPE);

        String strColorTypeSumm = getString(R.string.pref_analysisColorType);
        strColorTypeSumm = String.format(strColorTypeSumm, colorType);
        colorTypePref.setTitle(strColorTypeSumm);

        int colurNumber = sp.getInt(KEY_PREF_COLOR_NUMBER,
                getResources().getInteger(R.integer.pref_setColorNumber_default));
        Preference colorNumberPref = findPreference(KEY_PREF_COLOR_NUMBER);

        String strColorNumberSumm = getString(R.string.pref_setColorNumber);
        strColorNumberSumm = String.format(strColorNumberSumm, colurNumber);
        colorNumberPref.setTitle(strColorNumberSumm);

        String accuracy = sp.getString(KEY_PREF_ANALYSIS_ACCURACY,
                getString(R.string.pref_analysisColorAccuracy_default));
        Preference accuracyPref = findPreference(KEY_PREF_ANALYSIS_ACCURACY);

        String strAccuracySumm = getString(R.string.pref_analysisColorAccuracy);
        strAccuracySumm = String.format(strAccuracySumm, accuracy);
        accuracyPref.setTitle(strAccuracySumm);

        boolean enableKpp = sp.getBoolean(KEY_PREF_ENABLE_KPP, false);
        CheckBoxPreference enableKppPref = (CheckBoxPreference) findPreference(KEY_PREF_ENABLE_KPP);
        enableKppPref.setChecked(enableKpp);

        String versionSumm = ActivityUtils.getVersionName(getActivity());
        Preference versionPref = (Preference) findPreference(KEY_PREF_SYSTEM_VERSION);
        versionPref.setSummary(versionSumm);
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
