package edu.uiuc.cs427app;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.navigation.ui.AppBarConfiguration;

import edu.uiuc.cs427app.databinding.ActivityMainBinding;

import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private Account account;
    private String username;
    protected SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Shared Preferences and apply the saved theme
        sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE);

        // Process the Intent payload that has opened this Activity and show the information accordingly
        account = getIntent().getParcelableExtra("account");
        username = account.name;

        // Apply the theme based on the user's preference
        ThemeUtils.applyTheme(account, sharedPreferences, this);

        // set the content view
        setContentView(R.layout.activity_main);

        //changing the title at the top of the MainActivity
        this.setTitle(getString(R.string.app_name)+" - "+username);

        // Initializing the UI components
        // The list of locations should be customized per user (change the implementation so that
        // buttons are added to layout programmatically
        Button buttonNew = findViewById(R.id.buttonAddCity);
        buttonNew.setOnClickListener(this);

        Button settingsButton = findViewById(R.id.settingsPage);

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // If you're using shared preferences or any other method for session management, clear the session details here.

                // Redirect to Authentication Page(Create AccountActivity in our case)
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // This makes sure the user can't navigate back to previous activities using the back button
                startActivity(intent);
            }
        });

        // This code implements the dynamic list of cities and buttons
        String selection = DataStore.CityEntry.COL_USERNAME + " = '" + username+"'";
        Cursor cursor = getContentResolver().query(DataStore.CityEntry.CONTENT_URI, null, selection, null, null);

        LinearLayout linlay = findViewById(R.id.cityListLayout);
        linlay.setOrientation(LinearLayout.VERTICAL);

        if(cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                @SuppressLint("Range") String cityname = cursor.getString(cursor.getColumnIndex(DataStore.CityEntry.COL_CITY));
                LinearLayout row = new LinearLayout(this);
                TextView city = new TextView(this);

                // This creates the onClick listener tied to the button so that when clicks it creates the
                // proper intent with cityname for the CityDetailsActivity
                Button showDetails = new Button(this);

                // Add a button to remove a given city
                Button removeCity = new Button(this);

                // Handles the redirection to city details
                showDetails.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v){
                        Intent intent = new Intent(MainActivity.this , DetailsActivity.class);
                        intent.putExtra("city", cityname);
                        startActivity(intent);
                    }
                });

                // Remove the city from the DB under current user
                removeCity.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v){
                        // Create SQL query to do the job and perform delete
                        String query = "USERNAME = '" + username + "' AND CITY = '" + cityname + "'";
                        getContentResolver().delete(DataStore.CityEntry.CONTENT_URI, query, null);

                        // Displaying a toast message
                        Toast.makeText(getBaseContext(), cityname + " removed", Toast.LENGTH_LONG).show();

                        // Refresh MainActivity
                        finish();
                        startActivity(getIntent());
                    }
                });

                // Configure buttons and text
                city.setText(cityname);
                city.setWidth(400);
                city.setPadding(20,0,0,0);
                showDetails.setText("Show Details");
                removeCity.setText("Remove City");

                // Set up layout
                row.setOrientation(LinearLayout.HORIZONTAL);
                row.addView(city);
                row.addView(showDetails);
                row.addView(removeCity);
                linlay.addView(row);
                cursor.moveToNext();
            }
        }
        cursor.close();
    }

    //Sets the activity's theme based on the user's saved preference.

    // Function to handle adding cities to the user's list
    @Override
    public void onClick(View view) {
        Intent intent;
        if (view.getId() == R.id.buttonAddCity) {
            intent = new Intent(this, AddCityActivity.class);
//            intent.putExtra("username", username);
            intent.putExtra("account", account);
            finish();
            startActivity(intent);
        }
    }
}

