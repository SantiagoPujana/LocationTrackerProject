package com.locationtracker.redzones;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.locationtracker.Language;
import com.locationtracker.R;
import com.locationtracker.Verifications;
import com.locationtracker.db.RequestDBConnection;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;

import org.bson.Document;

import java.util.ArrayList;
import java.util.Objects;

public class RedZonesSettings extends AppCompatActivity {

    private Double latitude;
    private Double longitude;
    private TextView titleTextView;
    private TextInputEditText nameTextView;
    private TextInputEditText radiusTextView;
    private TextInputLayout nameLayout;
    private TextInputLayout radiusLayout;
    private Button redZoneButton;
    private Button goBackButton;
    private Language language;
    private String alert;
    private String accept;
    private String network;
    private String sameName;
    private String action;
    private String name;
    private String missingData;
    private Integer radius;
    private RemoteMongoCollection<Document> collection;

    private final Verifications verfications = new Verifications();
    private final ArrayList<String> names = new ArrayList<>();
    private final RequestDBConnection requestDBConn = new RequestDBConnection();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_red_zones);

        language = Language.getInstance(RedZonesSettings.this);

        latitude = getIntent().getDoubleExtra("latitude", 0);
        longitude = getIntent().getDoubleExtra("longitude", 0);

        titleTextView = findViewById(R.id.addRedZoneTitle);
        nameTextView = findViewById(R.id.textInput);
        radiusTextView = findViewById(R.id.textInput2);
        redZoneButton = findViewById(R.id.addRedZone);
        nameLayout = findViewById(R.id.textInputLayout);
        radiusLayout = findViewById(R.id.textInputLayout2);
        goBackButton = findViewById(R.id.previous);

        settingText();

        goBackButton.setOnClickListener(v -> {

            if(action.equals("update"))
                goBackToRedZones("update");
            else goBackToRedZones("add");
        });

        collection = requestDBConn.requestDBConnection("LocationTrackerClient",
                "Database",
                "Red Zones",
                RedZonesSettings.this);

        findingAction();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        finish();
    }

    @SuppressLint("SetTextI18n")
    private void settingText(){

        action = getIntent().getStringExtra("action");

        if(action.equals("update")){

            titleTextView.setText(language.getEditRedZone());
            redZoneButton.setText(language.getEditRedZone().toUpperCase());
            name = getIntent().getStringExtra("name");
            nameTextView.setText(name);
            radius = getIntent().getIntExtra("radius", 0);
            radiusTextView.setText(radius.toString());
        }
        else {

            titleTextView.setText(language.getAddRedZone());
            redZoneButton.setText(language.getAddRedZone().toUpperCase());
        }

        nameLayout.setPlaceholderText(language.getPlaceholderRedZoneName());
        nameLayout.setHelperText(language.getRequired());
        nameLayout.setHint(language.getHintRedZoneName());
        radiusLayout.setPlaceholderText(language.getPlaceholderRadius());
        radiusLayout.setHelperText(language.getRequired());
        radiusLayout.setHint(language.getHintRadius());
        goBackButton.setText(language.getGoBack());
        missingData = language.getMissingData();
        alert = language.getAlert();
        accept = language.getAccept();
        sameName = language.getSameName();
        network = language.getNetwork();
    }

    private void showAlertDialog(String message){

        AlertDialog.Builder builder = new AlertDialog.Builder(RedZonesSettings.this);

        builder.setMessage(message)
                .setTitle(alert)
                .setCancelable(false);

        builder.setPositiveButton(accept, (dialog, id) ->{ });

        AlertDialog dialog = builder.create();

        dialog.show();
    }

    private void goBackToRedZones(String actionValue){

        Intent intent = new Intent(RedZonesSettings.this, SetRedZoneCenter.class);

        if(actionValue.equals("update")){

            intent.putExtra("action", actionValue)
                    .putExtra("name",name)
                    .putExtra("latitude", latitude)
                    .putExtra("longitude", longitude)
                    .putExtra("radius", radius);
        }
        else intent.putExtra("action", actionValue);

        startActivity(intent);
        finish();
    }

    private void findingAction(){

        redZoneButton.setOnClickListener(v -> {

            if(verfications.isNetworkAvailable(RedZonesSettings.this)){

                String nameText = Objects.requireNonNull(nameTextView.getText())
                        .toString().toUpperCase();

                String radiusText = Objects.requireNonNull(radiusTextView.getText())
                        .toString();

                if(nameText.equals("") || radiusText.equals(""))
                    showAlertDialog(missingData);

                else {

                    if(action.equals("update"))
                        updateRedZone(nameText, radiusText);

                    else {

                        collection.find().forEach(document ->
                                names.add(Objects.requireNonNull(document.get("name")).toString())
                        ).addOnCompleteListener(task -> {

                            boolean flag = false;

                            for (int i = 0; i < names.size(); i++) {

                                if (names.get(i).equals(name)) {

                                    flag = true;
                                    showAlertDialog(sameName);
                                    break;
                                }
                            }

                            if (!flag)
                                insertRedZone(nameText, radiusText);
                        });
                    }
                }
            }
            else showAlertDialog(network);
        });
    }

    private void insertRedZone(String redZoneNameText, String redZoneRadiusText){

        Document insertNewData = new Document("name", redZoneNameText)
                .append("radius", Integer.parseInt(redZoneRadiusText))
                .append("latitude", latitude)
                .append("longitude", longitude);

        collection.insertOne(insertNewData).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {

                Toast.makeText(RedZonesSettings.this, language.getAddMessage(), Toast.LENGTH_SHORT).show();

                finish();
            }
            else Toast.makeText(RedZonesSettings.this, language.getErrorMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void updateRedZone(String redZoneNameText, String redZoneRadiusText){

        Document dataEdited = new Document("name", redZoneNameText)
                .append("radius", Integer.parseInt(redZoneRadiusText))
                .append("latitude", latitude)
                .append("longitude", longitude);

        Document filter = new Document("name", name);

        collection.findOneAndUpdate(filter, dataEdited).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {

                Toast.makeText(RedZonesSettings.this, language.getUpdateMessage(), Toast.LENGTH_SHORT).show();

                finish();
            }
            else Toast.makeText(RedZonesSettings.this, language.getErrorMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}