package com.example.fishfinder.data;

public class FishCaptureInfo extends FishInfo{

    /* Variables - (Variable names are same as JSON keys) */
    private String common_name; // From USGS NAS API
    private String FBname;      // From Fish Base API
    private String Species;
    private String BodyShapeI;
    private Double Length;
    private Double Weight;
    private String image;
    private String Dangerous;
    private String Comments;
    private boolean Fresh;
    private boolean Saltwater;

    //new attributes user can fill in.
    //These are variables for what user used to catch the fish or user information generated
    private String userComments;
    private String baitUsed;
    private String weatherCaught;
    private String fishCaughtImage; //a url reference to the image that will be stored in the database, downloaded into database and stored
    public String timeCaught; //when fish is caught date and time

    public int likes; //amount of likes, determines if it should be displayed in the community page or not.



    public FishCaptureInfo() {
        //just an init we can fill in info arbritarily.
    }


    public String getUserComments() {
        return userComments;
    }

    public void setUserComments(String userComments) {
        this.userComments = userComments;
    }

    public String getBaitUsed() {
        return baitUsed;
    }

    public void setBaitUsed(String baitUsed) {
        this.baitUsed = baitUsed;
    }

    public String getWeatherCaught() {
        return weatherCaught;
    }

    public void setWeatherCaught(String weatherCaught) {
        this.weatherCaught = weatherCaught;
    }

    public String getFishCaughtImage() {
        return fishCaughtImage;
    }

    public void setFishCaughtImage(String fishCaughtImage) {
        this.fishCaughtImage = fishCaughtImage;
    }

    public String getTimeCaught() {
        return timeCaught;
    }

    public void setTimeCaught(String timeCaught) {
        this.timeCaught = timeCaught;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }
}
