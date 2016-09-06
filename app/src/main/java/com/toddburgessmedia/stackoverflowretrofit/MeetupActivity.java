package com.toddburgessmedia.stackoverflowretrofit;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabSelectedListener;
import com.toddburgessmedia.stackoverflowretrofit.retrofit.MeetUpGroup;
import com.toddburgessmedia.stackoverflowretrofit.retrofit.MeetupAPI;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

public class MeetupActivity extends AppCompatActivity {

    String TAG = MainActivity.TAG;

    HashMap<String,Double> latLng;

    String searchTag;
    String searchsite;

    BottomBar bottomBar;
    final int TABPOS = 2;

    boolean hasPermission = false;

    List<MeetUpGroup> groups;

    @BindView(R.id.rv_meetup) RecyclerView rv;
    RecycleViewMeetup adapter;
    private ProgressDialog progress;

    @Inject @Named("meetup") Retrofit retrofit;

    Subscription subscribe;
    Subscription locationSub;

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

        ((TechDive) getApplication()).getOkHttpComponent().inject(this);

        if (rv != null) {
            rv.setHasFixedSize(true);
        }
        rv.setLayoutManager(new LinearLayoutManager(this));

        createScrollChangeListener();
        if (savedInstanceState != null) {
            return;
        }

//        if ((savedInstanceState != null) && (savedInstanceState.getBoolean("hasPermission")))  {
//            groups = (List<MeetUpGroup>) savedInstanceState.getSerializable("meetup_groups");
//            searchTag = savedInstanceState.getString("searchtag");
//            searchsite = savedInstanceState.getString("searchsite");
//            latLng = new HashMap<>();
//            latLng.put("latitude",savedInstanceState.getDouble("latitude"));
//            latLng.put("longitude",savedInstanceState.getDouble("longitude"));
//            createBottomBar(savedInstanceState);
//            meetupLoc.setText(savedInstanceState.getString("location"));
//            hasPermission = true;
//            if (groups != null) {
//                adapter = new RecycleViewMeetup(groups,getBaseContext());
//                adapter.setHeader(meetupLoc.getText().toString(),searchTag);
//                rv.setAdapter(adapter);
//                searchTerm.setText(searchTag);
//                bottomBar.selectTabAtPosition(TABPOS,false);
//                return;
//            }
//        }

        getGPSLocation();
        startProgressDialog();
        progress.setMessage(getString(R.string.meetupactivity_finding_groups));
        searchTag = getIntent().getStringExtra("searchtag");
        searchsite = getIntent().getStringExtra("searchsite");
        searchTerm.setText(searchTag);
        createBottomBar(savedInstanceState);

