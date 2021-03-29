package com.example.fishfinder;

public class FishInfo {

    /* Variables - (Variable names are same as JSON keys) */
    private String FBname;
    private String Species;
    private String BodyShapeI;
    private Double Length;
    private Double Weight;
    private String image;
    private String Dangerous;
    private String Comments;
    private boolean Fresh;
    private boolean Saltwater;

    /* Constructor - TODO: Possibly make default values null */
    FishInfo() {
        FBname = null;
        Species = null;
        BodyShapeI = null;
        Length = null;
        Weight = null;
        image = null;
        Dangerous = null;
        Comments = null;
    }

    /* Accessor Methods */

    public String getSpecies() {
        return Species;
    }

    public String getFBname() {
        return FBname;
    }

    public Double getLength() {
        return Length;
    }

    public String getBodyShapeI() {
        return BodyShapeI;
    }

    public Double getWeight() {
        return Weight;
    }

    public String getImage() {
        return image;
    }

    public String getComments() {
        return Comments;
    }

    public String getDangerous() {
        return Dangerous;
    }

    public boolean isFresh() {
        return Fresh;
    }

    public boolean isSaltwater() {
        return Saltwater;
    }

    /* Mutator Methods */

    public void setSpecies(String species) {
        this.Species = species;
    }

    public void setFBname(String FBname) {
        this.FBname = FBname;
    }

    public void setBodyShapeI(String bodyShapeI) {
        BodyShapeI = bodyShapeI;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setLength(Double length) {
        Length = length;
    }

    public void setWeight(Double weight) {
        Weight = weight;
    }

    public void setComments(String comments) {
        Comments = comments;
    }

    public void setDangerous(String dangerous) {
        Dangerous = dangerous;
    }

    public void setFresh(boolean fresh) {
        Fresh = fresh;
    }

    public void setSaltwater(boolean saltwater) {
        Saltwater = saltwater;
    }

    /* Logic Methods */

    @Override
    public String toString() {
        return String.format("<Fish: %s,\nSpecies: %s,\nBodyShape: %s,\nLength: %.2f,\nWeight: %.2f,\nComments: %s>",this.FBname, this.Species, this.BodyShapeI, this.Length, this.Weight, this.Comments);
    }
}
