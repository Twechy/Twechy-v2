package com.twechy.twechy_v2.Database;

import java.util.HashMap;
import java.util.Map;

public class WeatherModel {

    private int id;
    private String locationName;
    private String pressure;
    private String humidity;
    private String sunrise;
    private String sunset;
    private String conditionTitle;
    private String currentTemp;

    public WeatherModel(int id, String locationName, String pressure, String humidity, String sunrise, String sunset, String conditionTitle, String currentTemp){

        this.id=id;
        this.setLocationName(locationName);
        this.setPressure(pressure);
        this.setHumidity(humidity);
        this.setSunrise(sunrise);
        this.setSunset(sunset);
        this.setConditionTitle(conditionTitle);
        this.setCurrentTemp(currentTemp);
    }

    public WeatherModel(String locationName, String pressure, String humidity, String sunrise, String sunset, String conditionTitle, String temp) {
        this.setLocationName(locationName);
        this.setPressure(pressure);
        this.setHumidity(humidity);
        this.setSunrise(sunrise);
        this.setSunset(sunset);
        this.setConditionTitle(conditionTitle);
        this.setCurrentTemp(temp);

    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();

        result.put("locationName", getLocationName());
        result.put("pressure", getPressure());
        result.put("humidity", getHumidity());
        result.put("sunrise", getSunrise());
        result.put("sunset", getSunset());
        result.put("conditionTitle", getConditionTitle());
        result.put("currentTemp", getCurrentTemp());


        return result;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getSunrise() {
        return sunrise;
    }

    public void setSunrise(String sunrise) {
        this.sunrise = sunrise;
    }

    public String getSunset() {
        return sunset;
    }

    public void setSunset(String sunset) {
        this.sunset = sunset;
    }

    public String getConditionTitle() {
        return conditionTitle;
    }

    public void setConditionTitle(String conditionTitle) {
        this.conditionTitle = conditionTitle;
    }

    public String getCurrentTemp() {
        return currentTemp;
    }

    public void setCurrentTemp(String currentTemp) {
        this.currentTemp = currentTemp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
