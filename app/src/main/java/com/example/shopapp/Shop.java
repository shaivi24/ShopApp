package com.example.shopapp;

import java.io.Serializable;

public class Shop implements Serializable {

    private int id;
    private String companyName;
    private String contactName;
    private String mobileNumber;
    private String image;
    private String latitude;
    private String longitude;

    public Shop() {
    }

    public Shop(int id, String companyName, String contactName, String mobileNumber, String image,
                String latitude, String longitude, long timeStamp) {
        this.id = id;
        this.companyName = companyName;
        this.contactName = contactName;
        this.mobileNumber = mobileNumber;
        this.image = image;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timeStamp = timeStamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    private long timeStamp;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }
}
