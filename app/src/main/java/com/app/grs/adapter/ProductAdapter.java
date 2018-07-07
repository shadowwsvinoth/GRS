package com.app.grs.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorSpace;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.grs.R;
import com.app.grs.fragment.ProductFragment;
import com.app.grs.fragment.SubProductFragment;
import com.app.grs.helper.Constants;
import com.app.grs.helper.GetSet;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<HashMap<String, String>> productList;

    public ProductAdapter(Context mContext, ArrayList<HashMap<String, String>> productList) {
        this.mContext = mContext;
        this.productList = productList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        final HashMap<String, String> itemmap = productList.get(position);

        float rate = Float.parseFloat(itemmap.get("product_rating"));
        holder.productName.setText(itemmap.get("product_name"));
        holder.productPrice.setText("₹.\t" + itemmap.get("product_price"));
        holder.ratingBar.setRating(rate);

        Glide.with(mContext).load(itemmap.get("product_image")).into(holder.productImage);

        Constants.pref = mContext.getSharedPreferences("GRS", MODE_PRIVATE);
        Constants.productid = itemmap.get("product_id");

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Bundle bundle = new Bundle();
                bundle.putString("proid", itemmap.get("product_id"));
                bundle.putString("proname", itemmap.get("product_name"));

                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                Fragment myFragment = new SubProductFragment();
                myFragment.setArguments(bundle);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, myFragment).addToBackStack(null).commit();

            }
        });

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView productName, productPrice;
        public ImageView productImage;
        public CardView cardView;
        public RatingBar ratingBar;

        public MyViewHolder(View itemView) {
            super(itemView);

            productName = itemView.findViewById(R.id.productname_tv);
            productPrice = itemView.findViewById(R.id.productprice_tv);
            productImage = itemView.findViewById(R.id.productimage_iv);
            cardView = itemView.findViewById(R.id.cv_product);
            ratingBar = itemView.findViewById(R.id.product_rate);

        }
    }
}
