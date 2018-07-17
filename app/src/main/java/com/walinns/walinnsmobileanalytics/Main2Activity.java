package com.walinns.walinnsmobileanalytics;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;


import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.os.ConfigurationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.View;

import com.walinns.walinnsapi.WalinnsAPI;

import java.util.Locale;


public class Main2Activity extends AppCompatActivity {


     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
         WalinnsAPI.getInstance().initialize(this,"ee71aaf6bd87431c0e62");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);//b2bac52c84ea0f0a4139fbaecf99936e
          FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
         int show_all = Settings.Secure.getInt(getContentResolver(),"lock_screen_allow_private_notifications", -1);
         int noti_enabled = Settings.Secure.getInt(getContentResolver(),"lock_screen_show_notifications", -1);

         if(show_all > 0 && noti_enabled > 0){
             System.out.println("Notification is enabld or not"+ "yes");

         }else {
             System.out.println("Notification is enabld or not"+ "no");
         }
         Intent i = new Intent("com.android.vending.INSTALL_REFERRER");
//Set Package name
         i.setPackage("com.walinns.walinnsmobileanalytics");
//referrer is a composition of the parameter of the campaing
         i.putExtra("referrer", "utm_source%3Dgoogle" +
                 "%26utm_medium%3Dcpc" +
                 "%26utm_term%3Drunning%252Bshoes" +
                 "%26utm_content%3Dlogolink" +
                 "%26utm_campaign%3Dspring_sale");
         sendBroadcast(i);

      }



}
