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
    private double humidity = 0.0f;
    private WindModel wind;

    @Override
    public void constructClassFromJson(JSONObject jsonObject) {
        try {
            cityName = jsonObject.getString("name");
            temperature = new TemperatureModel();
            temperature.constructClassFromJson(jsonObject.getJSONObject("main"));
            humidity = jsonObject.getJSONObject("main").getDouble("humidity");
            wind = new WindModel();
            wind.constructClassFromJson(jsonObject.getJSONObject("wind"));
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

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(float humidity) {
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
}
