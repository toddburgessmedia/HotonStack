/*
 * Copyright 2016 Todd Burgess Media
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.toddburgessmedia.stackoverflowretrofit.mvp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.f2prateek.rx.preferences.Preference;
import com.f2prateek.rx.preferences.RxSharedPreferences;
import com.toddburgessmedia.stackoverflowretrofit.MainActivity;
import com.toddburgessmedia.stackoverflowretrofit.PreferencesActivity;
import com.toddburgessmedia.stackoverflowretrofit.PrivacyPolicyActivity;
import com.toddburgessmedia.stackoverflowretrofit.R;
import com.toddburgessmedia.stackoverflowretrofit.RecyclerViewTagsAdapter;
import com.toddburgessmedia.stackoverflowretrofit.SearchDialog;
import com.toddburgessmedia.stackoverflowretrofit.SiteSelectDialog;
import com.toddburgessmedia.stackoverflowretrofit.StackExchangeRankingDialog;
import com.toddburgessmedia.stackoverflowretrofit.TagsLongPressDialog;
import com.toddburgessmedia.stackoverflowretrofit.TechDive;
import com.toddburgessmedia.stackoverflowretrofit.TimeDelay;
import com.toddburgessmedia.stackoverflowretrofit.TimeFrameDialog;
import com.toddburgessmedia.stackoverflowretrofit.eventbus.MainActivityLongPressMessage;
import com.toddburgessmedia.stackoverflowretrofit.eventbus.SearchDialogMessage;
import com.toddburgessmedia.stackoverflowretrofit.eventbus.SelectSiteMessage;
import com.toddburgessmedia.stackoverflowretrofit.eventbus.StackExchangeRankingMessage;
import com.toddburgessmedia.stackoverflowretrofit.eventbus.TimeFrameDialogMessage;
import com.toddburgessmedia.stackoverflowretrofit.retrofit.StackOverFlowAPI;
import com.toddburgessmedia.stackoverflowretrofit.retrofit.StackOverFlowTags;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Response;
import retrofit2.Retrofit;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by Todd Burgess (todd@toddburgessmedia.com on 07/10/16.
 */

public class MainActivityPresenter extends Fragment implements TechDiveMVP {

    String TAG = MainActivity.TAG;

    int tagcount;
    String searchsite;
    String sitename;

    RecyclerViewTagsAdapter adapter;
    StackOverFlowTags tags;
    StackOverFlowTags callTags;

    String searchtype = "Popularity";

    @Inject @Named("stackexchangerx")
    Retrofit retrofit;

    @Inject
    Context context;

    @Inject
    SharedPreferences prefs;


    @BindView(R.id.mainactivity_fragment_recycleview)
    RecyclerView rv;
    private boolean synonymsearch;
    private int searchtime = TimeDelay.ALLTIME;
    private int pagecount = 1;
    private String searchtag = "";
    private Menu menu;
    private boolean tagsearch = false;

