package com.application.upapplication.Model;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

/**
 * Created by user on 12/26/2016.
 */

public class Constants {
    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS = 12*60*60*1000;
    public static final float GEOFENCE_RADIUS_IN_METERS = 100;

    public static final HashMap<String,LatLng> LANDMARKS = new HashMap<String,LatLng>();
    static{
        LANDMARKS.put("CITC",new LatLng(3.21463478517405,101.72627863592726));
        LANDMARKS.put("College Hall",new LatLng(3.2164772395894454,101.72918454132127));
        LANDMARKS.put("Sport Complex",new LatLng(3.2181054524461077,101.72976750764656));
        LANDMARKS.put("Library",new LatLng(3.217826942488,101.72794589714908));
        LANDMARKS.put("Canteen1",new LatLng(3.2162844246830464,101.7255148888944));
        LANDMARKS.put("Canteen2",new LatLng(3.216123745719101,101.7273323534937));
        LANDMARKS.put("Hostel",new LatLng(3.2179652235676866,101.73299664324372));
        LANDMARKS.put("DK AB",new LatLng(3.21596209281308,101.73130148714637));
        LANDMARKS.put("My home",new LatLng(3.194148,101.688843));

    }
}
