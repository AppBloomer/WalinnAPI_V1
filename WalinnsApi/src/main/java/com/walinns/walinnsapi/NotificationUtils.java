package com.walinns.walinnsapi;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.text.TextUtils;
import android.util.Patterns;



import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by walinnsinnovation on 18/01/18.
 */

public class NotificationUtils {
        private static String TAG = NotificationUtils.class.getSimpleName();

        private Context mContext;
        NotificationCompat.BigPictureStyle bigPictureStyle;
        public NotificationUtils(Context mContext) {
            this.mContext = mContext;
        }

        public void showNotificationMessage(String title, String message, String timeStamp, Intent intent) {
            showNotificationMessage(title, message, timeStamp, intent, null);
        }

        public void showNotificationMessage(final String title, final String message, final String timeStamp, Intent intent, String imageUrl) {
            // Check for empty push message
            if (TextUtils.isEmpty(message))
                return;

            int icon;
            try {
                String x = ManifestMetaData.getMetaData(mContext, WAConfig.LABEL_NOTIFICATION_ICON);
                if (x == null) throw new IllegalArgumentException();
                icon = mContext.getResources().getIdentifier(x, "drawable", mContext.getPackageName());
                if (icon == 0) throw new IllegalArgumentException();
            } catch (Throwable t) {
                ApplicationInfo ai = mContext.getApplicationInfo();

                icon = ai.icon;
            }
            // notification icon


            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            final PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            mContext,
                            0,
                            intent,
                            PendingIntent.FLAG_CANCEL_CURRENT
                    );

            final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                    mContext);

            final Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + mContext.getPackageName() + "/raw/notification");

            if (!TextUtils.isEmpty(imageUrl)) {

                if (imageUrl != null && imageUrl.length() > 4 && Patterns.WEB_URL.matcher(imageUrl).matches()) {

                    Bitmap bitmap = getBitmapFromURL(imageUrl);

                    if (bitmap != null) {
                        showBigNotification(bitmap, mBuilder, icon, title, message, timeStamp, resultPendingIntent, alarmSound);
                    } else {
                        showSmallNotification(mBuilder, icon, title, message, timeStamp, resultPendingIntent, alarmSound);
                    }
                }
            } else {
                showSmallNotification(mBuilder, icon, title, message, timeStamp, resultPendingIntent, alarmSound);
                playNotificationSound();
            }
        }


        private void showSmallNotification(NotificationCompat.Builder mBuilder, int icon, String title, String message, String timeStamp, PendingIntent resultPendingIntent, Uri alarmSound) {

            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

            inboxStyle.addLine(message);

            Notification notification;
            notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setContentIntent(resultPendingIntent)
                    .setSound(alarmSound)
                    .setStyle(inboxStyle)
                    .setWhen(getTimeMilliSec(timeStamp))
                    .setSmallIcon(icon)
                    .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                    .setContentText(message)
                    .build();

            NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(WAConfig.NOTIFICATION_ID, notification);
        }

        private void showBigNotification(final Bitmap bitmap, NotificationCompat.Builder mBuilder, int icon, final String title, final String message, String timeStamp, PendingIntent resultPendingIntent, Uri alarmSound) {

            bigPictureStyle = new NotificationCompat.BigPictureStyle();
            bigPictureStyle.setBigContentTitle(title);
            bigPictureStyle.setSummaryText(Html.fromHtml(message).toString());
            bigPictureStyle.bigPicture(bitmap);
            Notification notification;
            notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setContentIntent(resultPendingIntent)
                    .setSound(alarmSound)
                    .setStyle(bigPictureStyle)
                    .setWhen(getTimeMilliSec(timeStamp))
                    .setSmallIcon(icon)
                    .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                    .setContentText(message)
                    .build();


            NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(WAConfig.NOTIFICATION_ID_BIG_IMAGE, notification);
        }

        /**
         * Downloading push notification image before displaying it in
         * the notification tray
         */
        public Bitmap getBitmapFromURL(String strURL) {
            try {
                URL url = new URL(strURL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        // Playing notification sound
        public void playNotificationSound() {
            try {
                String strUri = "android.resource://"+
                        "com.walinns.walinnsapi"+  "/" + "raw/blasters";
                Uri alarmSound = Uri.parse(strUri);
//                Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
//                        + "://" + "com.walinns.walinnsapi" + "/raw/notification");
                Ringtone r = RingtoneManager.getRingtone(mContext, alarmSound);
                r.play();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * Method checks if the app is in background or not
         */
        public static boolean isAppIsInBackground(Context context) {
            boolean isInBackground = true;
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
                List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
                for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                    if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        for (String activeProcess : processInfo.pkgList) {
                            if (activeProcess.equals(context.getPackageName())) {
                                isInBackground = false;
                            }
                        }
                    }
                }
            } else {
                List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
                ComponentName componentInfo = taskInfo.get(0).topActivity;
                if (componentInfo.getPackageName().equals(context.getPackageName())) {
                    isInBackground = false;
                }
            }

            return isInBackground;
        }

        // Clears notification tray messages
        public static void clearNotifications(Context context) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancelAll();
        }

        public static long getTimeMilliSec(String timeStamp) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date date = format.parse(timeStamp);
                return date.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return 0;
        }

}