package com.example.fishfinder.data;

//a test save class to save to database
public class GeneralTest {

    String userId;
    String email;

    String title;
    String latitude;
    String longitude;
    String imgId; //the number + .jpg in the storagereference database

    String fishname;
    String weight;
    String length;
    String genus;
    String species;
    String bait;
    String bodyshape;
    String usercomment;

    //not added yet
//    boolean freshwater;
//    boolean saltwater;
//    String weather; //from weather api

    public GeneralTest() {
        this.title = "";
        this.latitude  = "";
        this.longitude = "";
        this.userId = "";
        this.email = "";
        this.fishname = "";
        this.weight = "";
        this.length = "";
        this.genus = "";
        this.species = "";
        this.bait = "";
        this.bodyshape = "";
        this.usercomment = "";

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

    public String getImgId() {
        return imgId;
    }

    public void setImgId(String imgId) {
        this.imgId = imgId;
    }

    public String getFishname() {
        return fishname;
    }

    public void setFishname(String fishname) {
        this.fishname = fishname;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getGenus() {
        return genus;
    }

    public void setGenus(String genus) {
        this.genus = genus;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getBait() {
        return bait;
    }

    public void setBait(String bait) {
        this.bait = bait;
    }

    public String getBodyshape() {
        return bodyshape;
    }

    public void setBodyshape(String bodyshape) {
        this.bodyshape = bodyshape;
    }

    public String getUsercomment() {
        return usercomment;
    }

    public void setUsercomment(String usercomment) {
        this.usercomment = usercomment;
    }

//    public boolean isFreshwater() {
//        return freshwater;
//    }
//
//    public void setFreshwater(boolean freshwater) {
//        this.freshwater = freshwater;
//    }
//
//    public boolean isSaltwater() {
//        return saltwater;
//    }
//
//    public void setSaltwater(boolean saltwater) {
//        this.saltwater = saltwater;
//    }
//
//    public String getWeather() {
//        return weather;
//    }
//
//    public void setWeather(String weather) {
//        this.weather = weather;
//    }
}
