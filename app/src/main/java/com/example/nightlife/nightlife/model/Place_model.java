package com.example.nightlife.nightlife.model;

public class Place_model {

    public String name,address,phoneno,addedby,type;
    public double lat,lon;

    public Place_model() {
    }

    public Place_model(String name, String address, String phoneno, String addedby, double lat, double lon, String type) {
        this.name = name;
        this.address = address;
        this.phoneno = phoneno;
        this.addedby = addedby;
        this.lat = lat;
        this.lon = lon;
        this.type=type;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhoneno() {
        return phoneno;
    }

    public String getAddedby() {
        return addedby;
    }

    public String getType() {
        return type;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }
}
