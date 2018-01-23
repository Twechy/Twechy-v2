package com.twechy.twechy_v2;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.twechy.twechy_v2.Database.LocationModel;

import static com.twechy.twechy_v2.MainActivity.helper;

public class MarkerInfo extends AppCompatActivity{

    LocationModel model;

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.marker_info);

        Intent i = getIntent();
        String markerName = i.getStringExtra("markerName");

        if (markerName != null) {

            model = helper.showLocationFromDb(markerName);

        }
    }

}
