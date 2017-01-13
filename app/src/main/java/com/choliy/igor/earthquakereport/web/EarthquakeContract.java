package com.choliy.igor.earthquakereport.web;

public interface EarthquakeContract {
    /**
     * Main URL
     */
    String URL_REQUEST = "http://earthquake.usgs.gov/fdsnws/event/1/query";
    /**
     * URI Keys
     */
    String URI_FORMAT = "format";
    String URI_GEO_JSON = "geojson";
    String URI_ORDER_BY = "orderby";
    String URI_LIMIT = "limit";
    String URI_MIN_MAG = "minmagnitude";
    String URI_MAX_MAG = "maxmagnitude";
    /**
     * JSON Keys
     */
    String JSON_FEATURES = "features";
    String JSON_PROPERTIES = "properties";
    String JSON_MAGNITUDE = "mag";
    String JSON_LOCATION = "place";
    String JSON_TIME = "time";
    String JSON_URL = "url";
    /**
     * HTTP Requests
     */
    String HTTP_CHARSET = "UTF-8";
    String HTTP_REQUEST = "GET";
    int HTTP_RESPONSE_CODE = 200;
    /**
     * Date & Time Format
     */
    String MAGNITUDE_FORMAT = "0.0";
    String DATE_FORMAT = "LLL dd, yyyy";
    String TIME_FORMAT = "h:mm a";
    /**
     * Logs
     */
    String LOG_JSON_RESPONSE = "Response from URL:";
    String LOG_JSON_EXCEPTION = "Problem parsing the earthquake JSON results";
    String LOG_HTTP_ERROR = "Error response code:";
    String LOG_HTTP_MALFORMED_URL = "Problem building the URL";
    String LOG_HTTP_IO = "Problem making the HTTP request";
}