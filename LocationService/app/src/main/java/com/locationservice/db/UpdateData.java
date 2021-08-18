package com.locationservice.db;

import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;

import org.bson.Document;

public class UpdateData {

    public void updateData(RemoteMongoCollection<Document> collection,
                           String username,
                           Double longitude,
                           Double latitude,
                           String address,
                           String currentDate){

        Document updateDocument = new Document(
                "username", username)
                .append("latitude", latitude)
                .append("longitude", longitude)
                .append("address", address)
                .append("date", currentDate);

        Document queryDocument = new Document(
                "username", username);

        collection.updateOne(queryDocument, updateDocument);
    }
}