package edu.uiuc.cs427app;

// The manifest file uses this to get permission from app.
// To-do: disclose privacy concerns to user given we are storing their data
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

// This activity should do the following:
//        1. Collects credentials from the user
//        2. Authenticates the credentials with the server
//        3. Stores the credentials on the device
// consider changing activity to 'authenticatorActivity'
public class CreateAccountActivity extends AppCompatActivity implements View.OnClickListener{
//    set using onClick
    private String username;
    private String password;
    private String theme;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initializing the UI components
        // The list of locations should be customized per user (change the implementation so that
        // buttons are added to layout programmatically
        Button buttonSignUp = findViewById(R.id.buttonSignUp);
        Button buttonSignIn = findViewById(R.id.buttonSignIn);
//        Button buttonLA = findViewById(R.id.buttonLA);
//        Button buttonNew = findViewById(R.id.buttonAddLocation);

        buttonSignUp.setOnClickListener(this);
        buttonSignIn.setOnClickListener(this);


    }

//    TO-DO: (1)
    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.buttonSignUp:
                intent = new Intent(this, SignUpActivity.class);
                startActivity(intent);
                break;
            case R.id.buttonSignIn:
                intent = new Intent(this, MainActivity.class);  // TO DO: switch to sign in activity
                startActivity(intent);
                break;
        }
    }
//    final Account account = new Account(username, your_account_type);
//    accountManager.addAccountExplicitly(account, password, null);
}

