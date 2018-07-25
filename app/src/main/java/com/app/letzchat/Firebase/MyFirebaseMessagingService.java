package com.app.letzchat.Firebase;


import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Belal on 03/11/16.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                sendPushNotification(json);
                Log.e(TAG, "Notification JSON " + json.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private void sendPushNotification(JSONObject json) {
        Log.e(TAG, "Notification JSON " + json.toString());
        try {
            JSONObject data = json.getJSONObject("data");

            String title = data.getString("title");
            String message = data.getString("message");
            String type = data.getString("type");
            String sendBy = data.getString("sendBy");
            String sentDateTime = data.getString("sentDateTime");


            NotiModel notiModel = new NotiModel();
            notiModel.setMessage(message);
            notiModel.setSendBy(sendBy);
            notiModel.setSentDateTime(sentDateTime);
            notiModel.setTitle(title);
            notiModel.setType(Integer.parseInt(type));

            new MyNotificationManager(getApplicationContext(), notiModel);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}