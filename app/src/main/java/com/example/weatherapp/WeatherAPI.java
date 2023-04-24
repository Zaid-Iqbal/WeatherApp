package com.example.weatherapp;

import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

public class WeatherAPI {

    private static final String TAG = "Visual Crossing Weather App";
    private static final String RAW_URL = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/<LOCATION>?unitGroup=us&lang=en&key=KG9EMUAY39ABKVVVE6AXJ8AHW";

    public static void getSourceDataForDailyWeather(DailyWeatherActivity dailyWeatherActivity) {
        RequestQueue queue = Volley.newRequestQueue(dailyWeatherActivity);

        String DATA_URL = RAW_URL.replace("LOCATION",MainActivity.location);

        Uri.Builder buildURL = Uri.parse(DATA_URL).buildUpon();
        String urlToUse = buildURL.build().toString();

        Response.Listener<JSONObject> listener =
                response -> handleResultsDailyWeather(dailyWeatherActivity, response.toString());

        Response.ErrorListener error = error1 -> {
            Log.d(TAG, "getSourceData: ");
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(new String(error1.networkResponse.data));
                Log.d(TAG, "getSourceData: " + jsonObject);
                handleResultsDailyWeather(dailyWeatherActivity, null);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        };

        // Request a string response from the provided URL.
//        JsonArrayRequest jsonArrayRequest =
//                new JsonArrayRequest(Request.Method.GET, urlToUse,
//                        null, listener, error);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(urlToUse, listener, error);

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    private static void handleResultsDailyWeather(DailyWeatherActivity dailyWeatherActivity, String s) {
        if (s == null) {
            Log.d(TAG, "handleResults: Failure in data download");
            dailyWeatherActivity.downloadFailed();
            return;
        }

        ArrayList<DailyWeather> dailyWeatherList = parseJSONDailyWeather(s);
        if (dailyWeatherList != null)
            Toast.makeText(dailyWeatherActivity, "Loaded weather.", Toast.LENGTH_SHORT).show();
        dailyWeatherActivity.updateDailyWeatherData(dailyWeatherList);
    }

    public static void getSourceDataForMain(MainActivity mainActivity) {
        RequestQueue queue = Volley.newRequestQueue(mainActivity);

        String DATA_URL = RAW_URL.replace("LOCATION", MainActivity.location);

        Uri.Builder buildURL = Uri.parse(DATA_URL).buildUpon();
        String urlToUse = buildURL.build().toString();

        Response.Listener<JSONObject> listener =
                response -> handleResultsMain(mainActivity, response.toString());

        Response.ErrorListener error = error1 -> {
            Log.d(TAG, "getSourceData: ");
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(new String(error1.networkResponse.data));
                Log.d(TAG, "getSourceData: " + jsonObject);
                handleResultsMain(mainActivity, null);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        };

        // Request a string response from the provided URL.
//        JsonArrayRequest jsonArrayRequest =
//                new JsonArrayRequest(Request.Method.GET, urlToUse,
//                        null, listener, error);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(urlToUse, listener, error);

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    private static void handleResultsMain(MainActivity mainActivity, String s) {

        if (s == null) {
            Log.d(TAG, "handleResults: Failure in data download");
            Toast.makeText(mainActivity, "handleResults: Failure in data download.", Toast.LENGTH_SHORT).show();
            mainActivity.downloadFailed();
            return;
        }

        Weather weather = parseJSONMain(s);
        if (weather != null)
            Toast.makeText(mainActivity, "Loaded weather.", Toast.LENGTH_SHORT).show();
        mainActivity.updateMainData(weather);
    }

    private static Weather parseJSONMain(String s) {
        try {
            JSONObject data = new JSONObject(s);

            long currentTime = data.getJSONObject("currentConditions").getLong("datetimeEpoch") * 1000;
            int temperature = data.getJSONObject("currentConditions").getInt("temp");
            int feelslike = data.getJSONObject("currentConditions").getInt("feelslike");
            String skyConditions = data.getJSONObject("currentConditions").getString("conditions");
            int cloudCover = data.getJSONObject("currentConditions").getInt("cloudcover");
            int windDirection = data.getJSONObject("currentConditions").getInt("winddir");
            double windSpeed = data.getJSONObject("currentConditions").getDouble("windspeed");
            double windGust = -1.0;
            try
            {
                windGust = data.getJSONObject("currentConditions").getDouble("windgust");
            }
            catch(Exception e){
                e.printStackTrace();
            }
            double humidity = data.getJSONObject("currentConditions").getDouble("humidity");
            int uv = data.getJSONObject("currentConditions").getInt("uvindex");
            double visibility = data.getJSONObject("currentConditions").getDouble("visibility");
            ArrayList<HourlyWeather> hourlyWeatherList = new ArrayList<>();

            //get remaining hours today
            JSONArray hours = ( (JSONObject) data.getJSONArray("days").get(0)).getJSONArray("hours");
            int currentHour = Integer.parseInt(new SimpleDateFormat("k", Locale.getDefault()).format(currentTime));
            for (int j = currentHour+1; j < hours.length(); j++) {
                JSONObject hour = hours.getJSONObject(j);
                long time = hour.getLong("datetimeEpoch") * 1000;
                double temp = hour.getDouble("temp");
                String description = hour.getString("conditions");
                String icon = hour.getString("icon");
                hourlyWeatherList.add(new HourlyWeather(time, temp, description, icon));
            }

            //get all hours tomorrow
            hours = ( (JSONObject) data.getJSONArray("days").get(1)).getJSONArray("hours");
            for (int j = 0; j < hours.length(); j++) {
                JSONObject hour = hours.getJSONObject(j);
                long time = hour.getLong("datetimeEpoch") * 1000;
                double temp = hour.getDouble("temp");
                String description = hour.getString("conditions");
                String icon = hour.getString("icon");
                hourlyWeatherList.add(new HourlyWeather(time, temp, description, icon));
            }

            //fill list with hours of the day after until we hit 48 hours total
            hours = ( (JSONObject) data.getJSONArray("days").get(2)).getJSONArray("hours");
            int remaining = hourlyWeatherList.size();
            for (int j = 0; j < (48 - remaining); j++) {
                JSONObject hour = hours.getJSONObject(j);
                long time = hour.getLong("datetimeEpoch") * 1000;
                double temp = hour.getDouble("temp");
                String description = hour.getString("conditions");
                String icon = hour.getString("icon");
                hourlyWeatherList.add(new HourlyWeather(time, temp, description, icon));
            }

            hours = ( (JSONObject) data.getJSONArray("days").get(0)).getJSONArray("hours");
            double morningTemp = hours.getJSONObject(8).getDouble("temp");
            double afternoonTemp = hours.getJSONObject(13).getDouble("temp");
            double eveningTemp = hours.getJSONObject(17).getDouble("temp");
            double nightTemp = hours.getJSONObject(23).getDouble("temp");
            long sunrise = data.getJSONObject("currentConditions").getLong("sunriseEpoch") * 1000;
            long sunset = data.getJSONObject("currentConditions").getLong("sunsetEpoch") * 1000;
            String icon = data.getJSONObject("currentConditions").getString("icon");

            return new Weather(currentTime, temperature, feelslike, skyConditions, cloudCover, windDirection, windSpeed,windGust, humidity,
                    uv, visibility, morningTemp, afternoonTemp, eveningTemp, nightTemp, sunrise, sunset, icon, hourlyWeatherList);
        } catch (Exception e) {
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private static ArrayList<DailyWeather> parseJSONDailyWeather(String s) {
        try {
            ArrayList<DailyWeather> dailyWeatherList = new ArrayList<>();
            JSONObject data = new JSONObject(s);
            JSONArray days = data.getJSONArray("days");
            for(int i = 0; i < days.length(); i++){
                JSONObject day = ( (JSONObject) days.get(i));
                long date = day.getLong("datetimeEpoch") * 1000;
                double tempmax =   day.getDouble("tempmax");
                double tempmin =   day.getDouble("tempmin");
                String desc = day.getString("description");
                String icon = day.getString("icon");
                int precip = day.getInt("precipprob");
                int uv = day.getInt("uvindex");
                JSONArray hours = day.getJSONArray("hours");
                double morning = ((JSONObject)hours.get(8)).getDouble("temp");
                double afternoon = ((JSONObject)hours.get(13)).getDouble("temp");
                double evening = ((JSONObject)hours.get(17)).getDouble("temp");
                double night = 0.0;
                if (day.getJSONArray("hours").length() == 24){
                    night = ((JSONObject)hours.get(23)).getDouble("temp");
                }
                else    //daylight savings day
                {
                    night = ((JSONObject)day.getJSONArray("hours").get(22)).getDouble("temp");
                }

                dailyWeatherList.add(new DailyWeather(date, tempmin, tempmax, desc, icon, precip, uv, morning, afternoon, evening, night));
            }
            return dailyWeatherList;
        } catch (Exception e) {
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

}
