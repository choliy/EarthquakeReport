package com.choliy.igor.earthquakereport.web;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.choliy.igor.earthquakereport.model.Earthquake;

import java.util.List;

public class EarthquakeLoader extends AsyncTaskLoader<List<Earthquake>> {

    private String mUrl;

    public EarthquakeLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Earthquake> loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        return QueryUtils.fetchEarthquakeData(mUrl);
    }
}