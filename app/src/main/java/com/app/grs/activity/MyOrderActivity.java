package com.app.grs.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.app.grs.R;

public class MyOrderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("My Orders");
        setContentView(R.layout.activity_my_order);
    }
}
