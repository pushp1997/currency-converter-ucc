package com.example.currencyconverter;

import static com.android.volley.toolbox.Volley.newRequestQueue;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;

public class ApiRequestQueueSingleton {
    private static ApiRequestQueueSingleton instance;
    private RequestQueue requestQueue;
    private static Context ctx;

    private ApiRequestQueueSingleton(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized ApiRequestQueueSingleton getInstance(Context context) {
        if (instance == null) {
            instance = new ApiRequestQueueSingleton(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}