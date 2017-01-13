package com.choliy.igor.earthquakereport.web;

import android.text.TextUtils;
import android.util.Log;

import com.choliy.igor.earthquakereport.model.Earthquake;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static com.choliy.igor.earthquakereport.web.EarthquakeContract.HTTP_CHARSET;
import static com.choliy.igor.earthquakereport.web.EarthquakeContract.HTTP_REQUEST;
import static com.choliy.igor.earthquakereport.web.EarthquakeContract.HTTP_RESPONSE_CODE;
import static com.choliy.igor.earthquakereport.web.EarthquakeContract.JSON_FEATURES;
import static com.choliy.igor.earthquakereport.web.EarthquakeContract.JSON_LOCATION;
import static com.choliy.igor.earthquakereport.web.EarthquakeContract.JSON_MAGNITUDE;
import static com.choliy.igor.earthquakereport.web.EarthquakeContract.JSON_PROPERTIES;
import static com.choliy.igor.earthquakereport.web.EarthquakeContract.JSON_TIME;
import static com.choliy.igor.earthquakereport.web.EarthquakeContract.JSON_URL;
import static com.choliy.igor.earthquakereport.web.EarthquakeContract.LOG_HTTP_ERROR;
import static com.choliy.igor.earthquakereport.web.EarthquakeContract.LOG_HTTP_IO;
import static com.choliy.igor.earthquakereport.web.EarthquakeContract.LOG_HTTP_MALFORMED_URL;
import static com.choliy.igor.earthquakereport.web.EarthquakeContract.LOG_JSON_EXCEPTION;
import static com.choliy.igor.earthquakereport.web.EarthquakeContract.LOG_JSON_RESPONSE;

public final class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Query the USGS data and return a list of {@link Earthquake} objects.
     */
    public static List<Earthquake> fetchEarthquakeData(String requestUrl) {
        URL url = createUrl(requestUrl);
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, LOG_HTTP_IO, e);
        }
        return extractFeatureFromJson(jsonResponse);
    }

    /**
     * Returns new URL object from the given string URL
     */
    private static URL createUrl(String stringUrl) {
        URL url;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, LOG_HTTP_MALFORMED_URL, e);
            return null;
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /** milliseconds */);
            urlConnection.setConnectTimeout(15000 /** milliseconds */);
            urlConnection.setRequestMethod(HTTP_REQUEST);
            urlConnection.connect();

            if (urlConnection.getResponseCode() == HTTP_RESPONSE_CODE) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, LOG_HTTP_ERROR + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, LOG_HTTP_IO, e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains
     * the whole JSON response from the server
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader =
                    new InputStreamReader(inputStream, Charset.forName(HTTP_CHARSET));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link Earthquake} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<Earthquake> extractFeatureFromJson(String earthquakeJSON) {
        if (TextUtils.isEmpty(earthquakeJSON)) {
            return null;
        }
        List<Earthquake> earthquakes = new ArrayList<>();
        Log.i(LOG_TAG, LOG_JSON_RESPONSE + earthquakeJSON);
        try {
            JSONObject baseJsonResponse = new JSONObject(earthquakeJSON);
            JSONArray earthquakeArray = baseJsonResponse.getJSONArray(JSON_FEATURES);
            for (int i = 0; i < earthquakeArray.length(); i++) {
                JSONObject earthquake = earthquakeArray.getJSONObject(i);
                JSONObject properties = earthquake.getJSONObject(JSON_PROPERTIES);
                double magnitude = properties.getDouble(JSON_MAGNITUDE);
                String location = properties.getString(JSON_LOCATION);
                long time = properties.getLong(JSON_TIME);
                String url = properties.getString(JSON_URL);
                earthquakes.add(new Earthquake(magnitude, location, time, url));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, LOG_JSON_EXCEPTION, e);
        }
        return earthquakes;
    }
}