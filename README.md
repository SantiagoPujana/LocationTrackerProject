<div align="justify"> 

<h1 align="center">Location Tracker Project</h1>

<div align="center">  
  <img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white" />
  <img src="https://img.shields.io/badge/MongoDB-4EA94B?style=for-the-badge&logo=mongodb&logoColor=white" />
  <img src="https://img.shields.io/badge/Android_Studio-3DDC84?style=for-the-badge&logo=android-studio&logoColor=white" />
  <img src="https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" />
</div>

<h3><b>About project</b></h3>

This project consists of two android apps, Location Service running a background service that get last known location (latitude, longitude, address) every 3 seconds and then upload it to a MongoDB collection with a username that you must set in the strings.xml file, this app must be installed in the user's device. Background service needs launch notifications while is working and app is not deployed, thus you must define channel id and other things.

<div align="center">
  <img src="https://raw.githubusercontent.com/ProzTock/LocationTrackerProject/master/UI_Pictures/Location_Service_App_UI.jpeg">
</div>

<h3><b>Features</b></h3> 

Location Tracker App contains the following features:

- Track User: App queries latitude and longitude of a specific username stored and uploaded for Location Service App, then of that it shows a marker on the map using Google Maps API, you can move the camera to the target, watch where you are, and enable and disable traffic. All these options are in a navigation drawler located in the left side of the map. 

- Red Zones: You can set up (insert, delete and update) Red Zones, you must define a center (latitude, longitude), radius in meters and a name, a red zone is a circle on the map that allows you know if user is inside of it, and it is inside, Location Tracker emits an alarm.

- Track All Users: It shows on the map all locations stored in the database, each user location will be a maker, thus won't have follow target option.
 
<div align="center">
  <img src="https://raw.githubusercontent.com/ProzTock/LocationTrackerProject/master/UI_Pictures/Location_Tracker_App_UI.png">
</div>

<h3><b>Using MongoDB and Realm</b></h3>

MongoDB Atlas and Realm were used in these projects, thus you must include app id (Realm) in the projects, besides this, set a database and two collections (Atlas), one for users and other for red zones, also custom authentication (Realm) with an authentication function (authFunc) because each app has an username predefine that access to database, in Location Service you define username but Location Tracker has a default username (LocationTrackerClient), so you don't must change it, and finally put collections schema (Realm) and set collections rules (Realm).

***authFunc***

    exports = async function(loginPayload) {
    
      const users = context.services
        .get("mongodb-atlas")
        .db("Put here name of your database")
        .collection("Put here name of your user collection");
    
      const { username } = loginPayload;
    
      const user = await users.findOne({ username });
    
      if (user) {
        return user._id.toString();
      } else {
        const result = await users.insertOne({ username });
        return result.insertedId.toString();
      }
    };

***Users Collection Schema***

    {
      "properties": {
        "_id": {
          "bsonType": "objectId"
        },
        "username": {
          "bsonType": "string"
        },
        "longitude": {
          "bsonType": "double"
        },
        "latitude": {
          "bsonType": "double"
        },
        "address": {
          "bsonType": "string"
        },
        "date": {
          "bsonType": "string"
        }
      }
    }

***Red Zones Collection Schema***

    {
      "properties": {
        "_id": {
          "bsonType": "objectId"
        },
        "name": {
          "bsonType": "string"
        },
        "longitude": {
          "bsonType": "double"
        },
        "latitude": {
          "bsonType": "double"
        },
        "radius": {
          "bsonType": "int"
        }
      }
    } 

<h3><b>Set up App Data</b></h3>

You must set following specific data in the strings.xml file in each app project:
- Username (Location Service)

- App Id Realm (Location Service, Location Tracker)

- Users Collection Name (Location Service, Location Tracker)

- Red Zones Collection Name (Location Tracker)

- Channel Id Notification (Location Service)

<h3><b>Additional Features</b></h3>

- Language: These apps detect phone language and changing text to Spanish or English according to language detected, if phone language isn't Spanish then English language will be selected.

- If you touch a marker on the Location Tracker map, it will show you address and geographical coordinates (Track User) or just username (Track All Users). 

<h3><b>Recommendations and Warnings</b></h3>

- These apps need to check location permissions accepted (All the time in Android Q or higher), access to Internet (Wi-Fi or Data Mobile), and GPS service enabled to working.

- Location Service App consumes a lot of battery because always runs a location background service, so some vendors like Samsung or Huawei block this type of apps.

<a href="https://github.com/SantiagoPujana/LocationTrackerProject/blob/main/LICENSE"><b>MIT License</b></a>

</div> 
