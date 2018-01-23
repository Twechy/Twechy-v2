package com.twechy.twechy_v2;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseGeoPoint;
import com.twechy.twechy_v2.Database.DbBitmapUtility;
import com.twechy.twechy_v2.Database.LocationModel;
import com.twechy.twechy_v2.Database.Location_Helper;
import com.twechy.twechy_v2.Database.MarkerModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.twechy.twechy_v2.MainActivity.gps_Location;
import static com.twechy.twechy_v2.MainActivity.helper;
import static com.twechy.twechy_v2.MainActivity.locationModel;
import static com.twechy.twechy_v2.MainActivity.markers;
import static com.twechy.twechy_v2.MainActivity.places;


interface LatLngInterpolator {

    LatLng interpolate(float fraction, LatLng a, LatLng b);

    class LinearFixed implements LatLngInterpolator {
        @Override
        public LatLng interpolate(float fraction, LatLng a, LatLng b) {
            double lat = (b.latitude - a.latitude) * fraction + a.latitude;
            double lngDelta = b.longitude - a.longitude;
            // Take the shortest path across the 180th meridian.
            if (Math.abs(lngDelta) > 180) {
                lngDelta -= Math.signum(lngDelta) * 360;
            }
            double lng = lngDelta * fraction + a.longitude;
            return new LatLng(lat, lng);
        }
    }
}

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        LocationListener, ResultCallback<Status>,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnInfoWindowLongClickListener,
        GoogleMap.OnMapClickListener {

    private static final int REQUEST_LOCATION = 0;
    private TextView latitudeView, longitudeView, distanceToMarker;
    private ImageView getCurrentLocation, zoomDown, zoomUp, eraseBtn, exploreBtn, navigationUP, navigationDown;
    private EditText currentLocationName;
    private Marker mk = null;
    private Context context;
    private TwechyTracker twechyTracker;
    private GoogleMap mMap;
    private LocationManager manager;
    private String provider;
    private Location location;
    private Double latitudeFromList, longitudeFromList;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private String currentLocationFromList;
    private int markerCount = 0;
    private LocationModel newModel;
    private LatLng currentLatLng;
    private List<Location> locationModels;
    private Location mLastLocation;
    private long time;

    public static void animateMarker(final android.location.Location destination, final Marker marker) {
        if (marker != null && destination != null) {
            final LatLng startPosition = marker.getPosition();
            final LatLng endPosition = new LatLng(destination.getLatitude(), destination.getLongitude());

            final float startRotation = marker.getRotation();

            final LatLngInterpolator latLngInterpolator = new LatLngInterpolator.LinearFixed();
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(2000); // duration 2 second
            valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    try {
                        float v = animation.getAnimatedFraction();
                        LatLng newPosition = latLngInterpolator.interpolate(v, startPosition, endPosition);
                        marker.setPosition(newPosition);
                        marker.setRotation(computeRotation(v, startRotation, destination.getBearing()));
                    } catch (Exception ex) {
                        // I don't care atm..
                    }
                }
            });

            valueAnimator.start();
        }
    }

    private static float computeRotation(float fraction, float start, float end) {
        float normalizeEnd = end - start; // rotate start to 0
        float normalizedEndAbs = (normalizeEnd + 360) % 360;

        float direction = (normalizedEndAbs > 180) ? -1 : 1; // -1 = anticlockwise, 1 = clockwise
        float rotation;
        if (direction > 0) {
            rotation = normalizedEndAbs;
        } else {
            rotation = normalizedEndAbs - 360;
        }

        float result = fraction * rotation + start;
        return (result + 360) % 360;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        context = getApplicationContext();

        latitudeView = findViewById(R.id.latitude);
        longitudeView = findViewById(R.id.longitude);
        distanceToMarker = findViewById(R.id.distancetToMarker);
        currentLocationName = findViewById(R.id.currentLocationName);
        navigationUP = findViewById(R.id.navigation_Up);
        navigationDown = findViewById(R.id.navigation_Down);
        getCurrentLocation = findViewById(R.id.currentLocation);
        zoomUp = findViewById(R.id.zoomUp);
        zoomDown = findViewById(R.id.zoomDown);
        eraseBtn = findViewById(R.id.eraseBtn);
        exploreBtn = findViewById(R.id.exploreBtn);

        iconFunc();

        twechyTracker = new TwechyTracker(this);
        locationModels = new ArrayList<>();
        locationModel = helper.locationToListView();
        openDp();


        if (getServicesAvailable()) {
            // Building the GoogleApi client
            buildGoogleApiClient();
            createLocationRequest();
            Toast.makeText(this, "Google Service Is Available!!", Toast.LENGTH_SHORT).show();
        }


        Intent i = getIntent();


        currentLocationFromList = i.getStringExtra("LocationName");
        latitudeFromList = i.getDoubleExtra("locationLatitude", -1);
        longitudeFromList = i.getDoubleExtra("locationLongitude", -1);

        if (latitudeFromList != -1 && latitudeFromList != 0 && longitudeFromList != -1 && longitudeFromList != 0) {

            newModel = new LocationModel(context, currentLocationFromList, latitudeFromList, longitudeFromList);
            Log.i("latitudeFromList", String.valueOf(latitudeFromList));
            Log.i("longitudeFromList", String.valueOf(longitudeFromList));
        }


        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = manager.getBestProvider(new Criteria(), false);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        location = manager.getLastKnownLocation(provider);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void iconFunc() {
        navigationUpBitmap();
        navigationDownBitmap();
        zoomUpIcon();
        zoomDownIcon();
        currentLocationBitmap();
        eraseBitmap();
        exploreBitmap();
    }

    private void currentLocationBitmap() {
        int height2 = 80;
        int width2 = 80;
        BitmapDrawable drawable2 = (BitmapDrawable) getResources().getDrawable(R.mipmap.ic_current_location);
        Bitmap b2 = drawable2.getBitmap();
        Bitmap smallMarker2 = Bitmap.createScaledBitmap(b2, width2, height2, false);
        getCurrentLocation.setImageBitmap(smallMarker2);
    }

    private void navigationDownBitmap() {
        int height2 = 100;
        int width2 = 100;
        BitmapDrawable drawable2 = (BitmapDrawable) getResources().getDrawable(R.mipmap.ic_map_navigation_down);
        Bitmap b2 = drawable2.getBitmap();
        Bitmap smallMarker2 = Bitmap.createScaledBitmap(b2, width2, height2, false);
        navigationDown.setImageBitmap(smallMarker2);
    }

    private void navigationUpBitmap() {
        int height2 = 100;
        int width2 = 100;
        BitmapDrawable drawable2 = (BitmapDrawable) getResources().getDrawable(R.mipmap.ic_map_navigation_up);
        Bitmap b2 = drawable2.getBitmap();
        Bitmap smallMarker2 = Bitmap.createScaledBitmap(b2, width2, height2, false);
        navigationUP.setImageBitmap(smallMarker2);
    }

    private void exploreBitmap() {
        int height2 = 80;
        int width2 = 80;
        BitmapDrawable drawable2 = (BitmapDrawable) getResources().getDrawable(R.mipmap.ic_explore);
        Bitmap b2 = drawable2.getBitmap();
        Bitmap smallMarker2 = Bitmap.createScaledBitmap(b2, width2, height2, false);
        exploreBtn.setImageBitmap(smallMarker2);
    }

    private void eraseBitmap() {
        int height2 = 80;
        int width2 = 80;
        BitmapDrawable drawable2 = (BitmapDrawable) getResources().getDrawable(R.mipmap.ic_erase);
        Bitmap b2 = drawable2.getBitmap();
        Bitmap smallMarker2 = Bitmap.createScaledBitmap(b2, width2, height2, false);
        eraseBtn.setImageBitmap(smallMarker2);
    }

    private void zoomUpIcon() {
        int height2 = 75;
        int width2 = 75;
        BitmapDrawable drawable2 = (BitmapDrawable) getResources().getDrawable(R.mipmap.ic_zoom_up);
        Bitmap b2 = drawable2.getBitmap();
        Bitmap smallMarker2 = Bitmap.createScaledBitmap(b2, width2, height2, false);
        zoomUp.setImageBitmap(smallMarker2);
    }

    private void zoomDownIcon() {
        int height2 = 75;
        int width2 = 75;
        BitmapDrawable drawable2 = (BitmapDrawable) getResources().getDrawable(R.mipmap.ic_zoom_down);
        Bitmap b2 = drawable2.getBitmap();
        Bitmap smallMarker2 = Bitmap.createScaledBitmap(b2, width2, height2, false);
        zoomDown.setImageBitmap(smallMarker2);
    }

    public Double distanceBetween(Marker marker) {

        double distance = 0.0;
        if (location != null) {

            ParseGeoPoint parseGeoPointStart, parseGeoPointEnd;
            LatLng start, end;

            LocationModel startLocation, endLocation;

            startLocation = new LocationModel(location.getLatitude(), location.getLongitude());
            endLocation = new LocationModel(marker.getPosition().latitude, marker.getPosition().longitude);

            parseGeoPointStart = new ParseGeoPoint(startLocation.getLatitude(), startLocation.getLongitude());
            parseGeoPointEnd = new ParseGeoPoint(endLocation.getLatitude(), endLocation.getLongitude());

            distance = parseGeoPointStart.distanceInKilometersTo(parseGeoPointEnd);


            /*start = new LatLng(startLocation.getLatitude(), startLocation.getLongitude());
            end = new LatLng(endLocation.getLatitude(), endLocation.getLongitude());
            LatLngBounds bounds = new LatLngBounds(start, end);*/
        }
        return distance;
    }

    private void openDp() {

        helper = new Location_Helper(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);


        markers = helper.getAllMarker();

        mMap.setOnMapLongClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMarkerClickListener(this);

        if (newModel != null) {

            displayMarkers();

            currentLatLng = new LatLng(newModel.getLatitude(), newModel.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 20));
            mMap.addMarker(new MarkerOptions().position(currentLatLng));

            latLngToMap(location);
            //Toast.makeText(context, currentLatLng + "not null", Toast.LENGTH_SHORT).show();

        } else if (location != null) {

            displayMarkers();

            currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 20));
            mMap.addMarker(new MarkerOptions().position(currentLatLng));

            latLngToMap(location);
            //Toast.makeText(context, "opening map from location", Toast.LENGTH_SHORT).show();

        }
    }

    public void getLocation(View view) {

        final String locationAddress = currentLocationName.getText().toString();

        if (locationAddress.isEmpty()) {
            Toast.makeText(context, "please Enter Location Name", Toast.LENGTH_SHORT).show();
        } else {
            {
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                //android.location.Location location = manager.getLastKnownLocation(provider);
                locationModels.add(location);

                final Location location = twechyTracker.getLocation();

                if (location != null) {

                    currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    Date date = new Date();
                    date.getTime();

                    final Double lat = Double.valueOf(String.valueOf(location.getLatitude()));
                    Log.i("LocationInfo:lat ", String.valueOf(lat));
                    final Double lng = Double.valueOf(String.valueOf(location.getLongitude()));
                    Log.i("LocationInfo:lng ", String.valueOf(lng));
                    final Double alt = Double.valueOf(String.valueOf(location.getAltitude()));
                    Log.i("LocationInfo:alt ", String.valueOf(alt));
                    final float bearing = location.getBearing();
                    Log.i("LocationInfo:bearing ", String.valueOf(bearing));
                    final float speed = location.getSpeed();
                    Log.i("LocationInfo:speed ", String.valueOf(speed));
                    final long time = date.getTime();
                    Log.i("LocationInfo:time ", String.valueOf(time));

                    MarkerModel markerModel = new MarkerModel(locationAddress, lat, lng);

                    LocationModel myLocationModel = new LocationModel(locationAddress, lat, lng, alt, bearing, speed, time);

                    places.add(locationAddress);
                    gps_Location.add(currentLatLng);
                    locationModel.add(myLocationModel);
                    markers.add(markerModel);

                    for (LocationModel i : locationModel) {

                        Log.i("locations", String.valueOf(i.toMap()));
                    }

                    //twechyAdapter.notifyDataSetChanged();
                    //twechyAdapter.update(locationModel);

                    AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                    builder.setMessage("Do you want to Upload  this location to Firebase ?").setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(context, SaveLocationDescription.class);
                                    intent.putExtra("locationName", locationAddress);
                                    intent.putExtra("locationLatitude", lat);
                                    intent.putExtra("locationLongitude", lng);
                                    intent.putExtra("locationTime", time);
                                    startActivity(intent);

                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            mMap.addMarker(new MarkerOptions().position(currentLatLng).title(locationAddress).icon(BitmapDescriptorFactory.defaultMarker()));
                            addMarker(mMap, location.getLatitude(), location.getLongitude(), locationAddress);

                            dialogInterface.cancel();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.setTitle("Confirm");
                    dialog.show();


                } else {
                    Toast.makeText(this, "Try Again..", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        getGpsLocation();
        mGoogleApiClient.connect();
        manager.requestLocationUpdates(provider, 500, 1, this);
        Toast.makeText(context, "google Api is connected..", Toast.LENGTH_SHORT).show();

        startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        manager.removeUpdates(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.requestLocationUpdates(provider, 500, 2, this);


        // Resuming the periodic location updates
        if (location != null) {
            getServicesAvailable();
            startLocationUpdates();
        }
    }

    private void getGpsLocation() {

        if (getServicesAvailable()) {

            // Building the GoogleApi client
            buildGoogleApiClient();
            createLocationRequest();
            startLocationUpdates();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            manager.getLastKnownLocation(provider);
            Toast.makeText(this, "Google Service Is Available!!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        if (location != null) {

            LatLng pos = new LatLng(location.getLatitude(), location.getLongitude());
            mk = mMap.addMarker(new MarkerOptions().position(pos).icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_car)));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 25));

            mk.remove();
            displayLocation();
            latLngToMap(location);
        }
    }


    private void latLngToMap(Location location) {

        if (location != null) {
            Double lat = location.getLatitude();
            Double latToMap = (double) Math.round(lat * 10) / 10;
            Double lng = location.getLongitude();
            Double lngToMap = (double) Math.round(lng * 10) / 10;
            latitudeView.setText(String.format("Latitude: %s", latToMap));
            longitudeView.setText(String.format("Longitude: %s", lngToMap));
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onResult(@NonNull Status status) {

        String message = status.getStatusMessage();
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    //Starting the location updates
    protected void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Check Permissions Now
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        } else {
            if (manager.isProviderEnabled(provider)) {
                manager.getLastKnownLocation(provider);
            }
        }
    }

    // Add A Map Pointer To The MAp
    public void addMarker2(GoogleMap googleMap, double lat, double lon) {

        if (markerCount == 1) {
            animateMarker(mLastLocation, mk);
        } else if (markerCount == 0) {
            //Set Custom BitMap for Pointer
            int height = 80;
            int width = 45;
            BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.mipmap.icon_car);
            Bitmap b = bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
            mMap = googleMap;

            LatLng latlong = new LatLng(lat, lon);
            mk = mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon))
                    //.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin3))
                    .icon(BitmapDescriptorFactory.fromBitmap((smallMarker))));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlong, 16));

            //Set Marker Count to 1 after first marker is created
            markerCount = 1;

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                return;
            }
            mMap.setMyLocationEnabled(true);
            startLocationUpdates();
        }
    }

    public void addMarker(GoogleMap googleMap, double lat, double lng, String title) {

        if (markerCount == 0) {

            animateMarker(location, mk);
            //Set Custom BitMap for Pointer
            int height = 80;
            int width = 45;
            BitmapDrawable drawable = (BitmapDrawable) getResources().getDrawable(R.mipmap.ic_location_icon);
            Bitmap b = drawable.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
            mMap = googleMap;

            LatLng latLng = new LatLng(lat, lng);
            mk = mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng))
                    .title(title));

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));
            markerCount++;

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            startLocationUpdates();
        }
    }

    //Method to display the location on UI
    private void displayLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            // Check Permissions Now
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        } else {


            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            /*if (twechyTracker.canGetLocation()) {

                mLastLocation = twechyTracker.getLocation();
                Toast.makeText(context, "Twechy Tracker is enabled", Toast.LENGTH_SHORT).show();
                Toast.makeText(context, mLastLocation.getProvider(), Toast.LENGTH_SHORT).show();
            }*/

            if (mLastLocation != null) {
                double latitude = mLastLocation.getLatitude();
                double longitude = mLastLocation.getLongitude();
                String loc = "" + latitude + " ," + longitude + " ";
                Toast.makeText(this, loc, Toast.LENGTH_SHORT).show();

                //Add pointer to the map at location
                addMarker2(mMap, latitude, longitude);


            } else {

                Toast.makeText(this, "Couldn't get the location. Make sure location is enabled on the device",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Creating google api client object
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    //Creating location request object
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(3000);
        mLocationRequest.setFastestInterval(3000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(10);
    }

    public boolean getServicesAvailable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(this);
        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (api.isUserResolvableError(isAvailable)) {

            Dialog dialog = api.getErrorDialog(this, isAvailable, 0);
            dialog.show();
        } else {
            Toast.makeText(this, "Cannot Connect To Play Services", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        // Retrieve the data from the marker.

        Integer clickCount = (Integer) marker.getTag();

        // Check if a click count was set, then display the click count.
        if (clickCount != null) {
            //clickCount = clickCount + 1;
            String latLng = "Latitude: " + marker.getPosition().latitude + "Longitude: " + marker.getPosition().longitude;
            Toast.makeText(this, latLng, Toast.LENGTH_SHORT).show();
            marker.setTag(clickCount);
            marker.showInfoWindow();
            distanceToNextMarker(marker);
        }
        return false;
    }

    private void distanceToNextMarker(Marker marker) {

        Double distance;
        Double currentLat = marker.getPosition().latitude;
        Double currentLng = marker.getPosition().longitude;

        distance = distanceBetween(marker);
        Double distanceOnrDp = (double) Math.round(distance * 10) / 10;

        distanceToMarker.setText(String.format("%s Km to %s", distanceOnrDp, marker.getTitle()));

    }

    @Override
    public void onInfoWindowClick(Marker marker) {

        String locationName = marker.getTitle();

        Intent i = new Intent(context, MarkerInfo.class);
        i.putExtra("markerName", locationName);
        startActivity(i);

    }

    @Override
    public void onInfoWindowLongClick(Marker marker) {


    }

    @Override
    public void onMapLongClick(LatLng latLng) {

        /*if (latLng != null) {

            mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker()));
            MarkerModel markerModel = new MarkerModel(latLng.latitude, latLng.longitude);

            //helper.addMarker(markerModel);
        }*/
    }

    public void displayMarkers() {

        locationModel = helper.locationToListView();
        if (locationModel != null) {

            for (LocationModel i : locationModel) {

                final LatLng markerLatLng = new LatLng(i.getLatitude(), i.getLongitude());

                Double lat = markerLatLng.latitude;
                Double latToMap = (double) Math.round(lat * 10) / 10;
                Double lng = markerLatLng.longitude;
                Double lngToMap = (double) Math.round(lng * 10) / 10;

                byte[] image = i.getImage();
                Bitmap bitmap = DbBitmapUtility.getImage(image);

                Marker marker = mMap.addMarker(new MarkerOptions().position(markerLatLng).title(i.getLocationName())
                        .snippet("Latitude: " + latToMap + " " + "Longitude: " + lngToMap));
                marker.setTag(0);
                marker.showInfoWindow();
            }
        }
    }

    public void clearMap(View view) {

        mMap.clear();
    }

    public void zoomDown(View view) {
        mMap.moveCamera(CameraUpdateFactory.zoomOut());
    }

    public void zoomUp(View view) {
        mMap.moveCamera(CameraUpdateFactory.zoomIn());
    }

    public void getCurrentLocation(View view) {

        if (location != null) {

            if (getServicesAvailable()) {
                LatLng pos = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 20));
                mMap.addMarker(new MarkerOptions().position(pos).title("CurrentLocation"));

                Toast.makeText(context, "Getting the Current Location..", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Cant't Find Current Location ", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void navigationUp(View view) {

        eraseBtn.animate().alpha(1f).setDuration(1000);
        exploreBtn.animate().alpha(1f).setDuration(1000);
        getCurrentLocation.animate().alpha(1f).setDuration(1000);

        navigationUP.setVisibility(View.INVISIBLE);
        navigationDown.setVisibility(View.VISIBLE);

    }

    public void navigationDown(View view) {

        eraseBtn.animate().alpha(0).setDuration(1000);
        exploreBtn.animate().alpha(0).setDuration(1000);
        getCurrentLocation.animate().alpha(0).setDuration(1000);

        navigationDown.setVisibility(View.INVISIBLE);
        navigationUP.setVisibility(View.VISIBLE);
    }

    public void eraseMarkers(View view) {

        mMap.clear();
    }

    public void displayMarkers(View view) {

        displayMarkers();
    }

    @Override
    public void onMapClick(LatLng latLng) {
/*
        mMap.clear();

        MarkerOptions markerOptions = new MarkerOptions();

        markerOptions.position(latLng);
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        Marker marker = mMap.addMarker(markerOptions);
        marker.showInfoWindow();
*/
    }
}
