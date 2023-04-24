package com.example.weatherapp;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HourlyWeatherViewAdapter extends RecyclerView.Adapter<HourlyWeatherViewHolder>
{

    private final List<HourlyWeather> weathers;
    private final MainActivity mainAct;

    HourlyWeatherViewAdapter(List<HourlyWeather> empList, MainActivity ma) {
        this.weathers = empList;
        mainAct = ma;
    }

    @NonNull
    @Override
    public HourlyWeatherViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.hourly_weather_list_row, parent, false);

        return new HourlyWeatherViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull HourlyWeatherViewHolder holder, int position) {

        HourlyWeather weather = weathers.get(position);
        String day = new SimpleDateFormat("EEEE", Locale.getDefault()).format(weather.Time);
        if(day.equals(new SimpleDateFormat("EEEE", Locale.getDefault()).format(new Date())))
        {
            day = "Today";
        }
        holder.Day.setText(day);
        holder.Time.setText(String.valueOf(new SimpleDateFormat("h:mm a", Locale.getDefault()).format(weather.Time)));
        holder.Temprature.setText(String.valueOf(!MainActivity.Fahrenheit ? mainAct.FtoC(weather.Temperature) : weather.Temperature) + (MainActivity.Fahrenheit ? "°F":"°C"));
        holder.Description.setText(weather.Description);
        holder.Icon.setImageResource(getImage(weather.Icon));
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
        return mainAct.getResources().getIdentifier(iconString, "drawable", mainAct.getPackageName());
    }

}
