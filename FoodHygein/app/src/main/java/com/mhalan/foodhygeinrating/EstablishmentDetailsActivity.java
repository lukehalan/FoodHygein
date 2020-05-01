/*
 * Copyright (c) 2019. Mohammad Halan - Portfolio Assignment
 */

package com.mhalan.foodhygeinrating;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;

import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;



public class EstablishmentDetailsActivity extends AppCompatActivity {

    private Establishment currentItem;
    private boolean locationAdded = false;
    private ShimmerFrameLayout loadingView;
    private View errorView;
    private ImageView galleryLogo;
    private ImageView favouriteStar;
    private boolean galleryLoaded = false;
    MapView map = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_establishment_details);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        favouriteStar = findViewById(R.id.favourite_star);
        galleryLogo = findViewById(R.id.gallery_logo);


        loadingView = findViewById(R.id.loading_view);
        loadingView.startShimmerAnimation();

        errorView = findViewById(R.id.no_results_message);

        RequestJSonUtil requestJSonUtil = RequestJSonUtil.getInstance(getApplicationContext());

        String id = getIntent().getStringExtra(ResultsActivity.ESTABLISHMENT_ID);
        requestJSonUtil.addJSonRequest(Request.Method.GET, "establishments/" + id, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response != null) {
                    try {
                        Gson gson = new Gson();
                        currentItem = gson.fromJson(response.toString(), Establishment.class);
                        if (currentItem != null) {
                            setTitle(currentItem.getBusinessName());
                            if (DataManager.getInstance().getFavouriteIdList().indexOf(currentItem.getFHRSID()) != -1) {
                                galleryLoaded = true;
                                Log.e("galleryLoaded","galleryLoaded in fisr");
                            }
                            fillViews(currentItem);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                loadingView.stopShimmerAnimation();
                TransitionManager.beginDelayedTransition((ConstraintLayout) findViewById(R.id.main_details_layout));
                findViewById(R.id.details_scroll_view).setVisibility(View.VISIBLE);
                loadingView.setVisibility(View.GONE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                loadingView.stopShimmerAnimation();
                TransitionManager.beginDelayedTransition((ConstraintLayout) findViewById(R.id.main_details_layout));
                loadingView.setVisibility(View.GONE);
                errorView.setVisibility(View.VISIBLE);
                Toast.makeText(EstablishmentDetailsActivity.this, getResources().getString(R.string.load_error_details), Toast.LENGTH_SHORT).show();
            }
        });


       // readAllFileInFolder(currentItem.getFHRSID());

        favouriteStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (DataManager.getInstance().getFavouriteIdList().indexOf(currentItem.getFHRSID()) == -1) {
                    Database.getInstance().getDb(getApplicationContext()).iEstablishmentDao().insert(currentItem);
                    DataManager.getInstance().addFavouriteId(currentItem.getFHRSID());
                    ((ImageView) view).setImageResource(R.drawable.star_on);
                    galleryLogo.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.favourite_add_msg), Toast.LENGTH_SHORT).show();
                } else {
                    Database.getInstance().getDb(getApplicationContext()).iEstablishmentDao().delete(currentItem);
                    ((ImageView) view).setImageResource(R.drawable.star_off);
                    DataManager.getInstance().removeFavouriteId(currentItem.getFHRSID());
                    galleryLogo.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.favourite_remove_msg), Toast.LENGTH_SHORT).show();

                }
            }
        });


            galleryLogo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  //  Toast.makeText(getApplicationContext(), "Gallery is clicked", Toast.LENGTH_SHORT).show();
                     Intent intent = new Intent(getApplicationContext(), PhotoGalleryActivity.class);
                     intent.putExtra("currentEstablishment", currentItem.getFHRSID());
                     intent.putExtra("currentEstablishmentName", currentItem.getBusinessName());
                     startActivity(intent);
                }
            });



        Configuration.getInstance().load(getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        map = (MapView) findViewById(R.id.map_osm);
//        addLocationOnMap(currentItem);

    }

    private void fillViews(Establishment currentItem) {
        ((TextView) findViewById(R.id.business_name_tv)).setText(currentItem.getBusinessName());
        ((TextView) findViewById(R.id.business_type_tv)).setText(currentItem.getBusinessType());
        ((TextView) findViewById(R.id.address_first_line_tv)).setText(currentItem.getAddressFirstLine());
        ((TextView) findViewById(R.id.address_second_line_tv)).setText(currentItem.getAddressSecondLine());
        ((TextView) findViewById(R.id.post_code_tv)).setText(currentItem.getPostCode());
        ((TextView) findViewById(R.id.auth_name_tv)).setText(currentItem.getLocalAuthorityName());
        ((TextView) findViewById(R.id.auth_email_tv)).setText(currentItem.getLocalAuthorityEmailAddress());
        ((TextView) findViewById(R.id.auth_website_tv)).setText(currentItem.getLocalAuthorityWebSite());

        if (currentItem.getSchemeType().equals("FHRS")) {
            findViewById(R.id.scores_card_view).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.hygiene_score_label)).setText(String.valueOf(currentItem.getScores().getHygiene()));
            ((TextView) findViewById(R.id.confidence_score_label)).setText(String.valueOf(currentItem.getScores().getConfidenceInManagement()));
            ((TextView) findViewById(R.id.structural_score_label)).setText(String.valueOf(currentItem.getScores().getStructural()));
        }
        try {

            String dateText = Convert.dateToString(currentItem.getRatingDate(), "yyyy-MM-dd'T'hh:mm:ss", "dd.MM.yyyy");
            ((TextView) findViewById(R.id.rating_date_text_view)).setText(dateText);
        } catch (Exception ex) {
            findViewById(R.id.textView23).setVisibility(View.GONE);
            findViewById(R.id.rating_date_text_view).setVisibility(View.GONE);
        }

        ((TextView) findViewById(R.id.rating_scheme_label)).setText(currentItem.getSchemeType());
        if (currentItem.getSchemeType().equals("FHRS")) {
            try {
                int score = Integer.parseInt(currentItem.getRatingValue());
                ((ImageView) findViewById(R.id.rating_image_view)).setImageResource(getRatingImage(score));
            } catch (NumberFormatException ex) {
            }
        } else {
            findViewById(R.id.rating_image_view).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.fhis_rating_label)).setText(currentItem.getRatingValue());
            findViewById(R.id.fhis_rating_label).setVisibility(View.VISIBLE);
        }

        if (DataManager.getInstance().getFavouriteIdList().indexOf(currentItem.getFHRSID()) == -1) {
            favouriteStar.setImageResource(R.drawable.star_off);
            galleryLogo.setVisibility(View.INVISIBLE);
        } else {
            favouriteStar.setImageResource(R.drawable.star_on);
            galleryLogo.setVisibility(View.VISIBLE);
        }

        if (currentItem.hasGeocode()) {
            addLocationOnMap(currentItem);
        } else {
            findViewById(R.id.map_card_view).setVisibility(View.GONE);
        }
    }

    private int getRatingImage(int score) {
        switch (score) {
            case 0:
                return R.drawable.rating_0;
            case 1:
                return R.drawable.rating_1;
            case 2:
                return R.drawable.rating_2;
            case 3:
                return R.drawable.rating_3;
            case 4:
                return R.drawable.rating_4;
            case 5:
                return R.drawable.rating_5;
        }
        return 0;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    private void addLocationOnMap(Establishment esta) {

        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        IMapController mapController = map.getController();
        mapController.setZoom(14);
        GeoPoint startPoint = new GeoPoint(esta.getGeocode().getLatitude(), esta.getGeocode().getLongitude());
        mapController.setCenter(startPoint);
        addMarker(startPoint);
    }


    public void addMarker(GeoPoint center){
        Marker marker = new Marker(map);
        marker.setPosition(center);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().clear();
        map.getOverlays().add(marker);
        map.invalidate();

    }

   /* private void readAllFileInFolder(String fhrsid) {
        Random rand = new Random();
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES + File.separator + "hygienerate" + File.separator + fhrsid);
        Log.d("Files", "Path: " + storageDir.getAbsolutePath());
        if (storageDir.exists()) {
            File[] files = storageDir.listFiles();
            Log.d("Files", "Size: " + files.length);
            if (files.length > 0){
                int n = rand.nextInt(files.length);
                Bitmap bmp = BitmapFactory.decodeFile(files[n].getAbsolutePath());
                ((ImageView)findViewById(R.id.textView5)).setImageBitmap(bmp);
            }
        }
    }*/
}
