package com.app.grs.fragment;


import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.grs.R;
import com.app.grs.activity.FeaturedAllActivity;
import com.app.grs.activity.OfferAllActivity;
import com.app.grs.adapter.FeaturedAdapter;
import com.app.grs.adapter.OfferAdapter;
import com.app.grs.adapter.SliderAdapter;
import com.app.grs.helper.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import me.relex.circleindicator.CircleIndicator;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private static ViewPager viewPager;
    private RecyclerView rv_offer, rv_featured;
    private OfferAdapter offerAdapter;
    private ArrayList<HashMap<String,String>> offerList=new ArrayList<HashMap<String, String>>();
    private CircleIndicator circleIndicator;
    private static int currentPage = 0;
    private static int numofPage = 0;
    private SliderAdapter sliderAdapter;
    private ArrayList<HashMap<String,String>> bannerList=new ArrayList<HashMap<String, String>>();
    RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<HashMap<String,String>> featuredList=new ArrayList<HashMap<String, String>>();
    private FeaturedAdapter featuredAdapter;
    TextView textItemCount, offer_all, featured_all;
    int numItemCount;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("GRS");

        Constants.pref = getActivity().getSharedPreferences("GRS", Context.MODE_PRIVATE);
        Constants.editor = Constants.pref.edit();

        String cusid = Constants.pref.getString("mobileno", "");
        new fetchCartCount(getActivity(), cusid).execute();
        numItemCount = Constants.pref.getInt("count", 0);
        setBadge();

        bannerList.clear();
        offerList.clear();
        featuredList.clear();

        new fetchBanner(getActivity()).execute();
        new fetchOffer(getActivity()).execute();
        new fetchFeatured(getActivity()).execute();

        viewPager = view.findViewById(R.id.pager);
        circleIndicator = view.findViewById(R.id.indicator);

        rv_offer = view.findViewById(R.id.rv_offer);
        mLayoutManager = new GridLayoutManager(getActivity(), 2);
        rv_offer.setLayoutManager(mLayoutManager);

        rv_featured = view.findViewById(R.id.rv_featured);
        mLayoutManager = new GridLayoutManager(getActivity(), 2);
        rv_featured.setLayoutManager(mLayoutManager);

       /* btnfeaturedview = view.findViewById(R.id.btn_featured_viewall);
        btnofferview = view.findViewById(R.id.btn_off_viewall);*/

        offer_all = view.findViewById(R.id.btn_off_viewall);
        featured_all = view.findViewById(R.id.btn_featured_viewall);

        offer_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               startActivity(new Intent(getActivity(), OfferAllActivity.class));
            }
        });

        featured_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getActivity(), FeaturedAllActivity.class));
            }
        });

        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem menuItem1 = menu.findItem(R.id.action_cart);
        View cart = MenuItemCompat.getActionView(menuItem1);
        textItemCount = cart.findViewById(R.id.cart_badge);
        setBadge();




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

    private class fetchBanner extends AsyncTask<String, Void, String>{

        Context context;
        String url = Constants.BASE_URL + Constants.BANNER;
        ProgressDialog progress;
        HashMap<String,String> map;
        String banid,banimgpath,banimgname;

        public fetchBanner(Context context) {
            this.context = context;
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
            Request request = new Request.Builder()
                    .url(url)
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
            if (jsonData != null){

            try {
                jonj = new JSONObject(jsonData);
                if (jonj.getString("status").equalsIgnoreCase(
                        "success")) {

                    String data = jonj.getString("data");
                    JSONArray array = new JSONArray(data);
                    for(int i=0;i<array.length();i++) {
                        JSONObject jcat = array.getJSONObject(i);
                        map=new HashMap<String, String>();

                        banid=jcat.getString("ban_id");
                        banimgpath=jcat.getString("image_url");
                        banimgname=jcat.getString("image_name");

                        map.put("category_id",banid);
                        map.put("image_url",banimgpath);
                        map.put("image_name",banimgname);

                        bannerList.add(map);
                    }
                    numofPage = bannerList.size();
                    sliderAdapter = new SliderAdapter((getActivity()),bannerList);
                    viewPager.setAdapter(sliderAdapter);
                    viewPager.setOffscreenPageLimit(4);
                    circleIndicator.setViewPager(viewPager);

                    final Handler handler = new Handler();
                    final Runnable Update = new Runnable() {
                        public void run() {
                            if (currentPage == numofPage) {
                                currentPage = 0;
                            }
                            viewPager.setCurrentItem(currentPage++, true);
                        }
                    };
                    Timer swipeTimer = new Timer();
                    swipeTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            handler.post(Update);
                        }
                    }, 1000, 5000);

                   /* Intent intent = new Intent(context, HomeActivity.class);
                    startActivity(intent);*/
                } else
                    Toast.makeText(context, jonj.getString("data"), Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            }
        }
    }

    private class fetchOffer extends AsyncTask<String, Void, String>{

        Context context;
        String url = Constants.BASE_URL + Constants.GET_OFFER_DETAILS;
        ProgressDialog progress;
        HashMap<String,String> map;
        String offid,offimg,offprice,offname, offrate, offdesc;

        public fetchOffer(Context context) {
            this.context = context;
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
            Request request = new Request.Builder()
                    .url(url)
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
                        map=new HashMap<String, String>();

                        offid=jcat.getString("product_id");
                        offimg=jcat.getString("product_image");
                        offname=jcat.getString("product_name");
                        offprice=jcat.getString("product_price");
                        offdesc=jcat.getString("product_desc");
                        offrate=jcat.getString("product_rating");

                        map.put("product_id", offid);
                        map.put("product_image", offimg);
                        map.put("product_name", offname);
                        map.put("product_price", offprice);
                        map.put("product_desc",offdesc);
                        map.put("product_rating",offrate);


                        offerList.add(map);
                    }

                    offerAdapter = new OfferAdapter(getActivity(),offerList);
                    rv_offer.setAdapter(offerAdapter);

                } else
                    Toast.makeText(context, jonj.getString("data"), Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class fetchFeatured extends AsyncTask<String, Void, String>{

        Context context;
        String url = Constants.BASE_URL + Constants.GET_FEATURED_DETAILS;
        ProgressDialog progress;
        HashMap<String,String> map;
        String feaid, feaname, feaimage, feaprice, feadesc, fearate;

        public fetchFeatured(Context context) {
            this.context = context;
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
            Request request = new Request.Builder()
                    .url(url)
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
                        map=new HashMap<String, String>();

                        feaid=jcat.getString("product_id");
                        feaname=jcat.getString("product_name");
                        feaimage=jcat.getString("product_image");
                        feaprice=jcat.getString("product_price");
                        feadesc=jcat.getString("product_desc");
                        fearate=jcat.getString("product_rating");

                        map.put("product_id",feaid);
                        map.put("product_name",feaname);
                        map.put("product_image",feaimage);
                        map.put("product_price",feaprice);
                        map.put("product_desc",feadesc);
                        map.put("product_rating",fearate);
                        featuredList.add(map);
                    }

                    featuredAdapter = new FeaturedAdapter(getActivity(),featuredList);
                    rv_featured.setAdapter(featuredAdapter);

                } else
                    Toast.makeText(context, jonj.getString("data"), Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
