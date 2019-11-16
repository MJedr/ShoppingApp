package com.example.shoppingapp;


import android.content.SharedPreferences;
import android.os.Bundle;
import com.example.shoppingapp.R;

import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat {

    ListPreference mListPreference;
    public static final String KEY_LIST_PREFERENCE = "background_color";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.setting_pref);
    }
}