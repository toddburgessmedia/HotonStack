package com.toddburgessmedia.stackoverflowretrofit;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.toddburgessmedia.stackoverflowretrofit.retrofit.MeetUpGroup;
import com.toddburgessmedia.stackoverflowretrofit.retrofit.MeetupAPI;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MeetupActivity extends AppCompatActivity {

    String TAG = MainActivity.TAG;

    HashMap<String,Double> latLng;
    String oauthtoken;
    int MEETUPAUTH = 1;

    String tagname;

    List<MeetUpGroup> groups;

    RecyclerView rv;
    RecycleViewMeetup adapter;
    private ProgressDialog progress;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(MainActivity.TAG, "onActivityResult: we are bck");
        if (resultCode == RESULT_OK) {
            Log.d(MainActivity.TAG, "onActivityResult: access token is ... " + data.getStringExtra("access_token"));
            Log.d(MainActivity.TAG, "onActivityResult: yay!!!");

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor edit = prefs.edit();
            edit.putString("oauthtoken",data.getStringExtra("access_token"));
            edit.commit();

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meetup);

        rv = (RecyclerView) findViewById(R.id.rv_meetup);
        if (rv != null) {
            rv.setHasFixedSize(true);
        }
        rv.setLayoutManager(new LinearLayoutManager(this));

        startProgressDialog();
        progress.setMessage("Getting GPS location");
        getGPSLocation();
        Log.d(MainActivity.TAG, "onCreate: lat " + latLng.get("latitude"));
        Log.d(MainActivity.TAG, "onCreate: long " + latLng.get("longitude"));
        progress.setMessage("Finding Meetup Groups");
        tagname = getIntent().getStringExtra("searchtag");
        Log.d(TAG, "onCreate: tag" + tagname);
        getMeetupGroups(tagname);

    }

    public void getGPSLocation() {

        Double lat,lng;
        if (latLng == null) {
            latLng = new HashMap<>();
        }

        try {
            LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                buildAlertMessageNoGps();
            }
            Criteria criteria = new Criteria();
            boolean gps = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            Log.d(MainActivity.TAG, "getGPSLocation: " + gps);
            boolean network = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            Log.d(MainActivity.TAG, "getGPSLocation: " + network);
            String provider = manager.getBestProvider(criteria, false);
            Log.d(TAG, "getGPSLocation: " + provider);

            Location location = manager.getLastKnownLocation(provider);
            if (location == null) {
                Toast.makeText(MeetupActivity.this, "GPS Failed to Work :(", Toast.LENGTH_SHORT).show();
                return;
            }
            lat = location.getLatitude();
            lng = location.getLongitude();
            latLng.put("latitude",lat);
            latLng.put("longitude",lng);

        } catch (SecurityException se) {
            Log.d(MainActivity.TAG, "getGPSLocation: no permission");
            Toast.makeText(MeetupActivity.this, "No Permission", Toast.LENGTH_SHORT).show();
        }

    }

    private void getMeetupGroups(String tagname) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.meetup.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MeetupAPI meetupAPI = retrofit.create(MeetupAPI.class);

        Call<List<MeetUpGroup>> call = meetupAPI.getMeetupGroups(
                latLng.get("latitude").toString(),
                latLng.get("longitude").toString(),
                tagname);
        call.enqueue(new Callback<List<MeetUpGroup>>() {
            @Override
            public void onResponse(Call<List<MeetUpGroup>> call, Response<List<MeetUpGroup>> response) {
                Log.d(TAG, "onResponse: it worked!!!");
                groups = response.body();
                stopProgressDialog();
                if (groups.size() == 0) {
                    Toast.makeText(MeetupActivity.this, "No Meetups Found", Toast.LENGTH_SHORT).show();
                    return;
                }
                adapter = new RecycleViewMeetup(groups,getBaseContext());
                rv.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<MeetUpGroup>> call, Throwable t) {
                stopProgressDialog();
                Toast.makeText(MeetupActivity.this, "No Network Connection", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure: it did not work!!! " + t.getMessage());
            }
        });
    }

    private void startProgressDialog() {

        if (progress == null) {
            progress = new ProgressDialog(this);
            //progress.setMessage("Loading Tags");
        }
        progress.show();
    }

    private void stopProgressDialog() {

        if (progress != null) {
            progress.dismiss();
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }


}
