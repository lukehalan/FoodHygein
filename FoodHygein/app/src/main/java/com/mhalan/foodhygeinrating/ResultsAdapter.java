/*
 * Copyright (c) 2019. Mohammad Halan - Portfolio Assignment
 */

package com.mhalan.foodhygeinrating;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class ResultsAdapter extends ArrayAdapter<Establishment> {

    private Context context;
    private int resId;
    private List<Establishment> items;
    private boolean showFavourite = true;
    private boolean showDistance = true;

    public ResultsAdapter(@NonNull Context context, int resource, @NonNull List<Establishment> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resId = resource;
        this.items = objects;
    }

    public List<Establishment> getItems() {
        return this.items;
    }

    public void setShowFavourite(boolean value) {
        this.showFavourite = value;
    }

    public void setShowDistance(boolean value) {
        this.showDistance = value;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        EstablishmentHolder holder = null;

        if (row == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(this.context);
            row = layoutInflater.inflate(this.resId, parent, false);
            holder = new EstablishmentHolder();
            holder.nameTextView = row.findViewById(R.id.textView_row_name);
            holder.typeTextView = row.findViewById(R.id.textView_row_type);
            holder.ratingBar = row.findViewById(R.id.textView_row_rating);
            holder.distanceTextView = row.findViewById(R.id.textView_distance);
            holder.favouriteStar = row.findViewById(R.id.imageView_row_favourite);
            holder.distanceGroup = row.findViewById(R.id.distance_group);
            holder.dateTextView = row.findViewById(R.id.row_date_text_view);

            if (!showDistance) {
                holder.distanceGroup.setVisibility(View.GONE);
            }
            if (!showFavourite) {
                holder.favouriteStar.setVisibility(View.GONE);
            }
            row.setTag(holder);
        } else {
            holder = (EstablishmentHolder) row.getTag();
        }

        Establishment currentItem = items.get(position);
        holder.nameTextView.setText(currentItem.getBusinessName());
        holder.typeTextView.setText(currentItem.getBusinessType());
        holder.ratingBar.setText(currentItem.getRatingValue());
        String date = "";
        try {
            date = Convert.dateToString(currentItem.getRatingDate(), "yyyy-MM-dd'T'hh:mm:ss", "dd.MM.yyyy");
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.dateTextView.setText(date);

        if (showDistance) {
            String distanceString = String.format("%.2f", currentItem.getDistance()) + " " + context.getResources().getString(R.string.miles);
            holder.distanceTextView.setText(distanceString);
        }
        if (showFavourite) {
            if (DataManager.getInstance().getFavouriteIdList().indexOf(currentItem.getFHRSID()) != -1) {
                holder.favouriteStar.setImageResource(R.drawable.star_on);
            } else {
                holder.favouriteStar.setImageResource(R.drawable.star_off);
            }
            holder.favouriteStar.setTag(currentItem);
            holder.favouriteStar.setOnClickListener(listener);
        }

        return row;
    }

    ImageView.OnClickListener listener = new ImageView.OnClickListener() {
        @Override
        public void onClick(View view) {
            Establishment est = (Establishment) view.getTag();
            if (est != null) {
                if (DataManager.getInstance().getFavouriteIdList().indexOf(est.getFHRSID()) == -1) {
                    Database.getInstance().getDb(context).iEstablishmentDao().insert(est);
                    DataManager.getInstance().addFavouriteId(est.getFHRSID());
                    ((ImageView) view).setImageResource(R.drawable.star_on);
                    Toast.makeText(context, context.getResources().getString(R.string.favourite_add_msg), Toast.LENGTH_SHORT).show();
                } else {
                    Database.getInstance().getDb(context).iEstablishmentDao().delete(est);
                    ((ImageView) view).setImageResource(R.drawable.star_off);
                    DataManager.getInstance().removeFavouriteId(est.getFHRSID());
                    Toast.makeText(context, context.getResources().getString(R.string.favourite_remove_msg), Toast.LENGTH_SHORT).show();
                }

            }
        }
    };

    private class EstablishmentHolder {
        public TextView nameTextView;
        public TextView typeTextView;
        public TextView ratingBar;
        public TextView distanceTextView;
        public ImageView favouriteStar;
        public TextView dateTextView;
        public View distanceGroup;
    }

}
