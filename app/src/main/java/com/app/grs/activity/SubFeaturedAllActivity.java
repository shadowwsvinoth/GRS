package com.app.grs.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.grs.R;
import com.app.grs.adapter.ReviewAdapter;
import com.app.grs.fragment.FeaturedDetailsFragment;
import com.app.grs.helper.Constants;
import com.bumptech.glide.Glide;
import com.libizo.CustomEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SubFeaturedAllActivity extends AppCompatActivity {

    public RecyclerView recyclerView;
    private ReviewAdapter reviewAdapter;
    private ArrayList<HashMap<String,String>> reviewList;
    LinearLayout empty_heart, filled_heart;
    String wish_flag, cart_flag;
    RecyclerView.LayoutManager mLayoutManager;
    private ImageView imageView;
    private TextView tvfeaname, tvfeaprice, tvfeadesc, tvfearate, tvtotalrating, tvnoreview;
    private Button btnfearate, btnfeacart, btnfeabuy;
    String proname = "", proprice = "", prodesc = "", proid = "", proimage = "", prorate ="";
    String custid = "";
    TextView textItemCount;
    int numItemCount;
    AlertDialog alertDialog;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        proname = intent.getStringExtra("proname");
        getSupportActionBar().setTitle(proname);
        setContentView(R.layout.activity_sub_featured_all);

       /* proprice = intent.getStringExtra("proprice");
        prodesc = intent.getStringExtra("prodesc");
        proimage = intent.getStringExtra("proimage");
        prorate = intent.getStringExtra("prorate");*/

        Constants.pref = getSharedPreferences("GRS", Context.MODE_PRIVATE);
        Constants.editor = Constants.pref.edit();
        custid = Constants.pref.getString("mobileno", "");
        new fetchCartCount(this, custid).execute();
        numItemCount = Constants.pref.getInt("count", 0);
        setBadge();

        proid = intent.getStringExtra("proid");
        new getFlag(this, proid,  custid).execute();

        imageView = findViewById(R.id.subfea_image_iv);
        tvfeaname = findViewById(R.id.subfea_name_tv);
        tvfeaprice = findViewById(R.id.subfea_price_tv);
        tvfeadesc = findViewById(R.id.subfea_desc_tv);
        tvfearate = findViewById(R.id.subfea_overall_rating_tv);
        btnfearate = findViewById(R.id.btn_subfea_ratenow);
        btnfeabuy = findViewById(R.id.btn_subfea_buynow);
        btnfeacart = findViewById(R.id.btn_subfea_addtocart);
        tvnoreview = findViewById(R.id.subfea_tv_no_review);
        empty_heart = findViewById(R.id.fea_det_unchecked_fav_layout);
        filled_heart = findViewById(R.id.fea_det_checked_fav_layout);

        reviewList = new ArrayList<HashMap<String, String>>();

        prorate = intent.getStringExtra("prorate");
        if (prorate.equalsIgnoreCase("0")){
            btnfearate.setEnabled(true);
        }else {
            btnfearate.setEnabled(false);
        }

        new fetchReview(this, proid).execute();

        recyclerView = findViewById(R.id.rv_subfea_rate);
        mLayoutManager = new LinearLayoutManager(SubFeaturedAllActivity.this);
        recyclerView.setLayoutManager(mLayoutManager);
        btnfearate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                rateDialog();
            }
        });
        btnfeacart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Constants.cart.equals("0")){

                    int flag = 1;
                    new addtoCart(SubFeaturedAllActivity.this, proid, custid, flag).execute();
                    Constants.cart="1";
                    btnfeacart.setText("REMOVE FROM CART");

                }else if (Constants.cart.equals("1")){

                    int flag = 0;
                    new addtoCart(SubFeaturedAllActivity.this, proid, custid, flag).execute();
                    Constants.cart="0";
                    btnfeacart.setText("ADD TO CART");

                }
            }
        });
        empty_heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int flag = 1;
                empty_heart.setVisibility(View.GONE);
                filled_heart.setVisibility(View.VISIBLE);
                new storeWishlist(SubFeaturedAllActivity.this, custid, proid, flag).execute();
            }
        });
        filled_heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int flag = 0;
                empty_heart.setVisibility(View.VISIBLE);
                filled_heart.setVisibility(View.GONE);
                new storeWishlist(SubFeaturedAllActivity.this, custid, proid, flag).execute();
            }
        });

    }

    private class storeWishlist extends AsyncTask<String, Integer, String> {

        private Context context;
        public String cusid, proid;
        int flagstatus;
        private String url = Constants.BASE_URL + Constants.ADD_WISHLIST;
        ProgressDialog progress;

        public storeWishlist(Context context, String cusid, String proid, int flag) {
            this.context = context;
            this.cusid = cusid;
            this.proid = proid;
            this.flagstatus = flag;
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
                    .add(Constants.CUSTOMER_ID, cusid)
                    .add(Constants.PRODUCT_ID, proid)
                    .add(Constants.cartflag, String.valueOf(flagstatus))
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
            Log.v("result", "" + jsonData);
            JSONObject jonj = null;
            try {
                jonj = new JSONObject(jsonData);
                if (jonj.getString("status").equalsIgnoreCase(
                        "Inserted")) {

                    Toast.makeText(context, jonj.getString("message"), Toast.LENGTH_SHORT).show();

                } else if (jonj.getString("status").equalsIgnoreCase(
                        "Already")) {

                    Toast.makeText(context, jonj.getString("message"), Toast.LENGTH_SHORT).show();
                } else {

                    Toast.makeText(context, jonj.getString("message"), Toast.LENGTH_SHORT).show();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private class getFlag extends AsyncTask<String, Integer, String>{

        Context context;
        String url = Constants.BASE_URL + Constants.GET_OFFER_LAST;
        String proid, cusid, offname, offimage, offprice, offdesc, offrate;
        ProgressDialog progress;

        public getFlag(Context context, String proid, String cusid) {
            this.context = context;
            this.proid = proid;
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
                    .add("product_id", proid)
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

            Log.v("result", "" + jsonData);
            JSONObject jonj = null;
            try {
                jonj = new JSONObject(jsonData);
                if (jonj.getString("status").equalsIgnoreCase(
                        "success")) {
                    String data = jonj.getString("data");
                    wish_flag = jonj.getString("wish_flag");
                    cart_flag = jonj.getString("cart_flag");
                    JSONArray array = new JSONArray(data);
                    JSONObject jcat = array.getJSONObject(0);

                    offdesc = jcat.getString("product_desc");
                    offname = jcat.getString("product_name");
                    offprice =  jcat.getString("product_price");
                    offimage = jcat.getString("product_image");
                    offrate = jcat.getString("product_rating");

                    Glide.with(SubFeaturedAllActivity.this).load(offimage).into(imageView);
                    tvfeaname.setText(offname);
                    tvfeaprice.setText("₹.\t" + offprice);
                    tvfeadesc.setText(offdesc);
                    tvfearate.setText(offrate);


                    if (cart_flag.equalsIgnoreCase("1")) {
                        btnfeacart.setText("REMOVE FROM CART");
                    }else {
                        btnfeacart.setText("ADD TO CART");
                    }
                    if (wish_flag.equalsIgnoreCase("1")){
                        empty_heart.setVisibility(View.GONE);
                        filled_heart.setVisibility(View.VISIBLE);
                    }else {
                        empty_heart.setVisibility(View.VISIBLE);
                        filled_heart.setVisibility(View.GONE);
                    }

                }else {
                    Toast.makeText(context, jonj.getString("message"), Toast.LENGTH_SHORT).show();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private void rateDialog() {

        final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        sdf.format(timestamp);

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SubFeaturedAllActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.rate_dialog, null);
        dialogBuilder.setView(dialogView);

        dialogBuilder.setTitle("Rate & Review :");

        final RatingBar ratingBar = dialogView.findViewById(R.id.rating_bar);
        Button btnlater = dialogView.findViewById(R.id.btn_later_ratedialog);
        Button btnsubmit = dialogView.findViewById(R.id.btn_submit_ratedialog);
        final CustomEditText etreview = dialogView.findViewById(R.id.rate_et_review);

        alertDialog = dialogBuilder.create();

        if (ratingBar != null){

            btnsubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String rate = String.valueOf(ratingBar.getRating());
                    String review = etreview.getText().toString().trim();
                    new postRating(SubFeaturedAllActivity.this, proid, custid, rate, review, timestamp).execute();
                    /*Toast.makeText(getActivity(), rate, Toast.LENGTH_SHORT).show();*/
                }
            });
        }

        btnlater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private class postRating extends AsyncTask<String, Integer, String> {

        Context context;
        String url = Constants.BASE_URL + Constants.RATING;
        String proid, cusid, rate, review;
        ProgressDialog progress;
        Timestamp timestamp;

        public postRating(Context context, String proid, String cusid, String rate, String review, Timestamp timestamp) {
            this.context = context;
            this.proid = proid;
            this.cusid = cusid;
            this.rate = rate;
            this.review = review;
            this.timestamp = timestamp;
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
                    .add("product_id", proid)
                    .add("rate", rate)
                    .add("review", review)
                    .add("timestamp", String.valueOf(timestamp))
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

            Log.v("result", "" + jsonData);
            JSONObject jonj = null;
            try {
                jonj = new JSONObject(jsonData);
                if (jonj.getString("status").equalsIgnoreCase(
                        "success")) {

                    alertDialog.dismiss();
                    /*GetSet.setRate(rate);*/
                    Toast.makeText(context,jonj.getString("message"),Toast.LENGTH_SHORT).show();

                }else if (jonj.getString("status").equalsIgnoreCase(
                        "Already")){

                    Toast.makeText(context,jonj.getString("message"),Toast.LENGTH_SHORT).show();
                }
                else {
                    alertDialog.dismiss();
                    Toast.makeText(context,jonj.getString("message"),Toast.LENGTH_SHORT).show();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private class fetchCartCount extends AsyncTask<String, Integer, String>{

        Context context;
        String url = Constants.BASE_URL + Constants.CART_COUNT;
        String cusid;
        ProgressDialog progress;

        public fetchCartCount(Context context, String cusid) {
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
            Log.v("result", "" + jsonData);
            JSONObject jonj = null;
            try {
                jonj = new JSONObject(jsonData);
                int count = Integer.parseInt(jonj.getString("count"));
                if (jonj.getString("status").equalsIgnoreCase(
                        "success")) {

                    Constants.editor.putInt("count", count);
                    Constants.editor.apply();
                    Constants.editor.commit();

                    numItemCount = Constants.pref.getInt("count", 0);
                    setBadge();

                }else  Toast.makeText(context,jonj.getString("message"),Toast.LENGTH_SHORT).show();
            }catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private void setBadge() {

        if (textItemCount != null) {
            if (numItemCount == 0) {
                if (textItemCount.getVisibility() != View.GONE) {
                    textItemCount.setVisibility(View.GONE);
                }
            } else {
                textItemCount.setText(String.valueOf(Math.min(numItemCount, 99)));
                if (textItemCount.getVisibility() != View.VISIBLE) {
                    textItemCount.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);

        MenuItem menuItem = menu.findItem(R.id.action_cart);
        View cart = MenuItemCompat.getActionView(menuItem);

        textItemCount = cart.findViewById(R.id.cart_badge);
        setBadge();

        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(SubFeaturedAllActivity.this, MyCartActivity.class);
                startActivity(i);
            }

        });
        return true;
    }

    private class fetchReview extends AsyncTask<String, Integer, String>{

        Context context;
        String url = Constants.BASE_URL + Constants.GET_REVIEW;
        String proid;
        ProgressDialog progress;
        HashMap<String,String> map;
        String name, review, rate, time;

        public fetchReview(Context context, String proid) {
            this.context = context;
            this.proid = proid;
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
                    .add("product_id", proid)
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
                        map = new HashMap<String, String>();

                        name = jcat.getString("name");
                        review = jcat.getString("review");
                        rate = jcat.getString("rate");
                        time = jcat.getString("timestamp");

                        map.put("name", name);
                        map.put("review", review);
                        map.put("rate", rate);
                        map.put("timestamp", time);

                        reviewList.add(map);
                    }
                    reviewAdapter = new ReviewAdapter(SubFeaturedAllActivity.this,reviewList);
                    recyclerView.setAdapter(reviewAdapter);

                } else {

                    recyclerView.setVisibility(View.GONE);
                    tvnoreview.setVisibility(View.VISIBLE);
/*
                    Toast.makeText(context, jonj.getString("message"), Toast.LENGTH_SHORT).show();
*/
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private class addtoCart extends AsyncTask<String, Void, String>{

        Context context;
        String url = Constants.BASE_URL + Constants.ADD_CART;
        String proid, cusid;
        int flag;
        ProgressDialog progress;

        public addtoCart(Context context, String proid, String cusid, int flag) {
            this.context = context;
            this.proid = proid;
            this.cusid = cusid;
            this.flag = flag;
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
                    .add(Constants.CUSTOMER_ID, cusid)
                    .add(Constants.PRODUCT_ID, proid)
                    .add(Constants.cartflag, String.valueOf(flag))
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

            Log.v("result", "" + jsonData);
            JSONObject jonj = null;
            try {
                jonj = new JSONObject(jsonData);
                if (jonj.getString("status").equalsIgnoreCase(
                        "Inserted")) {

                    new fetchCartCount(context, cusid).execute();
                    Toast.makeText(context,jonj.getString("message"),Toast.LENGTH_SHORT).show();

                }else if (jonj.getString("status").equalsIgnoreCase(
                        "Already"))
                {
                    Toast.makeText(context,jonj.getString("message"),Toast.LENGTH_SHORT).show();
                }
                else {
                    new fetchCartCount(context, cusid).execute();
                    Toast.makeText(context,jonj.getString("message"),Toast.LENGTH_SHORT).show();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
