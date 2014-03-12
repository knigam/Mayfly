package com.keonasoft.mayfly;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by kushal on 3/5/14.
 */
public class User {
    private static User ourInstance = new User();

    public static User getInstance() {
        return ourInstance;
    }

    private String email;
    private User() {
    }

    public String setEmail(String email){
        ourInstance.email = email;

        return ourInstance.email;
    }

    public String getEmail(){
        return ourInstance.email;
    }

    public void signOut(){
        ourInstance.email = null;
    }

}
