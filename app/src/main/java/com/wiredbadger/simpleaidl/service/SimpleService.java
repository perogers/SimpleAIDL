package com.wiredbadger.simpleaidl.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.wiredbadger.simpleaidl.aidl.NameCall;

/**
 * Minimal Service using AIDL
 * Created by progers on 6/4/15.
 */
public class SimpleService extends Service {
    private static final String TAG = "SimpleService";

    /**
     * Concrete implementation of the NameCall interface
     */
    private final NameCall.Stub mBinder = new NameCall.Stub() {
        @Override
        public String doName(String name) throws RemoteException {
            Log.d(TAG, "Got name: " + name);
            // put a delay to simulate something being done
            try { Thread.sleep( 5000L ); } catch (InterruptedException ignore){}

            return "Hello there " + name;
        }
    };

    /**
     * Factory method for getting an Intent for binding to this service
     * @param context the Context for this intent
     * @return the Intent
     */
    public static Intent makeIntent(Context context) {
        Log.d(TAG, "Make Intent");
        return new Intent(context, SimpleService.class);
    }


    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG,"Unbinding");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "destroyed");
        super.onDestroy();
    }
}