        watchLocationChange();
        setLocationName();

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState.getBoolean("hasPermission"))  {
            groups = (List<MeetUpGroup>) savedInstanceState.getSerializable("meetup_groups");
            searchTag = savedInstanceState.getString("searchtag");
            searchsite = savedInstanceState.getString("searchsite");
            latLng = new HashMap<>();
            latLng.put("latitude",savedInstanceState.getDouble("latitude"));
            latLng.put("longitude",savedInstanceState.getDouble("longitude"));
            createBottomBar(savedInstanceState);
            meetupLoc.setText(savedInstanceState.getString("location"));
            hasPermission = true;
            if (groups != null) {
                adapter = new RecycleViewMeetup(groups,getBaseContext());
                adapter.setHeader(meetupLoc.getText().toString(),searchTag);
                rv.setAdapter(adapter);
                searchTerm.setText(searchTag);
                bottomBar.selectTabAtPosition(TABPOS,false);
            }
        }
    }

    private void watchLocationChange () {

        if (locationSub != null) {
            return;
        }

        locationSub = RxTextView.textChanges(meetupLoc)
                .subscribe(new Action1<CharSequence>() {
                    @Override
                    public void call(CharSequence charSequence) {
                        Log.d(TAG, "call: location updated!");
                        if (charSequence.toString().length() > 0) {
                            getMeetupGroups(searchTag);
                        }
                    }
                });
    }

    private void createBottomBar(Bundle savedInstanceState) {

        NewTabListener listener = new NewTabListener();
        listener.setSearchsite(searchsite);
        listener.setSearchTag(searchTag);

        bottomBar = BottomBar.attach(this, savedInstanceState);

        bottomBar.setItemsFromMenu(R.menu.meetup_three_buttons, listener);
        bottomBar.selectTabAtPosition(TABPOS,false);

    }


    @Override
    protected void onDestroy() {

        if ((subscribe != null) && (!subscribe.isUnsubscribed())) {
            subscribe.unsubscribe();
        }

        if ((locationSub != null) && (!locationSub.isUnsubscribed())) {
            locationSub.unsubscribe();
        }

        super.onDestroy();
    }

    @Override
    protected void onResume() {

        super.onResume();

        bottomBar.selectTabAtPosition(TABPOS,false);
        searchTag = getIntent().getStringExtra("searchtag");
        searchsite = getIntent().getStringExtra("searchsite");
        searchTerm.setText(searchTag);
        //getMeetupGroups(searchTag);

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

    private void createScrollChangeListener() {
        rv.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {
                    bottomBar.hide();
                } else {
                    bottomBar.show();
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        if (hasPermission) {
            outState.putSerializable("meetup_groups", (Serializable) groups);
            outState.putString("searchtag", searchTag);
            if (latLng != null) {
                outState.putDouble("latitude", latLng.get("latitude"));
                outState.putDouble("longitude", latLng.get("longitude"));
                outState.putString("location", meetupLoc.getText().toString());
            }
            bottomBar.onSaveInstanceState(outState);
        }
        outState.putBoolean("hasPermission",hasPermission);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case 1:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    getGPSLocation();

//                    progress.setMessage(getString(R.string.meetupactivity_finding_groups));
                    searchTerm.setText(searchTag);
                    Log.d(TAG, "onCreate: tag" + searchTag);

                    watchLocationChange();
                    setLocationName();
                }
                else {
                    finish();
                }
                break;
        }
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

        if (checkLocationPermission()) {
            return;
        }

        try {
            LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                stopProgressDialog();
                Toast.makeText(MeetupActivity.this, "GPS is Disabled/Unavailable", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            Criteria criteria = new Criteria();
            String provider = manager.getBestProvider(criteria, false);

            if (provider == null) {
                stopProgressDialog();
                Toast.makeText(MeetupActivity.this, "GPS Failed to Work :(", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            Location location = manager.getLastKnownLocation(provider);
            if (location == null)  {
                stopProgressDialog();
                Toast.makeText(MeetupActivity.this, "GPS Failed to Work :(", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            lat = location.getLatitude();
            lng = location.getLongitude();
            Log.d(TAG, "getGPSLocation: " + lat + " " + lng);
            latLng.put("latitude",lat);
            latLng.put("longitude",lng);

        } catch (SecurityException se) {
            Log.d(MainActivity.TAG, "getGPSLocation: no permission");
            Toast.makeText(MeetupActivity.this, "No Permission", Toast.LENGTH_SHORT).show();
        }

    }

    private boolean checkLocationPermission() {
        if ( ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions( this, new String[] {  Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION },
                    1);

            return true;
        }
        hasPermission = true;
        return false;
    }

    private void getMeetupGroups(String tagname) {

        MeetupAPI meetupAPI = retrofit.create(MeetupAPI.class);

        Call<List<MeetUpGroup>> call = meetupAPI.getMeetupGroups(
                latLng.get("latitude").toString(),
                latLng.get("longitude").toString(),
                tagname);
        call.enqueue(new Callback<List<MeetUpGroup>>() {
            @Override
            public void onResponse(Call<List<MeetUpGroup>> call, Response<List<MeetUpGroup>> response) {
                groups = response.body();
                stopProgressDialog();
                if (groups.size() == 0) {
                    Toast.makeText(MeetupActivity.this, "No Meetups Found", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                MeetUpGroup holder = new MeetUpGroup();
                holder.setPlaceholder(true);
                groups.add(0,holder);
                adapter = new RecycleViewMeetup(groups,getBaseContext());
                adapter.setHeader(meetupLoc.getText().toString(),searchTag);
                rv.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<MeetUpGroup>> call, Throwable t) {
                stopProgressDialog();
                Toast.makeText(MeetupActivity.this, "No Network Connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startProgressDialog() {

        if (progress == null) {
            progress = new ProgressDialog(this);
        }
        progress.show();
    }

    private void stopProgressDialog() {

        if (progress != null) {
            progress.dismiss();
        }
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

        public void setSearchsite(String searchsite) {
            this.searchsite = searchsite;
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
