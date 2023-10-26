package edu.uiuc.cs427app;

import android.accounts.Account;
import android.accounts.AccountManager;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SettingsActivity extends AppCompatActivity {
    protected SharedPreferences sharedPreferences;
    Button signOutButton;
    Button changeThemeButton;
    private Account account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Theme logic
        sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE);
        // Process the Intent payload that has opened this Activity and show the information accordingly
        account = getIntent().getParcelableExtra("account");

        setContentView(R.layout.activity_settings);

        changeThemeButton = findViewById(R.id.themeButton);
        signOutButton = findViewById(R.id.signOutButton);

        // Apply the theme based on the user's preference
        ThemeUtils.applyTheme(account, sharedPreferences, this);

        // Created a button for changing theme
        changeThemeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show the theme selection dialog
                ThemeUtils.showThemeDialog(SettingsActivity.this);
            }
        });

        // Jump to the Authentication Page(Create AccountActivity in our case)
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to Authentication Page(Create AccountActivity in our case)
                Intent intent = new Intent(SettingsActivity.this, CreateAccountActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // This makes sure the user can't navigate back to previous activities using the back button
                intent.putExtra("signedOut", true);
                startActivity(intent);
            }
        });
    }
}
