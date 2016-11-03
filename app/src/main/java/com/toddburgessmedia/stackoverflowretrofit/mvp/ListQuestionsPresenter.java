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
import android.support.annotation.IdRes;
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
import android.widget.Toast;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabSelectedListener;
import com.toddburgessmedia.stackoverflowretrofit.GitHubActivity;
import com.toddburgessmedia.stackoverflowretrofit.MainActivity;
import com.toddburgessmedia.stackoverflowretrofit.MeetupActivity;
import com.toddburgessmedia.stackoverflowretrofit.PreferencesActivity;
import com.toddburgessmedia.stackoverflowretrofit.PrivacyPolicyActivity;
import com.toddburgessmedia.stackoverflowretrofit.R;
import com.toddburgessmedia.stackoverflowretrofit.RecycleViewFAQ;
import com.toddburgessmedia.stackoverflowretrofit.TechDive;
import com.toddburgessmedia.stackoverflowretrofit.TimeDelay;
import com.toddburgessmedia.stackoverflowretrofit.TimeFrameDialog;
import com.toddburgessmedia.stackoverflowretrofit.eventbus.TimeFrameDialogMessage;
import com.toddburgessmedia.stackoverflowretrofit.retrofit.StackOverFlowFAQ;
import com.toddburgessmedia.stackoverflowretrofit.retrofit.StackOverFlowFaqAPI;

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

/**
 * Created by Todd Burgess (todd@toddburgessmedia.com on 07/10/16.
 */

public class ListQuestionsPresenter extends Fragment implements TechDiveMVP {

    String searchTag;
    String searchsite;
    int faqpagesize;
    int searchtime = TimeDelay.ALLTIME;
    boolean listFAQ = false;

    @BindView(R.id.question_fragment_recycleview)
    RecyclerView rv;

    @Inject
    @Named("stackexchangerx")
    Retrofit retrofit;

    @Inject
    Context context;

    @Inject
    SharedPreferences prefs;
    private int pagecount = 1;
    private StackOverFlowFAQ newFaq;
    private StackOverFlowFAQ faq;
    private RecycleViewFAQ adapter;
    private BottomBar bottomBar;
    int TABPOS = 0;
    ProgressDialog progress;
    private MenuItem faqMenu;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((TechDive) getActivity().getApplication()).getOkHttpComponent().inject(this);

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        searchTag = getArguments().getString("searchtag");
        searchsite = getArguments().getString("searchsite");
        faqpagesize = getArguments().getInt("faqpagesize");

        setHasOptionsMenu(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_question, container, false);
        ButterKnife.bind(this, view);

        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        createScrollChangeListener();

        if (savedInstanceState != null) {
            restoreInstanceState(savedInstanceState);

            return view;
        }

