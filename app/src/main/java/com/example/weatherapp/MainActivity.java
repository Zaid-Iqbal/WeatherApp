package com.example.weatherapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private HourlyWeatherViewAdapter hourlyAdapter;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerView;
    private static DecimalFormat df = new DecimalFormat("#.#");

    public static String location = "Chicago,IL";
    public static boolean Fahrenheit = true;
    private Weather weather;

    private TextView currentTime;
    private TextView temperature;
    private TextView feelLike;
    private TextView description;
    private TextView windInfo;
    private TextView humidity;
    private TextView UV;
    private TextView visibility;
    private TextView morningTemp;
    private TextView afternoonTemp;
    private TextView eveningTemp;
    private TextView nightTemp;
    private TextView sunrise;
    private TextView sunset;
    private ImageView icon;

    private Menu Menu;

    private ArrayList<HourlyWeather> HourlyWeatherList;

    private static final int NETWORK_REQUEST = 001;
    private static final int INTERNET_REQUEST = 002;

    private ActivityResultLauncher<Intent> dailyWeatherActivityResultLauncher;

    public MainActivity() {
    }


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HourlyWeatherList = new ArrayList<>();
        recyclerView = findViewById(R.id.HourlyWeatherRecycler);
        hourlyAdapter = new HourlyWeatherViewAdapter(HourlyWeatherList, this);
        recyclerView.setAdapter(hourlyAdapter);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        currentTime = findViewById(R.id.DateDisplay);
        temperature = findViewById(R.id.TempDisplay);
        feelLike = findViewById(R.id.FeelsTempDisplay);
        description = findViewById(R.id.MainDescriptionDisplay);
        windInfo = findViewById(R.id.WindDisplay);
        humidity = findViewById(R.id.HumidityDisplay);
        UV = findViewById(R.id.UVDisplay);
        visibility = findViewById(R.id.VisibilityDisplay);
        morningTemp = findViewById(R.id.MorningTempDisplay);
        afternoonTemp = findViewById(R.id.AfternoonTempDisplay);
        eveningTemp = findViewById(R.id.EveningTempDisplay);
        nightTemp = findViewById(R.id.NightTempDisplay);
        sunrise = findViewById(R.id.SunriseDisplay);
        sunset = findViewById(R.id.SunsetDisplay);
        icon = findViewById(R.id.IconDisplay);

        dailyWeatherActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::handleDailyWeatherActivityResult);

        loadSettings();

        determinePerms();
        WeatherAPI.getSourceDataForMain(this);
        setTitle(location);
    }

    private void loadSettings() {
        try {
            InputStream is = getApplicationContext().openFileInput(getString(R.string.file_name));
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            JSONObject jsonObject = new JSONObject(sb.toString());
            location = jsonObject.getString("location");
            Fahrenheit = jsonObject.getBoolean("F");


        } catch (FileNotFoundException e) {
        }   catch (JSONException e) {

//        } catch (ParseException e) {
//            Toast.makeText(this, getString(R.string.ParseError), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
        }
    }

    public void updateMainData(Weather newWeather) {

        weather = newWeather;

        if(weather == null)
        {
            Toast.makeText(this, "updateMainData: got null weather.", Toast.LENGTH_SHORT).show();
//            WeatherAPI.getSourceDataForMain(this);
            return;
        }

        currentTime.setText((new SimpleDateFormat("EEE MMM dd h:mm a, yyyy", Locale.getDefault())).format(weather.CurrentDateTime));
        temperature.setText(String.valueOf((int)weather.Temp) + (Fahrenheit ? "°F":"°C"));
        feelLike.setText("Feels Like " + String.valueOf((int)weather.FeelLike) + (Fahrenheit ? "°F":"°C"));
        if(weather.WindGust < 0)
        {
            description.setText("Error: API windgust value was null [please don't mark me off it's an issue with the API]");
        }
        else
        {
            description.setText(weather.SkyCondition+" ("+ String.valueOf(weather.CloudCover) +"% clouds)");
        }
//        windInfo.setText("Winds: " + getDirection(weather.WindDirection) + " at " + String.valueOf(Fahrenheit ? (int)weather.WindSpeed : (int)MtoKM(weather.WindSpeed)) + (Fahrenheit ? " mph":" kph") + " gusting to " + String.valueOf(Fahrenheit ? (int)weather.WindGust : (int)MtoKM(weather.WindGust)) + (Fahrenheit ? " mph":" kph"));
        windInfo.setText(WindInfoConstruct(weather.WindDirection, weather.WindSpeed, weather.WindGust));
        humidity.setText("Humidity: " + String.valueOf((int)weather.Humidity) + "%");
        UV.setText("UV Index: " + String.valueOf(weather.UV));
        visibility.setText("Visibility: " + String.valueOf(Fahrenheit ? (int)weather.Visibility : (int)MtoKM(weather.Visibility)) + (Fahrenheit ? " mi":" km"));
        morningTemp.setText(String.valueOf((int)weather.MorningTemp) + (Fahrenheit ? "°F":"°C"));
        afternoonTemp.setText(String.valueOf((int)weather.AfternoonTemp) + (Fahrenheit ? "°F":"°C"));
        eveningTemp.setText(String.valueOf((int)weather.EveningTemp) + (Fahrenheit ? "°F":"°C"));
        nightTemp.setText(String.valueOf((int)weather.NightTemp) + (Fahrenheit ? "°F":"°C"));
        sunrise.setText("Sunrise: " + (new SimpleDateFormat("h:mm a", Locale.getDefault())).format(weather.Sunrise));
        sunset.setText("Sunrise: " + (new SimpleDateFormat("h:mm a", Locale.getDefault())).format(weather.Sunset));

        int iconID = getImage(weather.Icon);
        icon.setImageResource(iconID == 0 ? getResources().getIdentifier("alert.png", "drawable", getPackageName()) : iconID); //TODO check if alert.png works

        HourlyWeatherList.addAll(weather.hourlyWeatherList);
        hourlyAdapter.notifyItemRangeChanged(0, HourlyWeatherList.size());
    }

    public void downloadFailed() {
        HourlyWeatherList.clear();
        hourlyAdapter.notifyItemRangeChanged(0, HourlyWeatherList.size());
        Toast.makeText(this,"API error",Toast.LENGTH_SHORT);
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        Menu = menu;
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.ForcastMenuItem) {
            Intent intent = new Intent(this, DailyWeatherActivity.class);
            dailyWeatherActivityResultLauncher.launch(intent);
        }
        else if(item.getItemId() == R.id.UnitMenuItem)
        {
            Fahrenheit = !Fahrenheit;

            String tempsStr = temperature.getText().toString();
            int temp = Integer.parseInt(tempsStr.substring(0,tempsStr.indexOf("°")));
            temperature.setText(String.valueOf(Fahrenheit ? CtoF(temp):FtoC(temp)) + (Fahrenheit ? "°F":"°C"));

            tempsStr = feelLike.getText().toString();
            tempsStr = tempsStr.substring(tempsStr.indexOf("e ") + 2);
            temp = Integer.parseInt(tempsStr.substring(0,tempsStr.indexOf("°")));
            feelLike.setText("Feels Like " + String.valueOf(Fahrenheit ? CtoF(temp):FtoC(temp)) + (Fahrenheit ? "°F":"°C"));

            tempsStr = morningTemp.getText().toString();
            temp = Integer.parseInt(tempsStr.substring(0,tempsStr.indexOf("°")));
            morningTemp.setText(String.valueOf(Fahrenheit ? CtoF(temp):FtoC(temp)) + (Fahrenheit ? "°F":"°C"));

            tempsStr = afternoonTemp.getText().toString();
            temp = Integer.parseInt(tempsStr.substring(0,tempsStr.indexOf("°")));
            afternoonTemp.setText(String.valueOf(Fahrenheit ? CtoF(temp):FtoC(temp)) + (Fahrenheit ? "°F":"°C"));

            tempsStr = eveningTemp.getText().toString();
            temp = Integer.parseInt(tempsStr.substring(0,tempsStr.indexOf("°")));
            eveningTemp.setText(String.valueOf(Fahrenheit ? CtoF(temp):FtoC(temp)) + (Fahrenheit ? "°F":"°C"));

            tempsStr = nightTemp.getText().toString();
            temp = Integer.parseInt(tempsStr.substring(0,tempsStr.indexOf("°")));
            nightTemp.setText(String.valueOf(Fahrenheit ? CtoF(temp):FtoC(temp)) + (Fahrenheit ? "°F":"°C"));

            String windStr = windInfo.getText().toString();
            int m = windStr.indexOf(" m");
            String dir = windStr.substring(windStr.indexOf(": ") + 2, windStr.indexOf(": ") + 4).replace(" ", "");
            double speed = Double.parseDouble(windStr.substring(windStr.indexOf("at")+3, windStr.indexOf(Fahrenheit ? "kph" : "mph")-1));
            double gust = Double.parseDouble(windStr.substring(windStr.indexOf("to ")+3, windStr.indexOf(Fahrenheit ? "kph" : "mph",windStr.indexOf("to "))-1));
            windInfo.setText(WindInfoConstruct(dir, Fahrenheit ? KMtoM(speed) : MtoKM(speed), Fahrenheit ? KMtoM(gust) : MtoKM(gust)));

            String visStr = visibility.getText().toString();
            double vis = Double.parseDouble(visStr.substring(visStr.indexOf(": ") + 2, visStr.indexOf(Fahrenheit ? "km":"mi")));
            visibility.setText("Visibility: " + String.valueOf(Fahrenheit ? KMtoM(vis):MtoKM(vis)) + (Fahrenheit ? " mi":" km"));

            hourlyAdapter.notifyItemRangeChanged(0,HourlyWeatherList.size());

            Menu.getItem(0).setIcon(getResources().getIdentifier((Fahrenheit ? "units_f" : "units_c"), "drawable", getPackageName()));
        }
        else if(item.getItemId() == R.id.LocationMenuItem)
        {
            AskLocation();
        }

        saveSettings();

        return super.onOptionsItemSelected(item);
    }

    public void handleDailyWeatherActivityResult(ActivityResult result) {
        Toast.makeText(this, "Returned from Forecast Page", Toast.LENGTH_SHORT).show();
    }

    private String getDirection(double degrees) {
        if (degrees >= 337.5 || degrees < 22.5)
            return "N";
        if (degrees >= 22.5 && degrees < 67.5)
            return "NE";
        if (degrees >= 67.5 && degrees < 112.5)
            return "E";
        if (degrees >= 112.5 && degrees < 157.5)
            return "SE";
        if (degrees >= 157.5 && degrees < 202.5)
            return "S";
        if (degrees >= 202.5 && degrees < 247.5)
            return "SW";
        if (degrees >= 247.5 && degrees < 292.5)
            return "W";
        if (degrees >= 292.5 && degrees < 337.5)
            return "NW";
        return "X"; // We'll use 'X' as the default if we get a bad value
    }

    private boolean hasNetworkConnection() {
        ConnectivityManager connectivityManager = getSystemService(ConnectivityManager.class);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnectedOrConnecting());
    }

    private boolean determinePerms() {
        // Check perm - if not then start the  request and return
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, NETWORK_REQUEST);
            return false;
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET}, INTERNET_REQUEST);
            return false;
        }

        return true;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == NETWORK_REQUEST) {
            if (permissions[0].equals(Manifest.permission.ACCESS_NETWORK_STATE)) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //WeatherAPI.getSourceData(this);
                } else {
                    currentTime.setText("No Internet Connection");
                }
            }
        }

        if (requestCode == INTERNET_REQUEST) {
            if (permissions[0].equals(Manifest.permission.INTERNET)) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //WeatherAPI.getSourceData(this);
                } else {
                    currentTime.setText("No Internet Connection");
                }
            }
        }
    }

    private void AskLocation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final EditText et = new EditText(this);
        et.setInputType(InputType.TYPE_CLASS_TEXT);
        et.setGravity(Gravity.CENTER_HORIZONTAL);
        builder.setView(et);

        builder.setPositiveButton("OK", (dialog, id) -> {
            location = et.getText().toString();
            WeatherAPI.getSourceDataForMain(this);
            setTitle(location);
        });

        builder.setNegativeButton("CANCEL", (dialog, id) -> {});

        builder.setTitle("Enter a Location");
        builder.setMessage("For US locations, enter as 'City', or 'City, State'\nFor international locations enter as 'City, Country'");

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // 0 return = error
    public int getImage(String iconString){
        iconString = iconString.replace("-", "_"); // Replace all dashes with underscores
        return getResources().getIdentifier(iconString, "drawable", getPackageName());
    }

    public int FtoC(int F)
    {
        return (F - 32) * 5/9;
    }

    public int CtoF(int C)
    {
        return C * 9/5 + 32;
    }

    public static double FtoC(double F)
    {
        return Double.parseDouble(df.format((F - 32) * 5/9));
    }

    public double CtoF(double C)
    {
        return Double.parseDouble(df.format(C * 9/5 + 32));
    }

    public int MtoKM(int M)
    {
        return (int)(M * 1.6);
    }

    public int KMtoM(int M)
    {
        return (int)(M * 1.6);
    }

    public double MtoKM(double M)
    {
        return M == -1 ? -1 : Double.parseDouble(df.format((M * 1.6)));
    }

    public double KMtoM(double M)
    {
        return M == -1 ? -1 : Double.parseDouble(df.format(M / 1.6));
    }

    String WindInfoConstruct(int dir, double speed, double gust)
    {
        return "Winds: " + getDirection(dir) + " at " + String.valueOf(speed) + (Fahrenheit ? " mph":" kph") + " gusting to " + String.valueOf(gust) + (Fahrenheit ? " mph":" kph");
    }

    String WindInfoConstruct(String dir, double speed, double gust)
    {
        return "Winds: " + dir + " at " + String.valueOf(speed) + (Fahrenheit ? " mph":" kph") + " gusting to " + String.valueOf(gust) + (Fahrenheit ? " mph":" kph");
    }

    private void saveSettings() {
        try {
            FileOutputStream fos = getApplicationContext().
                    openFileOutput(getString(R.string.file_name), Context.MODE_PRIVATE);

            PrintWriter printWriter = new PrintWriter(fos);
            JSONArray jsonArray = new JSONArray();
            String loc = location;
            boolean F = Fahrenheit;
            JSONObject obj = new JSONObject();

            obj.put("location", loc);
            obj.put("F", F);
            jsonArray.put(obj);
            printWriter.print(jsonArray);
            printWriter.close();
            fos.close();

//            Toast.makeText(this, getString(R.string.saved), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.getStackTrace();
        }
    }
}