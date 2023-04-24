package com.example.weatherapp;

import java.util.ArrayList;

public class Weather {
    public long CurrentDateTime;
    public double Temp;
    public double FeelLike;
    public String SkyCondition;
    public int CloudCover;
    public int WindDirection;
    public double WindSpeed;
    public double WindGust;
    public double Humidity;
    public int UV;
    public double Visibility;
    public double MorningTemp;
    public double AfternoonTemp;
    public double EveningTemp;
    public double NightTemp;
    public long Sunrise;
    public long Sunset;
    public String Icon;
    public ArrayList<HourlyWeather> hourlyWeatherList;

    public Weather(long cdt, double temp, double feelLike, String skycondition, int cloudcover, int winddir,double windspeed, double windgust,
                   double humidity, int uv, double visibility, double morning, double afternoon, double evening, double night, long sunrise, long sunset,
                   String icon, ArrayList<HourlyWeather> hourlyWeathers){
        CurrentDateTime = cdt;
        Temp = temp;
        FeelLike = feelLike;
        SkyCondition = skycondition;
        CloudCover = cloudcover;
        WindDirection = winddir;
        WindSpeed = windspeed;
        WindGust = windgust;
        Humidity = humidity;
        UV = uv;
        Visibility = visibility;
        MorningTemp = morning;
        AfternoonTemp = afternoon;
        EveningTemp = evening;
        NightTemp = night;
        Sunrise = sunrise;
        Sunset = sunset;
        Icon = icon;
        hourlyWeatherList = hourlyWeathers;
    }
}
