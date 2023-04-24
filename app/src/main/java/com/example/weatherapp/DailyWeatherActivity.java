package com.example.weatherapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DailyWeatherActivity extends AppCompatActivity {

    private ArrayList<DailyWeather> DailyWeatherList;

    private DailyWeatherViewAdapter dailyAdapter;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_weather);

        DailyWeatherList = new ArrayList<>();
        recyclerView = findViewById(R.id.DailyWeatherRecycler);
        dailyAdapter = new DailyWeatherViewAdapter(DailyWeatherList, this);
        recyclerView.setAdapter(dailyAdapter);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        WeatherAPI.getSourceDataForDailyWeather(this);
        setTitle(MainActivity.location + "15 Day");
    }

    public void updateDailyWeatherData(ArrayList<DailyWeather> dailyWeatherList) {
        DailyWeatherList.addAll(dailyWeatherList);
        dailyAdapter.notifyItemRangeChanged(0, DailyWeatherList.size());
    }

    public void downloadFailed() {
        DailyWeatherList.clear();
        dailyAdapter.notifyItemRangeChanged(0, DailyWeatherList.size());
    }

}
