package com.walinns.walinnsapi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

/**
 * Created by walinnsinnovation on 30/12/17.
 */

public class WAGesture {
    int x =0,y=0,count=0;
    WAPref waPref;
    JSONObject hashMap;
    Activity activityy;
    public WAGesture(WalinnsAPIClient mWalinnsTrackerClient, Activity parent) {
        this.trackGestures(mWalinnsTrackerClient, parent);
    }
    private void trackGestures(WalinnsAPIClient mWalinnsTrackerClient, Activity parent) {
        parent.getWindow().getDecorView().setOnTouchListener(this.getGestureTrackerTouchListener(mWalinnsTrackerClient,parent));
        System.out.println("Track screen capture:" + parent.getClass().getSimpleName());
        waPref = new WAPref(parent);


    }
    private View.OnTouchListener getGestureTrackerTouchListener(final WalinnsAPIClient mWalinnsTrackerClient, final Activity activity) {
        return new View.OnTouchListener() {
            private long mSecondFingerTimeDown = -1L;
            private long mFirstToSecondFingerDifference = -1L;
            private int mGestureSteps = 0;
            private long mTimePassedBetweenTaps = -1L;
            private boolean mDidTapDownBothFingers = false;
            private final int TIME_BETWEEN_FINGERS_THRESHOLD = 100;
            private final int TIME_BETWEEN_TAPS_THRESHOLD = 1000;
            private final int TIME_FOR_LONG_TAP = 2500;
            int x =0,y=0,count =0;

            @SuppressLint("LongLogTag")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                System.out.println("WalinnsTrackerClient WAClient Lifecycle in gasture:" + "Called");
                final int action = event.getAction();

//                if(event.getPointerCount() > 2) {
//                    this.resetGesture();
//                    return false;
//                } else {
//                    switch(event.getActionMasked()) {
//                        case 0:
//                            this.mFirstToSecondFingerDifference = System.currentTimeMillis();
//                            break;
//                        case 1:
//                            if(System.currentTimeMillis() - this.mFirstToSecondFingerDifference < 100L) {
//                                if(System.currentTimeMillis() - this.mSecondFingerTimeDown >= 2500L) {
//                                    if(this.mGestureSteps == 3) {
//                                        mWalinnsTrackerClient.track_("$ab_gesture1");
//                                        this.resetGesture();
//                                    }
//
//                                    this.mGestureSteps = 0;
//                                } else {
//                                    this.mTimePassedBetweenTaps = System.currentTimeMillis();
//                                    if(this.mGestureSteps < 4) {
//                                        ++this.mGestureSteps;
//                                    } else if(this.mGestureSteps == 4) {
//                                        mWalinnsTrackerClient.track_("$ab_gesture2");
//                                        this.resetGesture();
//                                    } else {
//                                        this.resetGesture();
//                                    }
//                                }
//                            }
//                        case 2:
//                        case 3:
//                        case 4:
//                        default:
//                            break;
//                        case 5:
//                            if(System.currentTimeMillis() - this.mFirstToSecondFingerDifference < 100L) {
//                                if(System.currentTimeMillis() - this.mTimePassedBetweenTaps > 1000L) {
//                                    this.resetGesture();
//                                }
//
//                                this.mSecondFingerTimeDown = System.currentTimeMillis();
//                                this.mDidTapDownBothFingers = true;
//                            } else {
//                                this.resetGesture();
//                            }
//                            break;
//                        case 6:
//                            if(this.mDidTapDownBothFingers) {
//                                this.mFirstToSecondFingerDifference = System.currentTimeMillis();
//                            } else {
//                                this.resetGesture();
//                            }
//                    }
//
//                    return false;
//                }

                if(action== event.ACTION_DOWN ){
                    String img_byte = takeScreenshot(activity.getWindow().getDecorView().getRootView());
                     activityy = activity;
                     Log.e("Action Down:", String.valueOf(event.getX()));
                    x = (int) event.getX();
                    y = (int) event.getY();

                    String deviceId=waPref.getValue(WAPref.device_id);
                    hashMap= new JSONObject();
                    try {

                        hashMap.put("device_id",deviceId);
                        hashMap.put("screen_name",activity.getClass().getSimpleName());
                        hashMap.put("x_pos",x);
                        hashMap.put("y_pos",y);
                        hashMap.put("date_time",WAUtils.getCurrentUTC());
                        hashMap.put("img_data",img_byte);

                        System.out.println("Request_data heatmap :" + hashMap.toString());

                        new ExcuteThread().execute();



                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                return true;
             }


            private void resetGesture() {
                this.mFirstToSecondFingerDifference = -1L;
                this.mSecondFingerTimeDown = -1L;
                this.mGestureSteps = 0;
                this.mTimePassedBetweenTaps = -1L;
                this.mDidTapDownBothFingers = false;
            }
        };
    }
    public String takeScreenshot(View view){
        Bitmap bitmap = screenShot(view); // Get the bitmap
            // Save it to the external storage device.
        ByteBuffer byteBuffer = ByteBuffer.allocate(bitmap.getByteCount());
        bitmap.copyPixelsToBuffer(byteBuffer);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return imageString;

    }


    public Bitmap screenShot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    class ExcuteThread extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            System.out.println("WalinnsTrackerHttpConnection request "+ hashMap.toString());
            new ApiClientDummy(activityy, "heatmap", hashMap , "heatmap");
            return null;
        }
    }

}
