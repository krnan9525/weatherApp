package com.app.mark.weatherapp.model;

import com.app.mark.weatherapp.Constants;
import com.app.mark.weatherapp.interfaces.JSONSerializationInterface;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Mark on 05/04/2018.
 */

public class WeatherModel implements JSONSerializationInterface {
    private int weatherId = 0;
    private String mainDescription = "";
    private String description = "";
    private String iconId = "";

    @Override
    public void constructClassFromJson(JSONObject jsonObject) {
        try {
            weatherId = jsonObject.getInt("id");
            mainDescription = jsonObject.getString("main");
            description = jsonObject.getString("description");
            iconId = jsonObject.getString("icon");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String serializeToJson() {
        return null;
    }

    public int getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(int weatherId) {
        this.weatherId = weatherId;
    }

    public String getMainDescription() {
        return mainDescription;
    }

    public void setMainDescription(String mainDescription) {
        this.mainDescription = mainDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIconId() {
        return iconId;
    }

    public void setIconId(String iconId) {
        this.iconId = iconId;
    }

    public String getIconWebPath(){
        return Constants.OPEN_WEATHER_ICON_BASE_PATH + iconId + ".png";
    }
}
