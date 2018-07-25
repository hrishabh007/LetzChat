package com.app.letzchat.Firebase;

import android.util.Log;

import com.app.letzchat.utils.PreferenceUtils;
import com.app.letzchat.utils.SharedPrefManager;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Rishabh on 2/6/2018.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    @Override
    public void onTokenRefresh() {

        //Getting registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        //Displaying token on logcat
        Log.d(TAG, "Refreshed token: " + refreshedToken);
      //d3KLVY6qy7Q:APA91bFT278luyqvj9ysS5jH08SXQx443TVR1C5aQ3Y21vuEAuxN8NWpRA-0a9IsKBviW_E79kIUNUODT5qp1-5uv5_fvFDNACJ6smfinEl-kZSfyaWSO4NVn_GDb-MmLeme00ECR4Eq
      //  Prefs.putString("Token",refreshedToken);
        storeToken(refreshedToken);

    }
    private void storeToken(String token) {
        //saving the token on shared preferences
        SharedPrefManager.getInstance(getApplicationContext()).saveDeviceToken(token);

    }

}

