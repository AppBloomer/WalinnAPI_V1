package com.walinns.walinnsapi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.widget.LinearLayout.VERTICAL;

public class InAppNotification extends Activity implements View.OnClickListener {

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_in_app_notification);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        if(StringIsEmpty(getIntent().getStringExtra("ui_type"))) {
            inAppData(getIntent().getStringExtra("ui_type"));
        }

    }

    private void inAppData(String ui_type) {
        switch (ui_type){
            case "footer":
                setView(RelativeLayout.ALIGN_PARENT_BOTTOM,RelativeLayout.LayoutParams.WRAP_CONTENT,"footer");
                break;
            case "full":
                setView(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT,"full");
                break;
            case "header":
                setView(RelativeLayout.ALIGN_PARENT_TOP,RelativeLayout.LayoutParams.WRAP_CONTENT,"header");
                break;
            case "rating":
                setView(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT,"rating");
                break;
        }
    }



    private void setView(int type, int height, String char_type){
        LinearLayout.LayoutParams paramstxt;
        RelativeLayout relativeLayout = new RelativeLayout(this);
        relativeLayout.setId(12);
        RelativeLayout.LayoutParams rel_params = new RelativeLayout
                .LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                height);
        rel_params.addRule(type);
        if(char_type.equals("full")){
            rel_params.setMargins(15, 15, 15, 15);

        }else {
            rel_params.setMargins(0, 0, 0, 0);
        }


        RelativeLayout relativeLayout1 = new RelativeLayout(this);
        LinearLayout parentLayout = new LinearLayout(this);
        LinearLayout linearLayout1= new LinearLayout(this);
        LinearLayout linearLayout2= new LinearLayout(this);
        if(char_type.equals("full")){
             paramstxt = new LinearLayout.LayoutParams(
                    400, LinearLayout.LayoutParams.WRAP_CONTENT);
            paramstxt.gravity = Gravity.CENTER;
            linearLayout2.setOrientation(VERTICAL);

        }else {
            paramstxt = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            linearLayout2.setWeightSum(2);
            linearLayout2.setOrientation(LinearLayout.HORIZONTAL);
        }
        linearLayout2.setBackgroundColor(Color.TRANSPARENT);
        linearLayout2.setGravity(Gravity.BOTTOM);
        linearLayout1.setBackgroundColor(Color.TRANSPARENT);
        linearLayout1.setOrientation(VERTICAL);
        linearLayout1.setGravity(Gravity.TOP);

         LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
         parentLayout.setOrientation(VERTICAL);
         params.setMargins(15,15,15,15);
        if(StringIsEmpty(getIntent().getStringExtra("bg_color"))) {

           // shape.setStroke(3, borderColor);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                GradientDrawable shape = new GradientDrawable();
                shape.setShape(GradientDrawable.RECTANGLE);
                if(char_type.equals("full")) {
                    shape.setCornerRadii(new float[]{10, 10, 10, 10, 10, 10, 10, 10});
                }else if(char_type.equals("footer")){
                    shape.setCornerRadii(new float[]{10, 10, 10, 10, 0, 0, 0, 0});
                }else {
                    shape.setCornerRadii(new float[]{0, 0, 0, 0, 10, 10, 10, 10});
                }
                shape.setColor(Color.parseColor(getIntent().getStringExtra("bg_color")));
                parentLayout.setBackground(shape);
            }else {
                parentLayout.setBackgroundColor(Color.parseColor(getIntent().getStringExtra("bg_color")));

            }
        }

      //  Image img = new Image(/images/MyImage.png);
