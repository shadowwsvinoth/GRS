package com.app.grs.adapter;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.app.grs.R;
import com.app.grs.fragment.SubCategoryFragment;
import com.app.grs.helper.Constants;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.HashMap;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder>{

    private Context mContext;
    private ArrayList<HashMap<String,String>> categoryList;

    public CategoryAdapter(Context mContext, ArrayList<HashMap<String, String>> categoryList) {
        this.mContext = mContext;
        this.categoryList = categoryList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        final HashMap<String,String> itemmap = categoryList.get(position);

        holder.catName.setText(itemmap.get("category_name"));
        Glide.with(mContext).load(itemmap.get("image_url") + itemmap.get("image_name")).into(holder.catImage);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


               Constants.categoryid = itemmap.get("category_id");
               Constants.categoryname = itemmap.get("category_name");

               AppCompatActivity activity = (AppCompatActivity) view.getContext();
               Fragment myFragment = new SubCategoryFragment();
               activity.getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, myFragment).addToBackStack(null).commit();
               Log.d("Backstackcount", String.valueOf(activity.getFragmentManager().getBackStackEntryCount()));


                Toast.makeText(mContext, "You have selected :\t" + itemmap.get("category_name"), Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView catName;
        public ImageView catImage;
        public CardView cardView;
        public RelativeLayout relativeLayout;


        public MyViewHolder(View itemView) {
            super(itemView);

            catName = itemView.findViewById(R.id.catname_tv);
            catImage = itemView.findViewById(R.id.catimage_iv);
            cardView = itemView.findViewById(R.id.cv_category);
            relativeLayout = itemView.findViewById(R.id.category_layout);

        }
    }
}
