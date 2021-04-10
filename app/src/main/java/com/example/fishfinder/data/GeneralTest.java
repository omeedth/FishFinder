package com.example.fishfinder.data;

//a test save class to save to database
public class GeneralTest {

    String userId;
    String email;

    String title;
    String latitude;
    String longitude;


    public GeneralTest() {
        this.title = "";
        this.latitude  = "";
        this.longitude = "";
        this.userId = "";
        this.email = "";

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
