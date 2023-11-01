package edu.uiuc.cs427app;


import android.accounts.Account;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;

import com.google.android.libraries.places.api.Places;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

import edu.uiuc.cs427app.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private Account account;
    private String username;
    private ArrayList<String> city_list = new ArrayList<>();
    protected SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Shared Preferences and apply the saved theme
        sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE);

        // retrieve account object
        Account currentAccount = getAccountFromPreferences();

        // Apply the theme based on the user's preference
        ThemeUtils.applyTheme(currentAccount, this);

        // set the content view
        setContentView(R.layout.activity_main);

        // Process the Intent payload that has opened this Activity and show the information accordingly
        account = getIntent().getParcelableExtra("account");
        username = account.name;

        //changing the title at the top of the MainActivity
        this.setTitle(getString(R.string.app_name)+" - "+username);

        final String apiKey = "AIzaSyBkx7JHMWNPp838wtFANglZzGEr0tFmr9E";

        if (apiKey.equals("")) {
            Toast.makeText(this, getString(R.string.error_api_key), Toast.LENGTH_LONG).show();
            return;
        }

        // Setup Places Client
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }

        // Initializing the UI components
        // The list of locations should be customized per user (change the implementation so that
        // buttons are added to layout programmatically
        Button buttonNew = findViewById(R.id.buttonAddCity);
        buttonNew.setOnClickListener(this);

        Button settingsButton = findViewById(R.id.settingsPage);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // If you're using shared preferences or any other method for session management,
                // clear the session details here.

                // Redirect to Authentication Page(Create AccountActivity in our case)
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        /*
         TODO code to be implemented for delete city dialog
        removeCity.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v){

                        //launch a dialog to provide list of cities to delete and make it cancelable
                        CharSequence[] cs = city_list.toArray(new CharSequence[city_list.size()]);
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Choose a City to Delete:")
                                .setCancelable(true)
                                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener(){
                                            @Override
                                            public void onClick(DialogInterface dialog, int i) {
                                                dialog.dismiss();
                                            }
                                        }
                                )
                                .setSingleChoiceItems(cs, -1, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String selection = DataStore.CityEntry.COL_USERNAME + " = '" + username+"' AND "
                                                +DataStore.CityEntry.COL_CITY+" = '"+cs[which]+"'";
                                        //String query = "USERNAME = '" + username + "' AND CITY = '" + cityname + "'";
                                        getContentResolver().delete(DataStore.CityEntry.CONTENT_URI, selection, null);

                                        // Displaying a toast message
                                        Toast.makeText(getBaseContext(), cs[which] + " removed", Toast.LENGTH_LONG).show();

                                        dialog.dismiss();
                                        recreate();
                                        // Log.d("Theme Dialogz", "theme dialog dismissed.");
                                    }
                                });
                        builder.create().show();
                    }
                });
         */


        // This code implements the dynamic list of cities and buttons
        String selection = DataStore.CityEntry.COL_USERNAME + " = '" + username+"'";
        Cursor cursor = getContentResolver().query(DataStore.CityEntry.CONTENT_URI, null,
                                                        selection, null, null);

        LinearLayout linlay = findViewById(R.id.cityListLayout);
        linlay.setOrientation(LinearLayout.VERTICAL);

        if(cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String cityname = cursor.getString(cursor.getColumnIndexOrThrow(DataStore.CityEntry.COL_CITY));
                double latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(DataStore.CityEntry.COL_LATITUDE));
                double longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(DataStore.CityEntry.COL_LONGITUDE));
                city_list.add(cityname);
                LinearLayout row = new LinearLayout(this);
                TextView city = new TextView(this);
                TextView spacer = new TextView(this);

                // This creates the onClick listener tied to the button so that when clicks it creates the
                // proper intent with cityname for the CityDetailsActivity
                Button showDetails  = new MaterialButton(this);
                Button showMap      = new MaterialButton(this);
                //Button removeCity   = new MaterialButton(this);

                Button showWeather = new MaterialButton(this);

                // Handles the redirection to city weather detials
                showWeather.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View v){
                        Intent intent = new Intent(MainActivity.this, WeatherActivity.class);
                        intent.putExtra("city", cityname);
                        intent.putExtra("account", account);
                        startActivity(intent);
                    }
                });
                // Handles the redirection to city details
                showDetails.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v){
                        Intent intent = new Intent(MainActivity.this , MapsActivity.class);
                        intent.putExtra("city", cityname);
                        intent.putExtra("lat",latitude);
                        intent.putExtra("lon",longitude);
                        intent.putExtra("account", account);
                        startActivity(intent);
                    }
                });

                //redirects to MapActivity
                showMap.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v){
                        Intent intent = new Intent(MainActivity.this , MapsActivity.class);
                        intent.putExtra("city", cityname);
                        intent.putExtra("lat",latitude);
                        intent.putExtra("lon",longitude);
                        startActivity(intent);
                    }
                });



                // Configure buttons and text
                city.setText(cityname);
                city.setWidth(500);

                showDetails.setText("Map");
                showWeather.setText("Weather");
                //showMap.setText("Map");
                //removeCity.setText("Delete");
                spacer.setWidth(30);

                //showMap.setTooltipText("Show Map");
                //showWeather.setTooltipText("Show Weather");
                //removeCity.setTooltipText("Delete City");

                //experimenting with adding icons to buttons or icon-only buttons
//                showWeather.setIconResource(R.drawable.weather_cloudy);
//                showWeather.setWidth(50);
//                showWeather.setIconPadding(0);
//                showWeather.setPaddingRelative(0, 0, 0, 0);
//                showWeather.setIconGravity(MaterialButton.ICON_GRAVITY_TEXT_TOP);
//                showWeather.setMinWidth(0);

                // Set up layout
                row.setOrientation(LinearLayout.HORIZONTAL);
                row.setPadding(20,0,0,0);

                //add items to the horizontal layout
                row.addView(city);
                row.addView(showWeather);
                row.addView(spacer); //adding a blank textview to space out the buttons
                row.addView(showDetails);

                //row.addView(showMap); //TODO uncomment this to add a direct button to maps
                //row.addView(removeCity); //TODO replace delete city function with a dialog like for themes
                linlay.addView(row);
                cursor.moveToNext();
            }
        }
        cursor.close();

    }

    //Sets the activity's theme based on the user's saved preference.
    private Account getAccountFromPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE);
        String accountName = sharedPreferences.getString("account_name", null);
        // Retrieve more info if necessary
        if (accountName != null) {
            return new Account(accountName, getString(R.string.account_type));
        }
        return null;
    }

    // Function to handle adding cities to the user's list
    @Override
    public void onClick(View view) {
        Intent intent;
        if (view.getId() == R.id.buttonAddCity) {
            intent = new Intent(this, AddCityActivity.class);
//            intent.putExtra("username", username);
            intent.putExtra("account", account);
            startActivity(intent);
        }
    }

    //Function to refresh the main page when back button is pressed (refreshes city list)
    @Override
    public void onRestart()
    {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }
}

