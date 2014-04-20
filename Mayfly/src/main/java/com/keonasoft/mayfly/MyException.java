package com.keonasoft.mayfly;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by kushal on 4/19/14.
 */
public class MyException extends RuntimeException{
    //TODO
    public MyException(Context context, Exception e){
        super();
        e.printStackTrace();
        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
    }
}
