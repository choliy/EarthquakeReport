package com.choliy.igor.earthquakereport.model;

public class Earthquake {

    private double mMagnitude;
    private String mLocation;
    private long mTime;
    private String mUrl;

    public Earthquake(double magnitude, String location, long time, String url) {
        mMagnitude = magnitude;
        mLocation = location;
        mTime = time;
        mUrl = url;
    }

    public double getMagnitude() {
        return mMagnitude;
    }

    public String getLocation() {
        return mLocation;
    }

    public long getTime() {
        return mTime;
    }

    public String getUrl() {
        return mUrl;
    }
}