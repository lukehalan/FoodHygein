/*
 * Copyright (c) 2019. Mohammad Halan - Portfolio Assignment
 */

package com.mhalan.foodhygeinrating;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.facebook.shimmer.ShimmerFrameLayout;



import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ResultsActivity extends AppCompatActivity implements MapEventsReceiver {

    public static final String ESTABLISHMENT_ID = "establishment_id";

    private ResultsAdapter resultsAdapter;
    private BuildSearch buildSearch;
    private boolean isLoading = false;
    private boolean endOfList = false;
    private boolean firstStart = true;

    private View listFooter;
    private ListView resultsListView;
    private ShimmerFrameLayout loadingView;
    private TextView resultsCountTextView;
    private Spinner sortingSpinner;
    private View topBannerGroup;
    private ArrayList<String> filterOptions;
    private BottomNavigationView bottomNav;

    private ConstraintLayout mainLayout;
    private View mapWrapper;
    private Button loadMoreButton;
    private boolean addToMap = false;
    private View mapProgressBar;
    private boolean mapMode = false;
    private boolean loadingError = false;
    private MapView mapView;
    IMapController mapController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        setTitle(getResources().getString(R.string.search_results));


        buildSearch = (BuildSearch) getIntent().getSerializableExtra(MainActivity.BUILDSEARCH_KEY);

        setActionBar();
        setViews();
        Configuration.getInstance().load(getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        mapView = (MapView) findViewById(R.id.map_osm_ra);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        mapController = mapView.getController();
        mapController.setZoom(12);
        mapView.setBuiltInZoomControls(false);

        List<Establishment> establishments = new ArrayList<>();
        filterOptions = new ArrayList<>();

        resultsAdapter = new ResultsAdapter(getApplicationContext(), R.layout.result_row, establishments);
        if (!buildSearch.isLocationUsed()) {
            resultsAdapter.setShowDistance(false);
        }
        resultsListView.setAdapter(resultsAdapter);
        resultsListView.addFooterView(listFooter);

        loadMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (endOfList || isLoading)
                    return;
                if (buildSearch != null) {
                    buildSearch.next();
                    addToMap = true;
                    requestEstablishments();
                }
            }
        });

        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.list_menu:
                        TransitionManager.beginDelayedTransition(mainLayout);
                        mapMode = false;
                        mapWrapper.setVisibility(View.GONE);
                        topBannerGroup.setVisibility(View.VISIBLE);
                        resultsListView.setVisibility(View.VISIBLE);
                        loadMoreButton.setVisibility(View.GONE);
                        break;
                    case R.id.map_menu:
                        TransitionManager.beginDelayedTransition(mainLayout);
                        mapMode = true;
                        mapWrapper.setVisibility(View.VISIBLE);
                        topBannerGroup.setVisibility(View.GONE);
                        resultsListView.setVisibility(View.GONE);
                        loadMoreButton.setVisibility(View.VISIBLE);
                        markEstablishmentsOnMap();
                        break;
                }
                return true;
            }
        });

        resultsListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                if (endOfList || isLoading || loadingError)
                    return;
                if (i + i1 >= i2) {
                    if (buildSearch != null) {
                        buildSearch.next();
                        requestEstablishments();
                    }
                }
            }
        });

        resultsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Establishment currentItem = resultsAdapter.getItem(i);
                if (currentItem != null) {
                    String id = currentItem.getFHRSID();
                    startDetailsActivity(id);
                }
            }
        });
        String[] filters = getResources().getStringArray(R.array.sort_option);
        filterOptions.addAll(Arrays.asList(filters));

        if (!buildSearch.isLocationUsed()) {
            filterOptions.remove(filterOptions.size() - 1);
        }
        sortingSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, filterOptions));
        sortingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                buildSearch.reset();
                buildSearch.setSortOptionKey(i);
                resultsAdapter.clear();
                endOfList = false;
                firstStart = true;
                resultsListView.setVisibility(View.GONE);
                loadingView.setVisibility(View.VISIBLE);
                loadingView.startShimmerAnimation();
                resultsCountTextView.setText("0");
                requestEstablishments();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        Log.e("back", "onSupportNavigateUp");
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 16908332:
                onBackPressed();
                break;
            case R.id.favourite_menu_action:
                Intent intent = new Intent(this, FavouritesActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }



    private void setViews() {
        sortingSpinner = findViewById(R.id.sort_spinner);
        topBannerGroup = findViewById(R.id.results_top_banner);
        resultsListView = findViewById(R.id.listView_results);
        resultsCountTextView = findViewById(R.id.results_count_tv);
        loadingView = findViewById(R.id.loading_view);
        bottomNav = findViewById(R.id.bottom_navigation);
        mainLayout = findViewById(R.id.result_layout);
        mapWrapper = findViewById(R.id.map_wrapper);
        loadMoreButton = findViewById(R.id.load_more_button);
        mapProgressBar = findViewById(R.id.map_progress);


        loadingView.startShimmerAnimation();
        listFooter = LayoutInflater.from(this).inflate(R.layout.list_footer, resultsListView, false);
    }

    private void setActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }

    private void requestEstablishments() {
        onLoadingStarted();
        RequestJSonUtil.getInstance(this).addJSonRequest(Request.Method.GET, "Establishments", buildSearch, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loadingError = false;
                handleResponse(response);
                onLoadingDone();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingError = true;
                findViewById(R.id.load_error_view).setVisibility(View.VISIBLE);
                onLoadingDone();
                Toast.makeText(ResultsActivity.this, getResources().getString(R.string.result_error), Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });
    }

    private void startDetailsActivity(String id) {
        Intent intent = new Intent(getApplicationContext(), EstablishmentDetailsActivity.class);
        intent.putExtra(ESTABLISHMENT_ID, id);
        startActivity(intent);
    }

    private void markEstablishmentsOnMap() {


        final Drawable drawable = getResources().getDrawable(R.drawable.marker_default);

        boolean firstItem = true;
        for (Establishment est : this.resultsAdapter.getItems()) {
            if (est.getGeocode() != null) {
                Log.e("MAPVIE FILLER",est.getBusinessName());
                GeoPoint center = new GeoPoint(est.getGeocode().getLatitude(), est.getGeocode().getLongitude());
                org.osmdroid.views.overlay.Marker marker = new org.osmdroid.views.overlay.Marker(mapView);
                marker.setPosition(center);
                marker.setIcon(drawable);
                marker.setAnchor(org.osmdroid.views.overlay.Marker.ANCHOR_CENTER, org.osmdroid.views.overlay.Marker.ANCHOR_BOTTOM);
             /*   marker.setTitle(est.getBusinessName());
                marker.setSnippet("Local Authority: " + est.getLocalAuthorityName());
                marker.setSubDescription("Hygiene Rate: " + est.getScores().getHygiene());*/
                marker.setInfoWindow(new MapInfoWindowAdapter(R.layout.info_window_bubble,mapView, est));
                if (firstItem) {
                    mapController.setCenter(center);
                    firstItem = false;
                }
                mapView.getOverlays().add(marker);
            }
        }
        mapView.invalidate();
        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(this, this);
        mapView.getOverlays().add(0, mapEventsOverlay);
    }

    private void onLoadingStarted() {
        isLoading = true;
        listFooter.setVisibility(View.VISIBLE);
        findViewById(R.id.load_error_view).setVisibility(View.GONE);
        bottomNav.setEnabled(false);
        if (mapMode) {
            mapProgressBar.setVisibility(View.VISIBLE);
        }
    }

    private void onLoadingDone() {
        isLoading = false;
        listFooter.setVisibility(View.GONE);
        bottomNav.setEnabled(false);
        bottomNav.setEnabled(false);
        if (mapMode) {
            mapProgressBar.setVisibility(View.GONE);
        }
        if (firstStart) {
            loadingView.setVisibility(View.GONE);
            loadingView.stopShimmerAnimation();
            resultsListView.setVisibility(View.VISIBLE);
            resultsListView.smoothScrollToPosition(0);
            firstStart = false;
            if (!resultsAdapter.isEmpty())
                topBannerGroup.setVisibility(View.VISIBLE);
        } else {
            topBannerGroup.setVisibility(View.VISIBLE);
        }
    }

    private void handleResponse(JSONObject response) {
        try {
            Gson gson = new Gson();
            JSONArray establishmentsArray = response.getJSONArray("establishments");
            if (establishmentsArray.length() < buildSearch.getPageSize()) {
                endOfList = true;
                resultsListView.removeFooterView(listFooter);
            }

            if (establishmentsArray.length() == 0) {
                findViewById(R.id.no_results_message).setVisibility(View.VISIBLE);
            }

            if (this.buildSearch.getPageNumber() == 1) {
                this.resultsAdapter.clear();
            }

            for (int i = 0; i < establishmentsArray.length(); i++) {
                JSONObject establishmentJson = establishmentsArray.getJSONObject(i);
                Establishment establishment = gson.fromJson(establishmentJson.toString(), Establishment.class);
                resultsAdapter.add(establishment);
            }
            resultsCountTextView.setText(String.valueOf(resultsAdapter.getCount()));
            resultsAdapter.notifyDataSetChanged();

            if (addToMap) {
                markEstablishmentsOnMap();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean singleTapConfirmedHelper(GeoPoint p) {
        InfoWindow.closeAllInfoWindowsOn(mapView);
        return false;
    }

    @Override
    public boolean longPressHelper(GeoPoint p) {
        return false;
    }

}
