package com.locationservice;

import android.annotation.SuppressLint;
import android.content.Context;

import java.util.Locale;

public class Language {

    @SuppressLint("StaticFieldLeak")
    private static Language instance = null;

    private final Context context;
    private final String language;

    public static synchronized Language getInstance(Context context) {

        if (instance == null)
            instance = new Language(context);

        return instance;
    }

    private Language(Context context){

        this.language = Locale.getDefault().getLanguage();
        this.context = context;
    }

    public String getTitle() {

        String title;

        if(language.equals("es"))
            title = context.getResources().getString(R.string.app_name_sp);
        else title = context.getResources().getString(R.string.app_name);

        return title;
    }

    public String getImportantMessage() {

        String importantMessage;

        if(language.equals("es"))
            importantMessage = context.getResources().getString(R.string.important_message_sp);
        else importantMessage = context.getResources().getString(R.string.important_message);

        return importantMessage;
    }

    public String getPermissions() {

        String permissions;

        if(language.equals("es"))
            permissions = context.getResources().getString(R.string.permissions_sp);
        else permissions = context.getResources().getString(R.string.permissions);

        return permissions;
    }

    public String getNetwork() {

        String network;

        if(language.equals("es"))
            network = context.getResources().getString(R.string.network_sp);
        else network = context.getResources().getString(R.string.network);

        return network;
    }

    public String getGps() {

        String gps;

        if(language.equals("es"))
            gps = context.getResources().getString(R.string.gps_sp);
        else gps = context.getResources().getString(R.string.gps);

        return gps;
    }

    public String getAlert() {

        String alert;

        if(language.equals("es"))
            alert = "Alerta:";
        else alert = "Alert:";

        return alert;
    }

    public String getAccept() {

        String accept;

        if(language.equals("es"))
            accept = "Aceptar";
        else accept = "Accept";

        return accept;
    }

    public String getIsRunning() {

        String isRunning;

        if(language.equals("es"))
            isRunning = " " + context.getResources().getString(R.string.is_running_sp);
        else isRunning = " " + context.getResources().getString(R.string.is_running);

        return isRunning;
    }

    public String getUnknownAddress() {

        String unknownAddress;

        if(language.equals("es"))
            unknownAddress = context.getResources().getString(R.string.unknown_address_sp);
        else unknownAddress = context.getResources().getString(R.string.unknown_address);

        return unknownAddress;
    }

    public String getYouAreHere() {

        String youAreHere;

        if(language.equals("es"))
            youAreHere = context.getResources().getString(R.string.you_are_here_sp) + " ";
        else youAreHere = context.getResources().getString(R.string.you_are_here) + " ";

        return youAreHere;
    }
}
