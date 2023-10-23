package edu.uiuc.cs427app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddCityActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_city);

        Button buttonSaveCity = findViewById(R.id.buttonSaveCity);
        buttonSaveCity.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        //TODO need to add the real username not the placeholder string below
        // also need to implement any city verification in this OR have an autocomplete
        // in the text box

        if (view.getId()==R.id.buttonSaveCity) {
            //save the city to the db
            ContentValues values = new ContentValues();

            // fetching text from user
            values.put(DataStore.CityEntry.COL_USERNAME, "username");
            values.put(DataStore.CityEntry.COL_CITY, ((EditText) findViewById(R.id.CityName)).getText().toString());

            // inserting into database through content URI
            getContentResolver().insert(DataStore.CityEntry.CONTENT_URI, values);

            // displaying a toast message
            Toast.makeText(getBaseContext(), "New City Saved", Toast.LENGTH_LONG).show();

            //jump back to the Main Activity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }










}