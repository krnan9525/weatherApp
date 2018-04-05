package com.app.mark.weatherapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.app.mark.weatherapp.interfaces.OnRemoteCallFinishListener;
import com.app.mark.weatherapp.model.CurrentWeatherModel;
import com.app.mark.weatherapp.services.OpenWeatherService;

import static java.lang.Math.abs;
import static java.lang.Math.min;

public class MainActivity extends AppCompatActivity {

    /**
     * REFRESH_LOCATION_TIMEOUT:
     * refresh the current location and store in user preference if it hasn't been updated
     * within the given seconds.
     * Do this to avoid unnecessary power consumption
     * <p>
     * 1800s => 0.5 hour
     */
    private static final int REFRESH_LOCATION_TIMEOUT = 1800;
    private SharedPreferences preferences;

    private double userLatitude = 0.0f;
    private double userLongitude = 0.0f;
    private long lastRefreshLocationTimeStamp = 0;
    private LocationManager locc;
    private CurrentWeatherModel currentWeatherModel;
    TextView cityName, currentTemp, minTemp, maxTemp, humidity, windSpeed, windDirection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        setPreferences();
        getStoredGeoLocation();
        refreshGeoCoordinates();
        OpenWeatherService openWeatherService = new OpenWeatherService(this);
        currentWeatherModel = openWeatherService.getCurrentWeatherByLocation(userLatitude, userLongitude, new OnRemoteCallFinishListener() {
            @Override
            public void success() {
                setUI();
            }
        });
    }

    private void initUI() {
        cityName = findViewById(R.id.city_name);
        currentTemp = findViewById(R.id.current_temp);
        minTemp = findViewById(R.id.min_temp);
        maxTemp = findViewById(R.id.max_temp);
        humidity = findViewById(R.id.humidity);
        windSpeed = findViewById(R.id.wind_speed);
        windDirection = findViewById(R.id.wind_direction);
    }

    private void setUI() {
        cityName.setText(currentWeatherModel.getCityName());
        currentTemp.setText(currentWeatherModel.getTemperature().getAvgTempWithUnit());
        minTemp.setText(currentWeatherModel.getTemperature().getMinTempWithUnit());
        maxTemp.setText(currentWeatherModel.getTemperature().getMaxTempWithUnit());
        humidity.setText(currentWeatherModel.getHumidity() + "%");
        windSpeed.setText(currentWeatherModel.getWind().getWindSpeedWithUnit());
        windDirection.setText(currentWeatherModel.getWind().getDegree() + " Degree");
    }

    private void refreshGeoCoordinates() {
        /* Refresh location if is not set or is timeout*/
        if (shouldLocationBeRefreshed()) {
            /* Request for location permission if is not granted */
            requestLocationPermission();
            locc = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            LocationListener locationListener = new MyLocationListener();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locc.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 5000, 10, locationListener);
        }
    }

    private void setPreferences() {
        preferences = getSharedPreferences("weatherApp", Activity.MODE_PRIVATE);
    }

    /**
     * getStoredGeoLocation:
     * get stored location info from shared preferences
     */
    private void getStoredGeoLocation() {
        userLatitude = preferences.getFloat("lastLatitude", 0.0f);
        userLongitude = preferences.getFloat("lastLongitude", 0.0f);
        lastRefreshLocationTimeStamp = preferences.getLong("lastRefreshLocationTimeStamp", 0);
    }

    private boolean shouldLocationBeRefreshed() {
        return (abs(userLatitude - 0.0f) < 0.01f && abs(userLongitude - 0.0f) < 0.01f) //no record found
                || (System.currentTimeMillis() / 1000L > (lastRefreshLocationTimeStamp + REFRESH_LOCATION_TIMEOUT)); //timeout
    }

    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    Toast.makeText(getApplicationContext(), "This app wishes to use your location to provide personalized information for you.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {
            userLatitude = loc.getLatitude();
            userLongitude = loc.getLongitude();
            SharedPreferences.Editor prefEditor = preferences.edit();
            prefEditor.putFloat("lastLatitude", (float) userLatitude);
            prefEditor.putFloat("lastLongitude", (float) userLongitude);
            prefEditor.putLong("lastRefreshLocationTimeStamp", System.currentTimeMillis() / 1000L);
            prefEditor.apply();
            locc.removeUpdates(this);
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }
}
