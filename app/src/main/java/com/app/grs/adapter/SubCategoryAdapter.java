package com.app.grs.adapter;

import android.content.Context;
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
import com.app.grs.fragment.ProductFragment;
import com.app.grs.fragment.SubCategoryFragment;
import com.app.grs.helper.Constants;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;

public class SubCategoryAdapter extends RecyclerView.Adapter<SubCategoryAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<HashMap<String,String>> subcategoryList;

    public SubCategoryAdapter(Context mContext, ArrayList<HashMap<String, String>> subcategoryList) {
        this.mContext = mContext;
        this.subcategoryList = subcategoryList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.subcategory_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        final HashMap<String,String> itemmap = subcategoryList.get(position);

        holder.subcatName.setText(itemmap.get("subcategory_name"));

        Glide.with(mContext).load(itemmap.get("image_url") + itemmap.get("image_name")).thumbnail(0.1f).into(holder.subcatImage);


        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Constants.subcategoryid = itemmap.get("subcategory_id");
                Constants.subcategoryname = itemmap.get("subcategory_name");

                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                Fragment myFragment = new ProductFragment();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, myFragment).addToBackStack(null).commit();

                Toast.makeText(mContext, "You have selected :\t" + itemmap.get("subcategory_name"), Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return subcategoryList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView subcatName;
        public ImageView subcatImage;
        public CardView cardView;

        public MyViewHolder(View itemView) {
            super(itemView);

            subcatName = itemView.findViewById(R.id.subcatname_tv);
            subcatImage = itemView.findViewById(R.id.subcatimage_iv);
            cardView = itemView.findViewById(R.id.cv_subcategory);


        }
    }

}
