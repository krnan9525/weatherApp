package com.app.mark.weatherapp.model;

import com.app.mark.weatherapp.enumerations.Units;
import com.app.mark.weatherapp.interfaces.JSONSerializationInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by Mark on 05/04/2018.
 */

public class WindModel implements JSONSerializationInterface {
    private double speed = 0.0f;
    private int degree = 0;
    private Units unit = Units.METRIC;

    public WindModel(Boolean isUsingImperialUnit) {
        if (isUsingImperialUnit) {
            unit = Units.IMPERIAL;
        } else {
            unit = Units.METRIC;
        }
    }

    @Override
    public void constructClassFromJson(JSONObject jsonObject) {
        try {
            speed = jsonObject.getDouble("speed");
            degree = jsonObject.getInt("deg");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String serializeToJson() {
        return null;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public int getDegree() {
        return degree;
    }

    public void setDegree(int degree) {
        this.degree = degree;
    }

    public Units getUnit() {
        return unit;
    }

    public void setUnit(Units unit) {
        this.unit = unit;
    }

    public String getWindSpeedWithUnit() {
        NumberFormat formatter = new DecimalFormat("#0.0");
        return formatter.format(getSpeed()) + getSpeedUnitReadable();
    }

    private String getSpeedUnitReadable() {
        switch (unit) {
            case METRIC:
                return "m/s";
            case IMPERIAL:
                return "mile/hour";
            case SCIENTIFIC:
                return "m/s";
        }
        return null;
    }
}
