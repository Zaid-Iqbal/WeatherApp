package com.example.weatherapp;

public class HourlyWeather {

    public long Time;
    public double Temperature;
    public String Description;
    public String Icon;

    public HourlyWeather(long time, double temperature, String description, String icon){

        Time = time;
        Temperature = temperature;
        Description = description;
        Icon = icon;
    }
}