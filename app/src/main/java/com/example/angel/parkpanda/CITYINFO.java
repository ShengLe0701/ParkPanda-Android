package com.example.angel.parkpanda;

/**
 * Created by Angel on 7/27/2016.
 */
public class CITYINFO {

    private String lat;
    private String lon;
    private String name;
    private String id;


    public String getId() {
        return id;
    }

    public void setId(String name) {
        this.id = name;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String name) {
        this.lat = name;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String address) {
        this.lon = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String address) {
        this.name = address;
    }

}
