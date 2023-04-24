package com.example.weatherapp;

public class DailyWeather {
    public long Date;
    public double LowTemp;
    public double HighTemp;
    public String Description;
    public String Icon;
    public int Precipitation;
    public int UV;
    public double MorningTemp;
    public double AfternoonTemp;
    public double EveningTemp;
    public double NightTemp;



    public DailyWeather(long date, double low, double high, String desc, String icon, int precip, int uv, double morning, double afternoon, double evening, double night){
        Date = date;
        LowTemp = low;
        HighTemp = high;
        Description = desc;
        Icon = icon;
        Precipitation = precip;
        UV = uv;
        MorningTemp = morning;
        AfternoonTemp = afternoon;
        EveningTemp = evening;
        NightTemp = night;
    }


}
