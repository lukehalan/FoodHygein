/*
 * Copyright (c) 2019. Mohammad Halan - Portfolio Assignment
 */

package com.mhalan.foodhygeinrating;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.infowindow.BasicInfoWindow;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

public class MapInfoWindowAdapter extends InfoWindow {

    Establishment establishment;
    public static final String ESTABLISHMENT_ID = "establishment_id";

    public MapInfoWindowAdapter(int layoutResId, MapView mapView, Establishment establishment) {
        super(layoutResId, mapView);
        this.establishment = establishment;
    }

    public void onOpen(Object item) {

        for(int i=0; i<mMapView.getOverlays().size(); ++i){
            Overlay o = mMapView.getOverlays().get(i);
            if(o instanceof Marker){
                Marker m = (Marker) o;
                Marker current = (Marker) item;
               // if(!m.getTitle().equals(current.getTitle()))
                if(m!=current)
                    m.closeInfoWindow();
            }
        }

        LinearLayout layout = (LinearLayout) mView.findViewById(R.id.bubble_layout);
        Button btnMoreInfo = (Button) mView.findViewById(R.id.bubble_moreinfo);
        TextView txtTitle = (TextView) mView.findViewById(R.id.bubble_title);
        TextView txtDescription = (TextView) mView.findViewById(R.id.bubble_description);
        TextView txtSubdescription = (TextView) mView.findViewById(R.id.bubble_subdescription);

        txtTitle.setText(establishment.getBusinessName());
        txtDescription.setText("Hygiene Rate: " + establishment.getScores().getHygiene());
        txtSubdescription.setText("Local Authority: " + establishment.getLocalAuthorityName());
        layout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(mMapView.getContext(), EstablishmentDetailsActivity.class);
                intent.putExtra(ESTABLISHMENT_ID, establishment.getFHRSID());
                mMapView.getContext().startActivity(intent);
              //  Log.e("InfoWindow", "InfoWindowsHasClicked");
            }
        });
    }

    public void onClose() {

    }
}
