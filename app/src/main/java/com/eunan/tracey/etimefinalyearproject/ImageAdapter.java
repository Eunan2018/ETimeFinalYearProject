package com.eunan.tracey.etimefinalyearproject;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.eunan.tracey.etimefinalyearproject.upload.Upload;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    Context context;
    List<Upload> uploadList;

    public ImageAdapter(Context context, List<Upload> uploadList) {
        this.context = context;
        this.uploadList = uploadList;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.image_item,viewGroup,false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int i) {
        Upload upload = uploadList.get(i);
        holder.txtViewName.setText(upload.getName());
        Picasso.with(context)
                .load(upload.getImageUrl())
                .fit()
                .centerCrop()
                .into(holder.imgView);
    }

    @Override
    public int getItemCount() {
        return uploadList.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        public TextView txtViewName;
        public ImageView imgView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            txtViewName = itemView.findViewById(R.id.text_view_image_name);
            imgView = itemView.findViewById(R.id.image_view_uploaded_images);
        }
    }
}
