package com.app.mark.weatherapp.model;

import com.app.mark.weatherapp.interfaces.JSONSerializationInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Mark on 05/04/2018.
 */

public class SunRiseModel implements JSONSerializationInterface {
    private long sunRiseTimeUnix = 0;
    private long sunSetTimeUnit = 0;
    private String countryCode = "";

    @Override
    public void constructClassFromJson(JSONObject jsonObject) {
        try {
            sunRiseTimeUnix = jsonObject.getLong("sunrise");
            sunSetTimeUnit = jsonObject.getLong("sunset");
            countryCode = jsonObject.getString("country");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String serializeToJson() {
        return null;
    }

    public long getSunRiseTimeUnix() {
        return sunRiseTimeUnix;
    }

    public void setSunRiseTimeUnix(long sunRiseTimeUnix) {
        this.sunRiseTimeUnix = sunRiseTimeUnix;
    }

    public long getSunSetTimeUnit() {
        return sunSetTimeUnit;
    }

    public void setSunSetTimeUnit(long sunSetTimeUnit) {
        this.sunSetTimeUnit = sunSetTimeUnit;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getSunRiseTimeReadable()
    {
        Date date = new Date(sunRiseTimeUnix * 1000);
        return DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault()).format(date);
    }

    public String getSunSetTimeReadable()
    {
        Date date = new Date(sunSetTimeUnit * 1000);
        return DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault()).format(date);
    }
}
