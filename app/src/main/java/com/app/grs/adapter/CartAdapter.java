package com.app.grs.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.app.grs.R;
import com.app.grs.helper.Constants;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<HashMap<String,String>> cartList;

    public CartAdapter(Context mContext, ArrayList<HashMap<String, String>> cartList) {
        this.mContext = mContext;
        this.cartList = cartList;
    }

    @NonNull
    @Override
    public CartAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final CartAdapter.MyViewHolder holder, final int position) {

        final HashMap<String,String> itemmap = cartList.get(position);

        holder.productName.setText(itemmap.get("product_name"));
        holder.productPrice.setText( "₹.\t" +itemmap.get("product_price"));

        Glide.with(mContext).load(itemmap.get("product_image")).thumbnail(0.1f).into(holder.productImage);

        holder.deleteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Constants.pref = mContext.getSharedPreferences("GRS",MODE_PRIVATE);

                String cusid = Constants.pref.getString("mobileno", "");
                String proid = itemmap.get("product_id");

                cartList.remove(position);
                notifyItemRemoved(position);
                notifyDataSetChanged();

                int flag = 0;
               new deleteCart(mContext, cusid, proid, flag).execute();

            }
        });

        final String qty[] = new String[]{

                "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"
        };

        List<String> stringList = new ArrayList<>(Arrays.asList(qty));
        ArrayAdapter<String > arrayAdapter =new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, stringList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        holder.spinner.setPrompt("Qty");
        holder.spinner.setAdapter(arrayAdapter);

        holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

                String selectedItem = adapterView.getItemAtPosition(position).toString();
                String price = itemmap.get("product_price");
                Double priceDouble = Double.parseDouble(price);
                Integer qtyInteger = Integer.valueOf(selectedItem);
                int total = (int) (priceDouble * qtyInteger);
                holder.totalPrice.setText("₹." + Integer.toString(total));


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView productName, productPrice, totalPrice;
        public ImageView productImage;
        public CardView cardView;
        public LinearLayout deleteLayout;
        public Spinner spinner;

        public MyViewHolder(View itemView) {
            super(itemView);

            productName = itemView.findViewById(R.id.cart_productname_tv);
            productPrice = itemView.findViewById(R.id.cart_productprice_tv);
            totalPrice = itemView.findViewById(R.id.cart_tv_total);
            productImage = itemView.findViewById(R.id.cart_productimage_iv);
            cardView = itemView.findViewById(R.id.cv_cart);
            deleteLayout = itemView.findViewById(R.id.cart_delete_layout);
            spinner = itemView.findViewById(R.id.cart_spin_qty);

        }
    }

    private class deleteCart extends AsyncTask<String, Integer, String> {

        private Context context;
        public String cusid, proid;
        int flag;
        private String url = Constants.BASE_URL + Constants.ADD_CART;
        ProgressDialog progress;

        public deleteCart(Context context, String cusid, String proid, int flag) {
            this.context = context;
            this.cusid = cusid;
            this.proid = proid;
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
                        "Deleted")) {

                    Toast.makeText(context, jonj.getString("message"), Toast.LENGTH_SHORT).show();

                }else
                {
                    Toast.makeText(context,jonj.getString("message"),Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
