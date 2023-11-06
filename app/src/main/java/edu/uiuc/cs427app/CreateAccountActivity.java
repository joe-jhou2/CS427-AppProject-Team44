package edu.uiuc.cs427app;

// TODO (post Milestone 3) privacy concerns -- disclose to users we are storing their data
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

/**
 * Activity that serves as an Authentication Page, first activity presented to user upon using
 * application. Options to let new users sign up, returning users sign in, and select a theme upon
 * entering the application are used.
 *
 * For users to preview themes, a preview account is used on the backend to allow for previews. If a
 * theme preview is used the settings are passed onto any NEWLY created accounts or anyone SIGNING IN.
 */
public class CreateAccountActivity extends AppCompatActivity implements View.OnClickListener {
    /**
     * User's username input
     */
    private String mUsername;
    /**
     * User's password input
     */
    private String mPassword;

    /**
     * Preview account username
     */
    private static final String PREVIEW_USERNAME = "PREVIEW";
    /**
     * Preview account password
     */
    private static final String PREVIEW_PASSWORD = "PREVIEW";

    /**
     * A key for the theme, this is set by either a returning user's account
     * settings OR upon using the layout selection.
     */
    private String themeKey;

    /**
     * The previewAccount is a "constant" account that will have some settings
     * changed to display a theme preview.
     */
    private Account previewAccount;

    /**
     * Instance of the account manager for this activity.
     */
    private AccountManager mAccountManager;

    /**
     * Instance of the current account
     */
    private Account mCurrentAccount;

    /**
     * Instance of the account's username input view
     */
    private EditText mAccountNameView;

    /**
     * Instance of the account's password input view
     */
    private EditText mAccountPassView;

    /**
     * Initializes the activity for the authentication page.
     *
     * @param savedInstanceState saved instance states passed upon a recreate() request
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the app's account manager.
        mAccountManager = AccountManager.get(this);

        // TODO (post Milestone 3) helper method THEME REFRESH - create helper to handle this initialization
        // Checks if the login page was recreated -- if so, dynamically adjust the PREVIEW theme
        if (savedInstanceState != null) {
            // retrieve account object
            Account currentAccount = getAccountFromPreferences();

            // Apply the theme based on the user's preference
            ThemeUtils.applyTheme(currentAccount, this);

            // save the theme to for the next user
            themeKey = getThemePreferenceForAccount(this, currentAccount.name);
            Log.v("THEME", "theme key is = " + themeKey);
        } else {
            // Set default theme for the login activity
            setTheme(R.style.Theme_Day);
        }

        // TODO (post Milestone 3) helper method DUMMY ACCOUNT - create helper to handle this initialization
        // Checks if the dummy account is instanced - if so pull it from the account manager OR create it
        if (previewAccount != null) {
            // nothing to initialize
        } else {
            // Set default theme for the login activity
            previewAccount = new Account(PREVIEW_USERNAME, this.getString(R.string.account_type));

            if (mAccountManager.addAccountExplicitly(previewAccount, PREVIEW_PASSWORD, null)) {
                Log.v("DummyAccount", "Dummy account created.");//
            } else {
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


        // TODO (post Milestone 3) helper method USER INPUT - create helper to handle this initialization
        // Passed saved fields from savedInstanced to new Instance of this activity
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            mUsername = savedInstanceState.getString("saved_username");
            mPassword = savedInstanceState.getString("saved_password");
        } else {
            // consider moving mUsername and mPassword initializing here
        }
        if (mUsername != null && mPassword != null) {
            mAccountNameView.setText(mUsername);
            mAccountPassView.setText(mPassword);
        }
    }

    /**
     * Upon clicking the specified buttons, the corresponding actions are carried out.
     *
     * @param view current view in activity
     */
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
                break;
        }
    }

    /**
     * Upon closing this activity, specific instance states are passed across
     * instances of this activity.
     *
     * @param outState bundle containing instances to be saved, i.e. passed on to next instance
     *                 of this activity
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("saved_username", mUsername);
        outState.putString("saved_password", mPassword);
    }

    /**
     * Creates an account by reading the username and password input into the authentication page.
     */
    private void createAccount() {
        // Instance a new account to be added to the account manager
        mUsername = mAccountNameView.getText().toString();
        mPassword = mAccountPassView.getText().toString();
        mCurrentAccount = new Account(mUsername, this.getString(R.string.account_type));

        // Checks if the account was successfully added to the account manager, if so,
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
    }

    /**
     * Sign in to app using the input for username and password.
     */
    private void signIn() {
        mUsername = mAccountNameView.getText().toString();
        mPassword = mAccountPassView.getText().toString();

        // TODO (post Milestone 3) validation of accounts - use account validateLocally()
        Account[] accounts = mAccountManager.getAccountsByType(this.getString(R.string.account_type));
        boolean isSuccessful = false;


        /**
         * Pull a list of accounts and search for the matching username. Once the username is
         * matched, compare the password provided to the actual password.
         */
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

        /**
         * If the account credentials were verified the current theme is applied to the account and
         * the user is signed into the app.
         */
        // TODO (post Milestone 3) helper method SIGN IN W/THEME APPLICATION - apply theme and sign into account
        if (isSuccessful) {
            if (themeKey != null)
                ThemeUtils.saveThemePreferenceForAccount(CreateAccountActivity.this, mCurrentAccount.name, themeKey);
            Log.d("SignInInfo", "Saving themeKey: " + themeKey + " for account: "+mCurrentAccount.name);
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

    /**
     * Used to save account preferences for themes across difference users.
     *
     * @param account account to be saved and logged into preferences
     */
    private void saveAccountInfoToPreferences(Account account) {
        SharedPreferences sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("account_name", account.name);
        // Add more info if necessary
        editor.apply();
    }

    /**
     * Pulls the theme preference from the shared preferences in the app.
     *
     * @param context this current context
     * @param username account username
     * @return key for the account's theme
     */
    private static String getThemePreferenceForAccount(Context context, String username) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("theme_preferences", Context.MODE_PRIVATE);
        return sharedPreferences.getString(username, "Theme.Day");
    }

    /**
     * Retrieves the account from the shared preferences.
     *
     * @return account associated with the account name for the current user
     */
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

