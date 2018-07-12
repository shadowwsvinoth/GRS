package com.app.grs.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.app.grs.R;

public class ContactActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Contact Us");
        setContentView(R.layout.activity_contact);
    }
}
