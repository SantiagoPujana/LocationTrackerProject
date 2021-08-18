package com.locationtracker;

import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Context;

public class Language {

    @SuppressLint("StaticFieldLeak")
    private static Language instance = null;

    private final Context context;
    private final String language;

    private String traffic;

    public static synchronized Language getInstance(Context context) {

        if (instance == null)
            instance = new Language(context);

        return instance;
    }

    private Language(Context context){

        this.language = Locale.getDefault().getLanguage();
        this.context = context;
    }

    public String getConfirmationMessage() {

        String confirmationMessage;

        if(language.equals("es"))
            confirmationMessage = context.getResources().getString(R.string.confirmation_sp);
        else confirmationMessage = context.getResources().getString(R.string.confirmation);

        return confirmationMessage;
    }

    public String getCancel() {

        String cancel;

        if(language.equals("es"))
            cancel = "Cancelar";
        else cancel = "Cancel";

        return cancel;
    }

    public String getVoidData() {

        String voidData;

        if(language.equals("es"))
            voidData = context.getResources().getString(R.string.void_data_sp);
        else voidData = context.getResources().getString(R.string.void_data);

        return voidData;
    }

    public String getTrackUser() {

        String trackUser;

        if(language.equals("es"))
            trackUser = context.getResources().getString(R.string.track_user_sp);
        else trackUser = context.getResources().getString(R.string.track_user);

        return trackUser;
    }

    public String getTitle() {

        String title;

        if(language.equals("es"))
            title = context.getResources().getString(R.string.app_name_sp);
        else title = context.getResources().getString(R.string.app_name);

        return title;
    }

    public String getGoToMap() {

        String buttonText;

        if(language.equals("es"))
            buttonText = context.getResources().getString(R.string.go_to_map_sp);
        else buttonText = context.getResources().getString(R.string.go_to_map);

        return buttonText;
    }

    public String getRequired() {

        String required;

        if(language.equals("es"))
            required = context.getResources().getString(R.string.required_sp);
        else required = context.getResources().getString(R.string.required);

        return required;
    }

    public String getPlaceholderUsername() {

        String placeholderUsername;

        if(language.equals("es"))
            placeholderUsername = context.getResources().getString(R.string.placeholder_username_sp);
        else placeholderUsername = context.getResources().getString(R.string.placeholder_username);

        return placeholderUsername;
    }

