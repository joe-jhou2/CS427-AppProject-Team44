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

/**
 * Custom AccountAuthenticator that deals with "edu.uiuc.cs427app" account types.
 */
// TODO (post Milestone 3) improve rudimentary authentication
public class AppAccountAuthenticator extends AbstractAccountAuthenticator {
    /**
     * HashMap used as a means for authenticating users. Stores usernames and passwords
     * as key/value pairs, respectively.
     */
    // TODO (post Milestone 3) encrypt the password value
    private final Map<String, String> userData;
    /**
     * Context provided to the authenticator by the client when using the
     * authenticating service.
     */
    private final Context mContext;

    /**
     * Constructor for AppAccountAuthenticator that utilizes the parent class constructor
     * and sets the two fields in this child instance, mContext and userData.
     *
     * @param context context provided by the client when first instancing authenticator
     */
    public AppAccountAuthenticator(Context context) {
        super(context);
        mContext = context;
        userData = new HashMap<>();
    }
    /**
     * Adds a new user to the userData HashMap.
     *
     * @param username The username of the user to be added.
     * @param password The password associated with the username.
     */
    public void addUser(String username, String password) {
        userData.put(username, password);
    }

    /**
     * Validates the user's credentials.
     *
     * @param username The username entered by the user.
     * @param password The password entered by the user.
     * @return true if the credentials are valid, false otherwise.
     */
    public boolean validateUserCredentials(String username, String password) {
        String storedPassword = userData.get(username);
        return storedPassword != null && storedPassword.equals(password);
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse accountAuthenticatorResponse, String accountType) {
        //I mplement the logic to edit properties of the account here
        return null;
    }

    /**
     * Creates an account using the account manager and the custom account type
     * authentication.
     *
     * @param accountAuthenticatorResponse response from the account manager
     * @param accountType account type of the account
     * @param authTokenType token used for authentication -- not used
     * @param requiredFeatures not used
     * @param options bundle passed with account to be created
     * @return bundle with the created account
     * @throws NetworkErrorException report network errors
     */
    @Override
    public Bundle addAccount(AccountAuthenticatorResponse accountAuthenticatorResponse,
                             String accountType, String authTokenType,
                             String[] requiredFeatures,
                             Bundle options) throws NetworkErrorException {

        final Intent intent = new Intent(mContext, CreateAccountActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, accountAuthenticatorResponse);

        String username = options.getString(AccountManager.KEY_ACCOUNT_NAME);
        // TODO (post Milestone 3) encrypt the password value
        String encryptedPassword = options.getString(AccountManager.KEY_PASSWORD);

        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(encryptedPassword)) {
            if (validateLocally(username, encryptedPassword)) { // method to validate the credentials
                return createAccountBundle(username, encryptedPassword, accountAuthenticatorResponse); // create account if valid
            }
        }

        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
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

    /**
     * Validates the user's credentials, uses userData. For new users the, the username and password
     * combos are stored. For existing users, the combos are checked against the stored combos.
     *
     * @param username account username
     * @param password account password expressed as a cipher value
     * @return boolean, whether or not an existing account was accredited
     */
    private boolean validateLocally(String username, String password) {
        String storedPasswordHash = userData.get(username);
        if (storedPasswordHash == null) {
            // FOR NEW USER -- no record for the user, consider as a new user and save hashed password
            userData.put(username, hashPassword(password));
            return true;
        } else {
            // FOR EXISTING USER -- compares stored hash (in HashMap userData) vs hash method on prompted password
            return storedPasswordHash.equals(hashPassword(password));
        }
    }

    /**
     * Creates an account given the username/password/account type.
     *
     * @param username account username
     * @param password account password
     * @param response authenticator response, dismissed as this is a custom implementation that does not use this method
     * @return the Bundle to be added upon account creation
     */
    private Bundle createAccountBundle(String username, String password, AccountAuthenticatorResponse response) {
        Bundle result = new Bundle();
        Account account = new Account(username, mContext.getString(R.string.account_type));

        // Put the account information in the account bundle upon successful adding of the account
        // to the account manager for "edu.uiuc.cs427app"
        if (AccountManager.get(mContext).addAccountExplicitly(account, password, null)) {
            result.putString(AccountManager.KEY_ACCOUNT_NAME, username);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, mContext.getString(R.string.account_type));
        } else {
            // TODO (post Milestone 3) handle error -- this may be handled by CreateAccountActivity
        }
        return result;
    }

    /**
     * Encryption method by which to conceal and protect user's password.
     *
     * @param password a clean unencrypted version of the password
     * @return an encrypted version of the password
     */
    // TODO (post Milestone 3) encryption method -- use Cipher in place of a standard hashing function
    private String hashPassword(String password) {
        String storedPasswordHash = String.valueOf(password.hashCode());
        return storedPasswordHash;
    }
}