    ProgressDialog progress;
    private RxSharedPreferences rxPrefs;
    private Preference<String> rxDefaultsite;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((TechDive) getActivity().getApplication()).getOkHttpComponent().inject(this);

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        tagcount = getArguments().getInt("tagcount");
        searchsite = getArguments().getString("searchsite");

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_mainactivity,container,false);
        ButterKnife.bind(this,view);

        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (savedInstanceState != null) {
            restoreInstanceState(savedInstanceState);
            startPrefsObservables();
            return view;
        }

        fetchRestSource();
        startPrefsObservables();

        return view;
    }

    private void restoreInstanceState(@Nullable Bundle savedInstanceState) {
        tags = (StackOverFlowTags) savedInstanceState.getSerializable("taglist");
        searchsite = savedInstanceState.getString("searchsite");
        tagcount = savedInstanceState.getInt("tagcount");
        pagecount = savedInstanceState.getInt("pagecount");
        tagsearch = savedInstanceState.getBoolean("tagsearch");
        setSiteName();
        if (tags != null) {
            adapter = new RecyclerViewTagsAdapter(tags.tags, context);
            adapter.setDisplaySiteName(sitename);
            if (tagsearch) {
                adapter.setHasmore(false);
            } else {
                adapter.setHasmore(tags.isHasMore());
            }
            rv.setAdapter(adapter);
        }
    }

    private void startPrefsObservables() {
        rxPrefs = RxSharedPreferences.create(prefs);
        rxDefaultsite = rxPrefs.getString("defaultsite");
        rxDefaultsite.asObservable()
                .skip(1)
                .filter(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String s) {
                        if (s == null) {
                            return false;
                        } else {
                            return true;
                        }
                    }
                })
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                            updateSearchSite(s);

                    }
                });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putSerializable("taglist", tags);
        outState.putString("searchsite", searchsite);
        outState.putInt("tagcount", tagcount);
        outState.putInt("pagecount", pagecount);
        outState.putBoolean("tagsearch", tagsearch);

        super.onSaveInstanceState(outState);
    }



    private void startProgressDialog() {

        if (progress == null) {
            progress = new ProgressDialog(getActivity());
            progress.setMessage("Loading Tags");
        }
        progress.show();
    }

    private void stopProgressDialog() {

        if (progress != null) {
            progress.dismiss();
        }
    }

    public void updateSearchSite (String searchsite) {

        this.searchsite = searchsite;
        searchtime = TimeDelay.ALLTIME;
        synonymsearch = false;
        setSiteName();
        if (adapter != null) {
                adapter.setDisplaySiteName(sitename);
        }
        fetchRestSource();
    }

    void setSiteName() {

        String[] display = getResources().getStringArray(R.array.select_select_display);
        String[] values = getResources().getStringArray(R.array.site_select_array);
        boolean found = false;
        int i = 0;
        Log.d(TAG, "setSiteName: " + searchsite);
        while (!found) {
            if (values[i].equals(searchsite)) {
                found = true;
            } else {
                i++;
                if (i == values.length) { // this is to catch errors
                    i--;
                    found = true;
                }
            }
        }

        sitename = display[i];
        if (adapter != null) {
            adapter.setSitename(searchsite);
        }

    }

    @Override
    public void fetchRestSource() {

        startProgressDialog();
        setSiteName();

        StackOverFlowAPI stackOverFlowAPI = retrofit.create(StackOverFlowAPI.class);

        Observable<Response<StackOverFlowTags>> call;
        if (!synonymsearch) {
            call = getStackOverFlowFAQCall(stackOverFlowAPI);
        } else {
            call = stackOverFlowAPI.loadSynonymsObservable(searchtag, searchsite);
        }

        call.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Response<StackOverFlowTags>>() {
                    @Override
                    public void onCompleted() {
                        renderModel();

                    }

                    @Override
                    public void onError(Throwable e) {
                        stopProgressDialog();
                        Toast.makeText(getActivity(), "No Internet connection :(", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(Response<StackOverFlowTags> response) {

                        stopProgressDialog();
                        if ((response.code() != 200) || (response.body().tags.size() == 0)) {
                            Toast.makeText(getActivity(), "Unable to load tags", Toast.LENGTH_SHORT).show();
                        }

                        callTags = response.body();
                    }
                });

    }

    private Observable<Response<StackOverFlowTags>> getStackOverFlowFAQCall(StackOverFlowAPI faqAPI) {

        long secondsPassed;
        TimeDelay delay = new TimeDelay();

        String sort;
        if (searchtype.equals("Popularity")) {
            sort = "popular";
        } else {
            sort = "activity";
        }

        switch (searchtime) {
            case TimeDelay.TODAY:
                secondsPassed = delay.getTimeDelay(TimeDelay.TODAY);
                break;
            case TimeDelay.YESTERDAY:
                secondsPassed = delay.getTimeDelay(TimeDelay.YESTERDAY);
                break;
            case TimeDelay.THISMONTH:
                secondsPassed = delay.getTimeDelay(TimeDelay.THISMONTH);
                break;
            case TimeDelay.THISYEAR:
                secondsPassed = delay.getTimeDelay(TimeDelay.THISYEAR);
                break;
            default:
                if (searchtype.equals("Popularity")) {
                    return faqAPI.loadquestionsObservable(tagcount, searchsite, pagecount);
                } else {
                    return faqAPI.loadquestionsActivityObservable(tagcount, searchsite, pagecount);
                }
        }
        return faqAPI.loadsquestionsByDateObservable(tagcount, sort, searchsite, pagecount, secondsPassed);
    }

    @Override
    public void renderModel() {

        if ((adapter != null) && (pagecount > 1) && (!synonymsearch)) {
            callTags.insertLastPlaceHolder();
            adapter.addItems(callTags.tags);
            adapter.setHasmore(tags.isHasMore());
            tags.mergeTags(callTags);
        } else if (adapter != null) {
            tags = callTags;
            adapter.removeAllItems();
            tags.rankTags();
            setTimeFrame();
            adapter.updateAdapter(callTags.tags);
            if ((tags.tags.size() < tagcount) || (synonymsearch)) {
                tags.insertFirstPlaceHolder();
                adapter.setHasmore(false);
            } else {
                tags.insertPlaceHolders();
                adapter.setHasmore(tags.isHasMore());
            }
        } else {
            tags = callTags;
            tags.rankTags();
            adapter = new RecyclerViewTagsAdapter(tags.tags, context);
            adapter.setSitename(searchsite);
            if (tags.tags.size() < tagcount) {
                adapter.setHasmore(false);
                tags.insertFirstPlaceHolder();
            } else {
                tags.insertPlaceHolders();
                adapter.setHasmore(tags.isHasMore());
            }
            adapter.setDisplaySiteName(sitename);
            setTimeFrame();
            rv.setAdapter(adapter);
        }

    }

    void setTimeFrame() {

        String[] times = getResources().getStringArray(R.array.time_dialog);
        adapter.setTimeframe(times[searchtime]);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.actionbar,menu);
        this.menu = menu;

        MenuItem item = menu.findItem(R.id.menu_ranking);
        String rankby = getString(R.string.tags_dialog_ranking) + searchtype;
        item.setTitle(rankby);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);

                SearchDialog dialog = new SearchDialog();
                dialog.show(getActivity().getFragmentManager(),"search");
                break;
            case R.id.menu_refresh:
                pagecount = 1;
                fetchRestSource();
                break;
            case R.id.menu_preferences:
                Intent i = new Intent(getActivity(),PreferencesActivity.class);
                startActivity(i);
                break;
            case R.id.menu_siteselect:
                SiteSelectDialog siteSelectDialog = new SiteSelectDialog();
                siteSelectDialog.show(getActivity().getFragmentManager(),"sitesearch");
                break;
            case R.id.menu_privacy:
                Intent pi = new Intent(getActivity(), PrivacyPolicyActivity.class);
                startActivity(pi);
                break;
            case R.id.menu_ranking:
                StackExchangeRankingDialog rankingDialog = new StackExchangeRankingDialog();
                rankingDialog.show(getActivity().getFragmentManager(),"rankingdialog");
                break;
            case R.id.timeframe:
                TimeFrameDialog timeFrameDialog = new TimeFrameDialog();
                timeFrameDialog.show(getActivity().getFragmentManager(), "timeframedialog");
                break;
        }
        return true;
    }

    // Search Dialog positive click
    @Subscribe
    public void positiveClick(SearchDialogMessage message) {

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        if (message.isNegativeClick()) {
            return;
        }

        String tag = message.getSearch();
        tag = tag.replace(' ', '-');
        searchtag = tag;
        String newsite = sitename + " / " + searchtag;
        synonymsearch = true;
        searchtime = TimeDelay.ALLTIME;
        adapter.setDisplaySiteName(sitename + "/" + tag);
        fetchRestSource();

    }

    @Subscribe
    public void stackExchangeRankingpositiveClick(StackExchangeRankingMessage message) {

        String[] ranking = getResources().getStringArray(R.array.tags_ranking);
        int which = message.getPosition();

        if (searchtype.equals(ranking[which])) {
            return;
        }

        pagecount = 1;
        searchtype = ranking[which];

        MenuItem item = menu.findItem(R.id.menu_ranking);
        String sortby = getString(R.string.tags_dialog_ranking) + searchtype;
        item.setTitle(sortby);

        fetchRestSource();

    }



    // Change Site positive click
    @Subscribe
    public void siteSelectpositiveClick(SelectSiteMessage message) {

        int which = message.getPosition();

        String[] sites = getResources().getStringArray(R.array.site_select_array);
        searchsite = sites[which];

        setSiteName();
        if (adapter != null) {
            adapter.setDisplaySiteName(sitename);
        }
        pagecount = 1;
        searchtime = TimeDelay.ALLTIME;
        fetchRestSource();
    }

    // LongPress Dialog positive click handler
    @Subscribe
    public void longPresspositiveClick(MainActivityLongPressMessage message) {

        String[] values;
        int which = message.getPosition();

        if (!searchtag.equals("")) {
            values = getResources().getStringArray(R.array.tag_longpress_dialog);

            if (values[which].equals("Load Related Tags")) {
                String newsite = sitename + " / " + searchtag;
                adapter.setDisplaySiteName(newsite);
                synonymsearch = true;
            }
            fetchRestSource();

        } else {
            setSiteName();
            adapter.setDisplaySiteName(sitename);
            synonymsearch = false;
            pagecount = 1;
            fetchRestSource();
        }
    }

    @Subscribe
    public void onLongClick(RecyclerViewTagsAdapter.OnLongClickMessage message) {

        String tag = message.getTag();

        TagsLongPressDialog dialog = new TagsLongPressDialog();
        if (searchtag.equals("")) {
            searchtag = tag;
            dialog.setTagsearch(true);
        } else {
            searchtag = "";
            dialog.setTagsearch(false);
        }
        dialog.show(getActivity().getFragmentManager(),"long press");
    }

    @Subscribe
    public void loadMoreTags(RecyclerViewTagsAdapter.LoadMoreTagsMessage message) {

        pagecount++;
        fetchRestSource();

    }

    @Subscribe
    public void positiveClick(TimeFrameDialogMessage message) {
        String[] what = getResources().getStringArray(R.array.time_dialog);
        int which = message.getPosition();

        switch (what[which]) {
            case "All Time":
                searchtime = TimeDelay.ALLTIME;
                break;
            case "Today":
                searchtime = TimeDelay.TODAY;
                break;
            case "Since Yesterday":
                searchtime = TimeDelay.YESTERDAY;
                break;
            case "This Month":
                searchtime = TimeDelay.THISMONTH;
                break;
            case "This Year":
                searchtime = TimeDelay.THISYEAR;
                break;

        }
        pagecount = 1;
        fetchRestSource();

    }


}
