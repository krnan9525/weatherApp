package com.app.mark.weatherapp.services;

import android.util.Log;

import com.app.mark.weatherapp.Constants;
import com.app.mark.weatherapp.interfaces.WeatherInterface;
import com.app.mark.weatherapp.model.CurrentWeatherModel;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Mark on 04/04/2018.
 */

public class OpenWeatherService implements WeatherInterface {
    @Override
    public CurrentWeatherModel getCurrentWeatherByLocation(double latitude, double longitude) {
        String weatherUrl = composeCurrentWeatherUrl(String.format ("%.3f", latitude), String.format ("%.3f", longitude));
        final AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        client.post(weatherUrl, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                Log.d("response", new String(responseBody));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
        return null;
    }

    private static String composeCurrentWeatherUrl(String lat, String lon)
    {
        return Constants.OPEN_WEATHER_BASE_URL + Constants.WEATHER_ENDPOINT + "?APPID=" + Constants.OPEN_WEATHER_API_KEY + "&lat=" + lat + "&lon=" + lon + "&units=metric";
    }
}