//        URL resource = MyJavaFile.class.getClassLoader()
//                .getResource("PackageB/PackageBa/PackageBaa/MyImage.png");

        if(StringIsEmpty(getIntent().getStringExtra("title"))) {
            TextView textView = new TextView(this);
            textView.setText(getIntent().getStringExtra("title"));
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(Color.parseColor("#FFFFFF"));
            textView.setBackgroundColor(Color.TRANSPARENT);
            linearLayout1.addView(textView, params);
        }

        if(StringIsEmpty(getIntent().getStringExtra("message"))) {
            TextView textView_msg = new TextView(this);
            textView_msg.setText(getIntent().getStringExtra("message"));
            textView_msg.setGravity(Gravity.CENTER);
            textView_msg.setTextColor(Color.parseColor("#FFFFFF"));
            textView_msg.setBackgroundColor(Color.TRANSPARENT);
            linearLayout1.addView(textView_msg, params);
        }

        if(getIntent().getStringExtra("imageUrl")!=null&&!getIntent().getStringExtra("imageUrl").isEmpty()){
            if(getIntent().getStringExtra("imageUrl").startsWith("https://")||getIntent().getStringExtra("imageUrl").startsWith("http://")) {
                ImageView mImage = new ImageView(this);
                if(char_type.equals("full")){
                    LinearLayout.LayoutParams img_params = new LinearLayout.LayoutParams(
                            400, 400);
                    img_params.gravity = Gravity.CENTER;
                    mImage.setImageBitmap(WAUtils.StringToBitMap(getIntent().getStringExtra("imageUrl")));
                    linearLayout1.addView(mImage, img_params);
                }else {
                    mImage.setImageBitmap(WAUtils.StringToBitMap(getIntent().getStringExtra("imageUrl")));
                    linearLayout1.addView(mImage, params);
                }


            }
        }

         if(char_type.equals("rating")) {

            RatingBar ratingBar = new RatingBar(this, null, android.R.attr.ratingBarStyle);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            ratingBar.setLayoutParams(layoutParams);
            ratingBar.setMax(5);
            ratingBar.setNumStars(5);
            ratingBar.setStepSize((float) 0.5);
            LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
            stars.getDrawable(0).setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_ATOP);
            stars.getDrawable(1).setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_ATOP);
            stars.getDrawable(2).setColorFilter(Color.parseColor("#d18811"), PorterDuff.Mode.SRC_ATOP);
            layoutParams.gravity = Gravity.CENTER;
            linearLayout1.addView(ratingBar, layoutParams);
         }

        if(StringIsEmpty(getIntent().getStringExtra("btn_1_name"))) {
            Button textView1 = new Button(this);
            textView1.setText(getIntent().getStringExtra("btn_1_name"));
            textView1.setId(5);
            textView1.setGravity(Gravity.CENTER);
            textView1.setTextColor(Color.parseColor("#000000"));
            textView1.setOnClickListener(this);
            if(StringIsEmpty(getIntent().getStringExtra("btn_1_color"))) {
                textView1.setBackgroundColor(Color.parseColor(getIntent().getStringExtra("btn_1_color")));
            }
           // LinearLayout.LayoutParams paramstxt = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            linearLayout2.addView(textView1, paramstxt);
        }

        if(StringIsEmpty(getIntent().getStringExtra("btn_2_name"))) {
            Button textView_msg1 = new Button(this);
            textView_msg1.setText(getIntent().getStringExtra("btn_2_name"));
            textView_msg1.setId(6);
            textView_msg1.setGravity(Gravity.CENTER);
            textView_msg1.setTextColor(Color.parseColor("#000000"));
            textView_msg1.setOnClickListener(this);
            if(StringIsEmpty(getIntent().getStringExtra("btn_2_color"))) {
                textView_msg1.setBackgroundColor(Color.parseColor(getIntent().getStringExtra("btn_2_color")));
            }
          //  LinearLayout.LayoutParams paramstxt1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            if(char_type.equals("full")){
                paramstxt.setMargins(15, 15, 0, 0);
            }else {
                paramstxt.setMargins(15, 0, 0, 0);
            }

            linearLayout2.addView(textView_msg1, paramstxt);
        }
        parentLayout.addView(linearLayout1, params);
        if(StringIsEmpty(getIntent().getStringExtra("btn_1_name"))||StringIsEmpty(getIntent().getStringExtra("btn_2_name"))){


            parentLayout.addView(linearLayout2, params);
        }
        relativeLayout.addView(parentLayout,rel_params);


        //Close button

//        int resourceID = this.getResources().getIdentifier("ic_close", "drawable",getPackageName());
//        ImageView closeImg = new ImageView(this);
//        closeImg.setImageResource(resourceID);
//        RelativeLayout.LayoutParams closeIvLp = new RelativeLayout
//                .LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
//                RelativeLayout.LayoutParams.WRAP_CONTENT);
//        // Position it at the top right corner
//        rel_params.addRule(RelativeLayout.ABOVE,relativeLayout.getId());
//        rel_params.addRule(RelativeLayout.END_OF, relativeLayout.getId());
//
//        relativeLayout1.addView(closeImg,closeIvLp);
       // relativeLayout.addView(relativeLayout1);

        setContentView(relativeLayout);
    }

    private boolean StringIsEmpty(String value){
        if(value!=null&&!value.isEmpty()){
            return true;
        }
        return false;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case 5:

                if(getIntent().getStringExtra("external_link")!=null&&!getIntent().getStringExtra("external_link").isEmpty()) {
                    if (getIntent().getStringExtra("external_link").startsWith("https://")||getIntent().getStringExtra("external_link").startsWith("http://")) {
                        System.out.println("Request_data load url http" + getIntent().getStringExtra("external_link"));
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse(getIntent().getStringExtra("external_link")));
                        startActivity(browserIntent);
                    }
                }else {
                    if (getIntent().getStringExtra("deep_link") != null && !getIntent().getStringExtra("deep_link").isEmpty()) {

                        if(getIntent().getStringExtra("deep_link").startsWith("https://")||getIntent().getStringExtra("deep_link").startsWith("http://")){
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse(getIntent().getStringExtra("deep_link")));
                            startActivity(browserIntent);

                        }else {

                            String resumeName = this.getPackageName()+"."+getIntent().getStringExtra("deep_link");
                            Intent resume = null;
                            try {
                                Class newClass = Class.forName(resumeName);
                                resume = new Intent(this, newClass);
                                System.out.println("Activity name!!! ...:" +isCallable(resume) + ".."+ newClass.getSimpleName() + "....."+resumeName);
                                if(isCallable(resume)){
                                    startActivity(resume);
                                }
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                                System.out.println("Activity name!!! ...: erorr" +e.getMessage() +"..."+e.toString() );

                            }
                        }

                    }
                }
                break;
            case 6:
                System.out.println("Request_data load url222" + "Clicked!!!!");
                finish();
                break;
        }
    }
    private boolean isCallable(Intent intent) {
        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        System.out.println("Activity name list.size()...:" + list.size());

        return list.size() > 0;
    }
    private int getScaledPixels(int raw) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                raw, getResources().getDisplayMetrics());
    }
}
