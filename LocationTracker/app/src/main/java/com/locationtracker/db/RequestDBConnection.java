package com.locationtracker.db;

import android.content.Context;

import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;

import org.bson.Document;

public class RequestDBConnection {

    public RequestDBConnection() { }

    public RemoteMongoCollection<Document> requestDBConnection(String username, String database,
                                                               String collection, Context context){

        DatabaseConnection connection = DatabaseConnection.getInstance(username, context);

        return connection.connectWithMongodb(database, collection);
    }
}
