package com.app.grs.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.grs.R;
import com.app.grs.helper.Constants;
import com.app.grs.helper.GRS;
import com.app.grs.helper.GetSet;
import com.libizo.CustomEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class LoginActivity extends AppCompatActivity {

   private Button btnlogin, btngetotp, btnconfirmotp, btnupdatepassword;
   private CustomEditText etmobile, etpassword, etphoneotp, etconfirmotp, etnewpassword, etconfirmnewpassword;
   private ProgressDialog progressDialog;
   private TextView tvcreate, tvforgot;
   private String mobile_no="";
   private ScrollView scroll_layout;
   private LinearLayout otplayout, resetlayout;
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);

        btnlogin = findViewById(R.id.log_btn_login);
        etmobile = findViewById(R.id.log_et_mobile);
        etpassword = findViewById(R.id.log_et_password);
        tvcreate = findViewById(R.id.log_tv_create);
        tvforgot = findViewById(R.id.log_tv_forgot);
        scroll_layout = findViewById(R.id.scroll_layout);
        otplayout = findViewById(R.id.log_otplayout);
        resetlayout = findViewById(R.id.log_resetlayout);
        btngetotp = findViewById(R.id.log_btn_getotp);
        btnconfirmotp = findViewById(R.id.log_btn_confirmotp);
        btnupdatepassword = findViewById(R.id.log_btn_update);
        etphoneotp = findViewById(R.id.log_et_phoneotp);
        etconfirmotp = findViewById(R.id.log_et_confirmotp);
        etnewpassword = findViewById(R.id.log_et_newpassword);
        etconfirmnewpassword = findViewById(R.id.log_et_confirmnewpassword);

        permissioncheck();

        Constants.pref = getApplicationContext().getSharedPreferences("GRS",MODE_PRIVATE);
        Constants.editor = Constants.pref.edit();

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String mobile = etmobile.getText().toString().trim();
                String password = etpassword.getText().toString().trim();

                boolean emptyfeilds = false;

                if (etmobile.getText().toString().trim().length() == 0){
                    emptyfeilds = true;
                    etmobile.setError("Details required");
                }if (etpassword.getText().toString().trim().length() == 0){
                    emptyfeilds = true;
                    etpassword.setError("Details required");
                }if (emptyfeilds == false) {
                    new Login_Async(LoginActivity.this, mobile, password).execute();
                }

            }
        });
        tvcreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
            }
        });

        tvforgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                scroll_layout.setVisibility(View.GONE);
                otplayout.setVisibility(View.VISIBLE);

            }
        });

        btngetotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!etphoneotp.getText().toString().equals("") && etphoneotp.getText().toString().length()>6) {
                    mobile_no = etphoneotp.getText().toString().trim();
                    // new GetOTP(getApplicationContext(), mobile_no).execute();
                    //Toast.makeText(getApplicationContext(),"Otp sent to your mobile successfully",Toast.LENGTH_SHORT).show();
                    try {
                        etphoneotp.setVisibility(View.GONE);
                        btngetotp.setVisibility(View.GONE);
                        etconfirmotp.setVisibility(View.VISIBLE);
                        btnconfirmotp.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else Toast.makeText(LoginActivity.this,"Check your Number!",Toast.LENGTH_SHORT).show();

            }
        });

        btnconfirmotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String otp = etconfirmotp.getText().toString().trim();
                //   new VerifyOTP(getApplicationContext(), GetSet.getMobileno(), otp).execute();
                new VerifyOTP(LoginActivity.this, mobile_no, "1234").execute();
            }
        });

        btnupdatepassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new Forget_Password(LoginActivity.this,mobile_no,etconfirmnewpassword.getText().toString().trim()).execute();

            }
        });

    }

    private void permissioncheck() {

        List<String> permissionsNeeded = new ArrayList<String>();
        final List<String> permissionsList = new ArrayList<String>();
        if (!addPermission(permissionsList, Manifest.permission.READ_EXTERNAL_STORAGE))
            permissionsNeeded.add("Read External Storage");
        if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionsNeeded.add("Write External Storage");
        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                // Need Rationale
                String message = "We need you to grant access to " + permissionsNeeded.get(0);
                for (int i = 1; i < permissionsNeeded.size(); i++)
                    message = message + ", " + permissionsNeeded.get(i);
                showMessageOKCancel(message,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                                            REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                                }
                            }
                        });
                return;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            }
            return;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
            {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                }
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);

                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for ACCESS_FINE_LOCATION
                if (perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(LoginActivity.this, "All Permission Granted",Toast.LENGTH_SHORT).show();
                } else {
                    // Permission Denied
                    Toast.makeText(LoginActivity.this, "Some Permission is Denied",Toast.LENGTH_SHORT).show();

                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(LoginActivity.this)
                .setTitle("Permission Request")
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
    @TargetApi(Build.VERSION_CODES.M)
    private boolean addPermission(List<String> permissionsList, String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permission);
                // Check for Rationale Option
                if (!shouldShowRequestPermissionRationale(permission))
                    return false;
            }
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // For Internet checking
        GRS.registerReceiver(LoginActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // For Internet disconnect checking
        GRS.unregisterReceiver(LoginActivity.this);
    }

    public class VerifyOTP extends AsyncTask<String, Integer, String> {

        private Context context;
        private String mobileno, otp;
        private String url = Constants.BASE_URL + Constants.VERIFY_OTP;
        @Nullable
        String user_id;

        public VerifyOTP(Context ctx, String mobileno, String otp) {
            context = ctx;
            this.mobileno = mobileno;
            this.otp = otp;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Nullable
        @Override
        protected String doInBackground(String... params) {
            String jsonData = null;
            Response response = null;
            OkHttpClient client = new OkHttpClient();
            RequestBody body = new FormBody.Builder()
                    .add(Constants.mobileno, mobileno)
                    .add(Constants.otp, otp)
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
            Log.v("result", "" + jsonData);
            JSONObject jonj = null;
            try {
                jonj = new JSONObject(jsonData);
                if (jonj.getString("status").equalsIgnoreCase(
                        "success")) {
                    scroll_layout.setVisibility(View.GONE);
                    otplayout.setVisibility(View.GONE);
                    resetlayout.setVisibility(View.VISIBLE);
                }else Toast.makeText(getApplicationContext(),"Otp not verified",Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    public class Login_Async extends AsyncTask<String, Integer, String> {


        private Context context;
        private String mobileno, password;
        private String url = Constants.BASE_URL + Constants.LOGIN_USER;
        ProgressDialog progress;
        @Nullable
        String user_id;

        public Login_Async(Context ctx, String mobileno, String password) {
            context = ctx;
            this.mobileno = mobileno;
            this.password = password;
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

        @Nullable
        @Override
        protected String doInBackground(String... params) {
            String jsonData = null;
            Response response = null;
            OkHttpClient client = new OkHttpClient();
            RequestBody body = new FormBody.Builder()
                    .add(Constants.mobileno, mobileno)
                    .add(Constants.password, password)
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

                    String data = jonj.getString("message");
                    JSONArray array = new JSONArray(data);
                    JSONObject jcat = array.getJSONObject(0);

                    GetSet.setIsLogged(true);
                    GetSet.setUserId(jcat.getString("cus_id"));
                    GetSet.setMobileNo(jcat.getString("mobile_no"));


                    Constants.editor.putBoolean("isLogged", true);
                    Constants.editor.putString("userid", GetSet.getUserId());
                    Constants.editor.putString("mobileno", GetSet.getMobileNo());
                    Constants.editor.commit();

                   /* sessionManager.createLoginSession(GetSet.getMobileNo());*/

                    Intent intent = new Intent(context, HomeActivity.class);
                    startActivity(intent);

                    finish();
                } else
                    Toast.makeText(context, jonj.getString("message"), Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    public class Forget_Password extends AsyncTask<String, Integer, String> {

        private Context context;
        private String mobileno,password;
        private String url = Constants.BASE_URL + Constants.FORGOT_PASSWORD;

        public Forget_Password(Context ctx, String mobileno,String password) {
            context = ctx;
            this.mobileno = mobileno;
            this.password = password;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Nullable
        @Override
        protected String doInBackground(String... params) {
            String jsonData = null;
            Response response = null;
            OkHttpClient client = new OkHttpClient();
            RequestBody body = new FormBody.Builder()
                    .add(Constants.mobileno, mobileno)
                    .add("newpassword", password)
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
            Log.v("result", "" + jsonData);
            JSONObject jonj = null;
            try {
                jonj = new JSONObject(jsonData);
                if (jonj.getString("status").equalsIgnoreCase(
                        "success")) {
                    Intent intent=new Intent(context,LoginActivity.class);
                    startActivity(intent);
                    finish();
                    Toast.makeText(getApplicationContext(),"Password Changed Successfully!",Toast.LENGTH_SHORT).show();

                }else Toast.makeText(getApplicationContext(),jonj.getString("message"),Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if ((resetlayout.getVisibility() == View.GONE) && (otplayout.getVisibility() == View.VISIBLE)) {
            otplayout.setVisibility(View.GONE);
            scroll_layout.setVisibility(View.VISIBLE);
        } else {

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoginActivity.this);
            alertDialog.setTitle("Exit");
            alertDialog.setMessage("Are you sure you want to Exit?");
            alertDialog.setIcon(R.drawable.ic_exit_to_app_black_24dp);
            alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                public void onClick(@NonNull DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            alertDialog.show();
        }
    }
}
