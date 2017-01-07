package com.application.upapplication.Controller;

import android.app.Application;
import android.support.multidex.MultiDex;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by user on 12/14/2016.
 */

public class VolleyApplication extends Application {
public static RequestQueue queues;
    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        queues = Volley.newRequestQueue(getApplicationContext());
    }

    public static RequestQueue getHttpQueues(){
        return queues;
    }
}
