package com.example.sunshine_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.sunshine.R;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


}