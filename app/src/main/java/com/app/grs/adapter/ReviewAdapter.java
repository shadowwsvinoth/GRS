package com.app.grs.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.app.grs.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<HashMap<String, String>> reviewList;
    private float rate;
    private LinearLayout starbg;

    public ReviewAdapter(Context mContext, ArrayList<HashMap<String, String>> reviewList) {
        this.mContext = mContext;
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rate_layout, parent, false);


        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        final HashMap<String, String> itemmap = reviewList.get(position);
        starbg = holder.starLayout;
        holder.tvname.setText(itemmap.get("name"));
        holder.tvreview.setText(itemmap.get("review"));
        holder.tvrate.setText(itemmap.get("rate"));
        holder.tvtime.setText(itemmap.get("timestamp"));
        rate = Float.parseFloat(itemmap.get("rate"));



        if (rate <= 1.0){
            starbg.setBackgroundColor(Color.parseColor("#ED2B39"));
        }else if (rate > 1.0 && rate <= 3.0){
            starbg.setBackgroundColor(Color.parseColor("#F47D0C"));
        }else if (rate >3.0){
            starbg.setBackgroundColor(Color.parseColor("#53BE08"));
        }


    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tvname, tvreview, tvrate, tvtime;
        public CardView cardView;
        public LinearLayout starLayout;

        public MyViewHolder(View itemView) {
            super(itemView);

            tvname = itemView.findViewById(R.id.rate_tv_name);
            tvreview = itemView.findViewById(R.id.rate_tv_review);
            tvrate = itemView.findViewById(R.id.rate_tv_rate);
            cardView = itemView.findViewById(R.id.cv_rate);
            tvtime = itemView.findViewById(R.id.rate_tv_time);
            starLayout = itemView.findViewById(R.id.star_layout);
        }

    }

}
