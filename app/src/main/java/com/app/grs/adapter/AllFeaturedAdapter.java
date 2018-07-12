package com.app.grs.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.app.grs.R;
import com.app.grs.activity.SubFeaturedAllActivity;
import com.app.grs.activity.SubOfferAllActivity;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;

public class AllFeaturedAdapter extends RecyclerView.Adapter<AllFeaturedAdapter.MyViewHolder>{

    private Context mContext;
    private ArrayList<HashMap<String,String>> allFeaturedList;

    public AllFeaturedAdapter(Context mContext, ArrayList<HashMap<String, String>> allFeaturedList) {
        this.mContext = mContext;
        this.allFeaturedList = allFeaturedList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.all_featured_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AllFeaturedAdapter.MyViewHolder holder, int position) {

        final HashMap<String, String> itemmap = allFeaturedList.get(position);

        float rate = Float.parseFloat(itemmap.get("product_rating"));
        holder.productName.setText(itemmap.get("product_name"));
        holder.productPrice.setText("₹.\t" + itemmap.get("product_price"));
        holder.ratingBar.setRating(rate);

        Glide.with(mContext).load(itemmap.get("product_image")).thumbnail(0.1f).into(holder.productImage);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mContext, SubFeaturedAllActivity.class);
                intent.putExtra("proid", itemmap.get("product_id"));
                intent.putExtra("proname", itemmap.get("product_name"));
                intent.putExtra("prorate", itemmap.get("product_rating"));
                /*intent.putExtra("proprice", itemmap.get("product_price"));
                intent.putExtra("prodesc", itemmap.get("product_desc"));
                intent.putExtra("proimage", itemmap.get("product_image"));*/

                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return allFeaturedList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView productName, productPrice;
        public ImageView productImage;
        public CardView cardView;
        public RatingBar ratingBar;

        public MyViewHolder(View itemView) {
            super(itemView);

            productName = itemView.findViewById(R.id.all_featuredname_tv);
            productPrice = itemView.findViewById(R.id.all_featuredprice_tv);
            productImage = itemView.findViewById(R.id.all_featuredimage_iv);
            cardView = itemView.findViewById(R.id.cv_all_featured);
            ratingBar = itemView.findViewById(R.id.all_featured_rate);
        }
    }
}
