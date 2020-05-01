/*
 * Copyright (c) 2019. Mohammad Halan - Portfolio Assignment
 */

package com.mhalan.foodhygeinrating;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.util.ArrayList;


public class PhotoDetailActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    public ArrayList<PhotoModel> data = new ArrayList<>();
    int pos;
    int currentPos;
    String FHRSID;
    String establishmentName;
    private ViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());


        data = (ArrayList<PhotoModel>) getIntent().getSerializableExtra("data");
        pos = getIntent().getIntExtra("pos", 0);
        FHRSID = getIntent().getStringExtra("FHRSID");
        establishmentName = getIntent().getStringExtra("establishmentName");

        setTitle(data.get(pos).getName());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), data);

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setPageTransformer(true, new DepthPageTransformer());

        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(pos);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setTitle(data.get(position).getName());
                currentPos = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.photo_menu, menu);
       /* this.invalidateOptionsMenu();
        MenuItem item = menu.findItem(R.id.action_delete);
        item.setVisible(false);*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_delete:
                if (deleteSelectedFile())
                    Toast.makeText(this, getResources().getString(R.string.deletedSuccessfully) /* + stringBuilder.toString()*/, Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, getResources().getString(R.string.deletedError) /* + stringBuilder.toString()*/, Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_share:
                sharePhoto();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void sharePhoto(){
        final Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        final File photoFile = new File(data.get(currentPos).getPath());
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(photoFile));
        startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.shareImageUsing)));
    }

    private boolean deleteSelectedFile() {
        try {
            File file = new File(data.get(currentPos).getPath());
            Log.d("Delete File: ", file.getAbsolutePath());
            file.delete();
            Log.e("Deleted File: " , data.get(currentPos).getPath());
            Toast.makeText(this, getResources().getString(R.string.deletedSuccessfully)/* + stringBuilder.toString()*/, Toast.LENGTH_SHORT).show();
            finish();
           /* Intent intent = new Intent(this,PhotoGalleryActivity.class);
            intent.putExtra("currentEstablishment",FHRSID);
            intent.putExtra("currentEstablishmentName",establishmentName);
            startActivity(intent);*/

        } catch (Exception e) {
            Log.e("App", "Exception while deleting file " + e.getMessage());
            return false;
        }

        return true;
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        public ArrayList<PhotoModel> data = new ArrayList<>();

        public SectionsPagerAdapter(FragmentManager fm, ArrayList<PhotoModel> data) {
            super(fm);
            this.data = data;
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position, data.get(position).getName(), data.get(position).getPath());
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return data.get(position).getName();
        }
    }

    public static class PlaceholderFragment extends Fragment {

        String name, url;
        int pos;
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final String ARG_IMG_TITLE = "image_title";
        private static final String ARG_IMG_URL = "image_url";

        @Override
        public void setArguments(Bundle args) {
            super.setArguments(args);
            this.pos = args.getInt(ARG_SECTION_NUMBER);
            this.name = args.getString(ARG_IMG_TITLE);
            this.url = args.getString(ARG_IMG_URL);
        }

        public static PlaceholderFragment newInstance(int sectionNumber, String name, String url) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putString(ARG_IMG_TITLE, name);
            args.putString(ARG_IMG_URL, url);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public void onStart() {
            super.onStart();

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_photo_detail, container, false);
            // final ImageView imageView = (ImageView) rootView.findViewById(R.id.detail_image);
            final PhotoView imageView = (PhotoView) rootView.findViewById(R.id.detail_photoView);
            Glide.with(getActivity()).load(url).thumbnail(0.1f).into(imageView);
            return rootView;
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
