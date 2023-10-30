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

        // retrieve account object
        Account currentAccount = getAccountFromPreferences();
        // Apply the theme based on the user's preference
        ThemeUtils.applyTheme(currentAccount, this);

        setContentView(R.layout.activity_settings);

        changeThemeButton = findViewById(R.id.themeButton);
        signOutButton = findViewById(R.id.signOutButton);

        // Created a button for changing theme
        changeThemeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show the theme selection dialog
                ThemeUtils.showThemeDialog(SettingsActivity.this, currentAccount);
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

    private Account getAccountFromPreferences() {
        // Get user preferences from user profile
        SharedPreferences sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE);

        // get user name
        String accountName = sharedPreferences.getString("account_name", null);

        // Retrieve more info if necessary
        if (accountName != null) {
            return new Account(accountName, getString(R.string.account_type));
        }
        return null;
    }

    @Override
    public void onBackPressed() {
        // Clear the previous activities upon 'back button'
        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);

        // Clear previous activities with preset settings that may have been superseded
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Pass the current account to a clean instance of the MainActivity
        Account currentAccount = getAccountFromPreferences();
        intent.putExtra("account", currentAccount);

        startActivity(intent);

        finish();
    }

}
