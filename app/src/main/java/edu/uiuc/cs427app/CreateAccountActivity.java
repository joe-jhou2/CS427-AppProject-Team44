package edu.uiuc.cs427app;

// The manifest file uses this to get permission from app.
// TODO: disclose privacy concerns to user given we are storing their data
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
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
public class CreateAccountActivity extends AppCompatActivity implements View.OnClickListener {
    private String mUsername;
    private String mPassword;
    private AccountManager mAccountManager;
    private Account mCurrentAccount;
    private EditText mNewAccountNameView;
    private EditText mNewAccountPassView;
    private EditText mExistAccountNameView;
    private EditText mExistAccountPassView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Identifying UI components to be used as input for AccountManager
        // and buttons to initiate account creation / login
        mNewAccountNameView = findViewById(R.id.inputSignUpUsername);
        mNewAccountPassView = findViewById(R.id.inputSignUpPassword);
        mExistAccountNameView = findViewById(R.id.inputSignInUsername);
        mExistAccountPassView = findViewById(R.id.inputSignInPassword);
        Button buttonSignUp = findViewById(R.id.buttonSignUp);
        Button buttonSignIn = findViewById(R.id.buttonSignIn);

        if (buttonSignUp != null) {
            buttonSignUp.setOnClickListener(this);
        }
        if (buttonSignIn != null) {
            buttonSignIn.setOnClickListener(this);
        }

        mAccountManager = AccountManager.get(this);
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
        }
    }

    private void createAccount() {
        mUsername = mNewAccountNameView.getText().toString();
        mPassword = mNewAccountPassView.getText().toString();
        mCurrentAccount = new Account(mUsername, this.getString(R.string.account_type));

        if (mAccountManager.addAccountExplicitly(mCurrentAccount, mPassword, null)) {
            Toast.makeText(this, "Account created! Please sign in with your username and password.", Toast.LENGTH_LONG).show();
            Log.v("AccountCreate", "account created, username="+ mUsername); // Account creation succeeded
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
        mUsername = mExistAccountNameView.getText().toString();
        mPassword = mExistAccountPassView.getText().toString();

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
            // Login worked -- go to MainActivity
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("account", mCurrentAccount);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Sign in failed. Check username and password.", Toast.LENGTH_LONG).show();
        }
    }


}

