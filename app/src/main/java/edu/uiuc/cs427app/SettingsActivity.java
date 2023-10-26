package edu.uiuc.cs427app;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SettingsActivity extends ThemeActivity {
    protected SharedPreferences sharedPreferences;
    Button signOutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //changeThemeButton = findViewById(R.id.changeThemeButton);
        signOutButton = findViewById(R.id.signOutButton);

        // Created a button for changing theme
        //changeThemeButton.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View v) {
                // Logic to change theme
                //showThemeDialog();
        //    }
        //});

        signOutButton = findViewById(R.id.signOutButton);

        // Jump to the Authentication Page(Create AccountActivity in our case)
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If you're using shared preferences or any other method for session management, clear the session details here.
                // Reset theme to default
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("theme", "Theme.Day");  // Assuming Theme.Day is the default
                editor.apply();

                // Redirect to Authentication Page(Create AccountActivity in our case)
                Intent intent = new Intent(SettingsActivity.this, CreateAccountActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // This makes sure the user can't navigate back to previous activities using the back button
                intent.putExtra("signedOut", true);
                startActivity(intent);
            }
        });
    }
}
