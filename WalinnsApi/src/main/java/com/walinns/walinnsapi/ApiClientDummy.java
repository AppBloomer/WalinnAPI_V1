package com.walinns.walinnsapi;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.Iterator;
import java.net.URL;


import javax.net.ssl.HttpsURLConnection;

import static com.walinns.walinnsapi.WalinnsAPIClient.flag_once;

public class ApiClientDummy {
    static WAPref sharedPref;
    private static final WALog logger = WALog.getLogger();
    public Context mContext;
    public String mUrl,mType;
    public JSONObject mjsonObject;
    static String URL="http://192.168.0.106:3000/";
    public ApiClientDummy(Context context){
        sharedPref=new WAPref(context);
    }
    public ApiClientDummy(Context context,String url,JSONObject jsonObject , String type){
        this.mContext=context;
        this.mUrl=url;
        this.mjsonObject=jsonObject;
        this.mType = type ;
        sharedPref = new WAPref(mContext);
        Connection();

    }

    private void Connection(){
        try{
            logger.e("WalinnsTrackerClient","Request_data"+ mjsonObject.toString());
            java.net.URL url = new URL(URL+mUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(30000 /* milliseconds */);
            conn.setConnectTimeout(30000 /* milliseconds */);
            conn.setRequestMethod("POST");
            conn.addRequestProperty("CONTENT_TYPE", "application/json");
            conn.addRequestProperty("Authorization", "b9d2e92935000ffd585cc3092f9b03cd");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(mjsonObject));

            writer.flush();
            writer.close();
            os.close();

            logger.e("Http_connection_request_data",sharedPref.getValue(WAPref.project_token));

            int responseCode=conn.getResponseCode();


            if (responseCode == HttpsURLConnection.HTTP_OK) {

                logger.e("WalinnsTrackerHttpConnection dummy", conn.getResponseMessage());

            }else {
                logger.e("WalinnsTrackerHttpConnection dummy","Fail");

            }
        }
        catch(Exception e){
            logger.e("WalinnsTrackerHttpConnection dummy","Exce"+e);

        }
    }
    private String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key= itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }




}
