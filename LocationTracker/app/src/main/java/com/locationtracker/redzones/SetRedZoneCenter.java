package com.locationtracker.redzones;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import com.locationtracker.Language;
import com.locationtracker.LoadingDialog;
import com.locationtracker.R;
import com.locationtracker.Verifications;

public class SetRedZoneCenter extends AppCompatActivity {

    private Language language;
    private Button nextButton;
    private TextView messageTextView;
    private TextView titleTextView;
    private Double myLongitude;
    private Double myLatitude;
    private Double latitudeToEdit;
    private Double longitudeToEdit;
    private Double centerLongitude;
    private Double centerLatitude;
    private Bundle mySavedInstance;
    private String alert;
    private String accept;
    private String missingCenter;
    private String centerTitle;
    private String action;
    private LocationCallback locationCallBack;
    private LoadingDialog loadingDialog;

    private final Verifications verifications = new Verifications();

    private final CountDownTimer countDownTimer = new CountDownTimer(1000, 1000) {

        public void onTick(long millisUntilFinished) { }

        public void onFinish() {

            if(myLatitude != null && myLongitude != null)
                setMap(mySavedInstance, action);
            else countDownTimer.start();
        }
    };

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_red_zone_center);

        mySavedInstance = savedInstanceState;

        language = Language.getInstance(SetRedZoneCenter.this);
        loadingDialog = new LoadingDialog(SetRedZoneCenter.this);
        loadingDialog.startLoadingDialog();

        nextButton = findViewById(R.id.next);
        messageTextView = findViewById(R.id.messageTextView);
        titleTextView = findViewById(R.id.addRedZoneTitle);

        settingText();

        nextForm();

        requestMyLocation();

        countDownTimer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(locationCallBack != null)
            LocationServices.getFusedLocationProviderClient(SetRedZoneCenter.this)
                    .removeLocationUpdates(locationCallBack);

        finish();
    }

    @SuppressLint("MissingPermission")
    private void requestMyLocation() {

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationCallBack = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {

                        myLongitude = location.getLongitude();
                        myLatitude = location.getLatitude();

                        break;
                    }
                }
            }
        };

        LocationServices.getFusedLocationProviderClient(SetRedZoneCenter.this)
                .requestLocationUpdates(locationRequest, locationCallBack, null);
    }

    @SuppressLint("MissingPermission")
    private void setMap(Bundle savedInstanceState, String actionValue) {

        MarkerOptions markerOptions = new MarkerOptions();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        assert mapFragment != null;
        mapFragment.onCreate(savedInstanceState);
        mapFragment.getMapAsync(googleMap -> {

            setMapStyle(googleMap);

            markerOptions.title(centerTitle).icon(BitmapDescriptorFactory.
                    defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

            if(verifications.checkPermissions(SetRedZoneCenter.this) &&
                    verifications.isLocationServiceEnabled(SetRedZoneCenter.this))
                googleMap.setMyLocationEnabled(true);

            if(actionValue.equals("add"))
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(myLatitude, myLongitude)));
            else{

                googleMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(latitudeToEdit, longitudeToEdit)));

                markerOptions.position(new LatLng(latitudeToEdit, longitudeToEdit))
                        .snippet(latitudeToEdit+" / "+ longitudeToEdit);

                centerLongitude = longitudeToEdit;
                centerLatitude = latitudeToEdit;

                googleMap.addMarker(markerOptions);
            }

            loadingDialog.dismissLoadingDialog();

            googleMap.setOnMapClickListener(latLng -> {

                googleMap.clear();

                markerOptions.position(latLng)
                        .snippet(latLng.latitude+" / "+latLng.longitude);

                googleMap.addMarker(markerOptions);

                googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                centerLongitude = latLng.longitude;
                centerLatitude = latLng.latitude;
            });
        });
    }

    private void nextForm(){

        nextButton.setOnClickListener(v -> {

            if(centerLatitude == null && centerLongitude == null)
                showAlertDialog(missingCenter);

            else{

                Intent intent = new Intent(SetRedZoneCenter.this, RedZonesSettings.class);

                if(action.equals("update")){

                    intent.putExtra("latitude", centerLatitude)
                            .putExtra("longitude", centerLongitude)
                            .putExtra("action", action)
                            .putExtra("radius", getIntent().getIntExtra("radius", 0))
                            .putExtra("name", getIntent().getStringExtra("name"));
                }
                else{

                    intent.putExtra("latitude", centerLatitude)
                            .putExtra("longitude", centerLongitude)
                            .putExtra("action", action);
                }

                startActivity(intent);
                finish();
            }
        });
    }

    private void showAlertDialog(String message){

        AlertDialog.Builder builder = new AlertDialog.Builder(SetRedZoneCenter.this);

        builder.setMessage(message)
                .setTitle(alert)
                .setCancelable(false);

        builder.setPositiveButton(accept, (dialog, id) ->{ });

        AlertDialog dialog = builder.create();

        dialog.show();
    }

    private void settingText() {

        action = getIntent().getStringExtra("action");

        if(action.equals("update")){

            latitudeToEdit = getIntent().getDoubleExtra("latitude", 0);
            longitudeToEdit = getIntent().getDoubleExtra("longitude", 0);
            titleTextView.setText(language.getEditRedZone());
        }
        else titleTextView.setText(language.getAddRedZone());

        missingCenter = language.getMissingCenter();
        centerTitle = language.getCenterTitle();
        accept = language.getAccept();
        alert = language.getAlert();
        nextButton.setText(language.getNext());
        messageTextView.setText(language.getIntroMessage());
    }

    private void setMapStyle(GoogleMap mMap) {

        MapStyleOptions style = new MapStyleOptions(
                "[\n" +
                        "  {\n" +
                        "    \"elementType\": \"geometry\",\n" +
                        "    \"stylers\": [\n" +
                        "      {\n" +
                        "        \"color\": \"#ebe3cd\"\n" +
                        "      }\n" +
                        "    ]\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"elementType\": \"labels.text.fill\",\n" +
                        "    \"stylers\": [\n" +
                        "      {\n" +
                        "        \"color\": \"#523735\"\n" +
                        "      }\n" +
                        "    ]\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"elementType\": \"labels.text.stroke\",\n" +
                        "    \"stylers\": [\n" +
                        "      {\n" +
                        "        \"color\": \"#f5f1e6\"\n" +
                        "      }\n" +
                        "    ]\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"featureType\": \"administrative\",\n" +
                        "    \"elementType\": \"geometry.stroke\",\n" +
                        "    \"stylers\": [\n" +
                        "      {\n" +
                        "        \"color\": \"#c9b2a6\"\n" +
                        "      }\n" +
                        "    ]\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"featureType\": \"administrative.land_parcel\",\n" +
                        "    \"elementType\": \"geometry.stroke\",\n" +
                        "    \"stylers\": [\n" +
                        "      {\n" +
                        "        \"color\": \"#dcd2be\"\n" +
                        "      }\n" +
                        "    ]\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"featureType\": \"administrative.land_parcel\",\n" +
                        "    \"elementType\": \"labels.text.fill\",\n" +
                        "    \"stylers\": [\n" +
                        "      {\n" +
                        "        \"color\": \"#ae9e90\"\n" +
                        "      }\n" +
                        "    ]\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"featureType\": \"landscape.natural\",\n" +
                        "    \"elementType\": \"geometry\",\n" +
                        "    \"stylers\": [\n" +
                        "      {\n" +
                        "        \"color\": \"#dfd2ae\"\n" +
                        "      }\n" +
                        "    ]\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"featureType\": \"poi\",\n" +
                        "    \"elementType\": \"geometry\",\n" +
                        "    \"stylers\": [\n" +
                        "      {\n" +
                        "        \"color\": \"#dfd2ae\"\n" +
                        "      }\n" +
                        "    ]\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"featureType\": \"poi\",\n" +
                        "    \"elementType\": \"labels.text.fill\",\n" +
                        "    \"stylers\": [\n" +
                        "      {\n" +
                        "        \"color\": \"#93817c\"\n" +
                        "      }\n" +
                        "    ]\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"featureType\": \"poi.park\",\n" +
                        "    \"elementType\": \"geometry.fill\",\n" +
                        "    \"stylers\": [\n" +
                        "      {\n" +
                        "        \"color\": \"#a5b076\"\n" +
                        "      }\n" +
                        "    ]\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"featureType\": \"poi.park\",\n" +
                        "    \"elementType\": \"labels.text.fill\",\n" +
                        "    \"stylers\": [\n" +
                        "      {\n" +
                        "        \"color\": \"#447530\"\n" +
                        "      }\n" +
                        "    ]\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"featureType\": \"road\",\n" +
                        "    \"elementType\": \"geometry\",\n" +
                        "    \"stylers\": [\n" +
                        "      {\n" +
                        "        \"color\": \"#f5f1e6\"\n" +
                        "      }\n" +
                        "    ]\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"featureType\": \"road.arterial\",\n" +
                        "    \"elementType\": \"geometry\",\n" +
                        "    \"stylers\": [\n" +
                        "      {\n" +
                        "        \"color\": \"#fdfcf8\"\n" +
                        "      }\n" +
                        "    ]\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"featureType\": \"road.highway\",\n" +
                        "    \"elementType\": \"geometry\",\n" +
                        "    \"stylers\": [\n" +
                        "      {\n" +
                        "        \"color\": \"#f8c967\"\n" +
                        "      }\n" +
                        "    ]\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"featureType\": \"road.highway\",\n" +
                        "    \"elementType\": \"geometry.stroke\",\n" +
                        "    \"stylers\": [\n" +
                        "      {\n" +
                        "        \"color\": \"#e9bc62\"\n" +
                        "      }\n" +
                        "    ]\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"featureType\": \"road.highway.controlled_access\",\n" +
                        "    \"elementType\": \"geometry\",\n" +
                        "    \"stylers\": [\n" +
                        "      {\n" +
                        "        \"color\": \"#e98d58\"\n" +
                        "      }\n" +
                        "    ]\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"featureType\": \"road.highway.controlled_access\",\n" +
                        "    \"elementType\": \"geometry.stroke\",\n" +
                        "    \"stylers\": [\n" +
                        "      {\n" +
                        "        \"color\": \"#db8555\"\n" +
                        "      }\n" +
                        "    ]\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"featureType\": \"road.local\",\n" +
                        "    \"elementType\": \"labels.text.fill\",\n" +
                        "    \"stylers\": [\n" +
                        "      {\n" +
                        "        \"color\": \"#806b63\"\n" +
                        "      }\n" +
                        "    ]\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"featureType\": \"transit.line\",\n" +
                        "    \"elementType\": \"geometry\",\n" +
                        "    \"stylers\": [\n" +
                        "      {\n" +
                        "        \"color\": \"#dfd2ae\"\n" +
                        "      }\n" +
                        "    ]\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"featureType\": \"transit.line\",\n" +
                        "    \"elementType\": \"labels.text.fill\",\n" +
                        "    \"stylers\": [\n" +
                        "      {\n" +
                        "        \"color\": \"#8f7d77\"\n" +
                        "      }\n" +
                        "    ]\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"featureType\": \"transit.line\",\n" +
                        "    \"elementType\": \"labels.text.stroke\",\n" +
                        "    \"stylers\": [\n" +
                        "      {\n" +
                        "        \"color\": \"#ebe3cd\"\n" +
                        "      }\n" +
                        "    ]\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"featureType\": \"transit.station\",\n" +
                        "    \"elementType\": \"geometry\",\n" +
                        "    \"stylers\": [\n" +
                        "      {\n" +
                        "        \"color\": \"#dfd2ae\"\n" +
                        "      }\n" +
                        "    ]\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"featureType\": \"water\",\n" +
                        "    \"elementType\": \"geometry.fill\",\n" +
                        "    \"stylers\": [\n" +
                        "      {\n" +
                        "        \"color\": \"#b9d3c2\"\n" +
                        "      }\n" +
                        "    ]\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"featureType\": \"water\",\n" +
                        "    \"elementType\": \"labels.text.fill\",\n" +
                        "    \"stylers\": [\n" +
                        "      {\n" +
                        "        \"color\": \"#92998d\"\n" +
                        "      }\n" +
                        "    ]\n" +
                        "  }\n" +
                        "]"
        );

        mMap.setMapStyle(style);
    }
}