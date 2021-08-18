package com.locationservice;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;

import com.locationservice.db.UpdateData;
import com.locationservice.service.BackgroundLocationService;
import com.locationservice.service.SendLocation;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;

import org.bson.Document;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity
        extends AppCompatActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final Integer REQUEST_CODE = 200;

    private final Verifications verifications = new Verifications();

    private BackgroundLocationService backgroundLocationService;
    private Boolean bound = false;
    private String permissions;
    private String network;
    private String gps;
    private String alert;
    private String accept;
    private TextView titleTextView;
    private TextView messageTextView;
    private Language language;

    private final CountDownTimer countDownTimer = new CountDownTimer(1000, 1000) {

        public void onTick(long millisUntilFinished) { }

        public void onFinish() { validationsBeforeStartService(); }
    };

    private final CountDownTimer countDownTimerCheck = new CountDownTimer(1000, 1000) {

        public void onTick(long millisUntilFinished) { }

        public void onFinish() {

            if (backgroundLocationService != null) {

                new Handler(Looper.getMainLooper()).postDelayed(() ->
                        backgroundLocationService.requestLocationUpdates(), 0);
            }
            else countDownTimerCheck.start();
        }
    };

    private final ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            BackgroundLocationService.LocalBinder binder = (BackgroundLocationService.LocalBinder) service;
            backgroundLocationService = binder.getService();
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

            backgroundLocationService = null;
            bound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        titleTextView = findViewById(R.id.appName);
        messageTextView = findViewById(R.id.message);

        language = Language.getInstance(MainActivity.this);

        settingText();
        checkServices();
    }

    @Override
    protected void onStart() {
        super.onStart();

        PreferenceManager.getDefaultSharedPreferences(MainActivity.this)
                .registerOnSharedPreferenceChangeListener(MainActivity.this);

        EventBus.getDefault().register(MainActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        countDownTimer.start();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (bound) {

            unbindService(serviceConnection);
            bound = false;
        }

        PreferenceManager.getDefaultSharedPreferences(MainActivity.this)
                .unregisterOnSharedPreferenceChangeListener(MainActivity.this);

        EventBus.getDefault().unregister(MainActivity.this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] _permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, _permissions, grantResults);

        if (grantResults.length > 0) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                countDownTimer.start();
            else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION))
                        requestPermissions();
                }
                else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {

                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION},
                                REQUEST_CODE);
                    }
                }
                else {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION))
                        requestPermissions();

                }
            }
        }
    }

    private void checkServices() {

        boolean checkPermissions = verifications.checkPermissions(MainActivity.this);
        boolean networkService = verifications.isNetworkAvailable(MainActivity.this);
        boolean gpsService = verifications.isLocationServiceEnabled(MainActivity.this);

        if(!checkPermissions)
            showAlertDialog(permissions, "permissions");

        if(!networkService)
            showAlertDialog(network, null);

        if(!gpsService)
            showAlertDialog(gps, null);
    }

    private void settingText() {

        permissions = language.getPermissions();
        network = language.getNetwork();
        gps = language.getGps();
        alert = language.getAlert();
        accept = language.getAccept();

        titleTextView.setText(language.getTitle());
        messageTextView.setText(language.getImportantMessage());
    }

    private void showAlertDialog(String message, String idAlert) {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setMessage(message)
                .setTitle(alert)
                .setCancelable(false);

        builder.setPositiveButton(accept, (dialog, id) -> {

            if (idAlert != null) {

                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {

                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION},
                            REQUEST_CODE);
                }
                else requestPermissions();
            }
        });

        AlertDialog dialog = builder.create();

        dialog.show();
    }

    private void requestPermissions() {

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_CODE);
    }

    private void validationsBeforeStartService() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            if(ActivityCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED)
                startServiceLocation();
            else {

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                        REQUEST_CODE);
            }
        }
        else {

            if(ActivityCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                startServiceLocation();
            else requestPermissions();
        }
    }

    private void startServiceLocation() {

        boolean bindServiceTask = bindService(new Intent(MainActivity.this,
                        BackgroundLocationService.class),
                serviceConnection,
                Context.BIND_AUTO_CREATE);

        if (bindServiceTask)
            countDownTimerCheck.start();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) { }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onListenLocation(SendLocation event){

        double latitude = event.getLocation().getLatitude();

        double longitude = event.getLocation().getLongitude();

        RemoteMongoCollection<Document> collection = backgroundLocationService.getCollection();

        UpdateData update = backgroundLocationService.getUpdateDataObject();

        String username = backgroundLocationService.getUsername();

        String address = event.getAddress(MainActivity.this, latitude, longitude);

        String date = event.getDate();

        update.updateData(collection,
                username,
                longitude,
                latitude,
                address,
                date);
    }
}