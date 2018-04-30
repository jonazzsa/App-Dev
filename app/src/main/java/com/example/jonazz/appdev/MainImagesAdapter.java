package com.example.jonazz.appdev;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by jonazz on 2018/04/25.
 */

public class MainImagesAdapter extends RecyclerView.Adapter<MainImagesAdapter.ImagesRecyclerViewHolder> {
    static ClickListener clicks;
    public ArrayList<ArrayList<String>> mMainData;
    public Context mContext;

    public MainImagesAdapter(Context ctx, ArrayList data) {
        super();
        mMainData = data;
        mContext = ctx;
    }
    public MainImagesAdapter() {
        super();
    }


    @Override
    public ImagesRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ImagesRecyclerViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.main_recyclerview_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(ImagesRecyclerViewHolder holder, int position) {
        Glide.with(mContext)
                .load(mMainData.get(position).get(0))
                .into(holder.mainImage);
        holder.mainLocation.setText(mMainData.get(position).get(2));

    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return mMainData.size();
    }

    class ImagesRecyclerViewHolder extends RecyclerView.ViewHolder {

        ImageView mainImage;
        TextView mainLocation;

        public ImagesRecyclerViewHolder(View itemView) {
            super(itemView);
            mainImage = itemView.findViewById(R.id.main_image);
            mainLocation = itemView.findViewById(R.id.main_image_details);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clicks.clickedItem(mMainData.get(getAdapterPosition()).get(2), mMainData.get(getAdapterPosition()).get(1));
                }
            });
        }
    }

    public void setClick(ClickListener clicks) {
        MainImagesAdapter.clicks = clicks;
    }

    public interface ClickListener {
        void clickedItem(String location, String title);
    }
}
