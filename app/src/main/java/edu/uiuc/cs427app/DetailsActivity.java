package edu.uiuc.cs427app;

import android.accounts.Account;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;




public class DetailsActivity extends AppCompatActivity implements View.OnClickListener{
    private Account account;
    protected SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        account = getIntent().getParcelableExtra("account");

        // Apply the theme based on the user's preference
        ThemeUtils.applyTheme(account, this);
        setContentView(R.layout.activity_details);

        // Process the Intent payload that has opened this Activity and show the information accordingly
        String cityName = getIntent().getStringExtra("city");
        String welcome = "Welcome to "+cityName;
        String cityWeatherInfo = "Detailed information about the weather of "+cityName;

        // Initializing the GUI elements
        TextView welcomeMessage = findViewById(R.id.welcomeText);
        TextView cityInfoMessage = findViewById(R.id.cityInfo);

        welcomeMessage.setText(welcome);
        cityInfoMessage.setText(cityWeatherInfo);
        // Get the weather information from a Service that connects to a weather server and show the results

        Button buttonMap = findViewById(R.id.mapButton);
        buttonMap.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        //Implement this (create an Intent that goes to a new Activity, which shows the map)
        switch (view.getId()){
            case R.id.mapButton:
                Intent mapIntent = new Intent(DetailsActivity.this, MapsActivity.class);
                mapIntent.putExtra("city", getIntent().getStringExtra("city"));
                mapIntent.putExtra("lat", getIntent().getDoubleExtra("lat",0.0));
                mapIntent.putExtra("lon", getIntent().getDoubleExtra("lon",0.0));
                startActivity(mapIntent);
                break;
        }
    }
}

