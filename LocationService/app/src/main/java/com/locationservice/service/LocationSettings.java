package com.locationservice.service;

import android.content.Context;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class LocationSettings {

    private static final long UPDATE_INTERVAL_IN_MILLI_SECONDS = 3000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLI_SECONDS = UPDATE_INTERVAL_IN_MILLI_SECONDS/2;

    public LocationRequest getLocationRequest() {

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLI_SECONDS);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLI_SECONDS);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        return locationRequest;
    }

    public FusedLocationProviderClient getLocationProvider(Context context){

        return LocationServices.getFusedLocationProviderClient(context);
    }
}
