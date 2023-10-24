package edu.uiuc.cs427app;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

public class AppAccountAuthenticator extends AbstractAccountAuthenticator {
    // TODO - improve rudimentary authentication
    // 1. Replace HashMap with
    private final Map<String, String> userData = new HashMap<>();
    private final Context mContext;

    public AppAccountAuthenticator(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse accountAuthenticatorResponse, String accountType) {
        return null;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse accountAuthenticatorResponse,
                             String accountType, String authTokenType,
                             String[] requiredFeatures,
                             Bundle options) throws NetworkErrorException {

        final Intent intent = new Intent(mContext, CreateAccountActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, accountAuthenticatorResponse);

        // TODO: local server validation -- use Cipher to keep this encrypted
        String username = options.getString(AccountManager.KEY_ACCOUNT_NAME);
        String password = options.getString(AccountManager.KEY_PASSWORD);

        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
            if (validateLocally(username, password)) { // method to validate the credentials
                return createAccountBundle(username, password, accountAuthenticatorResponse); // create account if valid
            }
        }

        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }
    
    // Validate the user's credentials with the local data
    private boolean validateLocally(String username, String password) {
        String storedPasswordHash = userData.get(username);
        if (storedPasswordHash == null) {
            // FOR NEW USER -- no record for the user, consider as a new user and save hashed pass
            userData.put(username, hashPassword(password));
            return true;
        }
        // FOR EXISTING USER -- compares stored hash (in HashMap userData) vs hash method on prompted password
        return storedPasswordHash.equals(hashPassword(password));
    }

    private Bundle createAccountBundle(String username, String password, AccountAuthenticatorResponse response) {
        Bundle result = new Bundle();
        Account account = new Account(username, mContext.getString(R.string.account_type));
        if (AccountManager.get(mContext).addAccountExplicitly(account, password, null)) {
            result.putString(AccountManager.KEY_ACCOUNT_NAME, username);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, mContext.getString(R.string.account_type));
        } else {
            // TODO: handle error -- this may be handled by CreateAccountActivity
        }
        return result;
    }


    // TODO: Use Cipher in place of a hashmap
    private String hashPassword(String password) {
        // TODO: Replace entire functionality (w/Cipher) or replace with a real hashing function
        return String.valueOf(password.hashCode());
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, Bundle bundle) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String s, Bundle bundle) throws NetworkErrorException {
        return null;
    }

    @Override
    public String getAuthTokenLabel(String s) {
        return null;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String s, Bundle bundle) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String[] strings) throws NetworkErrorException {
        return null;
    }


}
