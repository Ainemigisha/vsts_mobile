package com.example.alvin.vstsmobile;

import com.google.android.gms.maps.model.Marker;

import java.io.Serializable;

public class Locations implements Serializable {

    private int id;
    private int lf_id;
    private int flag;
    private Double speed;
    private Double latitude;
    private Double longitude;
    private Marker marker;

    private String number_plate;
    private String bus_company;
    private String driver_name;


    public Locations(int id, int lf_id, Double speed, Double latitude, Double longitude, String number_plate, String bus_company, String driver_name, int flag){
        this.id = id;
        this.lf_id = lf_id;
        this.latitude = latitude;
        this.speed = speed;
        this.latitude = latitude;
        this.longitude = longitude;
        this.number_plate = number_plate;
        this.bus_company = bus_company;
        this.driver_name = driver_name;
        this.flag = flag;


    }

    public int getFlag() {
        return flag;
    }

    public int getId() {
        return id;
    }

    public int getLf_id() {
        return lf_id;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getSpeed() {
        return speed;
    }

    public String getBus_company() {
        return bus_company;
    }

    public String getDriver_name() {
        return driver_name;
    }

    public String getNumber_plate() {
        return number_plate;
    }



    public Marker getMarker() {
        return marker;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setLf_id(int lf_id) {
        this.lf_id = lf_id;
    }

    public void setBus_company(String bus_company) {
        this.bus_company = bus_company;
    }

    public void setDriver_name(String driver_name) {
        this.driver_name = driver_name;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public void setNumber_plate(String number_plate) {
        this.number_plate = number_plate;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
}
