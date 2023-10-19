package edu.uiuc.cs427app;

// The manifest file uses this to get permission from app.
// To-do: disclose privacy concerns to user given we are storing their data
import android.accounts.AccountAuthenticatorActivity;

public class AbstractAccountAuthenticator extends AccountAuthenticatorActivity {
//    set using the ConcreteAccountAuthenticator
    private String username;
    private String password;

}
