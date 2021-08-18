package com.locationservice.db;

import android.content.Context;

import com.locationservice.R;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;
import com.mongodb.stitch.core.auth.providers.function.FunctionCredential;

import org.bson.Document;

public class DatabaseConnection {

    private static DatabaseConnection instance = null;

    private final StitchAppClient client;

    private DatabaseConnection(String username, Context context) {

        String appId = context.getString(R.string.app_id);

        client = Stitch.initializeDefaultAppClient(appId);

        authenticateUser(username);
    }

    public static synchronized DatabaseConnection getInstance(String username, Context context) {

        if (instance == null)
            instance = new DatabaseConnection(username, context);

        return instance;
    }

    public RemoteMongoCollection<Document> connectWithMongodb(String database, String collection){

        RemoteMongoClient mongoClient = client
                .getServiceClient(RemoteMongoClient.factory, "mongodb-atlas");

        return mongoClient.getDatabase(database).getCollection(collection);
    }

    private void authenticateUser(String username){

        Stitch.getDefaultAppClient().getAuth().loginWithCredential(
                new FunctionCredential(new Document("username", username)));
    }
}

