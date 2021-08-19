package com.locationtracker.trackuser;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.locationtracker.Language;
import com.locationtracker.LoadingDialog;
import com.locationtracker.MainMenu;
import com.locationtracker.R;
import com.locationtracker.Verifications;
import com.locationtracker.db.RequestDBConnection;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;

import org.bson.Document;

import java.util.Objects;

public class TrackUser extends AppCompatActivity {

    @SuppressLint("StaticFieldLeak")
    private static String username;

    private String alert;
    private String accept;
    private String missingText;
    private String missingUsername;
    private String invalidUser;
    private TextView titleTextView;
    private TextInputEditText editText;
    private TextInputLayout editLayout;
    private Button goToMapButton;
    private Language language;
    private LoadingDialog loadingDialog;

    private final RequestDBConnection requestDBConn = new RequestDBConnection();
    private final Verifications verifications = new Verifications();

    private final CountDownTimer countDownTimer = new CountDownTimer(1000, 1000) {

        public void onTick(long millisUntilFinished) { }

        public void onFinish() {

            if(verifications.isLocationServiceEnabled(TrackUser.this) &&
                    verifications.checkPermissions(TrackUser.this) &&
                    verifications.isNetworkAvailable(TrackUser.this)) {

                goToMap();
                loadingDialog.dismissLoadingDialog();
            }
            else countDownTimer.start();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_track_user);

        language = Language.getInstance(TrackUser.this);
        loadingDialog = new LoadingDialog(TrackUser.this);

        editLayout = findViewById(R.id.textInputLayout);
        editText = findViewById(R.id.textInput);
        goToMapButton = findViewById(R.id.goToMap);
        titleTextView = findViewById(R.id.trackUserTitle);

        settingText();

        goToMapButton.setOnClickListener(v ->{

            if(Objects.requireNonNull(editText.getText()).toString().equals(""))
                showAlertDialog(missingText);
            else checkInput();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        finish();
    }

    private void findUsername(String username, RemoteMongoCollection<Document> collectionObject){

        final Document queryDocument = new Document(
                "username", username);

        collectionObject.findOne(queryDocument).addOnCompleteListener(task -> {

            if(task.getResult() != null
                && Objects.equals(Objects.requireNonNull(task.getResult()).get("username"), username)){

                countDownTimer.start();
                editText.setText(null);
            }
            else {

                loadingDialog.dismissLoadingDialog();
                showAlertDialog(username + " " + missingUsername);
            }
        });
    }

    private void checkInput(){

        String clientName = "LocationTrackerClient";

        if(Objects.requireNonNull(editText.getText()).toString().equals(clientName)) {

            editText.setText(null);

            showAlertDialog(invalidUser);
        }
        else {

            username = editText.getText().
                    toString().
                    toUpperCase();

            RemoteMongoCollection<Document> collection = requestDBConn.requestDBConnection(clientName,
                    getString(R.string.database_name),
                    getString(R.string.user_collection_name),
                    TrackUser.this);

            loadingDialog.startLoadingDialog();
            findUsername(username, collection);
        }
    }

    private void settingText() {

        alert = language.getAlert();
        accept = language.getAccept();
        invalidUser = language.getInvalidUser();
        missingText = language.getMissingText();
        missingUsername = language.getMissingUsername();

        titleTextView.setText(language.getTrackUser());
        goToMapButton.setText(language.getGoToMap());
        editLayout.setHelperText(language.getRequired());
        editLayout.setHint(language.getHintUsername());
        editLayout.setPlaceholderText(language.getPlaceholderUsername());
    }

    private void showAlertDialog(String message){

        AlertDialog.Builder builder = new AlertDialog.Builder(TrackUser.this);

        builder.setMessage(message)
                .setTitle(alert)
                .setCancelable(false);

        builder.setPositiveButton(accept, (dialog, id) ->{ });

        AlertDialog dialog = builder.create();

        dialog.show();
    }

    private void goToMap(){

        Intent intent = new Intent(TrackUser.this, MapsActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }
}