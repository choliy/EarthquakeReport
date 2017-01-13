package com.choliy.igor.earthquakereport.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.choliy.igor.earthquakereport.R;
import com.choliy.igor.earthquakereport.adapter.EarthquakeAdapter;
import com.choliy.igor.earthquakereport.model.Earthquake;
import com.choliy.igor.earthquakereport.web.EarthquakeLoader;
import com.choliy.igor.earthquakereport.web.QueryUtils;

import java.util.ArrayList;
import java.util.List;

import static com.choliy.igor.earthquakereport.web.EarthquakeContract.URI_FORMAT;
import static com.choliy.igor.earthquakereport.web.EarthquakeContract.URI_GEO_JSON;
import static com.choliy.igor.earthquakereport.web.EarthquakeContract.URI_LIMIT;
import static com.choliy.igor.earthquakereport.web.EarthquakeContract.URI_MAX_MAG;
import static com.choliy.igor.earthquakereport.web.EarthquakeContract.URI_MIN_MAG;
import static com.choliy.igor.earthquakereport.web.EarthquakeContract.URI_ORDER_BY;
import static com.choliy.igor.earthquakereport.web.EarthquakeContract.URL_REQUEST;

public class EarthquakeActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Earthquake>> {

    private static final String LOG_TAG = EarthquakeActivity.class.getSimpleName();
    private static final int LOADER_ID = 0;
    private ProgressBar mProgressBar;
    private TextView mTextView;
    private EarthquakeAdapter mEarthquakeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earthquake);

        setUi();
        checkInternetAndLoadData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_earthquake, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ic_update:
                checkInternetAndLoadData();
                break;
            case R.id.ic_info:
                showInfoDialog();
                break;
            case R.id.ic_settings:
                startActivity(new Intent(EarthquakeActivity.this, SettingsActivity.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Example with Loader
     */
    @Override
    public Loader<List<Earthquake>> onCreateLoader(int id, Bundle args) {
        return new EarthquakeLoader(this, setUrl());
    }

    @Override
    public void onLoadFinished(Loader<List<Earthquake>> loader, List<Earthquake> data) {
        mEarthquakeAdapter.clearData();
        if (data != null && !data.isEmpty()) {
            mEarthquakeAdapter.addData(data);
        } else {
            mTextView.setText(R.string.no_earthquakes);
        }
        hideProgressBar();
    }

    @Override
    public void onLoaderReset(Loader<List<Earthquake>> loader) {
        mEarthquakeAdapter.clearData();
    }

    /**
     * Example with AsyncTask
     */
    private class EarthquakeAsyncTask extends AsyncTask<String, Void, List<Earthquake>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mEarthquakeAdapter.clearData();
        }

        @Override
        protected List<Earthquake> doInBackground(String... urls) {
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }
            return QueryUtils.fetchEarthquakeData(urls[0]);
        }

        @Override
        protected void onPostExecute(List<Earthquake> earthquakes) {
            if (earthquakes != null && !earthquakes.isEmpty()) {
                mEarthquakeAdapter.addData(earthquakes);
            } else {
                mTextView.setText(R.string.no_earthquakes);
            }
            hideProgressBar();
        }
    }

    private void setUi() {
        mEarthquakeAdapter = new EarthquakeAdapter(this, new ArrayList<Earthquake>());
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mTextView = (TextView) findViewById(R.id.text_view_info);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_earthquakes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mEarthquakeAdapter);
    }

    private void checkInternetAndLoadData() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if (isConnected) {
            mProgressBar.setVisibility(View.VISIBLE);
            mTextView.setText(null);
            /** Example with Loader */
            getSupportLoaderManager().initLoader(LOADER_ID, null, this);
            /** Example with AsyncTask */
            /** new EarthquakeAsyncTask().execute(setUrl()); */
        } else {
            mEarthquakeAdapter.clearData();
            mTextView.setText(R.string.no_internet_connection);
            hideProgressBar();
        }
    }

    private String setUrl() {
        /** set URL by data from Preference */
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        /** get data from Order By */
        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));

        /** get data from Earthquakes to Show */
        String limit = sharedPrefs.getString(
                getString(R.string.settings_earthquakes_key),
                getString(R.string.settings_earthquakes_default));

        /** get data from Minimum Magnitude */
        String minMagnitude = sharedPrefs.getString(
                getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default));

        /** get data from Maximum Magnitude */
        String maxMagnitude = sharedPrefs.getString(
                getString(R.string.settings_max_magnitude_key),
                getString(R.string.settings_max_magnitude_default));

        Uri baseUri = Uri.parse(URL_REQUEST);

        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter(URI_FORMAT, URI_GEO_JSON);
        uriBuilder.appendQueryParameter(URI_ORDER_BY, orderBy);
        uriBuilder.appendQueryParameter(URI_LIMIT, limit);
        uriBuilder.appendQueryParameter(URI_MIN_MAG, minMagnitude);
        uriBuilder.appendQueryParameter(URI_MAX_MAG, maxMagnitude);

        Log.i(LOG_TAG, uriBuilder.toString());

        return uriBuilder.toString();
    }

    private void showInfoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.dialog_info_title))
                .setMessage(getString(R.string.dialog_info_message))
                .setPositiveButton(R.string.dialog_button_close, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                }).show();
    }

    private void hideProgressBar() {
        if (mProgressBar.getVisibility() != View.INVISIBLE) {
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }
}