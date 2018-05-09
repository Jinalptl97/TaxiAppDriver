package com.taxibookingdriver.Service;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import static com.taxibookingdriver.Activities.PasswordActivity.reg_id;

// Created by Dharvik on 6/25/2016.

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";
    public static String rtoken = "";

    @Override
    public void onTokenRefresh() {

        rtoken = FirebaseInstanceId.getInstance().getToken();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        String id = FirebaseInstanceId.getInstance().getId();
        Log.e(TAG, "Refreshed token: " + refreshedToken);
        Log.e(TAG, "Refreshed ID: " + id);
        reg_id = refreshedToken;

        // TODO: Implement this method to send any registration to your app's servers.
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token) {
        Log.e(TAG, "Refreshed token: " + token);
    }

//    @Override
//    public void uncaughtException(Thread t, Throwable e) {
//        FirebaseCrash.report(e);
//    }
}
