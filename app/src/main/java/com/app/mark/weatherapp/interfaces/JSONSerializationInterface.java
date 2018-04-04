package com.app.mark.weatherapp.interfaces;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Mark on 04/04/2018.
 */

public interface JSONSerializationInterface extends Serializable{

    void constructClassFromJson(JSONObject jsonObject);

    String serializeToJson();
}
