package com.locationtracker.trackuser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.material.navigation.NavigationView;
import com.locationtracker.Language;
import com.locationtracker.R;
import com.locationtracker.Verifications;
import com.locationtracker.db.RequestDBConnection;
import com.locationtracker.redzones.ListElement;

import com.locationtracker.redzones.RedZones;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;

import org.bson.Document;

import java.util.ArrayList;
import java.util.Objects;

public class MapsActivity
        extends FragmentActivity
        implements OnMapReadyCallback {

    private GoogleMap mMap;
    private RemoteMongoCollection<Document> usersCollection;
    private String username;
    private String address;
    private String currentDate;
    private String network;
    private String alert;
    private String accept;
    private Double longitude;
    private Double latitude;
    private LatLng location;
    private ImageButton drawerButton;
    private Integer soundId;
    private Boolean isLoaded;
    private SoundPool soundPool;
    private Language language;
    private TextView addressTargetTextView;
    private TextView titleTargetTextView;
    private TextView usernameTargetTextView;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private final ArrayList<Marker> tmpRealTimeMarkers = new ArrayList<>();
    private final ArrayList<Marker> realTimeMarkers = new ArrayList<>();
    private final ArrayList<Document> documents = new ArrayList<>();
    private final ArrayList<CircleOptions> circles = new ArrayList<>();
    private final ArrayList<ListElement> redZones = new ArrayList<>();
    private final MarkerOptions markerOptions = new MarkerOptions();
    private final Verifications verifications = new Verifications();
    private final RequestDBConnection requestDBConn = new RequestDBConnection();

    private final CountDownTimer countDownTimer = new CountDownTimer(3000, 1000) {

        public void onTick(long millisUntilFinished) { }

        public void onFinish() { updateMarkers(); }
    };

    private final CountDownTimer countDownTimerMap = new CountDownTimer(1000, 1000) {

        public void onTick(long millisUntilFinished) { }

        public void onFinish() {

            if (mMap != null && location != null &&
                    username != null) {

                for(CircleOptions circle : circles)
                    mMap.addCircle(circle);

                mMap.animateCamera(CameraUpdateFactory.newLatLng(location));

                drawerButton.setVisibility(View.VISIBLE);

            } else countDownTimerMap.start();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        assert mapFragment != null;
        mapFragment.getMapAsync(MapsActivity.this);

        drawerButton = findViewById(R.id.drawerButton);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navView);

        View header = navigationView.getHeaderView(0);

        titleTargetTextView = header.findViewById(R.id.titleHeader);
        addressTargetTextView = header.findViewById(R.id.addressTarget);
        usernameTargetTextView = header.findViewById(R.id.usernameTarget);

        drawerButton.setVisibility(View.GONE);

        language = Language.getInstance(MapsActivity.this);

        username = getIntent().getStringExtra("username");

        if(username.equals("")){

            addressTargetTextView.setVisibility(View.GONE);
            usernameTargetTextView.setVisibility(View.GONE);
        }

        drawerButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        usersCollection = requestDBConn.requestDBConnection("LocationTrackerClient",
                getString(R.string.database_name),
                getString(R.string.user_collection_name),
                MapsActivity.this);

        settingText();
        setNavigationView();
        setAudioSettings();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;

        markerOptions.icon(BitmapDescriptorFactory.
                defaultMarker(BitmapDescriptorFactory.HUE_RED));

        setMapStyle();

        if(verifications.checkPermissions(MapsActivity.this) &&
                verifications.isLocationServiceEnabled(MapsActivity.this))
            mMap.setMyLocationEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(mMap != null){

            mMap.clear();
            mMap.setTrafficEnabled(false);

            navigationView.getMenu().findItem(R.id.trafficItem)
                    .setTitle(language.getTrafficEnable());
        }

        redZones.clear();
        circles.clear();

        countDownTimer.start();
        countDownTimerMap.start();

        gatheringRedZones();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        soundPool.pause(soundId);
        soundPool.release();

        countDownTimer.cancel();
        countDownTimerMap.cancel();
    }

    @Override
    protected void onPause() {
        super.onPause();

        soundPool.pause(soundId);

        countDownTimer.cancel();
        countDownTimerMap.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();

        countDownTimer.start();
    }

    private void setMapStyle() {

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

    private void setAudioSettings(){

        soundPool = new SoundPool.Builder().build();

        soundId = soundPool.load(MapsActivity.this, R.raw.alarm, 1);

        soundPool.setOnLoadCompleteListener((soundPool1, sampleId, status) -> {
            if (sampleId == soundId){

                isLoaded=true;
                soundPool.play(soundId, 0,0,1,-1,1);
                soundPool.pause(soundId);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void settingText(){

        if(username.equals(""))
            navigationView.getMenu().findItem(R.id.followItem).setVisible(false);
        else{

            navigationView.getMenu().findItem(R.id.followItem)
                    .setTitle(language.getFollow()+" "+username.split(" ")[0]);
        }

        navigationView.getMenu().findItem(R.id.trafficItem)
                .setTitle(language.getTrafficEnable());
        navigationView.getMenu().findItem(R.id.redZonesItem)
                .setTitle(language.getRedZonesTitle().toUpperCase());
        navigationView.getMenu().findItem(R.id.goBackItem)
                .setTitle(language.getGoBack());

        titleTargetTextView.setText(language.getTitle());
        usernameTargetTextView.setText(language.getTarget()+username);
        network = language.getNetwork();
        alert = language.getAlert();
        accept = language.getAccept();
    }

    private void gatheringRedZones(){

        RemoteMongoCollection<Document> redZonesCollection = requestDBConn
                .requestDBConnection("LocationTrackerClient",
                getString(R.string.database_name),
                getString(R.string.red_zones_collection_name),
                MapsActivity.this);

        redZonesCollection.find().forEach(document ->
                redZones.add(new ListElement(Objects.requireNonNull(document.get("name")).toString(),
                        Double.parseDouble(Objects.requireNonNull(document.get("longitude")).toString()),
                        Double.parseDouble(Objects.requireNonNull(document.get("latitude")).toString()),
                        Integer.parseInt(Objects.requireNonNull(document.get("radius")).toString()))))
        .addOnCompleteListener(task -> {

            if(task.isSuccessful() && redZones.size() > 0){

                for(int i = 0; i < redZones.size(); i++)
                    setRedZones(redZones.get(i).getLatitude(),
                            redZones.get(i).getLongitude(),
                            redZones.get(i).getRadius());
            }
        });
    }

    private void updateMarkers() {

        if(!username.equals("")){

            Document queryDocument = new Document(
                    "username", username);

            usersCollection.findOne(queryDocument).addOnCompleteListener(task ->{

                if(task.isSuccessful())
                    settingMarkers(task.getResult());
                else {

                    if(Objects.requireNonNull(task.getException()).toString().contains("UnknownHostException"))
                        showAlertDialog(network);
                }
            });
        }
        else {

            usersCollection.find().forEach(document -> {

                if(!Objects.equals(document.get("username"), "LocationTrackerClient"))
                    documents.add(document);

            }).addOnCompleteListener(task -> {

                if(task.isSuccessful() && documents.size() > 0)
                    settingMarkers(null);
                else {

                    if(Objects.requireNonNull(task.getException()).toString().contains("UnknownHostException"))
                        showAlertDialog(network);
                }
            });
        }

        documents.clear();
        realTimeMarkers.clear();
        realTimeMarkers.addAll(tmpRealTimeMarkers);
        tmpRealTimeMarkers.clear();
    }

    private void settingMarkers(Document result) {

        countDownTimer.cancel();

        for(Marker marker : realTimeMarkers)
            marker.remove();

        if(result != null) {

            longitude = (Double) result.get("longitude");
            latitude = (Double) result.get("latitude");
            address = (String) result.get("address");
            currentDate = (String) result.get("date");

            setAddress();
            showMarkers(null);
        }
        else{

            for(Document document : documents){

                longitude = (Double) document.get("longitude");
                latitude = (Double) document.get("latitude");
                address = (String) document.get("address");
                currentDate = (String) document.get("date");

                setAddress();
                showMarkers((String) document.get("username"));
            }
        }

        countDownTimer.start();
    }

    private void setAddress(){

        if(address.equals("Unknown Address"))
            address = language.getUnknownAddress();

        if(!username.equals(""))
            addressTargetTextView.setText(address);
    }

    private void showMarkers(String user_name) {

        location = new LatLng(latitude, longitude);

        if(user_name != null)
            markerOptions.position(location).title(user_name);
        else markerOptions.position(location).title(address).snippet(currentDate);

        tmpRealTimeMarkers.add(mMap.addMarker(markerOptions));

        if(isInsideCircle() && isLoaded){
            soundPool.setVolume(soundId,1,1);
            soundPool.resume(soundId);
        }
        else soundPool.pause(soundId);
    }

    private Boolean isInsideCircle() {

        boolean isInside = false;
        float[] distance = new float[2];

        for(Marker marker : tmpRealTimeMarkers){

            for(CircleOptions circle : circles){

                assert circle.getCenter() != null;
                Location.distanceBetween(marker.getPosition().latitude, marker.getPosition().longitude,
                        circle.getCenter().latitude, circle.getCenter().longitude, distance);

                isInside = !(distance[0] > circle.getRadius());

                if(isInside) break;
            }

           if(isInside) break;
        }

        return isInside;
    }

    private void setRedZones(Double lat, Double lon, Integer radius){

        CircleOptions circleOptions = new CircleOptions()
                .center(new LatLng(lat, lon))
                .radius(radius)
                .strokeColor(Color.RED)
                .visible(true)
                .fillColor(Color.TRANSPARENT);

        circles.add(circleOptions);
    }

    @SuppressLint("NonConstantResourceId")
    private void setNavigationView() {

        navigationView.setNavigationItemSelectedListener(item -> {

            switch (item.getItemId()){

                case R.id.followItem:

                    mMap.animateCamera(CameraUpdateFactory.newLatLng(location));

                    break;

                case R.id.trafficItem:

                    if(mMap.isTrafficEnabled()){

                        navigationView.getMenu().findItem(R.id.trafficItem)
                                .setTitle(language.getTrafficEnable());
                        mMap.setTrafficEnabled(false);
                    }
                    else{

                        navigationView.getMenu().findItem(R.id.trafficItem)
                                .setTitle(language.getTrafficDisable());
                        mMap.setTrafficEnabled(true);
                    }

                    break;

                case R.id.redZonesItem:

                    Intent intent = new Intent(MapsActivity.this, RedZones.class);
                    startActivity(intent);

                    break;

                case R.id.goBackItem:

                    finish();

                    break;
            }

            item.setCheckable(false);

            drawerLayout.closeDrawer(GravityCompat.START);

            return true;
        });
    }

    private void showAlertDialog(String message){

        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);

        builder.setMessage(message)
                .setTitle(alert)
                .setCancelable(false);

        builder.setPositiveButton(accept, (dialog, id) ->finish());

        AlertDialog dialog = builder.create();

        dialog.show();
    }
}