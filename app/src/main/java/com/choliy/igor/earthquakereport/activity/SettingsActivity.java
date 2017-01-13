package com.choliy.igor.earthquakereport.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.choliy.igor.earthquakereport.R;
import com.choliy.igor.earthquakereport.fragment.SettingsFragment;

public class SettingsActivity extends AppCompatActivity {

    private SettingsFragment mSettingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mSettingsFragment = new SettingsFragment();

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.container, mSettingsFragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ic_settings_default:
                mSettingsFragment.restoreSettingsDialog();
                break;
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SettingsActivity.this, EarthquakeActivity.class));
        finish();
    }
}