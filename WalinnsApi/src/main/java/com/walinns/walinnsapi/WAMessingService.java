package com.walinns.walinnsapi;


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;

import android.text.TextUtils;
import android.util.Log;


import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;



import java.util.Map;


/**
 * Created by walinnsinnovation on 30/12/17.
 */

public class WAMessingService extends FirebaseMessagingService {


    private static final WALog logger = WALog.getLogger();
    private static final String TAG = WAMessingService.class.getSimpleName();
    public static String notification_clicked = "NA";
    WAPref waPref;

    private NotificationUtils notificationUtils;

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        if (remoteMessage == null)
            return;

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification clicked Body: " + remoteMessage.getNotification().getBody());
            waPref = new WAPref(this.getApplicationContext());
            notification_clicked = "received";
            waPref.save(WAPref.noify_clicked,remoteMessage.getNotification().getBody()+" clicked");
            Log.e(TAG, "Notification clicked Body after: " + waPref.getValue(WAPref.noify_clicked));

            WalinnsAPI.getInstance().track("default_event",remoteMessage.getNotification().getBody()+" received");

            handleNotification(remoteMessage.getNotification().getBody());
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
            waPref = new WAPref(this.getApplicationContext());
            notification_clicked = "received";

            try {
                Map<String, String> data = remoteMessage.getData();

                handleDataMessage(data);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }else {


        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

    /**
     * Schedule a job using FirebaseJobDispatcher.
     */

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNotification(String message) {
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message

            Intent pushNotification = new Intent(WAConfig.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            pushNotification.setAction("pushNotification");
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();

        }else{
            // If the app is in background, firebase itself handles the notification
            System.out.println("App Status :" + "background" + message);
//            Intent pushNotification = new Intent(WAConfig.PUSH_NOTIFICATION);
//            pushNotification.putExtra("message", message);
//

            Intent pushNotification = new Intent(this, WalinnsAPIClient.class);
            pushNotification.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pushNotification.putExtra("message", message);




             LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();

        }
    }


    private void handleDataMessage(Map<String, String> data) {
        Log.e(TAG, "push json: " + data.toString());

        try {

            String title = data.get("title");
            String message = data.get("message");
            String imageUrl = data.get("image");
            String timestamp = data.get ("timestamp");
            String ui_type = data.get("ui_type");
            String btn_1_name = data.get("btn_1_name");
            String deep_link = data.get("deep_link");
            String btn_2_name = data.get("btn_2_name");
            String bg_color = data.get("bg_color");
            String btn_1_color = data.get("btn_1_color");
            String btn_2_color = data.get("btn_2_color");
            String external_link = data.get("external_link");

            Log.e(TAG, "title: " + title);
            Log.e(TAG, "message: " + message);
            Log.e(TAG, "imageUrl: " + imageUrl);
            Log.e(TAG, "timestamp: " + timestamp);
            Log.e(TAG, "ui_type: " + ui_type);
            waPref.save(WAPref.noify_clicked,title+" clicked");

            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {

                Intent intent = new Intent(getApplicationContext(),InAppNotification.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("title",title);
                intent.putExtra("message",message);
                intent.putExtra("imageUrl",imageUrl);
                intent.putExtra("ui_type",ui_type);
                intent.putExtra("btn_1_name",btn_1_name);
                intent.putExtra("deep_link",deep_link);
                intent.putExtra("btn_2_name",btn_2_name);
                intent.putExtra("bg_color",bg_color);
                intent.putExtra( "btn_1_color",btn_1_color);
                intent.putExtra("btn_2_color",btn_2_color);
                intent.putExtra("external_link",external_link);
                getApplicationContext().startActivity(intent);


            } else {
                // app is in background, show the notification in notification tray
                System.out.println("App Status msg:" + "background" + "....."+ data.toString());

                Intent resultIntent = new Intent(WAConfig.PUSH_NOTIFICATION);
                resultIntent.putExtra("message", message);

                // check for image attachment
                if (TextUtils.isEmpty(imageUrl)) {
                    showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent,ui_type,deep_link,external_link,btn_1_name,btn_2_name);
                } else {
                    // image is present, show notification with image
                    showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl,ui_type,deep_link,external_link,btn_1_name,btn_2_name);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent,String ui_type,String deep_link,String external_link,String btn1_name,String btn2_name) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent,ui_type,deep_link,external_link,btn1_name,btn2_name);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl,String ui_type,String deep_link,String external_link,String btn1_name,String btn2_name) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl,ui_type,deep_link,external_link,btn1_name,btn2_name);
    }



}
