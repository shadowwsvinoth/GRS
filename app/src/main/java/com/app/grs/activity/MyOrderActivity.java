package com.app.grs.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.app.grs.R;
import com.app.grs.helper.GRS;

public class MyOrderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("My Orders");
        setContentView(R.layout.activity_my_order);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // For Internet checking
        GRS.registerReceiver(MyOrderActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // For Internet disconnect checking
        GRS.unregisterReceiver(MyOrderActivity.this);
    }
}
