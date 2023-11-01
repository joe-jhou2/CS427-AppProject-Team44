package edu.uiuc.cs427app;

import android.accounts.Account;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
//import java.net.http.HttpResponse;

import androidx.appcompat.app.AppCompatActivity;


public class WeatherActivity extends AppCompatActivity {

    private static final String BASE_URL = "http://api.weatherapi.com/v1/current.json";
    private static final String API_KEY = "1c9ca48edcd6459aaa514033233110";
    private static String cityname;

    private Account account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        account = getIntent().getParcelableExtra("account");

        // Apply the theme based on the user's preference
        ThemeUtils.applyTheme(account, this);
        setContentView(R.layout.activity_weather);

        // Process the Intent payload that has opened this Activity and show the information accordingly
        String cityName = getIntent().getStringExtra("city");
        String welcome = "Weather in " + cityName;

        // TODO: Get the actual date and time
        String date_and_time = "9:00 AM, October 31st, 2023";

        // Initializing the GUI elements
        TextView welcomeMessage = findViewById(R.id.welcomeText);
        TextView date = findViewById(R.id.date_and_time);
        welcomeMessage.setText(welcome);
        date.setText(date_and_time);

        // Get the weather information from a Service that connects to a weather server and show the results
        String solo_name = cityName.split(",", 2)[0];
        System.out.println(solo_name);
        String full_url = BASE_URL + "?key=" + API_KEY + "?q=" + solo_name;
        System.out.println(full_url);
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create(BASE_URL + "?key=" + API_KEY + "?q=" + SOLO_NAME))
//                .method("GET", HttpRequest.BodyPublishers.noBody())
//                .build();
//
//        HttpResponse<String> response = null;
//        response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    }
}
