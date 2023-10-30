package edu.uiuc.cs427app;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

/**
 * Service that deals with authentication requests. This service is specified in the
 * AndroidManifest file and is triggered by the AccountManager for "edu.uiuc.cs427app"
 * account types. It directs authentication callbacks to {@link AppAccountAuthenticator}.
 */
public class AuthenticatorService extends Service {
    /**
     * Instance of AppAccountAuthenticator that is used for all authentication related tasks.
     */
    private AppAccountAuthenticator authenticator;

    /**
     * Upon service request the authenticator is initialized.
     */
    @Override
    public void onCreate() {
        authenticator = new AppAccountAuthenticator(this);
    }

    /**
     * Returns an interface by which the client can use this service. If the clients are unable
     * to connect, this method may return null.
     *
     * @param intent the intent provided when using this service
     * @return an IBinder that clients can call to use this service
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return authenticator.getIBinder();
    }

}
