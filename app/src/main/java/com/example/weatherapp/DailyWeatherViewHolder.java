package com.example.weatherapp;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class DailyWeatherViewHolder  extends RecyclerView.ViewHolder {
    public TextView Date;
    public TextView LowHighTemp;
    public TextView Description;
    public ImageView Icon;
    public TextView Precipitation;
    public TextView UV;
    public TextView MorningTemp;
    public TextView AfternoonTemp;
    public TextView EveningTemp;
    public TextView NightTemp;

    DailyWeatherViewHolder(View view) {
        super(view);
        Date = view.findViewById(R.id.DayDateDisplay);
        LowHighTemp = view.findViewById(R.id.LowHiDisplay);
        Description = view.findViewById(R.id.DescriptionDisplay);
        Icon = view.findViewById(R.id.DailyIconDisplay);
        Precipitation = view.findViewById(R.id.PrecipDisplay);
        UV = view.findViewById(R.id.UV2Display);
        MorningTemp = view.findViewById(R.id.morningTempDailyDisplay);
        AfternoonTemp = view.findViewById(R.id.afternoonTempDailyDisplay);
        EveningTemp = view.findViewById(R.id.eveningTempDailyDisplay);
        NightTemp = view.findViewById(R.id.nightTempDailyDisplay);
    }
}
