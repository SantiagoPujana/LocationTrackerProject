package com.locationservice.service;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.locationservice.Language;
import com.locationservice.R;
import com.locationservice.db.DatabaseConnection;
import com.locationservice.db.UpdateData;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;

import org.bson.Document;
import org.greenrobot.eventbus.EventBus;

public class BackgroundLocationService extends Service {

    private static final Integer NOTIFICATION_ID = 463798521;

    private final IBinder binder = new LocalBinder();
    private final UpdateData update = new UpdateData();
    private final String channelId = "1234567890";

    private LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private Handler serviceHandler;
    private String username;
    private Boolean changingConfiguration = false;
    private NotificationManager notificationManager;
    private RemoteMongoCollection<Document> collection;
    private Language language;
    private Location location;

    @Override
    public void onCreate() {

        LocationSettings locationSettings = new LocationSettings();

        username = BackgroundLocationService.this.getString(R.string.username).toUpperCase();

        language = Language.getInstance(BackgroundLocationService.this);

        fusedLocationProviderClient = locationSettings.getLocationProvider(BackgroundLocationService.this);
        locationCallback = new LocationCallback() {

            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                location = locationResult.getLastLocation();
                onNewLocation();
            }
        };

        locationRequest = locationSettings.getLocationRequest();
        collection = requestDBConnection();

        HandlerThread handlerThread = new HandlerThread("Thread");
        handlerThread.start();
        serviceHandler = new Handler(handlerThread.getLooper());
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel notificationChannel = new NotificationChannel(channelId,
                language.getTitle(), NotificationManager.IMPORTANCE_DEFAULT);

                notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        changingConfiguration = true;
    }

    private RemoteMongoCollection<Document> requestDBConnection(){

        DatabaseConnection connection = DatabaseConnection.getInstance(username,
                BackgroundLocationService.this);

        return connection.connectWithMongodb(getString(R.string.database_name),
                getString(R.string.user_collection_name));
    }

    public RemoteMongoCollection<Document> getCollection() { return collection; }

    public UpdateData getUpdateDataObject() { return update; }

    public String getUsername() { return username; }

    private void onNewLocation() {

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        EventBus.getDefault().postSticky(new SendLocation(location));

        String address = new SendLocation(location).
                getAddress(BackgroundLocationService.this,
                        latitude, longitude);

        String date = new SendLocation(location).
                getDate();

        if (serviceIsRunningInForeground(BackgroundLocationService.this)) {

            notificationManager.notify(NOTIFICATION_ID, getNotification());

            update.updateData(collection,
                    username, longitude, latitude,
                    address, date);
        }
    }

    private Notification getNotification() {

        String text = Common.getAddressText(location,BackgroundLocationService.this);

        Intent intent = new Intent(BackgroundLocationService.this, BackgroundLocationService.class);

        intent.putExtra(BackgroundLocationService.this.getPackageName()
                +".STARTED_FROM_NOTIFICATION",true);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(BackgroundLocationService.this, channelId)
                .setPriority(Notification.PRIORITY_MIN)
                .setContentTitle(language.getTitle() + language.getIsRunning())
                .setContentText(text)
                .setSmallIcon(R.drawable.marker_icon)
                .setSilent(true)
                .setAutoCancel(true)
                .setOngoing(true)
                .setTicker(text)
                .setWhen(System.currentTimeMillis());

        return builder.build();
    }

    private boolean serviceIsRunningInForeground(Context context) {

        ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);

        for(ActivityManager.RunningServiceInfo service:manager.getRunningServices(Integer.MAX_VALUE)){

            if(getClass().getName().equals(service.service.getClassName())) {

                if (service.foreground)
                    return true;
            }
        }
        
        return false;
    }

    @SuppressLint("MissingPermission")
    public void requestLocationUpdates() {

        Common.setRequestingLocationUpdates(BackgroundLocationService.this, true);
        startService(new Intent(BackgroundLocationService.this, BackgroundLocationService.class));

        try{

            fusedLocationProviderClient.requestLocationUpdates(locationRequest,
                    locationCallback,
                    Looper.myLooper());

        }catch (SecurityException ignored){ }
    }

    public class LocalBinder extends Binder {

        public BackgroundLocationService getService(){
            return BackgroundLocationService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        stopForeground(true);
        changingConfiguration = false;

        return binder;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);

        stopForeground(true);
        changingConfiguration = false;
    }

    @Override
    public boolean onUnbind(Intent intent) {

        if(!changingConfiguration && Common.requestingLocationUpdates(BackgroundLocationService.this))
            startForeground(NOTIFICATION_ID, getNotification());

        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        serviceHandler.removeCallbacks(null);
    }
}