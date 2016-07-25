package com.toddburgessmedia.stackoverflowretrofit;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabSelectedListener;
import com.toddburgessmedia.stackoverflowretrofit.retrofit.MeetUpGroup;
import com.toddburgessmedia.stackoverflowretrofit.retrofit.MeetupAPI;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

public class MeetupActivity extends AppCompatActivity {

    String TAG = MainActivity.TAG;

    HashMap<String,Double> latLng;
    String oauthtoken;

    String searchTag;
    String searchsite;

    BottomBar bottomBar;

    List<MeetUpGroup> groups;

    @BindView(R.id.rv_meetup) RecyclerView rv;
    RecycleViewMeetup adapter;
    private ProgressDialog progress;

    Subscription subscribe;

    @BindView(R.id.meetup_location) TextView meetupLoc;
    @BindView(R.id.meetup_searchterm) TextView searchTerm;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(MainActivity.TAG, "onActivityResult: we are bck");
        if (resultCode == RESULT_OK) {
            Log.d(MainActivity.TAG, "onActivityResult: access token is ... " + data.getStringExtra("access_token"));
            Log.d(MainActivity.TAG, "onActivityResult: yay!!!");

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor edit = prefs.edit();
            edit.putString("oauthtoken",data.getStringExtra("access_token"));
            edit.apply();

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meetup);
        ButterKnife.bind(this);


        if (rv != null) {
            rv.setHasFixedSize(true);
        }
        rv.setLayoutManager(new LinearLayoutManager(this));

        if (savedInstanceState != null) {
            groups = (List<MeetUpGroup>) savedInstanceState.getSerializable("meetup_groups");
            searchTag = savedInstanceState.getString("searchtag");
            searchsite = savedInstanceState.getString("searchsite");
            latLng = new HashMap<>();
            latLng.put("latitude",savedInstanceState.getDouble("latitude"));
            latLng.put("longitude",savedInstanceState.getDouble("longitude"));
            meetupLoc.setText(savedInstanceState.getString("location"));
            if (groups != null) {
                adapter = new RecycleViewMeetup(groups,getBaseContext());
                rv.setAdapter(adapter);
                searchTerm.setText(searchTag);
                return;
            }

        }

        startProgressDialog();
        progress.setMessage(getString(R.string.meetupactivity_gettingGPS));
        getGPSLocation();

        progress.setMessage(getString(R.string.meetupactivity_finding_groups));
        searchTag = getIntent().getStringExtra("searchtag");
        searchsite = getIntent().getStringExtra("searchsite");
        searchTerm.setText(searchTag);
        createBottomBar(savedInstanceState);
        Log.d(TAG, "onCreate: tag" + searchTag);
        getMeetupGroups(searchTag);

        setLocationName();

    }

    private void createBottomBar(Bundle savedInstanceState) {

        NewTabListener listener = new NewTabListener();
        listener.setSearchsite(searchsite);
        listener.setSearchTag(searchTag);

        bottomBar = BottomBar.attach(this, savedInstanceState);

        bottomBar.setItemsFromMenu(R.menu.meetup_three_buttons, listener);

//                new OnMenuTabSelectedListener() {
//            @Override
//            public void onMenuItemSelected(@IdRes int menuItemId) {
//                Log.d(MainActivity.TAG, "onMenuItemSelected: " + menuItemId);
//                Intent i;
//                switch (menuItemId) {
//                    case R.id.meetup_bottom_faq:
//                        i = new Intent(MeetupActivity.this, ListQuestionsActivity.class);
//                        i.putExtra("name", searchTag);
//                        i.putExtra("searchsite", searchsite);
//                        startActivity(i);
//                        break;
//                    case R.id.meetup_bottom_github:
//                        i = new Intent(MeetupActivity.this, GitHubActivity.class);
//                        i.putExtra("name", searchTag);
//                        i.putExtra("searchsite", searchsite);
//                        startActivity(i);
//                        break;
//                }
//            }
//        });

        bottomBar.setDefaultTabPosition(2);
    }


    @Override
    protected void onDestroy() {

        if ((subscribe != null) && (!subscribe.isUnsubscribed())) {
            subscribe.unsubscribe();
        }

        super.onDestroy();
    }

    @Override
    protected void onResume() {

        super.onResume();
        bottomBar.setDefaultTabPosition(2);

        searchTag = getIntent().getStringExtra("searchtag");
        searchsite = getIntent().getStringExtra("searchsite");
        searchTerm.setText(searchTag);
        getMeetupGroups(searchTag);

        setLocationName();
    }

    private void setLocationName() {
        subscribe = getGeoCoderObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Address>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<Address> addresses) {
                        for (Address a : addresses) {
                            String loc = a.getLocality() + " - " + a.getCountryName();
                            if (loc.contains("null")) {
                                loc = "Local Area";
                            }

                            meetupLoc.setText(loc);
                        }
                    }
                });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putSerializable("meetup_groups", (Serializable) groups);
        outState.putString("searchtag", searchTag);
        outState.putDouble("latitude",latLng.get("latitude"));
        outState.putDouble("longitude",latLng.get("longitude"));
        outState.putString("location",meetupLoc.getText().toString());

        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.meetup_menu,menu);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.meetup_menu_refresh) {
            startProgressDialog();
            getMeetupGroups(searchTag);
        }

        return true;
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

        int cachesize =  10 * 1024 * 1024;
        final Cache cache = new Cache(new File(getApplicationContext().getCacheDir(), "http"), cachesize);
        OkHttpClient client = new OkHttpClient.Builder()
                .cache(cache)
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.meetup.com")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
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

    protected List<Address> getLocationName() {

        Geocoder coder = new Geocoder(getBaseContext(), Locale.ENGLISH);
        List<Address> addresses = null;
        try {
            addresses = coder.getFromLocation(latLng.get("latitude"),latLng.get("longitude"),1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return addresses;

    }

    protected Observable<List<Address>> getGeoCoderObservable () {
        return Observable.defer(new Func0<Observable<List<Address>>>() {
            @Override
            public Observable<List<Address>> call() {
                return Observable.just(getLocationName());
            }
        });
    }

    protected class NewTabListener implements OnMenuTabSelectedListener {

        private String searchTag;
        private String searchsite;

        public String getSearchsite() {
            return searchsite;
        }

        public void setSearchsite(String searchsite) {
            this.searchsite = searchsite;
        }

        public String getSearchTag() {
            return searchTag;
        }

        public void setSearchTag(String searchTag) {
            this.searchTag = searchTag;
        }

        @Override
        public void onMenuItemSelected(@IdRes int menuItemId) {
            Log.d(MainActivity.TAG, "onMenuItemSelected: " + menuItemId);
            Intent i;
            switch (menuItemId) {
                case R.id.meetup_bottom_faq:
                    i = new Intent(MeetupActivity.this, ListQuestionsActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("name", searchTag);
                    i.putExtra("sitename", searchsite);
                    startActivity(i);
                    break;
                case R.id.meetup_bottom_github:
                    i = new Intent(MeetupActivity.this, GitHubActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("name", searchTag);
                    i.putExtra("searchsite", searchsite);
                    startActivity(i);
                    break;
            }
        }

    }
}
