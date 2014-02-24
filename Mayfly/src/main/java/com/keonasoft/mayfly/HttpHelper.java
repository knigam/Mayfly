package com.keonasoft.mayfly;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by kushal on 2/23/14.
 */
public class HttpHelper {
    public static JSONObject jsonBuilder(Map<String, String> map){
        JSONObject json = new JSONObject();
        try {
            for (String key : map.keySet()){
                json.put(key, map.get(key));
            }
            return json;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static HttpResponse httpPost(String uri, JSONObject json){
        try {
            DefaultHttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(uri);
            httpPost.setEntity (new StringEntity(json.toString()));
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            return httpclient.execute(httpPost);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static HttpResponse httpGet(String uri){
        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(uri);
        try {
            return httpclient.execute(httpget);
        } catch (IOException e) {
            System.out.println("ERROR");
            e.printStackTrace();
        }
        return null;
    }
}
