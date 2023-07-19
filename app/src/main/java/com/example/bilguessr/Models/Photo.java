package com.example.bilguessr.Models;

import com.google.firebase.firestore.Exclude;

public class Photo {
    private String documentId;
    private String downloadUrl;
    private double longitude;
    private double latitude;
    private int difficulty;

    public Photo()
    {}

    public Photo(String downloadUrl, double longitude, double latitude, int difficulty) {
        this.downloadUrl = downloadUrl;
        this.longitude = longitude;
        this.latitude = latitude;
        this.difficulty = difficulty;
    }
    @Exclude
    public String getDocumentId()
    {
        return documentId;
    }
    public void setDocumentId(String documentId)
    {
        this.documentId = documentId;
    }


    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }
    public int getDifficulty(){return difficulty;}
}
