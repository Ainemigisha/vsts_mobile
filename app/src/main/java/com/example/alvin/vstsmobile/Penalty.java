package com.example.alvin.vstsmobile;

import java.io.Serializable;
import java.util.Date;

public class Penalty implements Serializable {

    private int lf_id;
    private int location_id;
    private int assigner_id;
    private Date assigned_at;
    private Date cleared_at;
    private Double latitude;
    private Double longitude;
    private int clearer_id;
    private String clearer_name;
    private String status;
    private Double speed;
    private String company;
    private String place;
    private String num_plate;


    public Penalty(int location_id,int lf_id,int assigner_id, Double latitude, Double longitude, String status, Double speed){
        this.location_id = location_id;
        this.lf_id = lf_id;
        this.assigner_id = assigner_id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
        this.speed = speed;
    }

    public void setLocation_id(int location_id) {
        this.location_id = location_id;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setAssigned_at(Date assigned_at) {
        this.assigned_at = assigned_at;
    }

    public void setAssigner_id(int assigner_id) {
        this.assigner_id = assigner_id;
    }

    public void setCleared_at(Date cleared_at) {
        this.cleared_at = cleared_at;
    }

    public void setClearer_id(int clearer_id) {
        this.clearer_id = clearer_id;
    }

    public void setLf_id(int lf_id) {
        this.lf_id = lf_id;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public void setClearer_name(String clearer_name) {
        this.clearer_name = clearer_name;
    }

    public void setNum_plate(String num_plate) {
        this.num_plate = num_plate;
    }

    public int getLocation_id() {
        return location_id;
    }

    public String getClearer_name() {
        return clearer_name;
    }

    public String getNum_plate() {
        return num_plate;
    }

    public String getCompany() {
        return company;
    }

    public String getPlace() {
        return place;
    }

    public int getLf_id() {
        return lf_id;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Date getAssigned_at() {
        return assigned_at;
    }

    public Date getCleared_at() {
        return cleared_at;
    }

    public int getAssigner_id() {
        return assigner_id;
    }

    public int getClearer_id() {
        return clearer_id;
    }

    public String getStatus() {
        return status;
    }

    public Double getSpeed() {
        return speed;
    }
}
