package com.app.grs.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.app.grs.R;
import com.app.grs.adapter.ProductAdapter;
import com.app.grs.adapter.WishlistAdapter;
import com.app.grs.helper.Constants;
import com.app.grs.helper.GetSet;

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

public class MyWishListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private WishlistAdapter wishlistAdapter;
    private ArrayList<HashMap<String,String>> wishlistList=new ArrayList<HashMap<String, String>>();
    RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("My Wishlists");
        setContentView(R.layout.activity_my_wish_list);

        String cusid = Constants.pref.getString("mobileno", "");
        recyclerView = findViewById(R.id.rv_wishlist);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        wishlistList.clear();
        new fetchWishlist(MyWishListActivity.this, cusid).execute();
    }

    private class fetchWishlist extends AsyncTask<String, Integer, String> {

        private Context context;
        private String cusid, proid, proname, proimage, proprice, prodesc, flag;
        private String url = Constants.BASE_URL + Constants.GET_WISHLIST;
        ProgressDialog progress;
        HashMap<String,String> map;

        public fetchWishlist(Context context, String cusid) {
            this.context = context;
            this.cusid = cusid;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(context);
            progress.setMessage("Please wait ....");
            progress.setTitle("Loading");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.show();
        }

        @Override
        protected String doInBackground(String... strings) {

            String jsonData = null;
            Response response = null;
            OkHttpClient client = new OkHttpClient();
            RequestBody body = new FormBody.Builder()
                    .add("customer_id", cusid)
                    .build();
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            Call call = client.newCall(request);

            try {
                response = call.execute();

                if (response.isSuccessful()) {
                    jsonData = response.body().string();
                } else {
                    jsonData = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return jsonData;
        }

        @Override
        protected void onPostExecute(String jsonData) {
            super.onPostExecute(jsonData);
            progress.dismiss();
            progress.dismiss();
            Log.v("result", "" + jsonData);
            JSONObject jonj = null;
            try {
                jonj = new JSONObject(jsonData);
                if (jonj.getString("status").equalsIgnoreCase(
                        "success")) {

                    String data = jonj.getString("data");
                    JSONArray array = new JSONArray(data);
                    for(int i=0;i<array.length();i++) {
                        JSONObject jcat = array.getJSONObject(i);
                        map=new HashMap<String, String>();

                        proid=jcat.getString("product_id");
                        proimage=jcat.getString("product_image");
                        proname=jcat.getString("product_name");
                        proprice=jcat.getString("product_price");
                        prodesc=jcat.getString("product_desc");
                        flag=jcat.getString("wish_flag");

                        map.put("product_id",proid);
                        map.put("product_image",proimage);
                        map.put("product_name",proname);
                        map.put("product_price",proprice);
                        map.put("product_desc", prodesc);
                        map.put("flag",flag);
                        wishlistList.add(map);

                    }

                    wishlistAdapter = new WishlistAdapter(MyWishListActivity.this,wishlistList);
                    recyclerView.setAdapter(wishlistAdapter);


                }else
                {
                    Toast.makeText(getApplicationContext(),jonj.getString("message"),Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}
