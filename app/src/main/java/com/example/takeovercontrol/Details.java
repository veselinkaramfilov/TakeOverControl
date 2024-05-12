package com.example.takeovercontrol;

import com.google.firebase.Timestamp;

public class Details {
    private String type, size, place, date;
    private Timestamp timestamp;
    private float unit, alcohol;
    private double cost;

    public Details() {
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getSize() {
        return size;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public void setSize(String size) {
        this.size = size;
    }
    public String getPlace() {
        return place;
    }
    public void setPlace(String place) {
        this.place = place;
    }
    public Timestamp getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
    public float getUnit() {
        return unit;
    }
    public void setUnit(float unit) {
        this.unit = unit;
    }
    public float getAlcohol() {
        return alcohol;
    }
    public void setAlcohol(float alcohol) {
        this.alcohol = alcohol;
    }
    public double getCost() {
        return cost;
    }
    public void setCost(double cost) {
        this.cost = cost;
    }
}
