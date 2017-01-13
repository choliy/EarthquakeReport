package com.choliy.igor.earthquakereport.fragment;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.choliy.igor.earthquakereport.R;

public class SettingsFragment extends PreferenceFragment
        implements Preference.OnPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_main);

        Preference orderBy = findPreference(getString(R.string.settings_order_by_key));
        bindPreferenceSummaryToValue(orderBy);

        Preference limit = findPreference(getString(R.string.settings_earthquakes_key));
        bindPreferenceSummaryToValue(limit);

        Preference minMagnitude = findPreference(getString(R.string.settings_min_magnitude_key));
        bindPreferenceSummaryToValue(minMagnitude);

        Preference maxMagnitude = findPreference(getString(R.string.settings_max_magnitude_key));
        bindPreferenceSummaryToValue(maxMagnitude);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        String stringValue = value.toString();
        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0) {
                CharSequence[] labels = listPreference.getEntries();
                preference.setSummary(labels[prefIndex]);
            }
        } else {
            preference.setSummary(stringValue);
        }
        return true;
    }

    public void restoreSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getString(R.string.dialog_restore_title))
                .setPositiveButton(R.string.dialog_button_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        restoreDefaultSettings();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.dialog_button_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                }).show();
    }

    private void restoreDefaultSettings() {
        /** Restored Order By */
        CharSequence orderByKey = getString(R.string.settings_order_by_key);
        ListPreference orderBy = (ListPreference) findPreference(orderByKey);
        orderBy.setValue(getString(R.string.settings_order_by_most_recent_value));
        orderBy.setSummary(getString(R.string.settings_order_by_most_recent_label));

        /** Restored Earthquakes to Show */
        CharSequence earthquakesKey = getString(R.string.settings_earthquakes_key);
        ListPreference earthquakes = (ListPreference) findPreference(earthquakesKey);
        earthquakes.setValue(getString(R.string.settings_earthquakes_default));
        earthquakes.setSummary(getString(R.string.settings_earthquakes_default));

        /** Restored Minimum Magnitude */
        CharSequence minMagnitudeKey = getString(R.string.settings_min_magnitude_key);
        ListPreference minMagnitude = (ListPreference) findPreference(minMagnitudeKey);
        minMagnitude.setValue(getString(R.string.settings_min_magnitude_default));
        minMagnitude.setSummary(getString(R.string.settings_min_magnitude_default));

        /** Restored Maximum Magnitude */
        CharSequence maxMagnitudeKey = getString(R.string.settings_max_magnitude_key);
        ListPreference maxMagnitude = (ListPreference) findPreference(maxMagnitudeKey);
        maxMagnitude.setValue(getString(R.string.settings_max_magnitude_default));
        maxMagnitude.setSummary(getString(R.string.settings_max_magnitude_default));

        Toast.makeText(getActivity(), R.string.menu_settings_toast, Toast.LENGTH_SHORT).show();
    }

    private void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(this);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
        String preferenceString = preferences.getString(preference.getKey(), "");
        onPreferenceChange(preference, preferenceString);
    }
}