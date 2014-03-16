package com.keonasoft.mayfly;

import android.app.Activity;
import android.app.Application;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.PersistentCookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by kushal on 2/23/14.
 */
public class HttpHelper{
    private static Object mLock = new Object();
    private static PersistentCookieStore mCookie = new PersistentCookieStore(;
    private static final String COOKIE_STORE = "cookie_store";

    /**
     * Creates a JSON Object based on String key/value mappings
     * @param map
     * @return
     */
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

    /**
     * sends a JSON Object to a given uri via HTTP POST and converts the response into a JSON
     * @param uri
     * @param json
     * @return
     */
    public static JSONObject httpPost(String uri, JSONObject json){
        try {
            HttpClient httpclient = getHttpClient();
            HttpPost httpPost = new HttpPost(uri);
            httpPost.setEntity (new StringEntity(json.toString()));
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            return httpToJson(httpclient.execute(httpPost));

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject httpDelete(String uri){
        HttpClient httpClient = getHttpClient();
        HttpDelete httpDelete = new HttpDelete(uri);
        try{
            return httpToJson(httpClient.execute(httpDelete));
        } catch (IOException e){
            System.out.println("ERROR");
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject httpGet(String uri){
        HttpClient httpclient = getHttpClient();
        HttpGet httpget = new HttpGet(uri);
        try {
            return httpToJson(httpclient.execute(httpget));
        } catch (IOException e) {
            System.out.println("ERROR");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Attempts to convert an HTTP Response into a string
     * and then create and return a JSON Object from that string
     * @param response
     * @return
     */
    public static JSONObject httpToJson(HttpResponse response){
        try {
            String json = EntityUtils.toString(response.getEntity());
            System.out.println(json);
            return new JSONObject(json);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static HttpClient getHttpClient() {
        final DefaultHttpClient httpClient = new DefaultHttpClient();
        synchronized (mLock) {
            if (mCookie == null) {
                mCookie = httpClient.getCookieStore();
            } else {
                httpClient.setCookieStore(mCookie);
            }
        }
        return httpClient;
    }

    public static void storeCookies(Activity activity){
//        final SharedPreferences prefs = activity.getPreferences(activity.MODE_PRIVATE);
//        SharedPreferences.Editor editor = prefs.edit();
//        editor.putString(COOKIE_STORE, mCookie);
//        editor.commit();
    }
}
