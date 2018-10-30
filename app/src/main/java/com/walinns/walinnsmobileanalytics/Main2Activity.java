package com.walinns.walinnsmobileanalytics;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;

import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;


import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.os.ConfigurationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


import com.appsee.Appsee;
import com.walinns.walinnsapi.WalinnsAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.regex.Pattern;


public class Main2Activity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST = 1,MY_PERMISSIONS_REQUEST_phone = 111,MY_PERMISSIONS_REQUEST_NAME=2;
    String email = "NA";
    ImageView img_screen;
    Button btn_click;
    private int click = 1, x = 0 , y = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

         int show_all = Settings.Secure.getInt(getContentResolver(),"lock_screen_allow_private_notifications", -1);
         int noti_enabled = Settings.Secure.getInt(getContentResolver(),"lock_screen_show_notifications", -1);

         if(show_all > 0 && noti_enabled > 0){
             System.out.println("Notification is enabld or not"+ "yes");

         }else {
             System.out.println("Notification is enabld or not"+ "no");
         }
         WalinnsAPI.getInstance().initialize(Main2Activity.this,"b9d2e92935000ffd585cc3092f9b03cd");
 //         Intent i = new Intent("com.android.vending.INSTALL_REFERRER");
////Set Package name
//         i.setPackage("com.walinns.walinnsmobileanalytics");
////referrer is a composition of the parameter of the campaing
//         i.putExtra("referrer", "utm_source%3Dgoogle" +
//                 "%26utm_medium%3Dcpc" +
//                 "%26utm_term%3Drunning%252Bshoes" +
//                 "%26utm_content%3Dlogolink" +
//                 "%26utm_campaign%3Dspring_sale");
//         sendBroadcast(i);

         img_screen = (ImageView)findViewById(R.id.img_screen);
         btn_click = (Button)findViewById(R.id.btn_click);

         getMail();
       //  WalinnsAPI.getInstance().track("test","sample event");

        if(getIntent()!=null){
            if(getIntent().getStringExtra("message")!=null){
                System.out.println("Notification intent data"+ getIntent().getStringExtra("message"));
            }
        }

         btn_click.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
               screenShot(getWindow().getDecorView().getRootView());
                 Intent intent = new Intent(Main2Activity.this,NewScreen.class);
                 startActivity(intent);
             }
         });


      }

    public Bitmap screenShot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888);
        System.out.println("Image bitmap:" + bitmap);
        img_screen.setImageBitmap(bitmap);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }
    private void getMail() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
           // ContextCompat.checkSelfPermission(mContext,  Manifest.permission.GET_ACCOUNTS);

            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {



                ActivityCompat.requestPermissions(Main2Activity.this,
                        new String[]{android.Manifest.permission.GET_ACCOUNTS},
                        MY_PERMISSIONS_REQUEST);

//                ActivityCompat.requestPermissions((Activity)mContext,
//                        new String[]{Manifest.permission.GET_ACCOUNTS},
//                        MY_PERMISSIONS_REQUEST);



            }else {
                System.out.println("Check email permission :" + "granted");
                Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
                Account[] accounts = AccountManager.get(Main2Activity.this).getAccounts();
                for (Account account : accounts) {
                    if (emailPattern.matcher(account.name).matches()) {
                        email = account.name;
                    }
                }
                System.out.println("Check email permission :" + "granted" +email);

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("First_name","Kevin");
                    jsonObject.put("Last_name","Fertilee");
                    jsonObject.put("email",email);
                    jsonObject.put("phone_number","95709045345");

                 } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }else {
            Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
                Account[] accounts = AccountManager.get(Main2Activity.this).getAccounts();
                for (Account account : accounts) {
                    if (emailPattern.matcher(account.name).matches()) {
                         email = account.name;
                    }
                }
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("First_name","Kevin");
                jsonObject.put("Last_name","Fertilee");
                jsonObject.put("email",email);
                jsonObject.put("phone_number","95709045345");

             } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case MY_PERMISSIONS_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
                    Account[] accounts = AccountManager.get(Main2Activity.this).getAccounts();
                    for (Account account : accounts) {
                        if (emailPattern.matcher(account.name).matches()) {
                            email = account.name;
                        }
                    }
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("First_name","Kevin");
                        jsonObject.put("Last_name","Fertilee");
                        jsonObject.put("email",email);
                        jsonObject.put("phone_number","95709045345");

                     } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Check email permission onrequest :" + "granted" + email);

                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                 }
                return;




            case MY_PERMISSIONS_REQUEST_phone:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                }else {
                    //Toast.makeText(mContext, "Permission Denied", Toast.LENGTH_SHORT).show();
                 }
                return;

            case MY_PERMISSIONS_REQUEST_NAME:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {



                }else {
                    //Toast.makeText(mContext, "Permission Denied", Toast.LENGTH_SHORT).show();

                }
                return;


            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        final int action = event.getAction();
//        if(action == MotionEvent.ACTION_DOWN) {
//            if(x != (int) event.getX() && y != (int) event.getY()) {
//                x = (int) event.getX();
//                y = (int) event.getY();
//                Log.d("event", "[" + x + ",    " + y + ",     " + click + "]");
//                 click = 1;
//            }else{
//                click++;
//            }
//        }
//        return super.onTouchEvent(event);
//    }
}
