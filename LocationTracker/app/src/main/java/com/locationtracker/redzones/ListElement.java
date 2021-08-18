package com.locationtracker.redzones;

import java.io.Serializable;

public class ListElement implements Serializable {

    private final String redZoneName;
    private final Double longitude;
    private final Double latitude;
    private final Integer radius;

    public ListElement(String redZoneName, Double longitude, Double latitude, Integer radius) {
        this.redZoneName = redZoneName;
        this.longitude = longitude;
        this.latitude = latitude;
        this.radius = radius;
    }

    public String getRedZoneName() {
        return redZoneName;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Integer getRadius() {
        return radius;
    }
}
