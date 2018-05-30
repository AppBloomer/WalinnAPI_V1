package com.walinns.walinnsapi;

import android.app.Dialog;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class InAppNotification extends AppCompatActivity {

    ImageView mImageview;
    TextView mTxtTitle,mTxtContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_app_notification);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

//        final Dialog dialog = new Dialog(InAppNotification.this );
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setCancelable(false);
//        dialog.setContentView(R.layout.activity_in_app_notification);
        mImageview = (ImageView) findViewById(R.id.image);
       mTxtContent = (TextView) findViewById(R.id.txt_content);
       mTxtTitle = (TextView) findViewById(R.id.txt_title);
        if(getIntent()!=null){
            if(getIntent().getStringExtra("title")!=null){
                System.out.println("App Status Notification : "+getIntent().getStringExtra("title") );
                mTxtTitle.setText(getIntent().getStringExtra("title"));
            }
            if(getIntent().getStringExtra("message")!=null){
                System.out.println("App Status Notification  message: "+getIntent().getStringExtra("message") );

                mTxtContent.setText(getIntent().getStringExtra("message"));
            }
            if(getIntent().getStringExtra("imageUrl")!=null){
                System.out.println("App Status Notification imageUrl: "+getIntent().getStringExtra("imageUrl") );

                mImageview.setImageBitmap(WAUtils.StringToBitMap(getIntent().getStringExtra("imageUrl")));
            }else {
                mImageview.setVisibility(View.GONE);
            }

        }

//        okButton.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                //Perfome Action
//            }
//        });
//        cancleButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.dismiss();
//            }
//        });

       //  dialog.show();

    }

}
