package com.app.grs.fragment;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import com.app.grs.R;
import com.app.grs.adapter.ProductAdapter;
import com.app.grs.adapter.ReviewAdapter;
import com.app.grs.helper.Constants;
import com.app.grs.helper.GetSet;
import com.bumptech.glide.Glide;
import com.libizo.CustomEditText;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class SubProductFragment extends Fragment{

    public Button btncart, btnbuy, btnrate;
    private ImageView imageView;
    private TextView tvproname, tvproprice, tvprodesc, tvtotalrating;
    String proid = "", proname = "";
    TextView textItemCount, tvnoreview;
    LinearLayout empty_heart, filled_heart;
    int numItemCount;
    AlertDialog alertDialog;
    String custid = "";
    public RecyclerView recyclerView;
    private ReviewAdapter reviewAdapter;
    public String wish_flag, cart_flag;
    private ArrayList<HashMap<String,String>> reviewList=new ArrayList<HashMap<String, String>>();
    RecyclerView.LayoutManager mLayoutManager;
    SimpleDateFormat sdf;
    Date now;

    public SubProductFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Constants.pref = getActivity().getSharedPreferences("GRS",Context.MODE_PRIVATE);
        Constants.editor = Constants.pref.edit();

        custid = Constants.pref.getString("mobileno", "");
        new fetchCartCount(getActivity(), custid).execute();

        numItemCount = Constants.pref.getInt("count", 0);
        setBadge();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sub_product, container, false);

        proid = getArguments().getString("proid");
        proname = getArguments().getString("proname");

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(proname);

        Constants.pref = getActivity().getSharedPreferences("GRS",Context.MODE_PRIVATE);
        Constants.editor = Constants.pref.edit();

        custid = Constants.pref.getString("mobileno", "");

        btncart = view.findViewById(R.id.btn_addtocart);
        btnbuy = view.findViewById(R.id.btn_buynow);
        imageView = view.findViewById(R.id.subproductimage_iv);
        tvproname = view.findViewById(R.id.subproductname_tv);
        tvproprice = view.findViewById(R.id.subproductprice_tv);
        tvprodesc = view.findViewById(R.id.subproductdesc_tv);
        btnrate = view.findViewById(R.id.ratenow_btn);
        tvtotalrating = view.findViewById(R.id.overall_rating_tv);
        tvnoreview = view.findViewById(R.id.tv_no_review);
        empty_heart = view.findViewById(R.id.unchecked_fav_layout);
        filled_heart = view.findViewById(R.id.checked_fav_layout);

        new fetchSubProducts(getActivity(), proid, custid).execute();
        new fetchReview(getActivity(), proid).execute();

        recyclerView = view.findViewById(R.id.rv_rate);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        btnrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                rateDialog();
            }
        });
        empty_heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int flag = 1;
                empty_heart.setVisibility(View.GONE);
                filled_heart.setVisibility(View.VISIBLE);
                new storeWishlist(getActivity(), custid, proid, flag).execute();
            }
        });
        filled_heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int flag = 0;
                empty_heart.setVisibility(View.VISIBLE);
                filled_heart.setVisibility(View.GONE);
                new storeWishlist(getActivity(), custid, proid, flag).execute();
            }
        });
        btncart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Constants.cart.equals("0")){

                    int flag = 1;
                    new addtoCart(getActivity(), proid, custid, flag).execute();
                    Constants.cart="1";
                    btncart.setText("REMOVE FROM CART");

                }else if (Constants.cart.equals("1")){

                    int flag = 0;
                    new addtoCart(getActivity(), proid, custid, flag).execute();
                    Constants.cart="0";
                    btncart.setText("ADD TO CART");

                }
            }
        });

        return view;
    }

    private void rateDialog() {

        now = new Date();
        sdf = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");
        final String timestamp = sdf.format(now);

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.rate_dialog, null);
        dialogBuilder.setView(dialogView);

        dialogBuilder.setTitle("Rate & Review :");

        final RatingBar ratingBar = dialogView.findViewById(R.id.rating_bar);
        Button btnlater = dialogView.findViewById(R.id.btn_later_ratedialog);
        Button btnsubmit = dialogView.findViewById(R.id.btn_submit_ratedialog);
        final CustomEditText etreview = dialogView.findViewById(R.id.rate_et_review);

        alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);

        if (ratingBar != null && etreview != null){

            btnsubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String rate = String.valueOf(ratingBar.getRating());
                    String review = etreview.getText().toString().trim();
                    new postRating(getActivity(), proid, custid, rate, review, timestamp).execute();
                    /*Toast.makeText(getActivity(), rate, Toast.LENGTH_SHORT).show();*/
                }
            });
        }else {
            btnsubmit.setEnabled(false);
        }

        btnlater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem menuItem = menu.findItem(R.id.action_cart);
        View cart = MenuItemCompat.getActionView(menuItem);
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

    private class postRating extends AsyncTask<String, Integer, String>{

        Context context;
        String url = Constants.BASE_URL + Constants.RATING;
        String proid, cusid, rate, review;
        String timestamp;
        ProgressDialog progress;

        public postRating(Context context, String proid, String cusid, String rate, String review, String timestamp) {
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
                    .add("timestamp", timestamp)
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

    private class fetchReview extends AsyncTask<String, Integer, String>{

        Context context;
        String url = Constants.BASE_URL + Constants.GET_REVIEW;
        String proid;
        ProgressDialog progress;
        HashMap<String,String> map;
        String name, time, review, rate;

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

                    reviewAdapter = new ReviewAdapter(getActivity(),reviewList);
                    recyclerView.setAdapter(reviewAdapter);

                }else {
                        recyclerView.setVisibility(View.GONE);
                        tvnoreview.setVisibility(View.VISIBLE);
                       /* Toast.makeText(context, jonj.getString("message"), Toast.LENGTH_SHORT).show();*/
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private class fetchSubProducts extends AsyncTask<String, Integer, String>{

        Context context;
        String url = Constants.BASE_URL + Constants.SUBPRODUCT;
        String proid, cusid;
        ProgressDialog progress;

        public fetchSubProducts(Context context, String proid, String cusid) {
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
                    .add("product_id", proid)
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
                if (jonj.getString("status").equalsIgnoreCase(
                        "success")) {

                    String data = jonj.getString("data");
                    wish_flag = jonj.getString("wish_flag");
                    cart_flag = jonj.getString("cart_flag");
                    JSONArray array = new JSONArray(data);
                    JSONObject jcat = array.getJSONObject(0);

                    GetSet.setSubproductid(jcat.getString("product_id"));
                    GetSet.setSubproductname(jcat.getString("product_name"));
                    GetSet.setSubproductprice(jcat.getString("product_price"));
                    GetSet.setSubproductdesc(jcat.getString("product_desc"));
                    GetSet.setSubproductimage(jcat.getString("product_image"));
                    GetSet.setSubproductrating(jcat.getString("product_rating"));

                    tvproname.setText(GetSet.getSubproductname());
                    tvproprice.setText("₹.\t" + GetSet.getSubproductprice());
                    tvprodesc.setText(GetSet.getSubproductdesc());
                    tvtotalrating.setText(GetSet.getSubproductrating());
                    Glide.with(context).load(GetSet.getSubproductimage()).into(imageView);


                    if (cart_flag.equalsIgnoreCase("1")) {
                        btncart.setText("REMOVE FROM CART");
                    }else {
                        btncart.setText("ADD TO CART");
                    }
                    if (wish_flag.equalsIgnoreCase("1")){
                        empty_heart.setVisibility(View.GONE);
                        filled_heart.setVisibility(View.VISIBLE);
                    }else {
                        empty_heart.setVisibility(View.VISIBLE);
                        filled_heart.setVisibility(View.GONE);
                    }
                    if (GetSet.getSubproductrating().equalsIgnoreCase("0")){
                        btnrate.setEnabled(true);
                        btnrate.setText("RATE NOW");
                    }else {
                        btnrate.setEnabled(false);
                        btnrate.setText("RATED");
                    }

                }else {

                    Toast.makeText(context, jonj.getString("message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

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
}
