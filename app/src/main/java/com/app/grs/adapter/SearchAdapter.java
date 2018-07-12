package com.app.grs.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.grs.R;
import com.app.grs.activity.HomeActivity;
import com.app.grs.activity.SearchActivity;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private Context context;
    private ArrayList<HashMap<String, String>> searchList;


    public SearchAdapter(Context context, ArrayList<HashMap<String, String>> searchList) {
        this.context = context;
        this.searchList = searchList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_layout, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        final HashMap<String, String> itemmap = searchList.get(position);

        holder.name.setText(itemmap.get("product_name"));
        holder.rate.setText(itemmap.get("product_rating"));

        Glide.with(context).load(itemmap.get("product_image")).thumbnail(0.1f).into(holder.image);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, SearchActivity.class);
                intent.putExtra("proid", itemmap.get("product_id"));
                intent.putExtra("proname", itemmap.get("product_name"));
                context.startActivity(intent);
                Toast.makeText(context, "You have selected\t" + itemmap.get("product_name"), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return searchList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name, rate;
        public ImageView image;
        public CardView cardView;


        public ViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.search_tv_name);
            rate = itemView.findViewById(R.id.search_tv_rating);
            image = itemView.findViewById(R.id.search_iv);
            cardView = itemView.findViewById(R.id.cv_search);
        }
    }
}
