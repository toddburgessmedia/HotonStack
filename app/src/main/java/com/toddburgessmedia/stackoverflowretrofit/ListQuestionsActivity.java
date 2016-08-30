package com.toddburgessmedia.stackoverflowretrofit;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabSelectedListener;
import com.toddburgessmedia.stackoverflowretrofit.retrofit.StackOverFlowFAQ;
import com.toddburgessmedia.stackoverflowretrofit.retrofit.StackOverFlowFaqAPI;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ListQuestionsActivity extends AppCompatActivity implements TimeFrameDialog.TimeFrameDialogListener {

    public static final int ALLTIME = 0;
    public static final int TODAY = 4;
    public static final int YESTERDAY = 3;
    public static final int THISMONTH = 2;
    public static final int THISYEAR = 1;

    int searchtime = ALLTIME;

    String searchTag;
    String searchsite;

    @BindString(R.string.questions_failure_toast) String failure;

    @BindView(R.id.questions_recycleview) RecyclerView rv;
    RecycleViewFAQ adapter;

    @Inject @Named("stackexchange") Retrofit retrofit;

    CoordinatorLayout coordinatorLayout;

    StackOverFlowFAQ faq;

    ProgressDialog progress;

    BottomBar bottomBar;
    final int TABPOS = 0;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflate = getMenuInflater();
        inflate.inflate(R.menu.whatishot_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case (R.id.whatshot_timeframe):
                TimeFrameDialog timeFrameDialog = new TimeFrameDialog();
                timeFrameDialog.show(getFragmentManager(),"timeframe");
                return true;

            case R.id.whats_hot_refresh:
                startProgressDialog();
                getQuestions(searchTag);
                break;
        }

        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list_questions);
        ButterKnife.bind(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        ((TechDive) getApplication()).getOkHttpComponent().inject(this);

        Intent i = getIntent();
        searchTag = i.getStringExtra("name");
        searchsite = i.getStringExtra("sitename");

        if (rv != null) {
            rv.setHasFixedSize(true);
        }
        rv.setLayoutManager(new LinearLayoutManager(this));

        createScrollChangeListener();

        if (savedInstanceState != null) {
            faq = (StackOverFlowFAQ) savedInstanceState.getSerializable("faqlist");
            searchTag = savedInstanceState.getString("searchtag");
            searchsite = savedInstanceState.getString("searchsite");
            createBottomBar(savedInstanceState);
            if (faq != null) {
                adapter = new RecycleViewFAQ(faq.faq, getBaseContext());
                setTimeFrame();
                setSiteName();
                rv.setAdapter(adapter);
                bottomBar.selectTabAtPosition(TABPOS,false);
                return;
            }
        }


        createBottomBar(savedInstanceState);

        startProgressDialog();
        getQuestions(searchTag);
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
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    private void createBottomBar(Bundle savedInstanceState) {

        NewTabListener listener = new NewTabListener();
        listener.setSearchTag(searchTag);
        listener.setSearchsite(searchsite);
        Log.d(MainActivity.TAG, "createBottomBar: "+ searchTag);
        bottomBar = BottomBar.attach(this, savedInstanceState);

        bottomBar.setItemsFromMenu(R.menu.three_buttons, listener);
    }


    @Override
    protected void onResume() {

        super.onResume();

        bottomBar.selectTabAtPosition(TABPOS,false);
        getQuestions(searchTag);
    }

    private OnMenuTabSelectedListener makeListener () {

        OnMenuTabSelectedListener listener = new OnMenuTabSelectedListener() {
            @Override
                public void onMenuItemSelected(@IdRes int menuItemId) {
                    Intent i;
                    switch (menuItemId) {
                        case R.id.faq_bottom_github:
                            i = new Intent(ListQuestionsActivity.this, GitHubActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            i.putExtra("name", searchTag);
                            i.putExtra("searchsite", searchsite);
                            startActivity(i);
                            break;
                        case R.id.faq_bottom_meetup:
                            i = new Intent(ListQuestionsActivity.this, MeetupActivity.class);
                            i.putExtra("searchtag", searchTag);
                            i.putExtra("searchsite", searchsite);
                            startActivity(i);
                            break;
                    }
                }

        };

        return listener;
    }

    void setTimeFrame() {

        String[] times = getResources().getStringArray(R.array.time_dialog);

        Log.d(MainActivity.TAG, "setTimeFrame: " + times[searchtime]);
        //timeframe.setText(times[searchtime]);
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
        //sitename.setText(displaytext);
        adapter.setSitename(displaytext);
    }

    private void startProgressDialog() {

        if (progress == null) {
            progress = new ProgressDialog(this);
            progress.setMessage(getString(R.string.questions_dialog_loading));
        }
        progress.show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putSerializable("faqlist",faq);
        outState.putString("searchtag",searchTag);
        outState.putString("searchsite",searchsite);
        bottomBar.onSaveInstanceState(outState);

        super.onSaveInstanceState(outState);
    }

    private void getQuestions(String tag) {

        final StackOverFlowFaqAPI faqAPI = retrofit.create(StackOverFlowFaqAPI.class);

        Call<StackOverFlowFAQ> call;
        call = getStackOverFlowFAQCall(tag,faqAPI);

        call.enqueue(new Callback<StackOverFlowFAQ>() {

            @Override
            public void onResponse(Call<StackOverFlowFAQ> call, Response<StackOverFlowFAQ> response) {

                faq = response.body();
                if (faq == null) {
                    return;
                }

                if (adapter != null) {
                    adapter.removeAllItems();
                    setTimeFrame();
                    setSiteName();
                    adapter.updateAdapter(faq.faq);
                } else {
                    adapter = new RecycleViewFAQ(faq.faq,getBaseContext());
                    setTimeFrame();
                    setSiteName();
                    rv.setAdapter(adapter);
                }
                stopProgressDialog();
            }

            @Override
            public void onFailure(Call<StackOverFlowFAQ> call, Throwable t) {
                Toast.makeText(ListQuestionsActivity.this,failure, Toast.LENGTH_SHORT).show();
                stopProgressDialog();
            }
        });
    }

    private void stopProgressDialog() {

        if (progress != null) {
            progress.dismiss();
        }
    }

    private Call<StackOverFlowFAQ> getStackOverFlowFAQCall(String tag,StackOverFlowFaqAPI faqAPI) {

        long secondsPassed;
        TimeDelay delay = new TimeDelay();

        switch (searchtime) {
            case TODAY:
                secondsPassed = delay.getTimeDelay(TimeDelay.TODAY);
                break;
            case YESTERDAY:
                secondsPassed = delay.getTimeDelay(TimeDelay.YESTERDAY);
                break;
            case THISMONTH:
                secondsPassed = delay.getTimeDelay(TimeDelay.THISMONTH);
                break;
            case THISYEAR:
                secondsPassed = delay.getTimeDelay(TimeDelay.THISYEAR);
                break;
            default:
                return faqAPI.loadQuestions(tag,searchsite);
        }
        return faqAPI.loadQuestionsToday(secondsPassed, tag, searchsite);
    }

    @Override
    public void positiveClick(DialogFragment fragment, int which) {

        String[] what = getResources().getStringArray(R.array.time_dialog);

        switch (what[which]) {
            case "All Time":
                searchtime = ALLTIME;
                break;
            case "Today":
                searchtime = TODAY;
                break;
            case "Since Yesterday":
                searchtime = YESTERDAY;
                break;
            case "This Month":
                searchtime = THISMONTH;
                break;
            case "This Year":
                searchtime = THISYEAR;
                break;
        }
        startProgressDialog();
        getQuestions(searchTag);
        //setTimeFrame();
    }

    @Override
    public void negativeClick(DialogFragment fragment, int which) {

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
                    i = new Intent(ListQuestionsActivity.this, GitHubActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("name", searchTag);
                    i.putExtra("searchsite", searchsite);
                    startActivity(i);
                    break;
                case R.id.faq_bottom_meetup:
                    i = new Intent(ListQuestionsActivity.this, MeetupActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("searchtag", searchTag);
                    i.putExtra("searchsite", searchsite);
                    startActivity(i);
                    break;
            }
        }



    }
}
