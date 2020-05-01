/*
 * Copyright (c) 2019. Mohammad Halan - Portfolio Assignment
 */

package com.mhalan.foodhygeinrating;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.ArrayList;
import java.util.List;

public class PhotoGalleryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<PhotoModel> data = new ArrayList<>();
    private List<String> selectedIds = new ArrayList<>();



    public PhotoGalleryAdapter(Context context, List<PhotoModel> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        RecyclerView.ViewHolder viewHolder;
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.image_list_item, parent, false);
        viewHolder = new MyItemHolder(v);
        return viewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        Glide.with(context).load(data.get(position).getPath())
                .thumbnail(0.5f)
                .override(200, 200)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(true)
                .into(((MyItemHolder) holder).mImg);

        String id = data.get(position).getName();

        if (selectedIds.contains(id)){
            holder.itemView.setForeground(new ColorDrawable(ContextCompat.getColor(context,R.color.colorControlActivated)));
        }
        else {
            holder.itemView.setForeground(new ColorDrawable(ContextCompat.getColor(context,android.R.color.transparent)));
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class MyItemHolder extends RecyclerView.ViewHolder {
        ImageView mImg;
        // final PhotoView mImg;

        public MyItemHolder(View itemView) {
            super(itemView);
            mImg = (ImageView) itemView.findViewById(R.id.item_img);
            //mImg = (PhotoView) itemView.findViewById(R.id.photoView);
        }

    }

    public PhotoModel getItem(int position){
        return data.get(position);
    }

    public void setSelectedIds(List<String> selectedIds) {
        this.selectedIds = selectedIds;
        notifyDataSetChanged();
    }
}
