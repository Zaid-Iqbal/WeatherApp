package com.example.weatherapp;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class HourlyWeatherViewHolder extends RecyclerView.ViewHolder {
    public TextView Day;
    public TextView Time;
    public TextView Temprature;
    public TextView Description;
    public ImageView Icon;

    HourlyWeatherViewHolder(View view) {
        super(view);
        Day = view.findViewById(R.id.DayRecycleDisplay);
        Time = view.findViewById(R.id.TimeRecycleDisplay);
        Temprature = view.findViewById(R.id.TempRecycleDisplay);
        Description = view.findViewById(R.id.SkyRecycleDisplay);
        Icon = view.findViewById(R.id.HourlyIconDisplay);
    }
}
