package com.twechy.twechy_v2.Database;


import com.google.android.gms.maps.model.LatLng;

public class MarkerModel {

    private int id;
    private Double markerLatitude;
    private Double markerLongitude;
    private String markerName;
    private LatLng markerLatLng;

    MarkerModel() {
    }

    public MarkerModel(int id, String title, Double latitude, Double longitude, LatLng latLng) {

        this.id=id;
        this.setMarkerName(title);
        this.setMarkerLatitude(latitude);
        this.setMarkerLongitude(longitude);
        this.markerLatLng = new LatLng(latitude, longitude);
    }

    public MarkerModel(String locationAddress, double latitude, double longitude) {
        this.markerName =locationAddress;
        this.markerLatitude =latitude;
        this.markerLongitude =longitude;
    }

    public MarkerModel(double latitude, double longitude) {
        this.markerLatitude =latitude;
        this.markerLongitude =longitude;
    }


    public Double getMarkerLatitude() {
        return markerLatitude;
    }

    public void setMarkerLatitude(Double markerLatitude) {
        this.markerLatitude = markerLatitude;
    }

    public Double getMarkerLongitude() {
        return markerLongitude;
    }

    public void setMarkerLongitude(Double markerLongitude) {
        this.markerLongitude = markerLongitude;
    }

    public String getMarkerName() {
        return markerName;
    }

    public void setMarkerName(String markerName) {
        this.markerName = markerName;
    }

    public LatLng getMarkerLatLng() {
        return markerLatLng;
    }

    public void setMarkerLatLng(LatLng markerLatLng) {
        this.markerLatLng = markerLatLng;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
