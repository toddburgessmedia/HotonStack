package com.toddburgessmedia.stackoverflowretrofit;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;

public class MeetupActivity extends AppCompatActivity {

    HashMap<String,Double> latLng;
    int MEETUPAUTH = 1;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(MainActivity.TAG, "onActivityResult: we are bck");
        if (resultCode == RESULT_OK) {
            Log.d(MainActivity.TAG, "onActivityResult: access token is ... " + data.getStringExtra("access_token"));
            Log.d(MainActivity.TAG, "onActivityResult: yay!!!");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meetup);

        Intent i = new Intent(this,MeetupAuthActivity.class);
        Log.d(MainActivity.TAG, "onCreate: starting activity");
        startActivityForResult(i,MEETUPAUTH);

//        getGPSLocation();
//        Log.d(MainActivity.TAG, "onCreate: lat " + latLng.get("latitude"));
//        Log.d(MainActivity.TAG, "onCreate: long " + latLng.get("longitude"));
    }

    public void getGPSLocation() {

        Double lat,lng;
        if (latLng == null) {
            latLng = new HashMap<>();
        }

        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        boolean gps = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        Log.d(MainActivity.TAG, "getGPSLocation: " + gps);
        boolean network = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Log.d(MainActivity.TAG, "getGPSLocation: " + network);
        String provider = manager.getBestProvider(criteria, false);

        try {
            Location location = manager.getLastKnownLocation(provider);
            lat = location.getLatitude();
            lng = location.getLongitude();
            latLng.put("latitude",lat);
            latLng.put("longitude",lng);

        } catch (SecurityException se) {
            Log.d(MainActivity.TAG, "getGPSLocation: no permission");
            Toast.makeText(MeetupActivity.this, "No Permission", Toast.LENGTH_SHORT).show();
        }

    }
}
