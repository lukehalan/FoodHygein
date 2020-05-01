/*
 * Copyright (c) 2019. Mohammad Halan - Portfolio Assignment
 */

package com.mhalan.foodhygeinrating;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.livinglifetechway.quickpermissions.annotations.OnPermissionsPermanentlyDenied;
import com.livinglifetechway.quickpermissions.annotations.OnShowRationale;
import com.livinglifetechway.quickpermissions.annotations.WithPermissions;
import com.livinglifetechway.quickpermissions.util.QuickPermissionsRequest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhotoGalleryActivity extends AppCompatActivity implements ActionMode.Callback {


    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Bitmap mImageBitmap;
    private String mCurrentPhotoPath;
    private ImageView mImageView;
    private String FHRSID;
    private String establishmentName;

    PhotoGalleryAdapter mAdapter;
    RecyclerView mRecyclerView;
    ArrayList<PhotoModel> data = new ArrayList<>();
    private boolean isMultiSelect = false;
    private List<String> selectedIds = new ArrayList<>();
    private ActionMode actionMode;
    private boolean isBackButton = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_gallery);
        FHRSID = getIntent().getStringExtra("currentEstablishment");
        establishmentName = getIntent().getStringExtra("currentEstablishmentName");

        setTitle(establishmentName);
        isBackButton = false;
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        /*mRecyclerView = (RecyclerView) findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mRecyclerView.setHasFixedSize(true);*/

        if (hasPermission()) {

            onCreateHandler();

           /* data = readAllFileInFolder();
            for (PhotoModel model : data)
                Log.d("Files", "FileName:" + model.getPath());


            mRecyclerView = (RecyclerView) findViewById(R.id.list);
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
            mRecyclerView.setHasFixedSize(true);

            Log.d("mRecyclerView", "FileName:" + mRecyclerView.toString());


            mAdapter = new PhotoGalleryAdapter(PhotoGalleryActivity.this, data);
            mRecyclerView.setAdapter(mAdapter);


            mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, mRecyclerView,
                    new RecyclerItemClickListener.OnItemClickListener() {

                        @Override
                        public void onItemClick(View view, int position) {
                            if (isMultiSelect) {
                                multiSelect(position);
                            } else {
                                Intent intent = new Intent(getApplicationContext(), PhotoDetailActivity.class);
                                intent.putExtra("data", data);
                                intent.putExtra("pos", position);
                                intent.putExtra("FHRSID", FHRSID);
                                intent.putExtra("establishmentName", establishmentName);
                                startActivity(intent);
                            }

                        }

                        @Override
                        public void onItemLongClick(View view, int position) {
                            if (!isMultiSelect) {
                                selectedIds = new ArrayList<>();
                                isMultiSelect = true;
                                if (actionMode == null) {
                                    actionMode = startActionMode(PhotoGalleryActivity.this);
                                }
                            }
                            multiSelect(position);
                        }

                    }));*/
        }


        Button button = findViewById(R.id.photobutton);
        button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                if (hasPermission()) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException ex) {
                            Log.i("createImageFile", "IOException" + ex.getMessage());
                        }
                        if (photoFile != null) {
                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
                        }
                    }
                }
            }


        });

    }

    public void onCreateHandler() {
        data = readAllFileInFolder();
        for (PhotoModel model : data)
            Log.d("Files", "FileName:" + model.getPath());

            mRecyclerView = (RecyclerView) findViewById(R.id.list);
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
            mRecyclerView.setHasFixedSize(true);
            Log.d("mRecyclerView", "FileName:" + mRecyclerView.toString());
            mAdapter = new PhotoGalleryAdapter(PhotoGalleryActivity.this, data);
            mRecyclerView.setAdapter(mAdapter);

            mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, mRecyclerView,
                    new RecyclerItemClickListener.OnItemClickListener() {

                        @Override
                        public void onItemClick(View view, int position) {
                            if (isMultiSelect) {
                                multiSelect(position);
                            } else {
                                Intent intent = new Intent(getApplicationContext(), PhotoDetailActivity.class);
                                intent.putExtra("data", data);
                                intent.putExtra("pos", position);
                                intent.putExtra("FHRSID", FHRSID);
                                intent.putExtra("establishmentName", establishmentName);
                                startActivity(intent);
                            }

                        }

                        @Override
                        public void onItemLongClick(View view, int position) {
                            if (!isMultiSelect) {
                                selectedIds = new ArrayList<>();
                                isMultiSelect = true;
                                if (actionMode == null) {
                                    actionMode = startActionMode(PhotoGalleryActivity.this);
                                }
                            }
                            multiSelect(position);
                        }

                    }));


    }

    private void multiSelect(int position) {
        PhotoModel model = mAdapter.getItem(position);
        if (data != null) {
            if (actionMode != null) {
                if (selectedIds.contains(model.getName()))
                    selectedIds.remove(model.getName());
                else
                    selectedIds.add(model.getName());

                if (selectedIds.size() > 0)
                    actionMode.setTitle(String.valueOf(selectedIds.size()));
                else {
                    actionMode.setTitle("");
                    actionMode.finish();
                }
                mAdapter.setSelectedIds(selectedIds);

            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = FHRSID + "_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES + File.separator + "hygienerate" + File.separator + FHRSID);

        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        Log.e("storageDir", storageDir.getAbsolutePath());
        File image = File.createTempFile(
                imageFileName,  //
                ".jpg",         // suffix
                storageDir      // directory
        );
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_CANCELED) {
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
                try {
                    mImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(mCurrentPhotoPath));
                    //  mImageView.setImageBitmap(mImageBitmap);
                    updateRecycleView(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            //File storageDir = Environment.getExternalStoragePublicDirectory(mCurrentPhotoPath);
            File storageDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES + File.separator + "hygienerate" + File.separator + FHRSID);
            File file = new File(storageDir + mCurrentPhotoPath.substring(mCurrentPhotoPath.lastIndexOf("/"),mCurrentPhotoPath.length()));
            file.delete();
            updateRecycleView(true);
        }
    }

    /*private boolean checkAndRequestPermissions() {
        int READ_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        int WRITE_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int CAMERA = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (READ_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (WRITE_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (CAMERA != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }*/

   /* @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.d("Permission", "Permission callback called-------");
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);

                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    if (perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        Log.d("Permission", "Storage and Camera services permission granted");
                    } else {
                        Log.d("Permission", "Some permissions are not granted ask again ");
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE) ||
                                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                            showDialogOK(getResources().getString(R.string.permissonneedsforcamera),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkAndRequestPermissions();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    break;
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(this, getResources().getString(R.string.gotoSettingForPermisson), Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                }
            }
        }

    }*/

   /* private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this, R.style.MyDialogTheme)
                .setMessage(message)
                .setPositiveButton(getResources().getString(R.string.ok), okListener)
                .setNegativeButton(getResources().getString(R.string.cancel), okListener)
                .create()
                .show();
    }*/

    private ArrayList<PhotoModel> readAllFileInFolder() {
        ArrayList<PhotoModel> imageList = new ArrayList<>();
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES + File.separator + "hygienerate" + File.separator + FHRSID);
        Log.d("Files", "Path: " + storageDir.getAbsolutePath());
        if (storageDir.exists()) {
            File[] files = storageDir.listFiles();
            Log.d("Files", "Size: " + files.length);
            for (int i = 0; i < files.length; i++) {
                Log.d("Files", "FileName:" + files[i].getName());
                imageList.add(new PhotoModel(files[i].getName(), files[i].getAbsolutePath()));
            }
        }
        return imageList;
    }

    private void updateRecycleView(boolean run) {
        if (mRecyclerView != null) {
            data = readAllFileInFolder();
            mAdapter = new PhotoGalleryAdapter(PhotoGalleryActivity.this, data);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.invalidate();
        }else{
            onCreateHandler();
        }

    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.photo_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                StringBuilder stringBuilder = new StringBuilder();
                List<String> deleteList = new ArrayList<>();
                for (PhotoModel model : this.data) {
                    if (selectedIds.contains(model.getName())) {
                        stringBuilder.append("\n").append(model.getName());
                        deleteList.add(model.getName());
                    }
                }
                if (deleteSelectedFiles(deleteList))
                    Toast.makeText(this, getResources().getString(R.string.deletedSuccessfully)/* + stringBuilder.toString()*/, Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, getResources().getString(R.string.deletedError)/* + stringBuilder.toString()*/, Toast.LENGTH_LONG).show();
                updateRecycleView(true);
                actionMode.finish();
                return true;
            case R.id.action_share:
                sharePhoto();
                return true;
        }
        return false;
    }

    public void sharePhoto() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.setType("image/*");
        ArrayList<Uri> files = new ArrayList<>();
        for (PhotoModel model : this.data) {
            if (selectedIds.contains(model.getName())) {
                File file = new File(model.getPath());
                Uri uri = Uri.fromFile(file);
                files.add(uri);
            }
        }
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
        startActivity(Intent.createChooser(intent, getResources().getString(R.string.shareImageUsing)));
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        actionMode = null;
        isMultiSelect = false;
        selectedIds = new ArrayList<>();
        mAdapter.setSelectedIds(new ArrayList<String>());
    }

    private boolean deleteSelectedFiles(List<String> deleteList) {
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES + File.separator + "hygienerate" + File.separator + FHRSID);
        if (storageDir.exists()) {
            for (String name : deleteList) {
                try {
                    File file = new File(storageDir + File.separator + name);
                    Log.d("Delete File: ", file.getAbsolutePath());
                    file.delete();
                } catch (Exception e) {
                    Log.e("App", "Exception while deleting file " + e.getMessage());
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void onResume() {
        updateRecycleView(true);
        super.onResume();
    }

    @Override
    protected void onRestart() {
        //updateRecycleView(true);
        // onCreateHandler();
        super.onRestart();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /*@Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        Intent intent = new Intent(PhotoGalleryActivity.this,EstablishmentDetailsActivity.class);
        intent.putExtra("establishment_id", FHRSID);
        startActivity(intent);
    }*/

    @WithPermissions(
            permissions = {Manifest.permission.CAMERA},
            handlePermanentlyDenied = true
    )
    public void requestPermissions() {
        Toast.makeText(this, getResources().getString(R.string.permissionsGranted), Toast.LENGTH_LONG).show();
        updateRecycleView(true);
        //onCreateHandler();
        // startActivity(getIntent());
    }

    @OnPermissionsPermanentlyDenied
    public void whenPermissionsArePermanentlyDenied(final QuickPermissionsRequest arg) {
        new AlertDialog.Builder(this, R.style.MyDialogTheme)
                .setTitle(getResources().getString(R.string.permissionsDenied))
                .setMessage(getResources().getString(R.string.permissionsAsking))
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
        new AlertDialog.Builder(this, R.style.MyDialogTheme)
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

    private boolean hasPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                /*|| ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED*/) {
            requestPermissions();
        } else {
            return true;
        }
        return false;
    }
}
