package com.app.mark.weatherapp.model;

import com.app.mark.weatherapp.interfaces.JSONSerializationInterface;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Mark on 04/04/2018.
 */

public class CurrentWeatherModel implements JSONSerializationInterface {
    private String cityName = "";
    private TemperatureModel temperature;
    private int humidity = 0;
    private WindModel wind;
    private WeatherModel weather;
    private SunRiseModel sunRise;
    private Boolean isUsingImperialUnit = false;

    public CurrentWeatherModel(Boolean isUsingImperialUnit) {
        this.isUsingImperialUnit = isUsingImperialUnit;
        temperature = new TemperatureModel(isUsingImperialUnit);
        wind = new WindModel(isUsingImperialUnit);
        weather = new WeatherModel();
        sunRise = new SunRiseModel();
    }

    @Override
    public void constructClassFromJson(JSONObject jsonObject) {
        try {
            cityName = jsonObject.getString("name");
            temperature.constructClassFromJson(jsonObject.getJSONObject("main"));
            humidity = jsonObject.getJSONObject("main").getInt("humidity");
            wind.constructClassFromJson(jsonObject.getJSONObject("wind"));
            weather.constructClassFromJson(jsonObject.getJSONArray("weather").getJSONObject(0));
            sunRise.constructClassFromJson(jsonObject.getJSONObject("sys"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String serializeToJson() {
        return null;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public WindModel getWind() {
        return wind;
    }

    public void setWind(WindModel wind) {
        this.wind = wind;
    }

    public TemperatureModel getTemperature() {
        return temperature;
    }

    public void setTemperature(TemperatureModel temperature) {
        this.temperature = temperature;
    }

    public WeatherModel getWeather() {
        return weather;
    }

    public void setWeather(WeatherModel weather) {
        this.weather = weather;
    }

    public SunRiseModel getSunRise() {
        return sunRise;
    }

    public void setSunRise(SunRiseModel sunRise) {
        this.sunRise = sunRise;
    }

    public Boolean getUsingImperialUnit() {
        return isUsingImperialUnit;
    }

    public void setUsingImperialUnit(Boolean usingImperialUnit) {
        isUsingImperialUnit = usingImperialUnit;
    }
}