    public String getHintUsername() {

        String hintUsername;

        if(language.equals("es"))
            hintUsername = context.getResources().getString(R.string.hint_username_sp);
        else hintUsername = context.getResources().getString(R.string.hint_username);

        return hintUsername;
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

    public String getMissingText() {

        String missingText;

        if(language.equals("es"))
            missingText = context.getResources().getString(R.string.missing_text_sp);
        else missingText = context.getResources().getString(R.string.missing_text);

        return missingText;
    }

    public String getMissingUsername() {

        String missingUsername;

        if(language.equals("es"))
            missingUsername = context.getResources().getString(R.string.missing_username_sp);
        else missingUsername = context.getResources().getString(R.string.missing_username);

        return missingUsername;
    }

    public String getInvalidUser() {

        String invalidUser;

        if(language.equals("es"))
            invalidUser = context.getResources().getString(R.string.invalid_user_sp);
        else invalidUser = context.getResources().getString(R.string.invalid_user);

        return invalidUser;
    }

    public String getTrafficEnable() {

        if(language.equals("es"))
            traffic = context.getResources().getString(R.string.traffic_enable_sp);
        else traffic = context.getResources().getString(R.string.traffic_enable);

        return traffic;
    }

    public String getTrafficDisable() {

        if(language.equals("es"))
            traffic = context.getResources().getString(R.string.traffic_disable_sp);
        else traffic = context.getResources().getString(R.string.traffic_disable);

        return traffic;
    }

    public String getFollow() {

        String trackLoct;

        if(language.equals("es"))
            trackLoct = context.getResources().getString(R.string.follow_sp);
        else trackLoct = context.getResources().getString(R.string.follow);

        return trackLoct;
    }

    public String getLatitude() {

        String latitude;

        if(language.equals("es"))
            latitude = "latitud: ";
        else latitude = "latitude: ";

        return latitude;
    }

    public String getLongitude() {

        String longitude;

        if(language.equals("es"))
            longitude = "longitud: ";
        else longitude = "longitude: ";

        return longitude;
    }

    public String getRadius() {

        String radius;

        if(language.equals("es"))
            radius = "radio: ";
        else radius = "radius: ";

        return radius;
    }

    public String getRedZonesTitle() {

        String redZonesTitle;

        if(language.equals("es"))
            redZonesTitle = context.getResources().getString(R.string.configurationTitle_sp);
        else redZonesTitle = context.getResources().getString(R.string.configurationTitle);

        return redZonesTitle;
    }

    public String getAddRedZone() {

        String addRedZone;

        if(language.equals("es"))
            addRedZone = context.getResources().getString(R.string.add_red_zone_sp);
        else addRedZone = context.getResources().getString(R.string.add_red_zone);

        return addRedZone;
    }

    public String getNext() {

        String next;

        if(language.equals("es"))
            next = "SIGUIENTE";
        else next = "NEXT";

        return next;
    }

    public String getIntroMessage() {

        String introMessage;

        if(language.equals("es"))
            introMessage = context.getResources().getString(R.string.intro_message_sp);
        else introMessage = context.getResources().getString(R.string.intro_message);

        return introMessage;
    }

    public String getCenterTitle() {

        String centerTitle;

        if(language.equals("es"))
            centerTitle = context.getResources().getString(R.string.center_title_sp);
        else centerTitle = context.getResources().getString(R.string.center_title);

        return centerTitle;
    }

    public String getMissingCenter() {

        String missingCenter;

        if(language.equals("es"))
            missingCenter = context.getResources().getString(R.string.missing_center_sp);
        else missingCenter = context.getResources().getString(R.string.missing_center);

        return missingCenter;
    }

    public String getHintRedZoneName() {

        String hintNameRZ;

        if(language.equals("es"))
            hintNameRZ = context.getResources().getString(R.string.hint_name_sp);
        else hintNameRZ = context.getResources().getString(R.string.hint_name);

        return hintNameRZ;
    }

    public String getHintRadius() {

        String hintRadiusRZ;

        if(language.equals("es"))
            hintRadiusRZ = context.getResources().getString(R.string.hint_radius_sp);
        else hintRadiusRZ = context.getResources().getString(R.string.hint_radius);

        return hintRadiusRZ;
    }

    public String getPlaceholderRedZoneName() {

        String namePlaceholderRZ;

        if(language.equals("es"))
            namePlaceholderRZ = context.getResources().getString(R.string.placeholder_name_sp);
        else namePlaceholderRZ = context.getResources().getString(R.string.placeholder_name);

        return namePlaceholderRZ;
    }

    public String getPlaceholderRadius() {

        String radiusPlaceholderRZ;

        if(language.equals("es"))
            radiusPlaceholderRZ = context.getResources().getString(R.string.placeholder_radius_sp);
        else radiusPlaceholderRZ = context.getResources().getString(R.string.placeholder_radius);

        return radiusPlaceholderRZ;
    }

    public String getMissingData() {

        String missingData;

        if(language.equals("es"))
            missingData = context.getResources().getString(R.string.missing_data_sp);
        else missingData = context.getResources().getString(R.string.missing_data);

        return missingData;
    }

    public String getGoBack() {

        String goBack;

        if(language.equals("es"))
            goBack = context.getResources().getString(R.string.go_back_sp);
        else goBack = context.getResources().getString(R.string.go_back);

        return goBack;
    }

    public String getSameName() {

        String sameName;

        if(language.equals("es"))
            sameName = context.getResources().getString(R.string.same_name_sp);
        else sameName = context.getResources().getString(R.string.same_name);

        return sameName;
    }

    public String getEditRedZone() {

        String editRedZone;

        if(language.equals("es"))
            editRedZone = context.getResources().getString(R.string.edit_red_zone_sp);
        else editRedZone = context.getResources().getString(R.string.edit_red_zone);

        return editRedZone;
    }

    public String getDeleteMessage() {

        String deleteMessage;

        if(language.equals("es"))
            deleteMessage = context.getResources().getString(R.string.delete_message_sp);
        else deleteMessage = context.getResources().getString(R.string.delete_message);

        return deleteMessage;
    }

    public String getUpdateMessage() {

        String updateMessage;

        if(language.equals("es"))
            updateMessage = context.getResources().getString(R.string.update_message_sp);
        else updateMessage = context.getResources().getString(R.string.update_message);

        return updateMessage;
    }

    public String getAddMessage() {

        String addMessage;

        if(language.equals("es"))
            addMessage = context.getResources().getString(R.string.insert_message_sp);
        else addMessage = context.getResources().getString(R.string.insert_message);

        return addMessage;
    }

    public String getErrorMessage() {

        String errorMessage;

        if(language.equals("es"))
            errorMessage = context.getResources().getString(R.string.error_message_sp);
        else errorMessage = context.getResources().getString(R.string.error_message);

        return errorMessage;
    }

    public String getTarget() {

        String target;

        if(language.equals("es"))
            target = "OBJETIVO: ";
        else target = "TARGET: ";

        return target;
    }

    public String getUnknownAddress() {

        String address;

        if(language.equals("es"))
            address = context.getResources().getString(R.string.unknown_address_sp);
        else address = context.getResources().getString(R.string.unknown_address);

        return address;
    }

    public String getTrackAllUsers() {

        String trackAllUsers;

        if(language.equals("es"))
            trackAllUsers = context.getResources().getString(R.string.track_all_users_sp);
        else trackAllUsers = context.getResources().getString(R.string.track_all_users);

        return trackAllUsers;
    }

    public String getInsufficientUsers() {

        String insufficientUsers;

        if(language.equals("es"))
            insufficientUsers = context.getResources().getString(R.string.insufficient_users_sp);
        else insufficientUsers = context.getResources().getString(R.string.insufficient_users);

        return insufficientUsers;
    }
}
