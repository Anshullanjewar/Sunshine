package com.example.sunshine_app;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.CheckBoxPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import com.example.sunshine.R;
import com.example.sunshine_app.data.SunshinePreferences;
import com.example.sunshine_app.data.WeatherContract;
import com.example.sunshine_app.sync.SunshineSyncUtils;


public class settingFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener {

  private void setPreferenceSummary(Preference preference, Object value) {
    String stringValue = value.toString();
    String key = preference.getKey();

    if (preference instanceof androidx.preference.ListPreference) {
       androidx.preference.ListPreference listPreference = (ListPreference) preference;
      int prefIndex = listPreference.findIndexOfValue(stringValue);
      if (prefIndex >= 0) {
        preference.setSummary(listPreference.getEntries()[prefIndex]);
      }
    } else {
      // For other preferences, set the summary to the value's simple string representation.
      preference.setSummary(stringValue);
    }
  }

  @Override
  public void onCreatePreferences(Bundle bundle, String s) {
    /* Add 'general' preferences, defined in the XML file */
    addPreferencesFromResource(R.xml.preference_general);

    SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
    PreferenceScreen prefScreen = getPreferenceScreen();
    int count = prefScreen.getPreferenceCount();
    for (int i = 0; i < count; i++) {
      androidx.preference.Preference p = prefScreen.getPreference(i);
      if (!(p instanceof CheckBoxPreference)) {
        String value = sharedPreferences.getString(p.getKey(), "");
        setPreferenceSummary(p, value);
      }
    }
  }

  @Override
  public void onStop() {
    super.onStop();
    /* Unregister the preference change listener */
    getPreferenceScreen().getSharedPreferences()
            .unregisterOnSharedPreferenceChangeListener(this);
  }

  @Override
  public void onStart() {
    super.onStart();
    getPreferenceScreen().getSharedPreferences()
            .registerOnSharedPreferenceChangeListener(this);
  }

  @Override
  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    Activity activity = getActivity();
    if(key.equals(getString(R.string.pref_location_key))){
      SunshinePreferences.resetLocationCoordinates(activity);
      SunshineSyncUtils.startImmediateSync(activity);
    }
    else if (key.equals(getString(R.string.pref_units_key))){
      activity.getContentResolver().notifyChange(WeatherContract.WeatherEntry.CONTENT_URI,null);
    }

    androidx.preference.Preference preference = findPreference(key);
    if (null != preference) {
      if (!(preference instanceof androidx.preference.CheckBoxPreference)) {
        setPreferenceSummary(preference, sharedPreferences.getString(key, ""));
      }
    }
  }
}