package com.twechy.twechy_v2.LocationsToDatabase;


public class ShowLocationItems {

    private String locationName;
    private String locationDescription;
    private String latitude;
    private String longitude;
    private String locationImage;

    public ShowLocationItems() {}


    public ShowLocationItems(String locationName, String locationDescription, String latitude, String longitude, String locationImage) {
        this.setLocationName(locationName);
        this.setLocationDescription(locationDescription);
        this.setLatitude(latitude);
        this.setLongitude(longitude);
        this.locationImage = locationImage;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getLocationDescription() {
        return locationDescription;
    }

    public void setLocationDescription(String locationDescription) {
        this.locationDescription = locationDescription;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLocationImage() {
        return locationImage;
    }

    public void setLocationImage(String locationImage) {
        this.locationImage = locationImage;
    }
}
