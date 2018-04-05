package com.app.mark.weatherapp.services;

import android.app.Activity;
import android.util.Log;

import com.app.mark.weatherapp.Constants;
import com.app.mark.weatherapp.interfaces.OnRemoteCallFinishListener;
import com.app.mark.weatherapp.interfaces.WeatherInterface;
import com.app.mark.weatherapp.model.CurrentWeatherModel;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Mark on 04/04/2018.
 */

public class OpenWeatherService implements WeatherInterface {
    private CurrentWeatherModel currentWeather;
    private Activity context;
    private OnRemoteCallFinishListener onRemoteCallFinishListener;

    public OpenWeatherService(Activity activity) {
        context = activity;
        currentWeather = new CurrentWeatherModel();
    }

    @Override
    public CurrentWeatherModel getCurrentWeatherByLocation(double latitude, double longitude, final OnRemoteCallFinishListener onRemoteCallFinishListener) {
        String weatherUrl = composeCurrentWeatherUrl(String.format ("%.3f", latitude), String.format ("%.3f", longitude));
        final AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        client.post(weatherUrl, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d("response", new String(responseBody));
                try {
                    currentWeather.constructClassFromJson(new JSONObject(new String(responseBody)));
                    onRemoteCallFinishListener.success();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
        return currentWeather;
    }

    private static String composeCurrentWeatherUrl(String lat, String lon)
    {
        return Constants.OPEN_WEATHER_BASE_URL + Constants.WEATHER_ENDPOINT + "?APPID=" + Constants.OPEN_WEATHER_API_KEY + "&lat=" + lat + "&lon=" + lon + "&units=metric";
    }

    public CurrentWeatherModel getCurrentWeather() {
        return currentWeather;
    }

    public void setCurrentWeather(CurrentWeatherModel currentWeather) {
        this.currentWeather = currentWeather;
    }
}