        createBottomBar(savedInstanceState);
        fetchRestSource();
        return view;

    }

    private void restoreInstanceState(@Nullable Bundle savedInstanceState) {
        faq = (StackOverFlowFAQ) savedInstanceState.getSerializable("faqlist");
        searchTag = savedInstanceState.getString("searchtag");
        searchsite = savedInstanceState.getString("searchsite");
        faqpagesize = savedInstanceState.getInt("faqpagesize");
        pagecount = savedInstanceState.getInt("pagecount");
        listFAQ = savedInstanceState.getBoolean("listFAQ");
        createBottomBar(savedInstanceState);
        if (faq != null) {
            adapter = new RecycleViewFAQ(faq.faq, context);
            adapter.setHasmore(faq.isHasmore());
            setTimeFrame();
            setSiteName();
            rv.setAdapter(adapter);
            bottomBar.selectTabAtPosition(TABPOS,false);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("faqlist",faq);
        outState.putString("searchtag",searchTag);
        outState.putString("searchsite",searchsite);
        outState.putInt("pagecount",pagecount);
        outState.putInt("faqpagesize",faqpagesize);
        outState.putBoolean("listFAQ",listFAQ);
        bottomBar.onSaveInstanceState(outState);

        super.onSaveInstanceState(outState);
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
    public void fetchRestSource() {

        startProgressDialog();
        final StackOverFlowFaqAPI faqAPI = retrofit.create(StackOverFlowFaqAPI.class);

        Observable<Response<StackOverFlowFAQ>> call;
        call = getStackOverFlowFAQCall(searchTag, faqAPI);

        call.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Response<StackOverFlowFAQ>>() {
                    @Override
                    public void onCompleted() {
                        renderModel();
                    }

                    @Override
                    public void onError(Throwable e) {
                        stopProgressDialog();
                        Toast.makeText(context, "Network Connection Error :(", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(Response<StackOverFlowFAQ> response) {
                        stopProgressDialog();
                        newFaq = response.body();
                    }
                });
    }

    @Override
    public void renderModel() {

        if ((adapter != null) && (pagecount > 1)) {
            newFaq.insertLastPlaceHolder();
            adapter.addItems(newFaq.faq);
            adapter.setHasmore(faq.isHasmore());
            faq.mergeTags(newFaq);
        } else if (adapter != null) {
            faq = newFaq;
            faq.insertPlaceHolders();
            adapter.removeAllItems();
            setTimeFrame();
            setSiteName();
            adapter.updateAdapter(faq.faq);
            adapter.setHasmore(faq.isHasmore());
        } else {
            faq = newFaq;
            faq.insertLastPlaceHolder();
            adapter = new RecycleViewFAQ(faq.faq, context);
            setTimeFrame();
            setSiteName();
            adapter.setHasmore(faq.isHasmore());
            rv.setAdapter(adapter);
        }

    }

    private Observable<Response<StackOverFlowFAQ>> getStackOverFlowFAQCall(String tag, StackOverFlowFaqAPI faqAPI) {

        long secondsPassed;
        TimeDelay delay = new TimeDelay();

        if (listFAQ) {
            return faqAPI.loadFAQ(tag, pagecount, faqpagesize, searchsite);
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
                return faqAPI.loadQuestionsObservable(tag, searchsite, pagecount, faqpagesize);
        }
        return faqAPI.loadQuestionsByDate(secondsPassed, tag, searchsite, pagecount, faqpagesize);
    }

    void setTimeFrame() {

        String[] times = getResources().getStringArray(R.array.time_dialog);
        adapter.setTimeframe(times[searchtime]);

    }

    void setSiteName() {

        String[] display = getResources().getStringArray(R.array.select_select_display);
        String[] values = getResources().getStringArray(R.array.site_select_array);
        boolean found = false;
        int i = 0;

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
        String displaytext = display[i] + " / " + searchTag;
        adapter.setSitename(displaytext);
    }

    private void startProgressDialog() {

        if (progress == null) {
            progress = new ProgressDialog(getActivity());
            progress.setMessage(getString(R.string.questions_dialog_loading));
        }
        progress.show();
    }

    private void stopProgressDialog() {

        if (progress != null) {
            progress.dismiss();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.whatishot_menu,menu);

        faqMenu = menu.findItem(R.id.whats_hot_faq);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case (R.id.whatshot_timeframe):
                TimeFrameDialog timeFrameDialog = new TimeFrameDialog();
                timeFrameDialog.show(getActivity().getFragmentManager(),"timeframe");
                return true;

            case R.id.whats_hot_refresh:
                pagecount = 1;
                fetchRestSource();
                break;
            case R.id.whats_hot_preferences:
                Intent i = new Intent(getActivity(),PreferencesActivity.class);
                startActivity(i);
                break;
            case R.id.whats_hot_privacy:
                Intent pi = new Intent(getActivity(), PrivacyPolicyActivity.class);
                startActivity(pi);
                break;
            case R.id.whats_hot_faq:
                if (listFAQ) {
                    listFAQ = false;
                    faqMenu.setTitle(getString(R.string.whats_hot_faq));
                } else {
                    listFAQ = true;
                    faqMenu.setTitle(getString(R.string.whats_hot_search));
                }
                pagecount = 1;
                fetchRestSource();
                break;
        }

        return true;
    }

    private void createBottomBar(Bundle savedInstanceState) {
        NewTabListener listener = new NewTabListener();
        listener.setSearchTag(searchTag);
        listener.setSearchsite(searchsite);
        bottomBar = BottomBar.attach(getActivity(), savedInstanceState);

        bottomBar.setItemsFromMenu(R.menu.three_buttons, listener);
    }

    @Override
    public void onResume() {
        super.onResume();

        bottomBar.selectTabAtPosition(TABPOS,false);
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
        fetchRestSource();
        //setTimeFrame();
    }

    @Subscribe
    public void onClick(RecycleViewFAQ.RecycleViewFAQMessage message) {
        pagecount++;
        startProgressDialog();
        fetchRestSource();
    }




    protected class NewTabListener implements OnMenuTabSelectedListener {

        public String getSearchTag() {
            return searchTag;
        }

        public void setSearchTag(String searchTag) {
            this.searchTag = searchTag;
        }

        String searchTag;

        public String getSearchsite() {
            return searchsite;
        }

        public void setSearchsite(String searchsite) {
            this.searchsite = searchsite;
        }

        String searchsite;

        @Override
        public void onMenuItemSelected(@IdRes int menuItemId) {
            Log.d(MainActivity.TAG, "onMenuItemSelected: " + menuItemId);
            Log.d(MainActivity.TAG, "onMenuItemSelected: " + searchTag);
            Intent i;
            switch (menuItemId) {
                case R.id.faq_bottom_github:
                    i = new Intent(getActivity(), GitHubActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("name", searchTag);
                    i.putExtra("searchsite", searchsite);
                    startActivity(i);
                    break;
                case R.id.faq_bottom_meetup:
                    i = new Intent(getActivity(), MeetupActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("searchtag", searchTag);
                    i.putExtra("searchsite", searchsite);
                    startActivity(i);
                    break;
            }
        }

    }



}
