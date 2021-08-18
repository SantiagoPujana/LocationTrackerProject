package com.locationservice.service;

import android.content.Context;
import android.location.Location;

import androidx.preference.PreferenceManager;

import com.locationservice.Language;

public class Common {

    public static final String KEY_REQUESTING_LOCATION_UPDATES = "LocationUpdatesEnable";

    public static void setRequestingLocationUpdates(Context context, boolean value) {

        PreferenceManager.getDefaultSharedPreferences(context)
                .edit().putBoolean(KEY_REQUESTING_LOCATION_UPDATES, value)
                .apply();
    }

    public static boolean requestingLocationUpdates(Context context) {

        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(KEY_REQUESTING_LOCATION_UPDATES, false);
    }

    public static String getAddressText(Location location, Context context) {

        Language language = Language.getInstance(context);

        return location == null ? language.getUnknownAddress() :
                language.getYouAreHere() + new SendLocation(location).getAddress(context,
                        location.getLatitude(), location.getLongitude());
    }
}
