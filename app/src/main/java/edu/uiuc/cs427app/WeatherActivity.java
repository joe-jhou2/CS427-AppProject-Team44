package edu.uiuc.cs427app;

import android.accounts.Account;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
//import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import androidx.appcompat.app.AppCompatActivity;

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

    private static final String BASE_URL = "https://api.weatherapi.com/v1/current.json";
    private static final String FORECAST_URL = "https://api.weatherapi.com/v1/forecast.json";

    private static final String API_KEY = "1c9ca48edcd6459aaa514033233110";
    private static String cityname;

    private Account account;
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        account = getIntent().getParcelableExtra("account");
        ThemeUtils.applyTheme(account, this);
        setContentView(R.layout.activity_weather);

        String cityName = getIntent().getStringExtra("city");
//        String welcome = "Weather in " + cityName;
        String date_and_time = new java.util.Date().toString();

//        TextView welcomeMessage = findViewById(R.id.welcomeText);
        TextView date = findViewById(R.id.date_and_time);
//        welcomeMessage.setText(welcome);
        date.setText(date_and_time);

        String solo_name = cityName.split(",", 2)[0];

        // Views to set upon initiation of this activity
        TextView cityNameView = findViewById(R.id.CityName);
        TextView weatherTemp = findViewById(R.id.CityTemperature);
        TextView weatherTempRange = findViewById(R.id.CityTemperatureRange);
        TextView weatherType = findViewById(R.id.CityWeather);
        TextView windInfo = findViewById(R.id.windData);
        TextView humidInfo = findViewById(R.id.humidityData);

        new Thread(new Runnable() {
            @Override
            public void run() {
                String apiUrl = FORECAST_URL + "?key=" + API_KEY + "&q=" + solo_name + "&days=1";

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(apiUrl)
                        .build();

                try {
                    Response response = client.newCall(request).execute();

                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();

                        JSONObject json = new JSONObject(responseBody);

                        // Extract weather information from the JSON response
                        String city = json.getJSONObject("location").getString("name");
                        String temperature = json.getJSONObject("current").getString("temp_c");

                        // This temperature range can exclude the current 'temperature' due to polling/predictions. Consider manually setting a min/max using the range and current temps
                        String temperatureMax = json.getJSONObject("forecast").getJSONArray("forecastday").getJSONObject(0).getJSONObject("day").getString("maxtemp_c");
                        String temperatureMin = json.getJSONObject("forecast").getJSONArray("forecastday").getJSONObject(0).getJSONObject("day").getString("mintemp_c");

                        String weatherDescription = json.getJSONObject("current").getJSONObject("condition").getString("text");
                        System.out.println(weatherDescription);
                        String wind = json.getJSONObject("current").getString("wind_mph").toString();
                        System.out.println(wind);
                        String humidity = json.getJSONObject("current").getString("humidity").toString();
                        System.out.println(humidity);

                        // Now, use Handler to post the UI update back on the main thread
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                cityNameView.setText(city);
                                weatherTemp.setText(temperature + "°C");
                                weatherTempRange.setText("H:" + temperatureMax + "°C" + " L:" + temperatureMax + "°C");
                                weatherType.setText(weatherDescription);
                                windInfo.setText(wind + " mph");
                                humidInfo.setText(humidity  + "%");
                            }
                        });
                    } else {
                        // Handle API request error
                        // Optionally, use Handler to show an error message on the main thread
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("WeatherActivity", "Network error: " + e.getMessage());
                    // Handle network or other errors
                    // Optionally, use Handler to show an error message on the main thread
                }
            }
        }).start();
    }

}
