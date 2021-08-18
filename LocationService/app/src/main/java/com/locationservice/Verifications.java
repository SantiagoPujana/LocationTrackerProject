package com.locationservice;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

import androidx.core.app.ActivityCompat;

public class Verifications {

    public boolean isLocationServiceEnabled(Context context){

        boolean gps_enabled = false;
        boolean network_enabled = false;

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        try{ gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER); }
        catch(Exception ignored){ }

        try{ network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER); }
        catch(Exception ignored){ }

        return gps_enabled || network_enabled;
    }

    public boolean checkPermissions(Context context) {

        boolean locationPermissions = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){

            locationPermissions = locationPermissions &&
                    ActivityCompat.checkSelfPermission(context,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                    == PackageManager.PERMISSION_GRANTED;
        }

        return locationPermissions;
    }

    public boolean isNetworkAvailable(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            Network network = connectivityManager.getActiveNetwork();

            if (network == null) return false;

            NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);

            return networkCapabilities != null && (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));

        } else {

            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            return networkInfo != null && networkInfo.isConnected();
        }
    }
}