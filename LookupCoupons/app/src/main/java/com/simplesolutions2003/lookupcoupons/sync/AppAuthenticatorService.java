package com.simplesolutions2003.lookupcoupons.sync;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
/**
 * Created by SuriyaKumar on 9/6/2016.
 */
public class AppAuthenticatorService extends Service {

    // Instance field that stores the authenticator object
    private AppAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new AppAuthenticator(this);
    }

    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
