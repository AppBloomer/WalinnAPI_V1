package com.walinns.walinnsapi;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class InAppNotification extends AppCompatActivity {

    ImageView mImageview;
    TextView mTxtContent,mTxtTitle;



    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_in_app_notification);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);



//        mImageview = (ImageView) findViewById(R.id.image);
//        mTxtContent = (TextView) findViewById(R.id.txt_content);
//        mTxtTitle = (TextView) findViewById(R.id.txt_title);
//        if(getIntent()!=null){
//            if(getIntent().getStringExtra("title")!=null){
//                System.out.println("App Status Notification : "+getIntent().getStringExtra("title") );
//                mTxtTitle.setText(getIntent().getStringExtra("title"));
//            }
//            if(getIntent().getStringExtra("message")!=null){
//                System.out.println("App Status Notification  message: "+getIntent().getStringExtra("message") );
//
//                mTxtContent.setText(getIntent().getStringExtra("message"));
//            }
//            if(getIntent().getStringExtra("imageUrl")!=null){
//                System.out.println("App Status Notification imageUrl: "+getIntent().getStringExtra("imageUrl") );
//
//                mImageview.setImageBitmap(WAUtils.StringToBitMap(getIntent().getStringExtra("imageUrl")));
//            }else {
//                mImageview.setVisibility(View.GONE);
//            }
//
//        }

        RelativeLayout relativeLayout = new RelativeLayout(this);

        // Defining the RelativeLayout layout parameters.
        // In this case I want to fill its parent
        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.FILL_PARENT,
                RelativeLayout.LayoutParams.FILL_PARENT);

        // Creating a new TextView
        TextView tv = new TextView(this);
        tv.setText("Test");

        // Defining the layout parameters of the TextView
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);

        // Setting the parameters on the TextView
        tv.setLayoutParams(lp);

        // Adding the TextView to the RelativeLayout as a child
        relativeLayout.addView(tv);

        // Setting the RelativeLayout as our content view
        setContentView(relativeLayout, rlp);


    }



}
