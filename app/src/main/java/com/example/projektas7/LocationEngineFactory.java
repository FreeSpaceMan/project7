//package com.example.projektas7;
//
//import android.content.Context;
//import android.location.Location;
//
//import com.mapbox.android.core.location.LocationEngine;
//import com.mapbox.android.core.location.LocationEnginePriority;
//import com.mapbox.android.core.location.LocationEngineProvider;
//
//public class LocationEngineFactory {
//
//    public static LocationEngine getLocationEngine(Context context) {
//        LocationEngine locationEngine = new LocationEngineProvider(context).obtainBestLocationEngineAvailable();
//        locationEngine.setPriority(LocationEnginePriority.HIGH_ACCURACY);
//        locationEngine.activate();
//
//        return locationEngine;
//    }
//
//}
