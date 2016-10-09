package com.toddburgessmedia.stackoverflowretrofit.mvp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabSelectedListener;
import com.toddburgessmedia.stackoverflowretrofit.GitHubActivity;
import com.toddburgessmedia.stackoverflowretrofit.ListQuestionsActivity;
import com.toddburgessmedia.stackoverflowretrofit.PreferencesActivity;
import com.toddburgessmedia.stackoverflowretrofit.PrivacyPolicyActivity;
import com.toddburgessmedia.stackoverflowretrofit.R;
import com.toddburgessmedia.stackoverflowretrofit.RecycleViewMeetup;
import com.toddburgessmedia.stackoverflowretrofit.TechDive;
import com.toddburgessmedia.stackoverflowretrofit.retrofit.MeetUpGroup;
import com.toddburgessmedia.stackoverflowretrofit.retrofit.MeetupAPI;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Response;
import retrofit2.Retrofit;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

/**
 * Created by Todd Burgess (todd@toddburgessmedia.com on 08/10/16.
 */

public class MeetupPresenter extends Fragment implements TechDiveMVP {

    String searchTag;
    String searchsite;
    String location;

    List<MeetUpGroup> groups;

    Subscription subscribe;

    @BindView(R.id.meetup_fragment_recycleview)
    RecyclerView rv;
    RecycleViewMeetup adapter;

    @Inject @Named("meeuprx")
    Retrofit retrofit;

    @Inject Context context;

    private HashMap<String, Double> latLng;

    BottomBar bottomBar;
    final int TABPOS = 2;

    ProgressDialog progress;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((TechDive) getActivity().getApplication()).getOkHttpComponent().inject(this);

        searchTag = getArguments().getString("searchtag");
        searchsite = getArguments().getString("searchsite");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_meetup, container, false);
        ButterKnife.bind(this, view);

        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));




        return view;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.meetup_menu_refresh:
                fetchRestSource();
                break;
            case R.id.meetup_menu_preferences:
                Intent i = new Intent(getActivity(),PreferencesActivity.class);
                startActivity(i);
                break;
            case R.id.meetup_menu_privacy:
                Intent pi = new Intent(getActivity(), PrivacyPolicyActivity.class);
                startActivity(pi);
                break;
        }

        return true;
    }


    @Override
    public void fetchRestSource() {
        MeetupAPI meetupAPI = retrofit.create(MeetupAPI.class);

        Observable<Response<List<MeetUpGroup>>> call = meetupAPI.getMeetupGroupsObservable(
                latLng.get("latitude").toString(),
                latLng.get("longitude").toString(),
                searchTag);

        call.subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Response<List<MeetUpGroup>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Response<List<MeetUpGroup>> listResponse) {

                        groups = listResponse.body();
                        if (groups.size() == 0) {
                            Toast.makeText(getActivity(), "No Meetups Found", Toast.LENGTH_SHORT).show();
                            getActivity().finish();
                        }

                    }
                });

    }

    @Override
    public void renderModel() {

        MeetUpGroup holder = new MeetUpGroup();
        holder.setPlaceholder(true);
        groups.add(0,holder);
        adapter = new RecycleViewMeetup(groups,context);
        adapter.setHeader(location,searchTag);
        rv.setAdapter(adapter);
    }

    public void getGPSLocation() throws Exception, SecurityException {

        Double lat,lng;
        if (latLng == null) {
            latLng = new HashMap<>();
        }

        LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            //Toast.makeText(getActivity(), "GPS is Disabled/Unavailable", Toast.LENGTH_SHORT).show();
            //getActivity().finish();
            throw new Exception("GPS is Disabled/Unavailable");
        }
        Criteria criteria = new Criteria();
        String provider = manager.getBestProvider(criteria, false);

        if (provider == null) {
//                Toast.makeText(getActivity(), "GPS Failed to Work :(", Toast.LENGTH_SHORT).show();
//                getActivity().finish();
//                return;
            throw new Exception("GPS Failed to Work :(");
        }

        Location location = manager.getLastKnownLocation(provider);
        if (location == null)  {
//                Toast.makeText(getActivity(), "GPS Failed to Work :(", Toast.LENGTH_SHORT).show();
//                getActivity().finish();
//                return;
            throw new Exception("GPS Failed to Work :(");
        }

        lat = location.getLatitude();
        lng = location.getLongitude();
        latLng.put("latitude",lat);
        latLng.put("longitude",lng);
    }

    private void setLocationName() {
        subscribe = getGeoCoderObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Address>>() {
                    @Override
                    public void onCompleted() {
                        /* getMeetupGroups(searchTag); */
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
                            location = loc;
                        }
                    }
                });
    }

    protected List<Address> getLocationName() {

        Geocoder coder = new Geocoder(context, Locale.ENGLISH);
        List<Address> addresses = null;
        try {
            addresses = coder.getFromLocation(latLng.get("latitude"),latLng.get("longitude"),1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return addresses;

    }

    private void startProgressDialog() {

        if (progress == null) {
            progress = new ProgressDialog(getActivity());
        }
        progress.show();
    }

    private void stopProgressDialog() {

        if (progress != null) {
            progress.dismiss();
        }
    }



    private void createBottomBar(Bundle savedInstanceState) {

        NewTabListener listener = new NewTabListener();
        listener.setSearchsite(searchsite);
        listener.setSearchTag(searchTag);

        bottomBar = BottomBar.attach(getActivity(), savedInstanceState);

        bottomBar.setItemsFromMenu(R.menu.meetup_three_buttons, listener);
        bottomBar.selectTabAtPosition(TABPOS,false);

    }

    protected Observable<List<Address>> getGeoCoderObservable () {
        return Observable.defer(new Func0<Observable<List<Address>>>() {
            @Override
            public Observable<List<Address>> call() {
                return Observable.just(getLocationName());
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
            Intent i;
            switch (menuItemId) {
                case R.id.meetup_bottom_faq:
                    i = new Intent(getActivity(), ListQuestionsActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("name", searchTag);
                    i.putExtra("sitename", searchsite);
                    startActivity(i);
                    break;
                case R.id.meetup_bottom_github:
                    i = new Intent(getActivity(), GitHubActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("name", searchTag);
                    i.putExtra("searchsite", searchsite);
                    startActivity(i);
                    break;
            }
        }

    }


}
