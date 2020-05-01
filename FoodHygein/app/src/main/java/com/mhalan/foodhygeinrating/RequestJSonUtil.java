/*
 * Copyright (c) 2019. Mohammad Halan - Portfolio Assignment
 */

package com.mhalan.foodhygeinrating;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RequestJSonUtil {
    private final String urlAddress = "http://api.ratings.food.gov.uk/";
    private static RequestJSonUtil mRequestJSonUtil;
    private RequestQueue mRequestQueue;
    private Context mContext;

    private RequestJSonUtil(Context ctx) {
        mContext = ctx;
        mRequestQueue = makeRequestQueue();
    }

    public static synchronized RequestJSonUtil getInstance(Context context) {
        if (mRequestJSonUtil == null) {
            mRequestJSonUtil = new RequestJSonUtil(context);
        }
        return mRequestJSonUtil;
    }

    public void addJSonRequest(int requestMethod, String endpoint, final BuildSearch buildSearch,
                               Response.Listener<JSONObject> success, Response.ErrorListener error) {

        String reqAddress = this.urlAddress + endpoint;
        if (buildSearch != null) {
            reqAddress += buildSearch.buildSearchString();
        }
        Log.e("Request", reqAddress);
        Request<JSONObject> request = new JsonObjectRequest(requestMethod, reqAddress, null, success, error) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> hParam = new HashMap<>();
                hParam.put("x-api-version", "2");
                return hParam;
            }
        };
        this.mRequestQueue.add(request);
    }

    private RequestQueue makeRequestQueue() {
        if (mRequestQueue == null)
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        return mRequestQueue;
    }
}
