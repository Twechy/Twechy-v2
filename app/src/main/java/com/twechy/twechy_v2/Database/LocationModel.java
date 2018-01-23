package com.twechy.twechy_v2.Database;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;
import com.twechy.twechy_v2.MapsActivity;

import java.util.HashMap;
import java.util.Map;

public class LocationModel implements LocationListener {

    private int projectId;
    private Context ctx;
    private String locationName;
    private String locationDescription;
    private byte[] image;
    private Double longitude;
    private Double latitude;
    private Double altitude;
    private MarkerModel marker;
    private float bearing;
    private float accuracy;
    private float speed;
    private float time;
    private LatLng latLng;
    private MarkerModel markerModel;

    public LocationModel() {
    }

    public LocationModel(String locationName, Double longitude, Double latitude, Double altitude, float bearing, float accuracy, float speed) {

        this.locationName = locationName;
        this.longitude = longitude;
        this.latitude = latitude;
        this.altitude = altitude;
        this.bearing = bearing;
        this.accuracy = accuracy;
        this.speed = speed;

    }

    public LocationModel(String locationName, Double longitude, Double latitude, Double altitude, float bearing, float accuracy, float speed, MarkerModel marker) {

        this.locationName = locationName;
        this.longitude = longitude;
        this.latitude = latitude;
        this.altitude = altitude;
        this.bearing = bearing;
        this.accuracy = accuracy;
        this.speed = speed;
        this.markerModel = marker;

    }

    public LocationModel(String name, Double lng, Double lat, long time, String description) {
    }

    public LocationModel(Context ctx, String locationName, Double latitude, Double longitude) {

        this.ctx = ctx;
        this.locationName = locationName;
        this.latitude = latitude;
        this.longitude = longitude;
        markerModel = new MarkerModel(locationName, latitude, longitude);
        latLng = new LatLng(latitude, longitude);

    }

    public LocationModel(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }


    public LocationModel(String locationName, Double longitude, Double latitude, long time, String locationDescription, byte[] image) {
        this.locationName = locationName;
        this.locationDescription = locationDescription;
        this.longitude = longitude;
        this.latitude = latitude;
        this.time = time;
        this.image = image;
    }

    public LocationModel(String string, String string1, double v, double v1, byte[] blob) {

    }

    public LocationModel(String locationName,  double latitude, double longitude, String locationDescription, long time, byte[] image) {
        this.locationName = locationName;
        this.locationDescription = locationDescription;
        this.longitude = longitude;
        this.latitude = latitude;
        this.time = time;
        this.image = image;
    }


    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("projectId", projectId);
        result.put("locationName", locationName);
        result.put("longitude", longitude);
        result.put("latitude", latitude);
        result.put("altitude", altitude);
        result.put("bearing", bearing);
        result.put("speed", speed);
        result.put("time", time);

        return result;
    }


    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }


    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }


    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getAltitude() {
        return altitude;
    }

    public void setAltitude(Double altitude) {
        this.altitude = altitude;
    }

    public float getBearing() {
        return bearing;
    }

    public void setBearing(float bearing) {
        this.bearing = bearing;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }


    public String getLocationDescription() {
        return locationDescription;
    }

    public void setLocationDescription(String locationDescription) {
        this.locationDescription = locationDescription;
    }

    public MarkerModel getMarker() {
        return marker;
    }

    public void setMarker(MarkerModel marker) {
        this.marker = marker;
    }

    @Override
    public void onLocationChanged(Location location) {


        MapsActivity activity = new MapsActivity();
        if (location != null) {


            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

            LocationModel model = new LocationModel(ctx, "location", location.getLatitude(), location.getLongitude());

        }

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
