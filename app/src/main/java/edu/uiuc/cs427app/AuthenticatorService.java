package edu.uiuc.cs427app;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class AuthenticatorService extends Service {
    private AppAccountAuthenticator authenticator;
    @Override
    public void onCreate() {
        authenticator = new AppAccountAuthenticator(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return authenticator.getIBinder();
    }

}
