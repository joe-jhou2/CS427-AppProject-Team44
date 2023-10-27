package edu.uiuc.cs427app;

// The manifest file uses this to get permission from app.
// TODO: disclose privacy concerns to user given we are storing their data
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

// TODO - add comments describing callbacks (typical)
// - incorporate more robust authenticator using Cipher
// This activity should do the following:
//        1. Collects credentials from the user                 createAccount()
//        2. Authenticates the credentials with the server      signIn()*
//        3. Stores the credentials on the device               createAccount()

// The authentication page or login page dynamical displays themes by using a dummy account "PREVIEW"
// All settings (atm just a theme) taken by the dummy account are passed on to a newly created account ONLY.

public class CreateAccountActivity extends AppCompatActivity implements View.OnClickListener {
    private String mUsername;
    private String mPassword;
    private String previewUsername = "PREVIEW";
    private String previewPassword = "PREVIEW";
    private String themeKey;
    private Account previewAccount;

    private AccountManager mAccountManager;
    private Account mCurrentAccount;
    private EditText mAccountNameView;
    private EditText mAccountPassView;
//    protected SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the app's account manager.
        mAccountManager = AccountManager.get(this);

        // DELETE THIS -- doesn't do anything
        mCurrentAccount = getIntent().getParcelableExtra("currentAccount");

        // TODO consider making a helper method to help with this "initialization"
        // For theme refresh -- consider making a helper method to help with this "initialization"
        // Checks if the login page was recreated -- if so, dynamically adjust the PREVIEW theme
        if (savedInstanceState != null) {
            // retrieve account object
            Account currentAccount = getAccountFromPreferences();
            // retrieve account object
//            Account currentAccount = previewAccount;

            // Apply the theme based on the user's preference
            ThemeUtils.applyTheme(currentAccount, this);

            // save the theme to for the next user
            themeKey = getThemePreferenceForAccount(this, currentAccount.name);
            Log.v("THEME", "theme key is = " + themeKey);
        } else {
            // Set default theme for the login activity
            setTheme(R.style.Theme_Day);
        }

        // TODO consider making a helper method to help with this "initialization"
        // For dummy account creation -- consider making a helper method to help with this "initialization"
        // Checks if the login page was recreated -- if so, dynamically adjust the PREVIEW theme
        if (previewAccount != null) {
            // nothing to initialize
        } else {
            // Set default theme for the login activity
            previewAccount = new Account(previewUsername, this.getString(R.string.account_type));

            if (mAccountManager.addAccountExplicitly(previewAccount, previewPassword, null)) {
//                Toast.makeText(this, "DUMMY account created!", Toast.LENGTH_LONG).show();
                Log.v("DummyAccount", "Dummy account created.");//
            } else {
//                Toast.makeText(this, "Dummy account already exists!", Toast.LENGTH_LONG).show();
                Log.v("DummyAccount", "Dummy account exists.");//
            }
        }

        // Initialize the ACTIVITY PAGE
        setContentView(R.layout.activity_login);

        // Identifying UI components to be used as input for AccountManager
        // and buttons to initiate account creation / login
        mAccountNameView = findViewById(R.id.inputUsername);
        mAccountPassView = findViewById(R.id.inputPassword);
        Button buttonSignUp = findViewById(R.id.buttonSignUp);
        Button buttonSignIn = findViewById(R.id.buttonSignIn);
        Button themeButton = findViewById(R.id.themeButton);

        if (buttonSignUp != null) {
            buttonSignUp.setOnClickListener(this);
        }
        if (buttonSignIn != null) {
            buttonSignIn.setOnClickListener(this);
        }
        if (themeButton != null) {
            themeButton.setOnClickListener(this);
        }


        // TODO consider making a helper method to help with this "initialization" of user/pass fields
        // pass saved fields from savedInstanced to new Instaced of this activity
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            mUsername = savedInstanceState.getString("saved_username");
            mPassword = savedInstanceState.getString("saved_password");
        } else {
            // consider moving mUsername and mPassowrd initializing here
        }
        if (mUsername != null && mPassword != null) {
            mAccountNameView.setText(mUsername);
            mAccountPassView.setText(mPassword);
        }
    }

