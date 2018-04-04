package com.app.mark.weatherapp.interfaces;

import com.app.mark.weatherapp.model.CurrentWeatherModel;

/**
 * Created by Mark on 04/04/2018.
 */

public interface WeatherInterface {
    CurrentWeatherModel getCurrentWeatherByLocation(double latitude, double longitude);
}
