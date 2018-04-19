package com.example.anton.photogallery.adapters;

import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.DiffCallback;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.anton.photogallery.model.Photo;
import com.example.anton.photogallery.PhotoActivity;
import com.example.anton.photogallery.R;

/**
 * Created by anton on 12.04.2018.
 */

public class PhotoGalleryPagedListAdapter extends PagedListAdapter<Photo, PhotoGalleryPagedListAdapter.MyViewHolder> {

    private Context context;
    private String TAG="MyLog";

    public PhotoGalleryPagedListAdapter(@NonNull DiffCallback<Photo> diffCallback, Context context) {
        super(diffCallback);
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder() called with: parent = [" + parent + "], viewType = [" + viewType + "]");
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View photoView = inflater.inflate(R.layout.item_photo, parent, false);

        return new MyViewHolder(photoView);

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.bindTo(getItem(position));
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.iv_photo);
            itemView.setOnClickListener(this);
        }

        public void bindTo(Photo photo) {
            Log.d(TAG, "bindTo() called with: photo = [" + photo.getUrl() + "]");
            Glide.with(context)
                    .load(photo.getUrl())
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.placeholder)
                    .into(imageView);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Log.d(TAG, "onClick() called with: view on position = [" + position + "]");
            if (position != RecyclerView.NO_POSITION) {
                Photo photo = getItem(position);
                Intent intent = new Intent(context, PhotoActivity.class);
                intent.putExtra("photo", photo);
                context.startActivity(intent);
            }
        }
    }
}
