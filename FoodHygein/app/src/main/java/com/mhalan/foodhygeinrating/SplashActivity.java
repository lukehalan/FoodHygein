/*
 * Copyright (c) 2019. Mohammad Halan - Portfolio Assignment
 */

package com.mhalan.foodhygeinrating;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

public class SplashActivity extends AppCompatActivity {

    private final int AUTHORITIES_KEY = 1;
    private final int REGIONS_KEY = 2;
    private final int BUSINESS_TYPES_KEY = 3;

    private boolean isLocalAuthoritiesLoaded = false;
    private boolean isBusinessTypesLoaded = false;
    private boolean idRegionsLoaded = false;

    private ShimmerFrameLayout shimmerFrameLayout;
    private Gson gson;
    private DataManager dataManager;
    private RequestJSonUtil requestJSonUtil;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        shimmerFrameLayout = findViewById(R.id.please_wait_view);
        shimmerFrameLayout.startShimmerAnimation();

        dataManager = DataManager.getInstance();
        requestJSonUtil = requestJSonUtil.getInstance(getApplicationContext());
        gson = new Gson();

        getLocalAuthorities();
        getFavourites();
        getRegions();
        getBusinessTypes();

    }

    private void loadMainActivity() {
        if (isLocalAuthoritiesLoaded && idRegionsLoaded && isBusinessTypesLoaded) {
            shimmerFrameLayout.stopShimmerAnimation();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    private void getFavourites() {
        dataManager.setFavouriteIdList(Database.getInstance()
                .getDb(this).iEstablishmentDao().selectIds());
        loadMainActivity();
    }

    private void getLocalAuthorities() {
        requestJSonUtil.addJSonRequest(Request.Method.GET, "/authorities", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // parseAuthorityResponse(response);
                pareseRespnses(response, AUTHORITIES_KEY);
                isLocalAuthoritiesLoaded = true;
                loadMainActivity();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("authorities onEr: ", error.toString());
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.local_authorities_fail_to_load), Toast.LENGTH_SHORT).show();
                isLocalAuthoritiesLoaded = true;
                loadMainActivity();
            }
        });
    }

    private void getBusinessTypes() {
        requestJSonUtil.addJSonRequest(Request.Method.GET, "/businesstypes", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // parseBusinessType(response);
                pareseRespnses(response, BUSINESS_TYPES_KEY);
                isBusinessTypesLoaded = true;
                loadMainActivity();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("businesstypes onEr: ", error.toString());
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.business_types_fail_to_load), Toast.LENGTH_SHORT).show();
                isBusinessTypesLoaded = true;
                loadMainActivity();
            }
        });
    }

    private void getRegions() {
        requestJSonUtil.addJSonRequest(Request.Method.GET, "/regions", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // parseRegionsResponse(response);
                pareseRespnses(response, REGIONS_KEY);
                idRegionsLoaded = true;
                loadMainActivity();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.regions_fail_to_load), Toast.LENGTH_SHORT).show();
                idRegionsLoaded = true;
                loadMainActivity();
            }
        });
    }




    private void pareseRespnses(JSONObject response, int type) {

        if (type == AUTHORITIES_KEY) {
            try {
                JSONArray jsonArray = response.getJSONArray("authorities");
                for (int i = 0; i < jsonArray.length(); i++) {
                    LocalAuthority authority = gson.fromJson(jsonArray.getJSONObject(i).toString(), LocalAuthority.class);
                    dataManager.addLocalAuthority(authority);
                }
            } catch (Exception ex) {
                Toast.makeText(this, getResources().getString(R.string.local_authorities_fail_to_parse), Toast.LENGTH_SHORT).show();
            }
        }

        if (type == REGIONS_KEY) {
            try {
                JSONArray jsonArray = response.getJSONArray("regions");
                for (int i = 0; i < jsonArray.length(); i++) {
                    Region region = gson.fromJson(jsonArray.getJSONObject(i).toString(), Region.class);
                    dataManager.addRegion(region);
                }
            } catch (Exception ex) {
                Toast.makeText(this, getResources().getString(R.string.regions_fail_to_parse), Toast.LENGTH_SHORT).show();
            }
        }

        if (type == BUSINESS_TYPES_KEY) {
            try {
                JSONArray jsonArray = response.getJSONArray("businessTypes");
                for (int i = 0; i < jsonArray.length(); i++) {
                    BusinessType businessType = gson.fromJson(jsonArray.getJSONObject(i).toString(), BusinessType.class);
                    dataManager.addBusinessType(businessType);
                }
            } catch (Exception ex) {
                Toast.makeText(this, getResources().getString(R.string.business_type_fail_to_parse), Toast.LENGTH_SHORT).show();
            }
        }
    }


}
