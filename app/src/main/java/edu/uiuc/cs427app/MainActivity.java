package edu.uiuc.cs427app;

import android.accounts.Account;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.navigation.ui.AppBarConfiguration;

import edu.uiuc.cs427app.databinding.ActivityMainBinding;

import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private Account account;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Process the Intent payload that has opened this Activity and show the information accordingly
        account = getIntent().getParcelableExtra("account");
        username = account.name;

        // Initializing the UI components
        // The list of locations should be customized per user (change the implementation so that
        // buttons are added to layout programmatically
        Button buttonChampaign = findViewById(R.id.buttonChampaign);
        Button buttonChicago = findViewById(R.id.buttonChicago);
        Button buttonLA = findViewById(R.id.buttonLA);
        Button buttonNew = findViewById(R.id.buttonSaveCity);

        buttonChampaign.setOnClickListener(this);
        buttonChicago.setOnClickListener(this);
        buttonLA.setOnClickListener(this);
        buttonNew.setOnClickListener(this);

        //this code implements the dynamic list of cities and buttons
        String selection = DataStore.CityEntry.COL_USERNAME + " = '" + username+"'";
        Cursor cursor = getContentResolver().query(DataStore.CityEntry.CONTENT_URI, null, selection, null, null);

        LinearLayout linlay = findViewById(R.id.cityListLayout);
        linlay.setOrientation(LinearLayout.VERTICAL);

        if(cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                @SuppressLint("Range") String cityname = cursor.getString(cursor.getColumnIndex(DataStore.CityEntry.COL_CITY));
                LinearLayout row = new LinearLayout(this);
                TextView city = new TextView(this);
                Button button = new Button(this);

                //this creates the onClick listener tied to the button so that when clicks it creates the
                //proper intent with cityname for the CityDetailsActivity
                button.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v){
                        Intent intent = new Intent(MainActivity.this , DetailsActivity.class);
                        intent.putExtra("city", cityname);
                        startActivity(intent);
                    }
                });
                row.setOrientation(LinearLayout.HORIZONTAL);
                city.setText(cityname);
                city.setWidth(400);
                city.setPadding(20,0,0,0);
                button.setText("Show Details");
                row.addView(city);
                row.addView(button);
                linlay.addView(row);
                cursor.moveToNext();
            }
        }

    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.buttonChampaign:
                intent = new Intent(this, DetailsActivity.class);
                intent.putExtra("city", "Champaign");
                startActivity(intent);
                break;
            case R.id.buttonChicago:
                intent = new Intent(this, DetailsActivity.class);
                intent.putExtra("city", "Chicago");
                startActivity(intent);
                break;
            case R.id.buttonLA:
                intent = new Intent(this, DetailsActivity.class);
                intent.putExtra("city", "Los Angeles");
                startActivity(intent);
                break;
            case R.id.buttonSaveCity:
                // TODO Implement this action to add a new location to the list of locations
                intent = new Intent(this, AddCityActivity.class);
                startActivity(intent);
                break;
        }
    }
}

