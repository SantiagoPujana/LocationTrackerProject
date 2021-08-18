package com.locationservice.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import com.locationservice.Language;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class SendLocation {

    private final Location location;

    public SendLocation(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public String getAddress(Context context, double latitude, double longitude) {

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        String address;

        List<Address> addresses;

        try{

            addresses = geocoder.getFromLocation(latitude, longitude, 1);

            address = addresses.get(0).getAddressLine(0);
        }
        catch (Exception exception){ address = Language.getInstance(context).getUnknownAddress(); }

        return address;
    }

    public String getDate(){

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss a");

        Calendar calendar = Calendar.getInstance();

        return DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime())
                + " - " + simpleDateFormat.format(calendar.getTime());
    }
}