package com.twechy.twechy_v2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.twechy.twechy_v2.Database.LocationModel;
import com.twechy.twechy_v2.Database.Location_Helper;
import com.twechy.twechy_v2.Database.MarkerModel;
import com.twechy.twechy_v2.Database.WeatherModel;
import com.twechy.twechy_v2.LocationsToDatabase.UploadInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    static Location_Helper helper;
    static ArrayList<String> places;
    static List<LocationModel> locationModel = Collections.emptyList();
    static List<WeatherModel> weatherModels = Collections.emptyList();
    static List<LatLng> gps_Location = Collections.emptyList();
    static List<MarkerModel> markers = Collections.emptyList();
    TextView riderOrDriverButton;
    Switch riderOrDriverSwitch;
    Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ctx = getApplicationContext();
        helper = new Location_Helper(ctx);

        places = new ArrayList<>();
        locationModel = new ArrayList<>();
        locationModel.add(new LocationModel());
        weatherModels = new ArrayList<>();
        gps_Location = new ArrayList<>();
        markers = new ArrayList<>();
        gps_Location.add(new LatLng(0, 0));

        riderOrDriverButton = findViewById(R.id.getStartedButtun);
        riderOrDriverSwitch = findViewById(R.id.riderOrDriverSwitch);

        riderOrDriverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (riderOrDriverSwitch.isChecked()) {

                    startActivity(new Intent(ctx, MapsActivity.class).putExtra("LocationType", "newMap"));
                    Toast.makeText(getApplicationContext(), "My Location", Toast.LENGTH_SHORT).show();
                } else {

                    startActivity(new Intent(ctx, LocationList.class));
                    Toast.makeText(getApplicationContext(), "Database", Toast.LENGTH_SHORT).show();
                }
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.locationFromFirebase) {

            startActivity(new Intent(getApplicationContext(), UploadInfo.class));

        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
