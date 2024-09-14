/*
 * Copyright (C) 2020-2024 TenX-OS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.settings.tenx.weather;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.settings.R;
import com.android.settings.tenx.weather.http.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WeatherView extends LinearLayout {

    private String temp;
    private String status;
    private String currentLocation;
    private LocationManager locationManager;
    private TextView mWeather;
    private TextView mWeatherCity;
    private TextView mWeatherTemperature;
    private TextView mWeatherStatus;

    private ExecutorService executorService;
    private final Handler mainHandler;

    public WeatherView(Context context) {
        this(context, null);
    }

    public WeatherView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeatherView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        initLocationManager();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mWeather = findViewById(R.id.weather);
        mWeatherCity = findViewById(R.id.weather_city);
        mWeatherTemperature = findViewById(R.id.weather_temperature);
        mWeatherStatus = findViewById(R.id.weather_status);
    }

    private void initLocationManager() {
        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                fetchWeatherData(latitude, longitude);
                stopLocationUpdates();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };

        requestLocationUpdates();
    }

    private void requestLocationUpdates() {
        boolean settingsTenXHeaderEnabled = Settings.System.getIntForUser(getContext().getContentResolver(),
                Settings.System.SETTINGS_TENX_DASHBOARD_ENABLED, 0, UserHandle.USER_CURRENT) == 1;
        if (!settingsTenXHeaderEnabled) return;
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    private void stopLocationUpdates() {
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
    }

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            fetchWeatherData(latitude, longitude);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    };

    private void fetchWeatherData(double latitude, double longitude) {
        if (!executorService.isShutdown()) {
            executorService.execute(() -> {
                String response = HttpRequest.getRequest("https://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&units=metric&appid=" + getContext().getString(R.string.owm_api_key));
                if (response != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONObject main = jsonObject.getJSONObject("main");
                        JSONObject weather = jsonObject.getJSONArray("weather").getJSONObject(0);
                        status = weather.getString("description");
                        temp = main.getString("temp") + "°C";
                        currentLocation = jsonObject.getString("name");
                    } catch (JSONException ignored) {
                    }

                    mainHandler.post(() -> {
                        setWeatherType();
                    });
                }
            });
        }
    }

    private void setWeatherType() {
        int type = Settings.System.getIntForUser(getContext().getContentResolver(),
            Settings.System.SETTINGS_TENX_DASHBOARD_WEATHER_TYPE, 0,
            UserHandle.USER_CURRENT);

        if (type == 0) {
            mWeather.setVisibility(View.VISIBLE);
            mWeatherCity.setVisibility(View.GONE);
            mWeatherTemperature.setVisibility(View.GONE);
            mWeatherStatus.setVisibility(View.GONE);
            if (temp != null && status != null) {
                String weatherText = getResources().getString(R.string.today_weather_title) +
                        " " +
                        temp +
                        ", " +
                        " " +
                        status.toUpperCase();
                mWeather.setText(weatherText);
            }
        } else if (type == 1) {
            mWeather.setVisibility(View.GONE);
            mWeatherCity.setVisibility(View.VISIBLE);
            mWeatherTemperature.setVisibility(View.VISIBLE);
            mWeatherStatus.setVisibility(View.GONE);
            if (currentLocation != null && temp != null) {
                mWeatherCity.setText(currentLocation);
                mWeatherTemperature.setText(temp);
            }
        } else {
            mWeather.setVisibility(View.GONE);
            mWeatherCity.setVisibility(View.VISIBLE);
            mWeatherTemperature.setVisibility(View.VISIBLE);
            mWeatherStatus.setVisibility(View.VISIBLE);
            if (currentLocation != null && temp != null && status != null) {
                mWeatherCity.setText(currentLocation);
                mWeatherTemperature.setText(temp);
                mWeatherStatus.setText(status.toUpperCase());
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (executorService.isShutdown()) {
            executorService = Executors.newSingleThreadExecutor();
        }
        initLocationManager();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopLocationUpdates();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
