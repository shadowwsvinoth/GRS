package com.app.grs.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.grs.R;
import com.app.grs.fragment.OfferDetailsFragment;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;

public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<HashMap<String,String>> offerList;

    public OfferAdapter(Context mContext, ArrayList<HashMap<String, String>> offerList) {
        this.mContext = mContext;
        this.offerList = offerList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.offer_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        final HashMap<String,String> itemmap = offerList.get(position);

        Glide.with(mContext).load(itemmap.get("product_image")).into(holder.offImage);

        holder.offName.setText(itemmap.get("product_name"));

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();
                bundle.putString("proid", itemmap.get("product_id"));
                bundle.putString("proname", itemmap.get("product_name"));
                bundle.putString("prorate", itemmap.get("product_rating"));

                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                Fragment myFragment = new OfferDetailsFragment();
                myFragment.setArguments(bundle);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, myFragment).addToBackStack(null).commit();

                Toast.makeText(mContext, "You have selected :\t" + itemmap.get("product_name"), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return offerList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView offImage;
        public TextView offName;
        public CardView cardView;

        public MyViewHolder(View itemView) {
            super(itemView);

            offImage = itemView.findViewById(R.id.offimage_iv);
            offName = itemView.findViewById(R.id.offdesc_tv);
            cardView = itemView.findViewById(R.id.cv_offer);
        }
    }
}
