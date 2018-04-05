package com.app.mark.weatherapp.model;

import com.app.mark.weatherapp.enumerations.Units;
import com.app.mark.weatherapp.interfaces.JSONSerializationInterface;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Mark on 05/04/2018.
 */

public class TemperatureModel implements JSONSerializationInterface {
    private double minTemp = 0.0f;
    private double maxTemp = 0.0f;
    private double avgTemp = 0.0f;
    private Units unit = Units.METRIC;

    public TemperatureModel(Boolean isUsingImperialUnit) {
        if (isUsingImperialUnit) {
            unit = Units.IMPERIAL;
        } else {
            unit = Units.METRIC;
        }
    }

    @Override
    public void constructClassFromJson(JSONObject jsonObject) {
        try {
            minTemp = jsonObject.getDouble("temp_min");
            maxTemp = jsonObject.getDouble("temp_max");
            avgTemp = jsonObject.getDouble("temp");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String serializeToJson() {
        return null;
    }

    public double getMinTemp() {
        return minTemp;
    }

    public void setMinTemp(double minTemp) {
        this.minTemp = minTemp;
    }

    public double getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(double maxTemp) {
        this.maxTemp = maxTemp;
    }

    public Units getUnit() {
        return unit;
    }

    public void setUnit(Units unit) {
        this.unit = unit;
    }

    public double getAvgTemp() {
        return avgTemp;
    }

    public void setAvgTemp(double avgTemp) {
        this.avgTemp = avgTemp;
    }

    public String getMinTempWithUnit() {
        return getMinTemp() + " " + getTempUnitReadable();
    }

    public String getAvgTempWithUnit() {
        return getAvgTemp() + " " + getTempUnitReadable();
    }

    public String getMaxTempWithUnit() {
        return getMaxTemp() + " " + getTempUnitReadable();
    }

    private String getTempUnitReadable() {
        switch (unit) {
            case METRIC:
                return "°C";
            case IMPERIAL:
                return "°F";
            case SCIENTIFIC:
                return "°K";
        }
        return null;
    }
}
