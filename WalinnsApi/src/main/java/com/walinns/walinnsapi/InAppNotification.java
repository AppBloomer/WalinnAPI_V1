package com.walinns.walinnsapi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.net.URLDecoder;

import static android.widget.LinearLayout.VERTICAL;

public class InAppNotification extends Activity {

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

//        RelativeLayout relativeLayout = new RelativeLayout(this);
//        relativeLayout.setBackgroundColor(R.color.red);
//        // Defining the RelativeLayout layout parameters.
//        // In this case I want to fill its parent
//        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
//                RelativeLayout.LayoutParams.WRAP_CONTENT,
//                RelativeLayout.LayoutParams.WRAP_CONTENT);
//        rlp.addRule(RelativeLayout.CENTER_IN_PARENT);
//
//        // Creating a new TextView
//        ImageView tv = new ImageView(this);
//        tv.setImageBitmap(WAUtils.StringToBitMap(getIntent().getStringExtra("imageUrl")));
//
//        // Defining the layout parameters of the TextView
//        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
//                RelativeLayout.LayoutParams.WRAP_CONTENT,
//                RelativeLayout.LayoutParams.WRAP_CONTENT);
//        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
//
//
//        // Setting the parameters on the TextView
//        tv.setLayoutParams(lp);
//
//        // Adding the TextView to the RelativeLayout as a child
//        relativeLayout.addView(tv);


//        LinearLayout layout = new LinearLayout(this);
//        layout.setBackgroundColor(Color.parseColor("#FF4081"));
//        layout.setOrientation(VERTICAL);
//        LinearLayout.LayoutParams labelLayoutParams = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
//        layout.setLayoutParams(labelLayoutParams);
//
//
//        // If you want to add some controls in this Relative Layout
//        labelLayoutParams = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//         labelLayoutParams.setMargins(25,25,25,25);





        RelativeLayout relativeLayout = new RelativeLayout(this);
        RelativeLayout.LayoutParams rel_params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        rel_params.addRule(RelativeLayout.ALIGN_TOP);
        LinearLayout parentLayout = new LinearLayout(this);
        LinearLayout linearLayout1= new LinearLayout(this);
        LinearLayout linearLayout2= new LinearLayout(this);
        linearLayout2.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout2.setWeightSum(2);
        linearLayout2.setBackgroundColor(Color.TRANSPARENT);
        linearLayout1.setBackgroundColor(Color.TRANSPARENT);
        linearLayout1.setOrientation(VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        parentLayout.setOrientation(VERTICAL);
        parentLayout.setGravity(Gravity.BOTTOM);
        params.setMargins(25,25,25,25);
        parentLayout.setBackgroundColor(Color.parseColor("#FF4081"));
        TextView textView = new TextView(this);
        textView.setText("Title");
        textView.setGravity(Gravity.CENTER);
        textView.setId(1);
        textView.setTextColor(Color.parseColor("#FFFFFF"));
        textView.setBackgroundColor(Color.TRANSPARENT);
        linearLayout1.addView(textView,params);

        TextView textView_msg = new TextView(this);
        textView_msg.setText("Message");
        textView_msg.setGravity(Gravity.CENTER);
        textView_msg.setId(2);
        textView_msg.setTextColor(Color.parseColor("#FFFFFF"));
        textView_msg.setBackgroundColor(Color.TRANSPARENT);
        linearLayout1.addView(textView_msg,params);

        ImageView mImage = new ImageView(this);
        mImage.setImageBitmap(WAUtils.StringToBitMap(getIntent().getStringExtra("imageUrl")));
        //linearLayout1.addView(mImage,params);


        Button textView1 = new Button(this);
        textView1.setText("ok");
        textView1.setId(1);
        textView1.setGravity(Gravity.CENTER);
        textView1.setTextColor(Color.parseColor("#000000"));
        textView1.setBackgroundColor(Color.parseColor("#FFFFFF"));
        LinearLayout.LayoutParams paramstxt = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        linearLayout2.addView(textView1,paramstxt);

        Button textView_msg1 = new Button(this);
        textView_msg1.setText("Cancel");
        textView_msg1.setId(2);
        textView_msg1.setGravity(Gravity.CENTER);
        textView_msg1.setTextColor(Color.parseColor("#000000"));
        textView_msg1.setBackgroundColor(Color.parseColor("#FFFFFF"));
        LinearLayout.LayoutParams paramstxt1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        paramstxt1.setMargins(15,0,0,0);
        linearLayout2.addView(textView_msg1,paramstxt1);

         parentLayout.addView(linearLayout1, params);
         parentLayout.addView(linearLayout2, params);
        relativeLayout.addView(parentLayout,rel_params);
        relativeLayout.setGravity(Gravity.BOTTOM);

         setContentView(relativeLayout);




        // Setting the RelativeLayout as our content view
       // setContentView(relativeLayout, rlp);


    }

}
