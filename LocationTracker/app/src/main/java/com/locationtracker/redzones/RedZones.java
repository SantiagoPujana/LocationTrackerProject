package com.locationtracker.redzones;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.locationtracker.Language;
import com.locationtracker.R;
import com.locationtracker.db.RequestDBConnection;

import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;

import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RedZones extends AppCompatActivity {

    private final RequestDBConnection requestDBConn = new RequestDBConnection();

    private RemoteMongoCollection<Document> collection;
    private Language language;
    private TextView titleTextView;
    private Button addRedZoneButton;
    private String alert;
    private String accept;
    private String confirmation;
    private String cancel;
    private String voidData;
    private List<ListElement> elementList;
    private ListAdapter listAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_red_zones);

        collection = requestDBConn.requestDBConnection("LocationTrackerClient",
                "Database",
                "Red Zones",
                RedZones.this);

        language = Language.getInstance(RedZones.this);

        titleTextView = findViewById(R.id.configurationTitle);
        addRedZoneButton = findViewById(R.id.addRedZone);
        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(RedZones.this));

        addRedZoneButton.setOnClickListener(v -> goToSetRedZoneCenter("add", null));

        elementList = new ArrayList<>();

        listAdapter = new ListAdapter(elementList, RedZones.this, (item, id) -> {

            if(id.equals("delete"))
                showAlertDialog(confirmation, item.getRedZoneName(), "delete");
            else goToSetRedZoneCenter("update", item);
        });

        settingText();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

        elementList.clear();
        initialize();
    }

    private void settingText(){

        titleTextView.setText(language.getRedZonesTitle());
        addRedZoneButton.setText(language.getAddRedZone());
        voidData = language.getVoidData();
        cancel = language.getCancel();
        confirmation = language.getConfirmationMessage();
        accept = language.getAccept();
        alert = language.getAlert();
    }

    private void initialize() {

        collection.find().forEach(document ->
                elementList.add(new ListElement(Objects.requireNonNull(document.get("name")).toString(),
                        Double.parseDouble(Objects.requireNonNull(document.get("longitude")).toString()),
                        Double.parseDouble(Objects.requireNonNull(document.get("latitude")).toString()),
                        Integer.parseInt(Objects.requireNonNull(document.get("radius")).toString()))))
                .addOnCompleteListener(task -> {

                    if(task.isSuccessful() && elementList.size() > 0)
                        recyclerView.setAdapter(listAdapter);
                    else showAlertDialog(voidData, null, "add");
        });
    }

    private void showAlertDialog(String message, String name, String idAlert){

        AlertDialog.Builder builder = new AlertDialog.Builder(RedZones.this);

        builder.setMessage(message)
                .setTitle(alert)
                .setCancelable(false);

        builder.setPositiveButton(accept, (dialog, id) ->{

            if(idAlert.equals("delete")){

                Document filter = new Document("name", name);

                collection.findOneAndDelete(filter).addOnCompleteListener(task -> {

                    if(task.isSuccessful()){

                        for(int i = 0; i < elementList.size(); i++){

                            if(elementList.get(i).getRedZoneName().equals(name)){
                                elementList.remove(i);
                                break;
                            }
                        }

                        listAdapter.notifyDataSetChanged();

                        Toast.makeText(RedZones.this, language.getDeleteMessage(), Toast.LENGTH_SHORT).show();
                    }
                    else Toast.makeText(RedZones.this, language.getErrorMessage(), Toast.LENGTH_SHORT).show();
                });
            }
            else if (idAlert.equals("add")){ goToSetRedZoneCenter("add", null); }
        });

        builder.setNegativeButton(cancel, (dialog, id) ->{ });

        AlertDialog dialog = builder.create();

        dialog.show();
    }

    private void goToSetRedZoneCenter(String actionValue, ListElement listItems){

        Intent intent = new Intent(RedZones.this, SetRedZoneCenter.class);

        if(actionValue.equals("update")){

            intent.putExtra("action", actionValue)
                    .putExtra("name",listItems.getRedZoneName())
                    .putExtra("latitude", listItems.getLatitude())
                    .putExtra("longitude", listItems.getLongitude())
                    .putExtra("radius", listItems.getRadius());
        }
        else intent.putExtra("action", actionValue);

        startActivity(intent);
    }
}