package com.example.weatherapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class DailyWeatherViewAdapter extends RecyclerView.Adapter<DailyWeatherViewHolder>{

    private final List<DailyWeather> weathers;
    private final DailyWeatherActivity activity;

    DailyWeatherViewAdapter(List<DailyWeather> empList, DailyWeatherActivity ma) {
        this.weathers = empList;
        activity = ma;
    }

    @NonNull
    @Override
    public DailyWeatherViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.daily_weather_list_row, parent, false);

        //itemView.setOnClickListener(mainAct);
        //itemView.setOnLongClickListener(mainAct);

        return new DailyWeatherViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DailyWeatherViewHolder holder, int position) {

        DailyWeather weather = weathers.get(position);

        holder.Date.setText((new SimpleDateFormat("EEEE MM/dd", Locale.getDefault())).format(weather.Date));
        holder.LowHighTemp.setText(String.valueOf(!MainActivity.Fahrenheit ? MainActivity.FtoC(weather.LowTemp) : weather.LowTemp) + (MainActivity.Fahrenheit ? "°F":"°C") + "/" + String.valueOf((int)weather.HighTemp) + (MainActivity.Fahrenheit ? "°F":"°C"));
        holder.Description.setText(weather.Description);
        holder.Icon.setImageResource(getImage(weather.Icon));
        holder.Precipitation.setText("(" + String.valueOf(weather.Precipitation) + "%) precip");
        holder.UV.setText("UV Index: " + String.valueOf(weather.UV));
        holder.MorningTemp.setText(String.valueOf(!MainActivity.Fahrenheit ? MainActivity.FtoC(weather.MorningTemp) : weather.MorningTemp) + (MainActivity.Fahrenheit ? "°F":"°C"));
        holder.AfternoonTemp.setText(String.valueOf(!MainActivity.Fahrenheit ? MainActivity.FtoC(weather.AfternoonTemp) : weather.AfternoonTemp) + (MainActivity.Fahrenheit ? "°F":"°C"));
        holder.EveningTemp.setText(String.valueOf(!MainActivity.Fahrenheit ? MainActivity.FtoC(weather.EveningTemp) : weather.EveningTemp) + (MainActivity.Fahrenheit ? "°F":"°C"));
        holder.NightTemp.setText(String.valueOf(!MainActivity.Fahrenheit ? MainActivity.FtoC(weather.NightTemp) : weather.NightTemp) + (MainActivity.Fahrenheit ? "°F":"°C"));
    }

    @Override
    public int getItemCount() {
        if (weathers == null){
            return 0;
        }
        return weathers.size();
    }

    public int getImage(String iconString){
        iconString = iconString.replace("-", "_"); // Replace all dashes with underscores
        return activity.getResources().getIdentifier(iconString, "drawable", activity.getPackageName());
    }

}
