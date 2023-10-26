package edu.uiuc.cs427app;

import static androidx.core.graphics.drawable.DrawableCompat.applyTheme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.accounts.Account;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;

public class AddCityActivity extends AppCompatActivity implements View.OnClickListener {

    private String username;
    private Account account;
    protected SharedPreferences sharedPreferences;
    private static final String TAG = "AddCityActivity";
    private String cityname;
    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        account = getIntent().getParcelableExtra("account");
        username = account.name;

        // Apply the theme based on the user's preference
        ThemeUtils.applyTheme(account, this);

        setContentView(R.layout.activity_add_city);

        Button buttonSaveCity = findViewById(R.id.buttonSaveCity);
        buttonSaveCity.setOnClickListener(this);

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS));

        // Restrict the Countries
        //autocompleteFragment.setCountries(Arrays.asList("US"));

        // Restrict to Cities
        autocompleteFragment.setTypesFilter(Arrays.asList("(cities)"));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {

            @Override
            public void onPlaceSelected(@NonNull Place place) {
                cityname = place.getAddress();
                latitude = place.getLatLng().latitude;
                longitude = place.getLatLng().longitude;

                String msg = "Place: "
                        + place.getAddress() + "\n"
                        + "Latitude: "
                        + place.getLatLng().latitude + "\n"
                        + "Longitude: "
                        + place.getLatLng().longitude;
                Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();

                Log.d(TAG, msg);

            }

            @Override
            public void onError(@NonNull Status status) {
                Log.d(TAG, "An error occurred: " + status);
            }
        });
    }

    @Override
    public void onClick(View view) {

        if (view.getId()==R.id.buttonSaveCity) {
            //save the city to the db
            ContentValues values = new ContentValues();

            // fetching text from user
            values.put(DataStore.CityEntry.COL_USERNAME, username);
            values.put(DataStore.CityEntry.COL_CITY, cityname);
            values.put(DataStore.CityEntry.COL_LATITUDE, latitude);
            values.put(DataStore.CityEntry.COL_LONGITUDE, longitude);

            // inserting into database through content URI
            getContentResolver().insert(DataStore.CityEntry.CONTENT_URI, values);

            // displaying a toast message
            Toast.makeText(getBaseContext(), cityname+" Saved", Toast.LENGTH_LONG).show();

            //jump back to the Main Activity
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("account", account);
            finish();
            startActivity(intent);
        }
    }

}