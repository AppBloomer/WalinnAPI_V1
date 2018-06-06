package com.walinns.walinnsapi;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.plus.model.people.Person;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Created by walinnsinnovation on 30/12/17.
 */

public class WalinnsAPIClient extends Activity {
    private static final WALog logger = WALog.getLogger();
    private WADeviceInfo deviceInfo;
    protected static Context context,mContext;
    protected String deviceId;
    protected String project_token;
    WAWorkerThread logThread;
    WAWorkerThread httpThread;
    protected String instanceName;
    protected WAPref shared_pref;
    protected WALifeCycle mWalinnsactivitylifecycle;
    private static final int MY_PERMISSIONS_REQUEST = 1,MY_PERMISSIONS_REQUEST_phone = 111,MY_PERMISSIONS_REQUEST_NAME=2;
    private String email_m= "na";
    private String possibleEmail = "NA",phone_number = "NA",first_name = "NA",last_name = "NA";
    public JSONObject device_hashMap;
    final Handler handler = new Handler();
    final Handler handler1 = new Handler();
    final Handler handler2 = new Handler();
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private boolean isReceiverRegistered;


    Runnable runnable,runnable1,runnable2;
    boolean bFlagForceExit = false;
    public static boolean flag_once=false;
    WAProfile waProfile;
    public static final String REGISTRATION_COMPLETE = "registrationComplete";



