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
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.mark.weatherapp.interfaces.OnRemoteCallFinishListener;
import com.app.mark.weatherapp.model.CurrentWeatherModel;
import com.app.mark.weatherapp.services.OpenWeatherService;
import com.q42.qlassified.Qlassified;
import com.q42.qlassified.Storage.QlassifiedSharedPreferencesService;
import com.squareup.picasso.Picasso;

import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Math.abs;
import static java.lang.Math.min;

public class MainActivity extends AppCompatActivity {

    /**
     * REFRESH_LOCATION_TIMEOUT:
     * refresh the current location and store in user preference if it hasn't been updated
     * within the given seconds.
     * Do this to avoid unnecessary power consumption
     * <p>
     * 1800s => 15 minutes
     */
    private static final int REFRESH_LOCATION_TIMEOUT = 900;
    /**
     * CURRENT_WEATHER_REFRESH_DELAY and CURRENT_WEATHER_REFRESH_INTERVAL
     * used to automatically refresh the weather if user stays on this activity
     */
    private static final int CURRENT_WEATHER_REFRESH_DELAY = 1000; //1 second
    private static final int CURRENT_WEATHER_REFRESH_INTERVAL = 15000; //15 second
    private static final int LOCATION_PERMISSION_CODE = 1;

    private SharedPreferences preferences;
    private double userLatitude = 0.0f;
    private double userLongitude = 0.0f;
    private long lastRefreshLocationTimeStamp = 0;
    private LocationManager locc;
    private CurrentWeatherModel currentWeatherModel;
    private TextView cityName, currentTemp, minTemp, maxTemp, humidity, windSpeed, windDirection, weatherText, sunRiseTime, sunSetTime;
    private ImageView weatherImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        setPreferences();
        putSecurityApiKey();
        getStoredGeoLocation();
        refreshGeoCoordinates();
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refreshCurrentWeather();
                        Toast.makeText(MainActivity.this, "Weather has been updated", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }, CURRENT_WEATHER_REFRESH_DELAY, CURRENT_WEATHER_REFRESH_INTERVAL);
    }

    /**
     * call remote server to refresh current weather data
     */
    private void refreshCurrentWeather() {
        OpenWeatherService openWeatherService = new OpenWeatherService(this);
        currentWeatherModel = openWeatherService.getCurrentWeatherByLocation(userLatitude, userLongitude, new OnRemoteCallFinishListener() {
            @Override
            public void success() {
                String countryCode = currentWeatherModel.getSunRise().getCountryCode();
                SharedPreferences.Editor prefEditor = preferences.edit();
                prefEditor.putString("countryCode", countryCode);
                prefEditor.putBoolean("isImperialUnitCountry", isImperialUnitCountry(countryCode));
                prefEditor.apply();
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
        weatherText = findViewById(R.id.weather_text);
        weatherImage = findViewById(R.id.weather_icon);
        sunRiseTime = findViewById(R.id.sun_rise_time);
        sunSetTime = findViewById(R.id.sun_set_time);
    }

    private void setUI() {
        cityName.setText(currentWeatherModel.getCityName());
        currentTemp.setText(currentWeatherModel.getTemperature().getAvgTempWithUnit());
        minTemp.setText(currentWeatherModel.getTemperature().getMinTempWithUnit());
        maxTemp.setText(currentWeatherModel.getTemperature().getMaxTempWithUnit());
        humidity.setText(currentWeatherModel.getHumidity() + "%");
        windSpeed.setText(currentWeatherModel.getWind().getWindSpeedWithUnit());
        windDirection.setText(currentWeatherModel.getWind().getDegree() + " Degree");
        weatherText.setText(currentWeatherModel.getWeather().getMainDescription());
        Picasso.with(this).load(currentWeatherModel.getWeather().getIconWebPath()).into(weatherImage);
        sunRiseTime.setText(currentWeatherModel.getSunRise().getSunRiseTimeReadable());
        sunSetTime.setText(currentWeatherModel.getSunRise().getSunSetTimeReadable());
    }

    private void refreshGeoCoordinates() {
        /* Refresh location if is not set or is timeout*/
        if (shouldLocationBeRefreshed()) {
            /* Request for location permission if is not granted */
            requestLocationPermission();
            setupLocationListener();
        }
    }

    private void setupLocationListener() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locc = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new MyLocationListener();
        locc.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 5000, 10, locationListener
        );
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
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_CODE);
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    Toast.makeText(getApplicationContext(), "This app wishes to use your location to provide personalized information for you.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private boolean isImperialUnitCountry(String countryCode) {
        String[] imperialCountries = getResources().getStringArray(R.array.country_with_imperial);
        for (String imperialCountry : imperialCountries) {
            if (countryCode.equalsIgnoreCase(imperialCountry)) {
                return true;
            }
        }
        return false;
    }

    private void putSecurityApiKey()
    {
        Boolean isFirstRun = preferences.getBoolean("isFirstRun", true);
        if(isFirstRun){
            Qlassified.Service.start(this);
            Qlassified.Service.setStorageService(new QlassifiedSharedPreferencesService(this, "api_key_storage"));
            Qlassified.Service.put("api_key", Constants.OPEN_WEATHER_API_KEY);
            SharedPreferences.Editor prefEditor = preferences.edit();
            prefEditor.putBoolean("isFirstRun", false);
            prefEditor.apply();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == LOCATION_PERMISSION_CODE){
            setupLocationListener();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
            refreshCurrentWeather();
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
