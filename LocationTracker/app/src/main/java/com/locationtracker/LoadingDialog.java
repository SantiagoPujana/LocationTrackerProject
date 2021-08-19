package com.locationtracker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.widget.TextView;

public class LoadingDialog {

    private final Activity activity;
    private AlertDialog alertDialog;
    private final LayoutInflater layoutInflater;

    @SuppressLint({"ResourceType", "InflateParams"})
    public LoadingDialog(Activity activity){

        this.activity = activity;

        layoutInflater = this.activity.getLayoutInflater();

        TextView loadingTextView = layoutInflater.inflate(R.layout.custom_dialog, null).findViewById(R.id.loadingTextView);
        loadingTextView.setText(Language.getInstance(activity).getLoading());
    }

    @SuppressLint("InflateParams")
    public void startLoadingDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setView(layoutInflater.inflate(R.layout.custom_dialog, null))
                .setCancelable(false);

        alertDialog = builder.create();
        alertDialog.show();
    }

    public void dismissLoadingDialog() { alertDialog.dismiss(); }
}
