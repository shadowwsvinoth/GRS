package com.app.grs.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.app.grs.R;
import com.app.grs.helper.Constants;
import com.app.grs.helper.GRS;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_splash);

        Constants.pref = getApplicationContext().getSharedPreferences("GRS",MODE_PRIVATE);

        if (GRS.isNetworkAvailable(SplashActivity.this)) {

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    // This method will be executed once the timer is over
                    if (Constants.pref.getBoolean("isLogged", false)) {

                        startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                        finish();

                    } else {

                        Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(i);
                        finish();

                        overridePendingTransition(R.anim.fade_in,
                                R.anim.fade_out);
                    }
                }
            }, 1500);
        }else {

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Network Error");
            alertDialog.setMessage("Please check your Internet Connection!");
            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    finish();
                    System.exit(0);
                }
            });

            alertDialog.show();
        }
    }
}
