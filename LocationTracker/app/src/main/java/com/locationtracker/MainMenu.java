package com.locationtracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.locationtracker.db.RequestDBConnection;
import com.locationtracker.redzones.RedZones;
import com.locationtracker.trackuser.MapsActivity;
import com.locationtracker.trackuser.TrackUser;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;

import org.bson.Document;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class MainMenu extends AppCompatActivity {

    private String permissions;
    private String network;
    private String gps;
    private String alert;
    private String accept;
    private String insufficientUsers;
    private Language language;
    private TextView trackUserTextView;
    private TextView redZonesTextView;
    private TextView titleTextView;
    private Button trackAllUsersButton;

    private final RequestDBConnection requestDBConn = new RequestDBConnection();
    private final Verifications verifications = new Verifications();

    private static final int REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        language = Language.getInstance(MainMenu.this);

        trackUserTextView = findViewById(R.id.trackUserText);
        redZonesTextView = findViewById(R.id.configurationText);
        titleTextView = findViewById(R.id.appName);
        View trackUserButton = findViewById(R.id.trackUserButton);
        View configurationButton = findViewById(R.id.configurationButton);
        trackAllUsersButton = findViewById(R.id.allUsers);

        settingText();

        trackAllUsersButton.setOnClickListener(v -> {

            if(checkServices())
                howManyUsers();
        });

        trackUserButton.setOnClickListener(v ->{

            if(checkServices()){

                Intent intent = new Intent(MainMenu.this, TrackUser.class);
                startActivity(intent);
            }

        });

        configurationButton.setOnClickListener(v ->{

            if(checkServices()){

                Intent intent = new Intent(MainMenu.this, RedZones.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] _permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, _permissions, grantResults);

        if (grantResults.length > 0){

            if (grantResults[0] == PackageManager.PERMISSION_DENIED)
                showAlertDialog(permissions, "permissions");
        }
    }

    private boolean checkServices() {

        boolean checkPermissions = verifications.checkPermissions(MainMenu.this);
        boolean networkService = verifications.isNetworkAvailable(MainMenu.this);
        boolean gpsService = verifications.isLocationServiceEnabled(MainMenu.this);

        if(!checkPermissions)
            showAlertDialog(permissions, "permissions");

        if(!networkService)
            showAlertDialog(network, null);

        if(!gpsService)
            showAlertDialog(gps, null);

        return checkPermissions && networkService && gpsService;
    }

    private void showAlertDialog(String message, String idAlert){

        AlertDialog.Builder builder = new AlertDialog.Builder(MainMenu.this);

        builder.setMessage(message)
                .setTitle(alert)
                .setCancelable(false);

        builder.setPositiveButton(accept, (dialog, id) ->{

            if (idAlert != null) {

                ActivityCompat.requestPermissions(MainMenu.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_CODE);
            }
        });

        AlertDialog dialog = builder.create();

        dialog.show();
    }

    private void settingText() {

        permissions = language.getPermissions();
        network = language.getNetwork();
        gps = language.getGps();
        alert = language.getAlert();
        accept = language.getAccept();
        insufficientUsers = language.getInsufficientUsers();

        titleTextView.setText(language.getTitle());
        redZonesTextView.setText(language.getRedZonesTitle());
        trackUserTextView.setText(language.getTrackUser());
        trackAllUsersButton.setText(language.getTrackAllUsers());
    }

    private void goToMap(){

        Intent intent = new Intent(MainMenu.this, MapsActivity.class);
        intent.putExtra("username", "");
        startActivity(intent);
    }

    private void howManyUsers(){

        AtomicInteger count = new AtomicInteger();

        RemoteMongoCollection<Document> usersCollection = requestDBConn
                .requestDBConnection("LocationTrackerClient",
                        getString(R.string.database_name),
                        getString(R.string.user_collection_name),
                        MainMenu.this);

        usersCollection.find().forEach(document ->{

            if(!Objects.equals(document.get("username"), "LocationTrackerClient"))
                count.getAndIncrement();

        }).addOnCompleteListener(task -> {

            if(task.isSuccessful()){

                if(count.get() > 1)
                    goToMap();
                else showAlertDialog(insufficientUsers,null);
            }
        });
    }
}