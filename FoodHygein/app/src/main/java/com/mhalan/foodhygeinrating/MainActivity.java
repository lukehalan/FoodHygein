/*
 * Copyright (c) 2019. Mohammad Halan - Portfolio Assignment
 */

package com.mhalan.foodhygeinrating;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.livinglifetechway.quickpermissions.annotations.OnPermissionsPermanentlyDenied;
import com.livinglifetechway.quickpermissions.annotations.OnShowRationale;
import com.livinglifetechway.quickpermissions.annotations.WithPermissions;
import com.livinglifetechway.quickpermissions.util.QuickPermissionsRequest;

public class MainActivity extends AppCompatActivity {

    public static final String BUILDSEARCH_KEY = "search_params";
    private boolean isAdvancedSearch = false;
    private boolean isFhis = false;
    private static final int FINE_LOCATION_PERMISSION = 5512;


    private BuildSearch buildSearch;
    private LocationListener locationListener;
    private Location currentLocation;

    private LocationManager locationManager;
    private EditText businessNameEditText;
    private EditText businessAddressEditText;
    private TextView radiusTextView;
    private Spinner authoritySpinner;
    private CheckBox businessTypeCheckBox;
    private CheckBox ratingCheckBox;
    private SeekBar radiusSeekBar;
    private Button advancedSearchButton;
    private Button localSearchButton;
    private View advancedSearchGroup;
    private Spinner typeSpinner;
    private Spinner regionSpinner;
    private RatingBar ratingBar;
    private ProgressDialog progressDialog;
    private RadioGroup ratingSchemeRadioGroup;
    private Spinner fhisRatingSpinner;
    private Spinner ratingOperatorSpinner;
    private CheckBox regionCheckBox;
    private CheckBox searchRadiusCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(getResources().getString(R.string.app_name));
        initialiseViews();

        loadSpinners();

        setCheckboxListeners();

        buildSearch = new BuildSearch();

        radiusSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                radiusTextView.setText(String.valueOf(i + 1));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        advancedSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isAdvancedSearch) {
                    advancedSearchButton.setText(getResources().getString(R.string.simpleSearch));
                    advancedSearchGroup.setVisibility(View.VISIBLE);

                    ratingBar.setVisibility(View.VISIBLE);
                    ratingOperatorSpinner.setVisibility(View.VISIBLE);

                    localSearchButton.setVisibility(View.GONE);
                } else {
                    advancedSearchButton.setText(getResources().getString(R.string.advancedSearch));
                    advancedSearchGroup.setVisibility(View.GONE);

                    findViewById(R.id.textView_authority).setVisibility(View.GONE);
                    regionSpinner.setSelection(0);
                    authoritySpinner.setVisibility(View.GONE);

                    fhisRatingSpinner.setVisibility(View.GONE);
                    ratingSchemeRadioGroup.check(R.id.radioButton_fhrs);
                    ratingBar.setVisibility(View.GONE);
                    ratingOperatorSpinner.setVisibility(View.GONE);
                    isFhis = false;

                    localSearchButton.setVisibility(View.VISIBLE);
                }
                isAdvancedSearch = !isAdvancedSearch;
            }
        });

        ratingSchemeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.radioButton_fhrs:
                        buildSearch.setSchemeTypeKey(null);
                        fhisRatingSpinner.setVisibility(View.GONE);
                        ratingBar.setVisibility(View.VISIBLE);
                        ratingOperatorSpinner.setVisibility(View.VISIBLE);
                        isFhis = false;
                        break;
                    case R.id.radioButton_fhis:
                        buildSearch.setSchemeTypeKey("FHIS");
                        fhisRatingSpinner.setVisibility(View.VISIBLE);
                        ratingBar.setVisibility(View.GONE);
                        ratingOperatorSpinner.setVisibility(View.GONE);
                        isFhis = true;
                        break;
                }
            }
        });

        regionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    findViewById(R.id.textView_authority).setVisibility(View.GONE);
                    authoritySpinner.setVisibility(View.GONE);
                    authoritySpinner.setSelection(0);
                    buildSearch.setLocalAuthorityId(null);
                } else {
                    authoritySpinner.setVisibility(View.VISIBLE);
                    findViewById(R.id.textView_authority).setVisibility(View.VISIBLE);
                    Region selectedItem = (Region) regionSpinner.getSelectedItem();
                    ArrayAdapter adapter = (ArrayAdapter) authoritySpinner.getAdapter();
                    adapter.clear();
                    adapter.addAll(DataManager.getInstance().getAuthoritiesForRegion(selectedItem.getShowName()));
                    adapter.notifyDataSetChanged();
                }
                authoritySpinner.setSelection(0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        findViewById(R.id.button_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSearchActivity();
            }
        });

        localSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doLocationBasedSearch();
            }
        });

        regionCheckBox.setChecked(!regionCheckBox.isChecked());
        searchRadiusCheckBox.setChecked(!searchRadiusCheckBox.isChecked());
        ratingCheckBox.setChecked(!ratingCheckBox.isChecked());
        businessTypeCheckBox.setChecked(!businessTypeCheckBox.isChecked());
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
            case R.id.favourite_menu_action:
                Intent intent = new Intent(this, FavouritesActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

  /*  @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.e("onRequestPermission", "onRequestPermissionsResult");

        switch (requestCode) {
            case FINE_LOCATION_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    retrieveCurrentLocation();
                }
        }
    }*/

    private void retrieveCurrentLocation() {
        Log.e("retrieveCurrentLocation", "retrieveCurrentLocation");
        if (locationManager == null) {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        }

        if (!isGpsActive()) {
            gotoGpsSettings();
            return;
        }
        getCurrentLocation();
    }

    private boolean isGpsActive() {
        boolean gpsActive = false;
        try {
            gpsActive = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            Toast.makeText(this, getResources().getString(R.string.gpsError), Toast.LENGTH_SHORT).show();
        }
        return gpsActive;
    }

    private void gotoGpsSettings() {
        new AlertDialog.Builder(this,R.style.MyDialogTheme)
                .setMessage(getResources().getString(R.string.gps_activation_message))
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
    }

    private void getCurrentLocation() {
       /* Log.e("getCurrentLocation","getCurrentLocation");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            Log.e("getCurrentLocation","inside if");
            requestLocationPermissions();
            return;
        }*/
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermissions();
            return;
        }
        currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (currentLocation != null && System.currentTimeMillis() - currentLocation.getTime() < 900000) {
            beginLocationBasedSearch();
            return;
        }
        startLocationListener();
    }

    private void beginLocationBasedSearch() {
        if (!isAdvancedSearch) {
            buildSearch = new BuildSearch();
            buildSearch.setMaxDistanceLimit("2");
        }
        buildSearch.setLongitude(String.valueOf(currentLocation.getLongitude()));
        buildSearch.setLatitude(String.valueOf(currentLocation.getLatitude()));
        Intent intent = new Intent(getApplicationContext(), ResultsActivity.class);
        intent.putExtra(BUILDSEARCH_KEY, buildSearch);
        buildSearch = new BuildSearch();
        startActivity(intent);
    }

    @SuppressLint("MissingPermission")
    private void startLocationListener() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.wait_for_location));
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                locationManager.removeUpdates(locationListener);
            }
        });
        progressDialog.show();
        if (locationListener == null) {
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    progressDialog.dismiss();
                    progressDialog = null;
                    currentLocation = location;
                    locationManager.removeUpdates(locationListener);
                    beginLocationBasedSearch();
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {
                }

                @Override
                public void onProviderEnabled(String s) {
                }

                @Override
                public void onProviderDisabled(String s) {

                }
            };
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    private void initialiseViews() {
        businessNameEditText = findViewById(R.id.editText_businessName);
        businessAddressEditText = findViewById(R.id.editText_businessAddress);
        radiusTextView = findViewById(R.id.textView_radius);
        radiusSeekBar = findViewById(R.id.seekBar_radius);
        advancedSearchButton = findViewById(R.id.button_advancedSearch);
        localSearchButton = findViewById(R.id.button_searchNearMe);
        ratingSchemeRadioGroup = findViewById(R.id.radioGroup_scheme);
        advancedSearchGroup = findViewById(R.id.group_advancedSearch);
        authoritySpinner = findViewById(R.id.spinner_authority);
        ratingBar = findViewById(R.id.ratingBar);
        ratingCheckBox = findViewById(R.id.checkBox_rating);
        regionCheckBox = findViewById(R.id.checkBox_region);
        searchRadiusCheckBox = findViewById(R.id.checkBox_searchRadius);
        fhisRatingSpinner = findViewById(R.id.spinner_fhisRating);
        ratingOperatorSpinner = findViewById(R.id.spinner_ratingOperator);
        typeSpinner = findViewById(R.id.spinner_businessType);
        regionSpinner = findViewById(R.id.spinner_region);
        businessTypeCheckBox = findViewById(R.id.checkBox_businessType);
    }

    private void loadSpinners() {
        typeSpinner.setAdapter(new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, DataManager.getInstance().getBusinessTypeList()));
        regionSpinner.setAdapter(new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, DataManager.getInstance().getRegionList()));
        authoritySpinner.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                DataManager
                        .getInstance()
                        .getAuthoritiesForRegion(
                                ((GeneralParameter) regionSpinner.getSelectedItem())
                                        .getShowName()
                        ))
        );
    }

    private void setCheckboxListeners() {
        businessTypeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                typeSpinner.setEnabled(b);
                buildSearch.setBusinessTypeId(null);
            }
        });
        ratingCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                ratingBar.setEnabled(b);
                fhisRatingSpinner.setEnabled(b);
                ratingOperatorSpinner.setEnabled(b);
                for (int i = 0; i < ratingSchemeRadioGroup.getChildCount(); i++) {
                    ratingSchemeRadioGroup.getChildAt(i).setEnabled(b);
                }
                buildSearch.setRatingKey(null);
            }
        });
        regionCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                regionSpinner.setEnabled(b);
                authoritySpinner.setEnabled(b);
                buildSearch.setCountryId(null);
                buildSearch.setLocalAuthorityId(null);
            }
        });
        searchRadiusCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                radiusSeekBar.setEnabled(b);
                buildSearch.setBusinessTypeId(null);
            }
        });
    }

    private void startSearchActivity() {
        Intent intent = new Intent(getApplicationContext(), ResultsActivity.class);
        if (!getParametersFromViews())
            return;
        intent.putExtra(BUILDSEARCH_KEY, buildSearch);
        buildSearch = new BuildSearch();
        startActivity(intent);
    }

    private boolean getParametersFromViews() {
        if (!isViewsValid())
            return false;
        buildSearch = new BuildSearch();
        buildSearch.setAddress(businessAddressEditText.getText().toString());
        buildSearch.setName(businessNameEditText.getText().toString());

        if (isAdvancedSearch) {
            if (businessTypeCheckBox.isChecked()) {
                buildSearch.setBusinessTypeId(((GeneralParameter) typeSpinner.getSelectedItem()).getId());
            }
            if (ratingCheckBox.isChecked()) {
                if (isFhis) {
                    buildSearch.setSchemeTypeKey("fhis");
                    buildSearch.setFhisRatingKey(fhisRatingSpinner.getSelectedItemPosition());
                } else {
                    buildSearch.setRatingOperator((ratingOperatorSpinner).getSelectedItem().toString());
                    buildSearch.setSchemeTypeKey(null);
                    buildSearch.setRatingKey(String.valueOf((int) ratingBar.getRating()));
                }
            }
            if (regionCheckBox.isChecked()) {
                if (regionSpinner.getSelectedItemPosition() != 0) {
                    buildSearch.setLocalAuthorityId(((GeneralParameter) authoritySpinner.getSelectedItem()).getId());
                }
            }
            if (searchRadiusCheckBox.isChecked()) {
                buildSearch.setMaxDistanceLimit(radiusTextView.getText().toString());
                doLocationBasedSearch();
                return false;
            }
        }
        return true;
    }

    private boolean isViewsValid() {
        boolean valid = true;
        String message = null;
        if (isAdvancedSearch) {
            if (!searchRadiusCheckBox.isChecked() && !regionCheckBox.isChecked() &&
                    !ratingCheckBox.isChecked() && !businessTypeCheckBox.isChecked() &&
                    isNoString(businessNameEditText.getText().toString()) &&  isNoString(businessAddressEditText.getText().toString())) {
                valid = false;
                message = getResources().getString(R.string.search_criteria_filter);
            }
        } else {
            if (businessAddressEditText.getText().toString().isEmpty() && businessNameEditText.getText().toString().isEmpty()) {
                valid = false;
                message = getResources().getString(R.string.empty_search_error);
            }
        }
        if (!valid) {
            new AlertDialog.Builder(this,R.style.MyDialogTheme)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .setMessage(message)
                    .show();
        }
        return valid;
    }

    private boolean isNoString(String s) {
        return s.isEmpty() && s.length() == 0;
    }

    public void requestLocationPermissions() {
        /*ActivityCompat.requestPermissions(MainActivity.this, new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_PERMISSION);*/
        methodWithPermissions();
    }

    private void doLocationBasedSearch() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermissions();
            return;
        }
        retrieveCurrentLocation();
    }

    @WithPermissions(
            permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},
            handlePermanentlyDenied = true
    )
    public void methodWithPermissions() {
        Toast.makeText(this, getResources().getString(R.string.permissionsGranted), Toast.LENGTH_LONG).show();
        retrieveCurrentLocation();
    }

    @OnPermissionsPermanentlyDenied
    public void whenPermissionsArePermanentlyDenied(final QuickPermissionsRequest arg) {
        new AlertDialog.Builder(this, R.style.MyDialogTheme)
                .setTitle(getResources().getString(R.string.permissionsPermanentlyDenied))
                .setMessage(getResources().getString(R.string.locationServiceRequired))
                .setPositiveButton(getResources().getString(R.string.action_settings), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        arg.openAppSettings();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.close), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        arg.cancel();
                    }
                })
                .show();

    }

    @OnShowRationale
    public void rationaleCallback(final QuickPermissionsRequest req) {
        new AlertDialog.Builder(this,R.style.MyDialogTheme)
                .setTitle(getResources().getString(R.string.permissionsDenied))
                .setMessage(getResources().getString(R.string.permissionsAsking))
                .setPositiveButton(getResources().getString(R.string.goAhead), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        req.proceed();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        req.cancel();
                    }
                })
                .setCancelable(false)
                .show();
    }

}
