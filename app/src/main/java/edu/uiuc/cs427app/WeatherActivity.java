package edu.uiuc.cs427app;

import android.accounts.Account;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
//import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class WeatherActivity extends AppCompatActivity {

    private Account account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        account = getIntent().getParcelableExtra("account");
        ThemeUtils.applyTheme(account, this);
        setContentView(R.layout.activity_weather);

        // Load in the passed values from the previous activity
        String cityName = getIntent().getStringExtra("city");
        double latitude = getIntent().getDoubleExtra("lat",-34);
        double longitude = getIntent().getDoubleExtra("lon",151);

        String date_and_time = new java.util.Date().toString();
        String solo_name = cityName.split(",", 2)[0];


        // Views to set upon initiation of this activity
        TextView cityNameView = findViewById(R.id.CityName);
        TextView weatherTemp = findViewById(R.id.CityTemperature);
        TextView weatherTempRange = findViewById(R.id.CityTemperatureRange);
        TextView weatherType = findViewById(R.id.CityWeather);
        TextView windInfo = findViewById(R.id.windData);
        TextView humidInfo = findViewById(R.id.humidityData);
        TextView dewInfo = findViewById(R.id.dewPointData);
        TextView PrecipitationInfo = findViewById(R.id.PrecipitationData);
        TextView PrecipitationChanceInfo = findViewById(R.id.PrecipitationChanceData);
        TextView UVInfo = findViewById(R.id.UVData);
        TextView AirInfo = findViewById(R.id.AirData);

        TextView date = findViewById(R.id.date_and_time);
        date.setText(date_and_time);

        // [MUST HAPPEN FIRST TO MAINTAIN APP RESPONSIVENESS]
        // Set weather data using pre-fetched metrics in database
        String selection = DataStore.WeatherEntry.COL_CITY+" = '"+cityName+"'";
        Cursor cursor = getContentResolver().query(DataStore.WeatherEntry.CONTENT_URI, null,
                selection, null, null);

        if(cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String cityStateName = cursor.getString(cursor.getColumnIndexOrThrow(DataStore.WeatherEntry.COL_CITY));
                String city = cityStateName.split(",", 2)[0];
                String temperature = cursor.getString(cursor.getColumnIndexOrThrow(DataStore.WeatherEntry.COL_TEMPERATURE));
                String temperatureMax = cursor.getString(cursor.getColumnIndexOrThrow(DataStore.WeatherEntry.COL_TEMPERATUREMAX));
                String temperatureMin = cursor.getString(cursor.getColumnIndexOrThrow(DataStore.WeatherEntry.COL_TEMPERATUREMIN));
                String weatherDescription = cursor.getString(cursor.getColumnIndexOrThrow(DataStore.WeatherEntry.COL_DESCRIPTION));
                String wind = cursor.getString(cursor.getColumnIndexOrThrow(DataStore.WeatherEntry.COL_WINDSPEED));
                String humidity = cursor.getString(cursor.getColumnIndexOrThrow(DataStore.WeatherEntry.COL_HUMIDITY));
                String dewPoint = cursor.getString(cursor.getColumnIndexOrThrow(DataStore.WeatherEntry.COL_DEWPOINT));
                String UV = cursor.getString(cursor.getColumnIndexOrThrow(DataStore.WeatherEntry.COL_UV));
                String Air = cursor.getString(cursor.getColumnIndexOrThrow(DataStore.WeatherEntry.COL_AIRINDEX));
                String precipitation = cursor.getString(cursor.getColumnIndexOrThrow(DataStore.WeatherEntry.COL_PRECIPITATION));
                String precipitationChance = cursor.getString(cursor.getColumnIndexOrThrow(DataStore.WeatherEntry.COL_PRECIPITATIONCHANCE));

                cityNameView.setText(city);
                weatherTemp.setText(temperature + "°C");
                weatherTempRange.setText("H:" + temperatureMax + "°C" + " L:" + temperatureMin + "°C");
                weatherType.setText(weatherDescription);
                windInfo.setText(wind + " mph");
                humidInfo.setText(humidity  + "%");
                dewInfo.setText("dew point is " + dewPoint + "°C now");
                UVInfo.setText(UV);
                AirInfo.setText(Air);
                PrecipitationInfo.setText(precipitation + " inch");
                PrecipitationChanceInfo.setText(precipitationChance + "%");
                Log.v("Render Finish", "Weather metrics updated.");
                cursor.moveToNext();
            }
        }
        cursor.close();

        // Update weather data if database metrics are outdated and set respective fields
        // TODO (post Milestone 4) add timestamp to database for weather metric entries and use the
        //  predetermined cutoff (say ~5 mins?) to determine if updating values is to take place
        if (true) {
            Log.v("Render Start", "Updating database with weather metrics from server.");
            Weather.fetch(getApplicationContext(), cityName, latitude, longitude);

            // Set weather data using pre-fetched metrics in database
            selection = DataStore.WeatherEntry.COL_CITY+" = '"+cityName+"'";
            cursor = getContentResolver().query(DataStore.WeatherEntry.CONTENT_URI, null,
                    selection, null, null);

            if(cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    String cityStateName = cursor.getString(cursor.getColumnIndexOrThrow(DataStore.WeatherEntry.COL_CITY));
                    String city = cityStateName.split(",", 2)[0];
                    String temperature = cursor.getString(cursor.getColumnIndexOrThrow(DataStore.WeatherEntry.COL_TEMPERATURE));
                    String temperatureMax = cursor.getString(cursor.getColumnIndexOrThrow(DataStore.WeatherEntry.COL_TEMPERATUREMAX));
                    String temperatureMin = cursor.getString(cursor.getColumnIndexOrThrow(DataStore.WeatherEntry.COL_TEMPERATUREMIN));
                    String weatherDescription = cursor.getString(cursor.getColumnIndexOrThrow(DataStore.WeatherEntry.COL_DESCRIPTION));
                    String wind = cursor.getString(cursor.getColumnIndexOrThrow(DataStore.WeatherEntry.COL_WINDSPEED));
                    String humidity = cursor.getString(cursor.getColumnIndexOrThrow(DataStore.WeatherEntry.COL_HUMIDITY));
                    String dewPoint = cursor.getString(cursor.getColumnIndexOrThrow(DataStore.WeatherEntry.COL_DEWPOINT));
                    String UV = cursor.getString(cursor.getColumnIndexOrThrow(DataStore.WeatherEntry.COL_UV));
                    String Air = cursor.getString(cursor.getColumnIndexOrThrow(DataStore.WeatherEntry.COL_AIRINDEX));
                    String precipitation = cursor.getString(cursor.getColumnIndexOrThrow(DataStore.WeatherEntry.COL_PRECIPITATION));
                    String precipitationChance = cursor.getString(cursor.getColumnIndexOrThrow(DataStore.WeatherEntry.COL_PRECIPITATIONCHANCE));

                    cityNameView.setText(city);
                    weatherTemp.setText(temperature + "°C");
                    weatherTempRange.setText("H:" + temperatureMax + "°C" + " L:" + temperatureMin + "°C");
                    weatherType.setText(weatherDescription);
                    windInfo.setText(wind + " mph");
                    humidInfo.setText(humidity  + "%");
                    dewInfo.setText("dew point is " + dewPoint + "°C now");
                    UVInfo.setText(UV);
                    AirInfo.setText(Air);
                    PrecipitationInfo.setText(precipitation + " inch");
                    PrecipitationChanceInfo.setText(precipitationChance + "%");
                    Log.v("Render Finish", "Weather metrics updated (post server calls & database updates).");
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }

    }

}