//    TODO - add descriptions (typical)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonSignUp:
                createAccount();
                break;
            case R.id.buttonSignIn:
                signIn();
                break;
            case R.id.themeButton:
                Log.d("Theme Dialog", "processed layout selection");
                ThemeUtils.showThemeDialog(CreateAccountActivity.this, previewAccount);
                saveAccountInfoToPreferences(previewAccount);
//                saveAccountInfoToPreferences(mCurrentAccount);//  move to signUP
                break;
        }
    }

    private void createAccount() {
        mUsername = mAccountNameView.getText().toString();
        mPassword = mAccountPassView.getText().toString();
        mCurrentAccount = new Account(mUsername, this.getString(R.string.account_type));

        if (mAccountManager.addAccountExplicitly(mCurrentAccount, mPassword, null)) {
            Toast.makeText(this, "Account created! Please sign in with your username and password.", Toast.LENGTH_LONG).show();
            Log.v("AccountCreate", "account created, username="+ mUsername); // Account creation succeeded
            ThemeUtils.saveThemePreferenceForAccount(CreateAccountActivity.this, mCurrentAccount.name, themeKey);
            saveAccountInfoToPreferences(mCurrentAccount);
            ThemeUtils.applyTheme(mCurrentAccount, this);


            Toast.makeText(this, "Account created! Choose your theme and then sign in with your username and password.", Toast.LENGTH_LONG).show();
            return;

        } else {
            Toast.makeText(this, "Account already exists! Please sign in with your username and password.", Toast.LENGTH_LONG).show();
            Log.v("AccountCreate", "account NOT created, username="+ mUsername);// Account creation failed
        }

        // Log all accounts/passwords for testing purposes
        Account[] accounts = mAccountManager.getAccounts();
        for (Account account : accounts) {
            Log.d("AccountInfo", "Account name: " + account.name + " - Account password: " + mAccountManager.getPassword(account) + " - Account type: " + account.type);
        }
    }

    private void signIn() {
        mUsername = mAccountNameView.getText().toString();
        mPassword = mAccountPassView.getText().toString();

        //TODO: unify this account validation process with AppAccountAuthenticator.validateLocally()
        Account[] accounts = mAccountManager.getAccountsByType(this.getString(R.string.account_type));
        boolean isSuccessful = false;

        for (Account account : accounts) {
            if (account.name.equals(mUsername)) {
                // Local authentication -- not real authentication or OAuth
                String storedPassword = mAccountManager.getPassword(account);
                Log.d("SignInInfo", "Account found: " + account.name);

                if (storedPassword != null && storedPassword.equals(mPassword)) {
                    Log.d("SignInInfo", "Passed password == stored password: " + mPassword);
                    isSuccessful = true;
                    mCurrentAccount = account;
                    break;
                }
            }
        }
        if (isSuccessful) {
            ThemeUtils.saveThemePreferenceForAccount(CreateAccountActivity.this, mCurrentAccount.name, themeKey);
            saveAccountInfoToPreferences(mCurrentAccount); // Save account to SharedPreferences
            ThemeUtils.applyTheme(mCurrentAccount, this);


            // Login worked -- go to MainActivity
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("account", mCurrentAccount);
            startActivity(intent);
            Log.d("SignInInfo", "Successfully logged into Account: " + mCurrentAccount.name);
        } else {
            Toast.makeText(this, "Sign in failed. Check username and password.", Toast.LENGTH_LONG).show();
        }
    }

    private void saveAccountInfoToPreferences(Account account) {
        SharedPreferences sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("account_name", account.name);
        // Add more info if necessary
        editor.apply();
    }
    private static String getThemePreferenceForAccount(Context context, String username) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("theme_preferences", Context.MODE_PRIVATE);
        return sharedPreferences.getString(username, "Theme.Day");
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("saved_username", mUsername);
        outState.putString("saved_password", mPassword);
//        outState.putParcelable("preview_account", previewAccount);
    }

    private Account getAccountFromPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE);
        String accountName = sharedPreferences.getString("account_name", null);
        // Retrieve more info if necessary
        if (accountName != null) {
            return new Account(accountName, getString(R.string.account_type));
        }
        return null;
    }
}

