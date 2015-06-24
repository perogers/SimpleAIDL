/*
 * Copyright 2015 Paul E. Rogers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wiredbadger.simpleaidl.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.wiredbadger.epaurog.simpleaidl.R;
import com.wiredbadger.simpleaidl.aidl.NameCall;
import com.wiredbadger.simpleaidl.service.SimpleService;


/**
 * <b>Simple Demo for AIDL/Remote Service</b>
 *
 *</br></br>
 *
 * <b>The app makes no attempt to handle configuration changes!!
 *
 * </b></br>
 *
 * The main Activity that demos simple remote service using AIDL interface
 * Steps:</br>
 * 1. Define AIDL interface in aidl directory and package</br>
 * 2. Create Service definition</br>
 * 2.1 Create Binder implementation based on interface Stub</br>
 * 2.2 Return the Binder implementation in the onBind method</br>
 * 3. Add new service to AndroidManifest.xml</br>
 * 4. Create ServiceConnection instance implementing the onServiceConnected &
 *    onServiceDisconnected methods.  Use the Interface's Stub.asInterface method to
 *    create an instance of the Interface.</br>
 * 5. Add the ServiceConnection object to the onBind method</br>
 *
 */
public class MainActivity extends ActionBarActivity {

    private static final String TAG = "MainActivity";

    private EditText mNameText;
    private TextView mResponseText;
    private ProgressBar mProgressbar;
    private boolean mWaiting;


    // Reference to the remote interface
    private NameCall mNameCall;



    // Create the connection to the service
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mNameCall = NameCall.Stub.asInterface( service );
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mNameCall = null;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNameText = (EditText) findViewById(R.id.name_text);

        mResponseText = (TextView) findViewById(R.id.response_text);

        mProgressbar = (ProgressBar) findViewById(R.id.progress_bar);


        bindService();
    }

    @Override
    protected void onDestroy() {
        if( mServiceConnection != null ) {
            Log.d(TAG, "unbinding");
            unbindService(mServiceConnection);
        }
        super.onDestroy();
    }


    /**
     * Binds to the NameCall service
     */
    private void bindService() {
        Log.d(TAG, "binding to NameCall service");
        Intent intent = SimpleService.makeIntent( this );
        this.bindService( intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }


    /**
     * Submit button callback
     * @param v the submit button
     */
    public void sendName(View v) {
        Log.d(TAG, "sending name");

        // A very simplistic means to handle a blocking call
        // However, there are issues associated with configuration changes
        // that could crash the app
        mProgressbar.setVisibility(ProgressBar.VISIBLE);
        new Thread() {
            public void run() {
                try {
                    String name = mNameText.getText().toString();
                    
                    // Make the remote synchronous blocking call
                    final String response = mNameCall.doName(name);

                    // Create a Runnable to post the results to the View
                    if( mResponseText != null ) {
                        mResponseText.post(new Runnable() {
                            @Override
                            public void run() {
                                mResponseText.setText(response);
                                mProgressbar.setVisibility(ProgressBar.INVISIBLE);
                            }
                        });
                    }
                }
                catch(RemoteException e) {
                    Log.e(TAG, "Remote failure: " + e.getMessage(), e);
                    Toast.makeText(MainActivity.this,
                                    "Failed getting response: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                }
            }
        }.start();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }





}
