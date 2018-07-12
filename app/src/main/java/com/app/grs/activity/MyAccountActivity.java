package com.app.grs.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.app.grs.R;
import com.app.grs.helper.Constants;
import com.app.grs.helper.GRS;
import com.app.grs.helper.GetSet;
import com.bumptech.glide.Glide;
import com.libizo.CustomEditText;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyAccountActivity extends AppCompatActivity {

    private CustomEditText etname, etmobile, etaddress1, etemail, etaddress2, etcity, etstate, etpincode;
    String imagepath = "", uploadedImage = "", imageurl = "";
    private ImageView ivprofilepic, ivprofileedit;
    private Button btnsave;
    String image = "";
    String mobileno = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("My Account");
        setContentView(R.layout.activity_my_account);

        etname = findViewById(R.id.prof_et_name);
        etmobile = findViewById(R.id.prof_et_mobile);
        etemail = findViewById(R.id.prof_et_email);
        etcity = findViewById(R.id.prof_et_city);
        etstate = findViewById(R.id.prof_et_state);
        etpincode = findViewById(R.id.prof_et_pincode);
        etaddress1 = findViewById(R.id.prof_et_add1);
        etaddress2 = findViewById(R.id.prof_et_add2);
        ivprofilepic = findViewById(R.id.profilepic_iv);
        ivprofileedit = findViewById(R.id.profile_edit_iv);
        btnsave = findViewById(R.id.prof_btn_save);

        Constants.pref = getApplicationContext().getSharedPreferences("GRS",MODE_PRIVATE);
        Constants.editor = Constants.pref.edit();

        mobileno = Constants.pref.getString("mobileno", "");

        new fetchProfile(MyAccountActivity.this).execute();

        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean emptyfeilds = false;

                if (etmobile.getText().toString().trim().length() == 0) {
                    emptyfeilds = true;
                    etmobile.setError("Details required");
                }if (etname.getText().toString().trim().length() == 0){
                    emptyfeilds = true;
                    etname.setError("Details required");
                }if (etemail.getText().toString().trim().length() == 0){
                    emptyfeilds = true;
                    etemail.setError("Details required");
                }if (etcity.getText().toString().trim().length() == 0){
                    emptyfeilds = true;
                    etcity.setError("Details required");
                }if (etstate.getText().toString().trim().length() == 0){
                    emptyfeilds = true;
                    etstate.setError("Details required");
                }if (etpincode.getText().toString().trim().length() == 0){
                    emptyfeilds = true;
                    etpincode.setError("Details required");
                }if (etaddress1.getText().toString().trim().length() == 0){
                    emptyfeilds = true;
                    etaddress1.setError("Details required");
                }if (emptyfeilds == false){

                    new feedProfile(MyAccountActivity.this, mobileno).execute();

                }

            }
        });
        ivprofileedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent in = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(in, 2);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // For Internet checking
        GRS.registerReceiver(MyAccountActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // For Internet disconnect checking
        GRS.unregisterReceiver(MyAccountActivity.this);
    }

    private class feedProfile extends AsyncTask<String, Integer, String> {

        private Context context;
        private String mobileno;
        private String url = Constants.BASE_URL + Constants.POST_PROFILE;
        ProgressDialog progress;

        public feedProfile(Context context, String mobileno) {
            this.context = context;
            this.mobileno = mobileno;
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

            String name = etname.getText().toString().trim();
            String email = etemail.getText().toString().trim();
            String city = etcity.getText().toString().trim();
            String state = etstate.getText().toString().trim();
            String pincode = etpincode.getText().toString().trim();
            String add1 = etaddress1.getText().toString().trim();
            String add2 = etaddress2.getText().toString().trim();

            String jsonData = null;
            Response response = null;
            OkHttpClient client = new OkHttpClient();
            RequestBody body = new FormBody.Builder()
                    .add(Constants.mobileno, etmobile.getText().toString().trim())
                    .add("name", name)
                    .add("email", email)
                    .add("city", city)
                    .add("state", state)
                    .add("pincode", pincode)
                    .add("address1", add1)
                    .add("address2", add2)
                    .add("userpic", image)
                    .add("userpicurl", imageurl)
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


                    Intent i = new Intent(MyAccountActivity.this, HomeActivity.class);
                    startActivity(i);
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private class uploadProfilePic extends AsyncTask<String, Integer, String>{

        JSONObject jsonobject = null;
        String Json = "";
        ProgressDialog progressDialog;
        String status;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog= new ProgressDialog(MyAccountActivity.this);
            progressDialog.setMessage("Uploading please wait...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... imgpath) {

            HttpURLConnection conn = null;
            DataOutputStream dos = null;
            DataInputStream inStream = null;
            StringBuilder builder = new StringBuilder();
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;
            String urlString = Constants.BASE_URL + Constants.UPLOAD_PROFILEPIC;
            try {
                String exsistingFileName = imgpath[0];
                Log.v(" exsistingFileName", exsistingFileName);
                FileInputStream fileInputStream = new FileInputStream(saveBitmapToFile(new File(exsistingFileName)));
                URL url = new URL(urlString);
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                dos = new DataOutputStream(conn.getOutputStream());
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"images\";filename=\""
                        + exsistingFileName + "\"" + lineEnd);
                dos.writeBytes(lineEnd);
                Log.e("MediaPlayer", "Headers are written");
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                Log.v("buffer", "buffer" + buffer);

                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    Log.v("bytesRead", "bytesRead" + bytesRead);
                }
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        conn.getInputStream()));
                String inputLine;
                Log.v("in", "" + in);
                while ((inputLine = in.readLine()) != null)
                    builder.append(inputLine);

                Log.e("MediaPlayer", "File is written");
                fileInputStream.close();
                Json = builder.toString();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {
                Log.e("MediaPlayer", "error: " + ex.getMessage(), ex);
            } catch (IOException ioe) {
                Log.e("MediaPlayer", "error: " + ioe.getMessage(), ioe);
            }
            try {
                inStream = new DataInputStream(conn.getInputStream());
                String str;
                while ((str = inStream.readLine()) != null) {
                    Log.e("MediaPlayer", "Server Response" + str);
                }
                inStream.close();
            } catch (IOException ioex) {
                Log.e("MediaPlayer", "error: " + ioex.getMessage(), ioex);
            }
            try {
                jsonobject = new JSONObject(Json);
                Log.v("json", "json" + Json);
                status = jsonobject.getString("status");
                if (status.equals("success")) {
                    image = jsonobject.getString("image");
                    imageurl = jsonobject.getString("imageurl");

                }

            } catch (JSONException e) {
                status = "false";
                e.printStackTrace();
            } catch (NullPointerException e) {
                status = "false";
                e.printStackTrace();
            } catch (Exception e) {
                status = "false";
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Log.v("editprofile", "imageupload" + uploadedImage);
            Glide.with(MyAccountActivity.this).load(imageurl).into(ivprofilepic);
            progressDialog.dismiss();
        }
    }

    public void galleryAddPic(String file) {
        File f = new File(file);
        Uri contentUri = Uri.fromFile(f);
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, contentUri);
        sendBroadcast(mediaScanIntent);
    }

    public File saveBitmapToFile(File file) {
        try {

            // BitmapFactory options to downsize the image
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 2;
            o.inPreferredConfig = Bitmap.Config.RGB_565;
            // factor of downsizing the image

            FileInputStream inputStream = new FileInputStream(file);
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();

            // The new size we want to scale to
            final int REQUIRED_SIZE = 1024;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            o2.inPreferredConfig = Bitmap.Config.RGB_565;
            inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();

            // here i override the original image file

            //file.createNewFile();
            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File(sdCard.getAbsolutePath() + "/" + getString(R.string.app_name));
            dir.mkdirs();
            file = new File(dir, String.valueOf(System.currentTimeMillis()) + ".jpg");
            FileOutputStream outputStream = new FileOutputStream(file);
            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream);
            galleryAddPic(file.toString());
            outputStream.flush();
            outputStream.close();

            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private Bitmap decodeFile(String fPath) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        opts.inDither = false;
        opts.inPurgeable = true;
        opts.inInputShareable = true;
        BitmapFactory.decodeFile(fPath, opts);
        final int REQUIRED_SIZE = 1024;
        int scale = 1;

        if (opts.outHeight > REQUIRED_SIZE || opts.outWidth > REQUIRED_SIZE) {
            final int heightRatio = Math.round((float) opts.outHeight
                    / (float) REQUIRED_SIZE);
            final int widthRatio = Math.round((float) opts.outWidth
                    / (float) REQUIRED_SIZE);
            scale = heightRatio < widthRatio ? heightRatio : widthRatio;//
        }
        opts.inJustDecodeBounds = false;
        opts.inSampleSize = scale;
        Bitmap bm = BitmapFactory.decodeFile(fPath, opts).copy(
                Bitmap.Config.RGB_565, false);
        return bm;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Log.v("RESULT_OK", "");
            if (requestCode == 2) {
                try {
                    Uri selectedImage = data.getData();
                    String[] filePath = {MediaStore.Images.Media.DATA};
                    Cursor c = getContentResolver().query(selectedImage,
                            filePath, null, null, null);
                    c.moveToFirst();
                    int columnIndex = c.getColumnIndex(filePath[0]);
                    String picturePath = c.getString(columnIndex);
                    Log.v("path of gallery", picturePath + "");
                    c.close();
                    Bitmap thumbnail = decodeFile(picturePath);
                   Log.v("gallery code bitmap", "" + thumbnail);
                    new uploadProfilePic().execute(picturePath);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                } catch (OutOfMemoryError ome) {
                    ome.printStackTrace();
                }
            }
        } else {
            Log.v("else" + requestCode, "result" + resultCode);
        }
    }


    private class fetchProfile extends AsyncTask<String, Integer, String>{

        private Context context;
        private String url = Constants.BASE_URL + Constants.GET_PROFILE;
        ProgressDialog progress;

        public fetchProfile(Context context) {
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
            RequestBody body = new FormBody.Builder()
                    .add(Constants.mobileno, mobileno)
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
                    JSONObject object = array.getJSONObject(0);

                    GetSet.setIsLogged(true);
                    GetSet.setUserId(object.getString("cus_id"));
                    GetSet.setMobileNo(object.getString("mobile_no"));
                    GetSet.setName(object.getString("name"));
                    GetSet.setEmail(object.getString("email"));
                    GetSet.setCity(object.getString("city"));
                    GetSet.setState(object.getString("state"));
                    GetSet.setPincode(object.getString("post_code"));
                    GetSet.setAddress1(object.getString("address1"));
                    GetSet.setAddress2(object.getString("address2"));
                    GetSet.setUserpic(object.getString("userimage"));
                    GetSet.setUserpicurl(object.getString("userimageurl"));

                    Constants.editor.putString("cus_id", GetSet.getUserId());
                    Constants.editor.putString("name", GetSet.getName());
                    Constants.editor.putString("email", GetSet.getEmail());
                    Constants.editor.putString("mobileno", GetSet.getMobileNo());
                    Constants.editor.putString("city", GetSet.getCity());
                    Constants.editor.putString("state", GetSet.getState());
                    Constants.editor.putString("pincode", GetSet.getPincode());
                    Constants.editor.putString("address1", GetSet.getAddress1());
                    Constants.editor.putString("address2", GetSet.getAddress2());
                    Constants.editor.putString("userpic", GetSet.getUserpic());
                    Constants.editor.putString("userpicurl", GetSet.getUserpicurl());
                    Constants.editor.commit();
                    Constants.editor.apply();

                    setdata();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
    private void setdata() {
        if (!Constants.pref.getString("userpicurl", "").equalsIgnoreCase("")) {
            image = Constants.pref.getString("userpicurl", "");
            Glide
                    .with(MyAccountActivity.this)
                    .load(image)
                    .into(ivprofilepic);
        }else {
            Glide
                    .with(MyAccountActivity.this)
                    .load(R.drawable.grslogo)
                    .into(ivprofilepic);
        }

        if (GetSet.getMobileNo() != null && !GetSet.getMobileNo().equalsIgnoreCase("")) {

            etmobile.setText(GetSet.getMobileNo());
            etmobile.setEnabled(false);
        }

        etname.setText(GetSet.getName());
        etcity.setText(GetSet.getCity());
        etemail.setText(GetSet.getEmail());
        etstate.setText(GetSet.getState());
        etpincode.setText(GetSet.getPincode());
        etaddress1.setText(GetSet.getAddress1());
        etaddress2.setText(GetSet.getAddress2());

    }
}