    public WalinnsAPIClient(Context context) {
        this((String)null);
        mContext=context;
        this.shared_pref=new WAPref(mContext);
        Thread.setDefaultUncaughtExceptionHandler(handleAppCrash);

    }
    private void registerReceiver(){
       // if(!isReceiverRegistered) {
            logger.d("WalinnsTrackerClient gcm notify:" , "statrs"  );

            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(REGISTRATION_COMPLETE));
            isReceiverRegistered = true;
       // }
    }
    public WalinnsAPIClient(String instance) {
        this.logThread = new WAWorkerThread("logThread");
        this.httpThread = new WAWorkerThread("httpThread");
        Thread.setDefaultUncaughtExceptionHandler(handleAppCrash);

        this.instanceName = WAUtils.normalizeInstanceName(instance);
        this.logThread.start();
        this.httpThread.start();


        //this.apiService= APIClient.getClient().create(ApiService.class);

    }

    public WalinnsAPIClient initialize(Context context, String project_token) {
        this.mContext=context;
        new APIClient(mContext);
        this.shared_pref=new WAPref(context);
        shared_pref.save(WAPref.project_token,project_token);

//        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                logger.d("WalinnsTrackerClient gcm notify:" , intent.getData().toString() );
//
//            }
//        };
//        registerReceiver();
        mContext.startService(new Intent(mContext,WAIntentService.class));

        logger.d("WalinnsTrackerClient Token:" , project_token );

        return this.initialize(context, project_token, (String)null);
    }

    private synchronized WalinnsAPIClient initialize(final Context context, String apiKey, final String userId) {
        // connect to app and dashboard
        if(context == null && mContext ==null) {
            logger.e("WalinnsTrackerClient", "Argument context cannot be null in initialize()");
            return this;
        } else if(WAUtils.isEmptyString(apiKey)) {
            logger.e("WalinnsTrackerClient", "Argument apiKey cannot be null or blank in initialize()");
            return this;
        }else {
            String v1 = "NA",v2 = "NA",f1="NA",l1="NA";
            this.context = context.getApplicationContext();
            this.project_token = apiKey;
            this.shared_pref=new WAPref(this.context);
            if(shared_pref.getValue(WAPref.gender)!=null){
                logger.e("WalinnsTrackerClientt gender", "Argument"+ shared_pref.getValue(WAPref.gender));
                v1 = shared_pref.getValue(WAPref.gender);
            }
            if(shared_pref.getValue(WAPref.age)!=null){
                logger.e("WalinnsTrackerClientt age", "Argument"+ shared_pref.getValue(WAPref.age));

                v2 = shared_pref.getValue(WAPref.age);
            }
            if(!shared_pref.getValue(WAPref.first_name).isEmpty()){
                f1 = shared_pref.getValue(WAPref.first_name);
            }
            if(!shared_pref.getValue(WAPref.last_name).isEmpty()){
                l1 = shared_pref.getValue(WAPref.last_name);
            }
            deviceCall(v1,v2,f1,l1);

        }
        return this;
    }



    private  WADeviceInfo.CachedInfo initializeDeviceInfo() {
        
        this.deviceInfo = new WADeviceInfo(mContext);
        this.deviceId =  Settings.Secure.getString(this.context.getContentResolver(), "android_id");
        shared_pref.save(WAPref.device_id,deviceId);
        logger.e("WalinnsTrackerClient",deviceId +"..."+ deviceInfo.toString());
        this.deviceInfo.prefetch();
       // System.out.println("Device_data"+deviceInfo.getCountry()+"...."+deviceInfo.getOsName());
        return this.deviceInfo.prefetch();
    }
    protected void runOnLogThread(Runnable r) {
        if(Thread.currentThread() != this.logThread) {
            this.logThread.post(r);
        } else {
            r.run();

        }

    }

    protected long getCurrentTimeMillis() {
        logger.e("Current session",String.valueOf(System.currentTimeMillis()));
        return System.currentTimeMillis();
    }

    public void track(String eventType /*view name like Button*/, String event_name/*Button name like submit*/) {
        this.logEvent(eventType, event_name);
    }

    private void logEvent(String eventType, String event_name) {
       // if(this.validateLogEvent(eventType)) {
            this.logEventAsync(eventType,event_name, this.getCurrentTimeMillis());
        //}
    }
    protected boolean validateLogEvent(String eventType) {
        if(TextUtils.isEmpty(eventType)) {
            logger.e("WalinnsTrackerClient", "Argument eventType cannot be null or blank in eventTrack()");
            return false;
        } else {
            return this.contextAndApiKeySet("logEvent()");
        }
    }
    protected synchronized boolean contextAndApiKeySet(String methodName) {
        if(this.context == null && mContext ==null) {
            logger.e("WalinnsTrackerClient", "context cannot be null, set context with initialize() before calling " + methodName);
            return false;
        } else if(TextUtils.isEmpty(this.project_token)) {
            logger.e("WalinnsTrackerClient", "apiKey cannot be null or empty, set apiKey with initialize() before calling " + methodName);
            return false;
        } else {
            return true;
        }
    }
    protected void logEventAsync(final String eventType,final String event_name,final long timestamp) {
        if(event_name != null) {
            this.runOnLogThread(new Runnable() {
                public void run() {
                    WalinnsAPIClient.this.logEvent(eventType,event_name ,timestamp);
                }
            });
        }


    }

    private void logEvent(String eventType, String event_name, long timestamp) {
        deviceId=shared_pref.getValue(WAPref.device_id);
        logger.e("walinnstrackerclient device_id",deviceId);
        JSONObject hashMap= new JSONObject();
        try {
            hashMap.put("event_type",eventType);
            hashMap.put("event_name",event_name);
            hashMap.put("device_id",deviceId);
            hashMap.put("date_time",WAUtils.getCurrentUTC());
            //Call<ResponseBody> call = apiService.event_post(hashMap);
            //call.enqueue(callResponse);
            logger.e("WalinnTrackerClient date_time_event",hashMap.toString());
            new APIClient(mContext,"events",hashMap);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public void install_refferer( ){
        JSONObject install  = new JSONObject();
        final SharedPreferences referralInfo = mContext.getSharedPreferences("WAInstall_refferer", Context.MODE_PRIVATE);

        String lanSettings = referralInfo.getString("utm_source", null);
        String device_id = Settings.Secure.getString(mContext.getContentResolver(), "android_id");
        try {
            if (referralInfo.getString("utm_source", null) != null) {
                install.put("install_refferrer", referralInfo.getString("utm_source", null));
            }
            if (referralInfo.getString("referrer", null) != null) {
                install.put("install_refferrer_test", referralInfo.getString("referrer", null));
            }
            install.put("device_id",device_id);
            install.put("date_time",WAUtils.getCurrentUTC());

           // System.out.println("install_refferer source refferer value pref req" + install.toString());
            new APIClient(mContext,"refferrer",install);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
       // System.out.println("install_refferer source refferer value pref:" +lanSettings + "////Token" + shared_pref.getValue(WAPref.project_token)+"....Device id:" + device_id);
    }

    @TargetApi(14)
    private void registerWalinnsActivityLifecycleCallbacks(String token) {
        if(Build.VERSION.SDK_INT >= 14) {
            if(this.mContext !=null) {
                logger.e("WalinnsTrackerClient","life_cycle_method"+"inside_if");
                Application app = (Application)this.mContext.getApplicationContext();
                this.mWalinnsactivitylifecycle = new WALifeCycle(this,WAConfig.getInstance(mContext),mContext,token);
                app.registerActivityLifecycleCallbacks(this.mWalinnsactivitylifecycle);
                mContext.startService(new Intent(mContext, WAService.class)); //start service which is MyService.java

            } else {
                logger.i("WalinnsTrackerClient", "Context is not an Application, Walinns will not automatically show in-app notifications or A/B test experiments. We won\'t be able to automatically flush on an app background.");
            }
        }

    }
    protected void track_(String eventName) {//
        logger.e("WalinnsTrackerClient gesture tracker", eventName);
    }
    protected  void track_(String eventName, JSONObject properties, boolean isAutomaticEvent) {
        try {
            // logger.e("WalinnsTrackerClient  tracker_session", eventName + Utils.convertUtctoCurrent(properties.getString("$start_time"),properties.getString("$end_time")));
             if(isAutomaticEvent){

                 final JSONObject hashMapp=new JSONObject();
                hashMapp.put("device_id",shared_pref.getValue(WAPref.device_id));
                if(!WAUtils.convertUtctoCurrent(properties.getString("$start_time"),properties.getString("$end_time")).isEmpty()){
                    hashMapp.put("session_length", WAUtils.convertUtctoCurrent(properties.getString("$start_time"), properties.getString("$end_time")));

                }else {
                    hashMapp.put("session_length", properties.getString("$ae_session_length"));
                }
                hashMapp.put("start_time", properties.getString("$start_time"));
                hashMapp.put("end_time", properties.getString("$end_time"));

                 this.runOnLogThread(new Runnable() {
                    public void run() {
                        // Call<ResponseBody> call = apiService.session(hashMapp);
                        //  call.enqueue(callResponse);
                        new APIClient(mContext,"session",hashMapp);
                    }
                });

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    protected void track_(String value,boolean status){
        logger.e("WalinnsTrackerClient active_status",String.valueOf(status)+shared_pref.getValue(WAPref.device_id));
        JSONObject hash = null;
        try {
            if(status){
                hash=new JSONObject();
                hash.put("active_status","yes");
                hash.put("date_time",value);
                hash.put("device_id",shared_pref.getValue(WAPref.device_id));

            }else {

                hash=new JSONObject();
                hash.put("active_status","no");
                hash.put("date_time",value);
                hash.put("device_id",shared_pref.getValue(WAPref.device_id));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final JSONObject finalHash = hash;
        this.runOnLogThread(new Runnable() {
            public void run() {
                //  Call<ResponseBody> call = apiService.fetchAppUserDetailPost(hash);
                // call.enqueue(callResponse);
                new APIClient(mContext,"fetchAppUserDetail", finalHash);
            }
        });
    }
    private Thread.UncaughtExceptionHandler handleAppCrash =
            new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread thread, final Throwable ex) {
                    logger.e("WalinnsTrackerClient crash report", ex.getMessage()+thread.getName());
                    shared_pref.save(WAPref.crash_report,ex.toString());
                    runOnLogThread(new Runnable() {
                        @Override
                        public void run() {

                            sendCreash();

                        }
                    });

                }
            };

    private void sendCreash(){
        runOnLogThread(new Runnable() {
            public void run() {
                final JSONObject hash;
                hash=new JSONObject();
                if(!WAUtils.isEmptyString(shared_pref.getValue(WAPref.crash_report))) {
                    try {
                        hash.put("reason", shared_pref.getValue(WAPref.crash_report));
                        hash.put("device_id", shared_pref.getValue(WAPref.device_id));
                        hash.put("date_time",WAUtils.getCurrentUTC());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    // hash.put("date_time",Utils.getDate(Long.parseLong(String.valueOf(System.currentTimeMillis()))));


                    //Call<ResponseBody> call = apiService.send_crash(hash);
                    //call.enqueue(callResponse);
                    new APIClient(mContext,"crashReport", hash);
                }
            }
        });
    }
    public void track(final String screen_name){
        runOnLogThread(new Runnable() {
            public void run() {
                final JSONObject hash;
                hash=new JSONObject();
                if(!WAUtils.isEmptyString(screen_name)) {
                    try {
                        hash.put("screen_name", screen_name );
                        hash.put("date_time", WAUtils.getCurrentUTC());
                        hash.put("device_id", shared_pref.getValue(WAPref.device_id));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }else {
                    logger.e("WalinnsTrackerClient","ScreenView value is empty"+"Please enter valid name");
                }

                //Call<ResponseBody> call = apiService.screenView(hash);
                // call.enqueue(callResponse);
                new APIClient(mContext,"screenView", hash);

            }
        });
    }
    protected void sendpush(){
        String pushtoken=null;
        if(!shared_pref.getValue(WAPref.push_token).isEmpty()) {
            pushtoken = shared_pref.getValue(WAPref.push_token);
        }else {
            pushtoken="null";
        }

        logger.d("WalinnsTracker push token", pushtoken);
        logger.d("WalinnsTracker package name", mContext.getPackageName());
        final String finalPushtoken = pushtoken;
        runOnLogThread(new Runnable() {
            public void run() {
                final JSONObject hash;
                hash=new JSONObject();
                if(!WAUtils.isEmptyString(finalPushtoken)) {
                    try {
                        hash.put("push_token", finalPushtoken);
                        hash.put("package_name",mContext.getPackageName());
                        hash.put("device_id", shared_pref.getValue(WAPref.device_id));
                        hash.put("date_time",WAUtils.getCurrentUTC());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //Call<ResponseBody> call = apiService.uninstallcount(hash);
                    // call.enqueue(callResponse);
                    new APIClient(mContext,"uninstallcount", hash);
                }
            }
        });
    }

    protected void lifeCycle(String token){
        logger.e("WalinnsTrackerClient","life_cycle_method_detected");
        registerWalinnsActivityLifecycleCallbacks(token);
    }

    private  void getPhone(final JSONObject hashMap){
        String[] permissons = {Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_CONTACTS
                 };
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity)mContext,
                    permissons,
                    MY_PERMISSIONS_REQUEST_phone);
            runnable1 = new Runnable() {
                public void run() {
                   // System.out.println( "Request_Data _ bFlagForceExit"+bFlagForceExit);
                    if(!bFlagForceExit){
                        getPhone(hashMap);
                    }


                }
            };
            handler1.postDelayed(runnable1,500);
        }else {
            TelephonyManager tMgr = (TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE);
            phone_number = tMgr.getLine1Number();
            if(phone_number !=null && !phone_number.isEmpty()){
                handler1.removeCallbacks(runnable1);
                handler1.removeCallbacksAndMessages(null);
            }
        }


        runOnLogThread(new Runnable() {
            public void run() {
                try {

                    hashMap.put("phone_number",phone_number);

                   // logger.e("Request_Data _ phone",phone_number);
                    logger.e("Request_Data _ grant",hashMap.toString() + flag_once);
                    new APIClient(mContext, "devices", hashMap);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void getMail(final JSONObject hashMap) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
           // ContextCompat.checkSelfPermission(mContext,  Manifest.permission.GET_ACCOUNTS);
            ContextCompat.checkSelfPermission(mContext,  Manifest.permission.GET_ACCOUNTS);

            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity)mContext,
                        new String[]{
                                Manifest.permission.GET_ACCOUNTS,
                                Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST);
//                ActivityCompat.requestPermissions((Activity)mContext,
//                        new String[]{Manifest.permission.GET_ACCOUNTS},
//                        MY_PERMISSIONS_REQUEST);

                runnable = new Runnable() {
                    public void run() {
                       // System.out.println( "Request_Data _ bFlagForceExit"+bFlagForceExit);
                        if(!bFlagForceExit){
                            getMail(hashMap);
                        }


                    }
                };
                handler.postDelayed(runnable,500);

            }else {

                Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
                Account[] accounts = AccountManager.get(mContext).getAccounts();
                for (Account account : accounts) {
                    if (emailPattern.matcher(account.name).matches()) {
                        possibleEmail = account.name;

                        bFlagForceExit = true;
                        logger.e("Synced email is", possibleEmail);

                        logger.e("Synced email is +++", account.toString());

                    }
                }

                logger.e("Request_Data _ mail",possibleEmail);
                if(!possibleEmail.equals("NA")){
                    handler.removeCallbacks(runnable);
                    handler.removeCallbacksAndMessages(null);
                }
            }

            runOnLogThread(new Runnable() {
                public void run() {
                    try {
                        hashMap.put("email",possibleEmail);
                        logger.e("Request_Data _ grant",hashMap.toString() + flag_once);
                        new APIClient(mContext, "devices", hashMap);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    }

     private  void getName(final JSONObject hashMap){

        ContextCompat.checkSelfPermission(mContext,Manifest.permission.READ_CONTACTS);
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
          //  System.out.println("Permission contact : "+"inside");

            ActivityCompat.requestPermissions((Activity) mContext,
                    new String[]{Manifest.permission.READ_CONTACTS,Manifest.permission.GET_ACCOUNTS},
                    MY_PERMISSIONS_REQUEST_NAME);


//        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_CONTACTS)
//                != PackageManager.PERMISSION_GRANTED) {
//
//            if (shouldShowRequestPermissionRationale(
//                    Manifest.permission.WRITE_CONTACTS)) {
//                // Show our own UI to explain to the user why we need to read the contacts
//                // before actually requesting the permission and showing the default UI
//            }
//
//            // Fire off an async request to actually get the permission
//            // This will show the standard permission request dialog UI
//            ActivityCompat.requestPermissions((Activity)mContext,new String[]{Manifest.permission.READ_CONTACTS},
//                    MY_PERMISSIONS_REQUEST_NAME);
//



//            ActivityCompat.requestPermissions((Activity)mContext,
//                    new String[]{Manifest.permission.READ_CONTACTS},
//                    MY_PERMISSIONS_REQUEST_NAME);
             runnable2 = new Runnable() {
                public void run() {
                   // System.out.println( "Request_Data _ bFlagForceExit"+bFlagForceExit);
                    if(!bFlagForceExit){
                        getName(hashMap);
                    }


                }
            };
            handler2.postDelayed(runnable2,500);
        }else {

            ContentResolver cr=mContext.getContentResolver();
            Cursor curser = cr.query(ContactsContract.Profile.CONTENT_URI, null, null, null, null);
            if(curser.getCount()>0){
                curser.moveToFirst();
                String name=curser.getString(curser.getColumnIndex(
                        ContactsContract.Profile.DISPLAY_NAME));

                String[] splited = name.split("\\s");
                if(splited.length>0){
                   // System.out.println("cursor Last name :" + splited[1] + ".." );
                    first_name = splited[0];
                    last_name = splited[1];

                }else {
                    first_name = "NA";
                    last_name = "NA";
                }

            }
            curser.close();
            if(first_name !=null && !first_name.isEmpty()){
                handler2.removeCallbacks(runnable1);
                handler2.removeCallbacksAndMessages(null);
            }
        }


        runOnLogThread(new Runnable() {
            public void run() {
                try {

                    hashMap.put("First_name",first_name);
                    hashMap.put("Last_name",last_name);
                  //  logger.e("Request_Data _ first_name",first_name);
                    logger.e("Request_Data _ grant",hashMap.toString() + flag_once);
                    new APIClient(mContext, "devices", hashMap);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case MY_PERMISSIONS_REQUEST:
                 if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(mContext, "Permission Granted", Toast.LENGTH_SHORT).show();
                    Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
                    Account[] accounts = AccountManager.get(mContext).getAccounts();
                    for (Account account : accounts) {
                        if (emailPattern.matcher(account.name).matches()) {
                            possibleEmail = account.name;
                            logger.e("Synced email is", email_m);
                            logger.e("Synced email is (((+++", account.toString());

                           // System.out.println("User content :" + account.name + account.toString());
                        }
                    }
                     runOnLogThread(new Runnable() {
                         public void run() {
                             try {
                                 device_hashMap.put("email",possibleEmail);
                             } catch (JSONException e) {
                                 e.printStackTrace();
                             }
                             new APIClient(mContext, "devices", device_hashMap);
                         }
                     });
                } else {
                   // Toast.makeText(mContext, "Permission Denied", Toast.LENGTH_SHORT).show();
                     Log.d("WalinnsApi Get_Task","Permission Denied");
                }
                return;




            case MY_PERMISSIONS_REQUEST_phone:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    TelephonyManager tMgr = (TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE);
                    phone_number = tMgr.getLine1Number();
                    runOnLogThread(new Runnable() {
                        public void run() {
                            try {
                                device_hashMap.put("phone_number",phone_number);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            new APIClient(mContext, "devices", device_hashMap);
                        }
                    });
                }else {
                    //Toast.makeText(mContext, "Permission Denied", Toast.LENGTH_SHORT).show();
                    Log.d("WalinnsApi READ_PHONE_STATE","Permission Denied");
                }
                return;

            case MY_PERMISSIONS_REQUEST_NAME:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    runOnLogThread(new Runnable() {
                        public void run() {
                            try {
                                ContentResolver cr=mContext.getContentResolver();
                                Cursor curser = cr.query(ContactsContract.Profile.CONTENT_URI, null, null, null, null);
                                if(curser.getCount()>0){
                                    curser.moveToFirst();
                                    String name=curser.getString(curser.getColumnIndex(
                                            ContactsContract.Profile.DISPLAY_NAME));
                                    String[] splited = name.split("\\s");
                                    if(splited.length>0){
                                       // System.out.println("cursor Last name :" + splited[1] + ".." );
                                        first_name = splited[0];
                                        last_name = splited[1];

                                    }else {
                                        first_name = "NA";
                                        last_name = "NA";
                                    }

                                }
                                curser.close();
                                device_hashMap.put("First_name",first_name);
                                device_hashMap.put("Last_name",last_name);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            new APIClient(mContext, "devices", device_hashMap);
                        }
                    });
                }else {
                    //Toast.makeText(mContext, "Permission Denied", Toast.LENGTH_SHORT).show();
                    Log.d("WalinnsApi  READ_CONTACT","Permission Denied");

                }
                return;


            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void pushProfile(JSONObject jsonObject){
        waProfile = new WAProfile();
        if(jsonObject!=null) {
            logger.e("WalinnsTrackerClient Profile :", jsonObject.toString());
            try {
                if (jsonObject.has("gender")) {
                    waProfile.setGender(jsonObject.getString("gender"));
                } else {
                    waProfile.setGender("NA");
                }

                if (jsonObject.has("birthday")) {
                    SimpleDateFormat df = new SimpleDateFormat("dd/mm/yyyy");
                    Date birthdate = df.parse(jsonObject.getString("birthday"));
                    logger.e("WalinnsTrackerClient Profile birthdate :", "Age: " + WAUtils.calculateAge(birthdate));
                    waProfile.setAge(String.valueOf(WAUtils.calculateAge(birthdate)));
                } else {
                    waProfile.setAge("NA");
                }

                if(jsonObject.has("first_name")){
                    waProfile.setFirst_name(jsonObject.getString("first_name"));
                }else {
                    waProfile.setFirst_name(" ");
                }

                if(jsonObject.has("last_name")){
                    waProfile.setLast_name(jsonObject.getString("last_name"));
                }else {
                    waProfile.setLast_name(" ");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            logger.e("WalinnsTrackerClient Profile birthdate :", "Age: " + waProfile.gender + "..." + waProfile.age);
            shared_pref.save(WAPref.gender,waProfile.getGender());
            shared_pref.save(WAPref.age,waProfile.getAge());
            shared_pref.save(WAPref.first_name,waProfile.getFirst_name());
            shared_pref.save(WAPref.last_name,waProfile.getLast_name());
            deviceCall(waProfile.getGender(), waProfile.getAge(),waProfile.getFirst_name(),waProfile.getLast_name());
        }
    }
    public void pushProfile(String acess_token){
        if(acess_token !=null) {
            logger.e("WalinnsTrackerClient Profile token:", acess_token);
            waProfile = new WAProfile();
            URL url = null;
            try {
                url = new URL("https://www.googleapis.com/oauth2/v1/userinfo?access_token=" + acess_token);

                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                int sc = con.getResponseCode();
                if (sc == 200) {
                    InputStream is = con.getInputStream();
                    String response = readResponse(is);
                    is.close();
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has("gender")) {
                        waProfile.setGender(jsonObject.getString("gender"));
                    } else {
                        waProfile.setGender("NA");
                    }
                    if (jsonObject.has("birthday")) {
                        SimpleDateFormat df = new SimpleDateFormat("dd/mm/yyyy");
                        Date birthdate = df.parse(jsonObject.getString("birthday"));
                        logger.e("WalinnsTrackerClient Profile birthdate :", "Age: " + WAUtils.calculateAge(birthdate));
                        waProfile.setAge(String.valueOf(WAUtils.calculateAge(birthdate)));
                    } else {
                        waProfile.setAge("NA");
                    }
                    if(jsonObject.has("given_name")){
                        waProfile.setFirst_name(jsonObject.getString("given_name"));
                    }else {
                        waProfile.setLast_name("");
                    }

                    if(jsonObject.has("family_name")){
                        waProfile.setLast_name(jsonObject.getString("family_name"));
                    }else {
                        waProfile.setLast_name("");
                    }

                   // System.out.println("Googgle user data : " + jsonObject.toString());
                    shared_pref.save(WAPref.gender,waProfile.getGender());
                    shared_pref.save(WAPref.age,waProfile.getAge());
                    shared_pref.save(WAPref.first_name,waProfile.getFirst_name());
                    shared_pref.save(WAPref.last_name,waProfile.getLast_name());
                    deviceCall(waProfile.getGender(), waProfile.getAge(),waProfile.getFirst_name(),waProfile.getLast_name());
                    return;
                } else if (sc == 401) {

                    logger.e("Server auth error, please try again.", null);
                    //Toast.makeText(mActivity, "Please try again", Toast.LENGTH_SHORT).show();
                    //mActivity.finish();
                    return;
                } else {
                    logger.e("Server returned the following error code: " + sc, null);
                    return;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
    public void pushGoogleProfile(Person person){
             waProfile = new WAProfile();

                 if (person.getGender() == 0) {
                     waProfile.setGender("Male");
                 } else if(person.getGender() == 1) {
                     waProfile.setGender("Female");
                 }
                 else {

                     waProfile.setGender("NA");
                 }


            if (person.hasBirthday()) {
                SimpleDateFormat df = new SimpleDateFormat("dd/mm/yyyy");
                Date birthdate = null;
                try {
                    birthdate = df.parse(person.getBirthday());
                   // System.out.println("WalinnsTrackerClient Profile birthdate :"+ "Age: " + WAUtils.calculateAge(birthdate));
                    waProfile.setAge(String.valueOf(WAUtils.calculateAge(birthdate)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }else if(person.hasAgeRange()){
                 if(person.getAgeRange() !=null) {
                     if (person.getAgeRange().hasMin())
                         waProfile.setAge("Min " + person.getAgeRange().getMin());
                     if (person.getAgeRange().hasMax()) {
                         waProfile.setAge("Max " + person.getAgeRange().getMax());
                     }
                 }else {
                     waProfile.setAge("NA");
                 }

            }
            else {
                waProfile.setAge("NA");
            }


            if(person.hasDisplayName()){
                String[] splited = person.getDisplayName().split("\\s");
                 if(splited.length>0){
                     waProfile.setFirst_name(splited[0]);
                     waProfile.setLast_name(splited[1]);
                 }
            }

        //System.out.println("Walinns gender"+waProfile.getGender() + waProfile.getAge());

           shared_pref.save(WAPref.gender, waProfile.getGender());
           shared_pref.save(WAPref.age, waProfile.getAge());
           shared_pref.save(WAPref.first_name,waProfile.getFirst_name());
           shared_pref.save(WAPref.last_name,waProfile.getLast_name());
           deviceCall(waProfile.getGender(), waProfile.getAge(),waProfile.getFirst_name(),waProfile.getLast_name());


    }
    private static String readResponse(InputStream is) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] data = new byte[2048];
        int len = 0;
        while ((len = is.read(data, 0, data.length)) >= 0) {
            bos.write(data, 0, len);
        }
        return new String(bos.toByteArray(), "UTF-8");
    }
    protected void deviceCall(final String v1, final String v2 , final String f1 , final String l1){
        this.runOnLogThread(new Runnable() {
            @Override
            public void run() {
                WADeviceInfo.CachedInfo cachedInfo=initializeDeviceInfo();
                logger.e("Device_data)))",cachedInfo.country+ cachedInfo.model);
                 device_hashMap =new JSONObject();
                //getPermissionToReadUserContacts();
                try {
                    device_hashMap.put("device_id",deviceId);
                    device_hashMap.put("device_model",cachedInfo.brand);
                    device_hashMap.put("os_name",cachedInfo.osName);
                    device_hashMap.put("os_version",cachedInfo.osVersion);
                    device_hashMap.put("app_version",cachedInfo.app_version);
                    device_hashMap.put("connectivity",cachedInfo.connectivty);
                    device_hashMap.put("carrier", cachedInfo.carrier);
                    device_hashMap.put("play_service",String.valueOf(cachedInfo.playservice));
                    device_hashMap.put("bluetooth",String.valueOf(cachedInfo.bluetooth));
                    device_hashMap.put("screen_dpi",cachedInfo.screen_dpi);
                    device_hashMap.put("screen_height",cachedInfo.screen_height);
                    device_hashMap.put("screen_width",cachedInfo.screen_width);
                    device_hashMap.put("gender",v1);
                    device_hashMap.put("age", v2);
                    device_hashMap.put("language",cachedInfo.language);
                    device_hashMap.put("country", cachedInfo.country);
                    device_hashMap.put("date_time",WAUtils.getCurrentUTC());
                    device_hashMap.put("sdk_version",cachedInfo.sdk_version);


                    if(cachedInfo.city==null){
                        device_hashMap.put("city","NA");
                    }else {
                        device_hashMap.put("city",cachedInfo.city);

                    }
                    if(cachedInfo.state==null){
                        device_hashMap.put("state","NA");

                    }else {
                        device_hashMap.put("state",cachedInfo.state);

                    }
                   // System.out.println("State and city loction :" + cachedInfo.city + "state" + cachedInfo.state);


                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                       // System.out.println("Above MarshMallow Data ifff:" + "1111111ifffff");
                        getMail(device_hashMap);
                        getPhone(device_hashMap);
                        if(!f1.isEmpty() || !l1.isEmpty()){
                           // System.out.println("Above MarshMallow Data :" + "1111111ifffff");

                            device_hashMap.put("First_name",f1);
                            device_hashMap.put("Last_name",l1);
                        }else {
                           // System.out.println("Above MarshMallow Data :" + "1111111");
                            getName(device_hashMap);
                        }


                    }else {
                      //  System.out.println("Above MarshMallow Data else :" + "1111111ifffff");
                        Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
                        Account[] accounts = AccountManager.get(context).getAccounts();
                        for (Account account : accounts) {
                            if (emailPattern.matcher(account.name).matches()) {
                                possibleEmail = account.name;
                                logger.e("Synced email is", possibleEmail);

                            }
                        }
                        device_hashMap.put("email", possibleEmail);

                        TelephonyManager tMgr = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
                        phone_number = tMgr.getLine1Number();
                        if(phone_number ==null){
                            device_hashMap.put("phone_number", "NA");

                        }
                        device_hashMap.put("First_name",getFirstName());
                        device_hashMap.put("Last_name",getLastName());
                        logger.e("Request_Data",device_hashMap.toString());
                        new APIClient(mContext, "devices", device_hashMap);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void getPermissionToReadUserContacts() {
        // 1) Use the support library version ContextCompat.checkSelfPermission(...) to avoid
        // checking the build version since Context.checkSelfPermission(...) is only available
        // in Marshmallow
        // 2) Always check for permission (even if permission has already been granted)
        // since the user can revoke permissions at any time through Settings
        if (ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // The permission is NOT already granted.
            // Check if the user has been asked about this permission already and denied
            // it. If so, we want to give more explanation about why the permission is needed.
            if (shouldShowRequestPermissionRationale(
                    android.Manifest.permission.READ_CONTACTS)) {
                // Show our own UI to explain to the user why we need to read the contacts
                // before actually requesting the permission and showing the default UI
            }

            // Fire off an async request to actually get the permission
            // This will show the standard permission request dialog UI
            requestPermissions(new String[]{android.Manifest.permission.READ_CONTACTS},
                    MY_PERMISSIONS_REQUEST_NAME);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
      //  System.out.println("Walinns sdk Onresume:"+ "started");

    }
    private String getFirstName(){

            ContentResolver cr = mContext.getContentResolver();
            Cursor curser = cr.query(ContactsContract.Profile.CONTENT_URI, null, null, null, null);
            if (curser.getCount() > 0) {
                curser.moveToFirst();
                String name = curser.getString(curser.getColumnIndex(
                        ContactsContract.Profile.DISPLAY_NAME));
                String[] splited = name.split("\\s");
                if (splited.length > 0) {
                    //System.out.println("cursor name :" + splited[0] + "..");

                    return splited[0];
                }

            }
            curser.close();


        return "NA";

    }
    private String getLastName() {

        ContentResolver cr = mContext.getContentResolver();
        Cursor curser = cr.query(ContactsContract.Profile.CONTENT_URI, null, null, null, null);
        if (curser.getCount() > 0) {
            curser.moveToFirst();
            String name = curser.getString(curser.getColumnIndex(
                    ContactsContract.Profile.DISPLAY_NAME));
            String[] splited = name.split("\\s");
            if (splited.length > 0) {
              //  System.out.println("cursor Last name :" + splited[1] + "..");

                return splited[1];
            }

        }
        curser.close();


        return "NA";

    }


}
