package com.toddburgessmedia.stackoverflowretrofit;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.toddburgessmedia.stackoverflowretrofit.retrofit.StackOverFlowFAQ;
import com.toddburgessmedia.stackoverflowretrofit.retrofit.StackOverFlowFaqAPI;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ListQuestionsActivity extends AppCompatActivity implements TimeFrameDialog.TimeFrameDialogListener {

    public static final int ALLTIME = 0;
    public static final int TODAY = 4;
    public static final int YESTERDAY = 3;
    public static final int THISMONTH = 2;
    public static final int THISYEAR = 1;

    int searchtime = ALLTIME;

    String searchTag;
    String searchsite;

    RecyclerView rv;
    RecycleViewFAQ adapter;

    StackOverFlowFAQ faq;

    ProgressDialog progress;

    TextView sitename;
    TextView timeframe;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflate = getMenuInflater();
        inflate.inflate(R.menu.whatishot_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_gogithub) {
            Intent i = new Intent(this,GitHubActivity.class);
            i.putExtra("name",searchTag);
            startActivity(i);
            return true;
        } else if (item.getItemId() == R.id.menu_meetup) {
            Intent i = new Intent(this, MeetupActivity.class);
            i.putExtra("searchtag", searchTag);
            startActivity(i);
            return true;
        }

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
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Intent i = getIntent();
        searchTag = i.getStringExtra("name");
        searchsite = i.getStringExtra("sitename");
        setTitle(getString(R.string.app_name));

        sitename = (TextView) findViewById(R.id.questions_sitename);
        setSiteName();
        timeframe = (TextView) findViewById(R.id.questions_timeframe);
        setTimeFrame();

        rv = (RecyclerView) findViewById(R.id.questions_recycleview);
        if (rv != null) {
            rv.setHasFixedSize(true);
        }
        rv.setLayoutManager(new LinearLayoutManager(this));

        if (savedInstanceState != null) {
            faq = (StackOverFlowFAQ) savedInstanceState.getSerializable("faqlist");
            if (faq != null) {
                adapter = new RecycleViewFAQ(faq.faq, getBaseContext());
                rv.setAdapter(adapter);
                return;
            }
        }

        startProgressDialog();
        getQuestions(searchTag);
    }

    void setTimeFrame() {

        String[] times = getResources().getStringArray(R.array.time_dialog);

        Log.d(MainActivity.TAG, "setTimeFrame: " + times[searchtime]);
        timeframe.setText(times[searchtime]);

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
        sitename.setText(display[i] + " / " + searchTag);
    }



    private void startProgressDialog() {

        if (progress == null) {
            progress = new ProgressDialog(this);
            progress.setMessage("Loading questions");
        }
        progress.show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putSerializable("faqlist",faq);

        super.onSaveInstanceState(outState);
    }

    private void getQuestions(String tag) {

        int cachesize =  10 * 1024 * 1024;
        final Cache cache = new Cache(new File(getApplicationContext().getCacheDir(), "http"), cachesize);
        OkHttpClient client = new OkHttpClient.Builder()
                .cache(cache)
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.stackexchange.com")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();


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

                if (faq.isEmpty()) {
                    NothingFoundDialog nothing = new NothingFoundDialog();
                    nothing.show(getFragmentManager(),"nothing");
                }

                if (adapter != null) {
                    adapter.removeAllItems();
                    adapter.updateAdapter(faq.faq);
                } else {
                    adapter = new RecycleViewFAQ(faq.faq,getBaseContext());
                    rv.setAdapter(adapter);
                }
                stopProgressDialog();
            }

            @Override
            public void onFailure(Call<StackOverFlowFAQ> call, Throwable t) {
                Toast.makeText(ListQuestionsActivity.this, "No Network Connection!", Toast.LENGTH_SHORT).show();
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

        Call<StackOverFlowFAQ> call;
        long secondsPassed;
        TimeDelay delay = new TimeDelay();

        switch (searchtime) {
            case TODAY:
                secondsPassed = delay.getTimeDelay(TimeDelay.TODAY);
                call = faqAPI.loadQuestionsToday(secondsPassed, tag, searchsite);
                break;
            case YESTERDAY:
                secondsPassed = delay.getTimeDelay(TimeDelay.YESTERDAY);
                call = faqAPI.loadQuestionsYesterday(secondsPassed, tag, searchsite);
                break;
            case THISMONTH:
                secondsPassed = delay.getTimeDelay(TimeDelay.THISMONTH);
                call = faqAPI.loadQuestionsToday(secondsPassed, tag, searchsite);
                break;
            case THISYEAR:
                secondsPassed = delay.getTimeDelay(TimeDelay.THISYEAR);
                call = faqAPI.loadQuestionsToday(secondsPassed,tag,searchsite);
                break;
            default:
                call = faqAPI.loadQuestions(tag,searchsite);
                break;
        }
        return call;
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
        setTimeFrame();
        startProgressDialog();
        getQuestions(searchTag);
    }

    @Override
    public void negativeClick(DialogFragment fragment, int which) {

    }

    public static class NothingFoundDialog extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.nothing_dialog_title);
            builder.setMessage(R.string.nothing_dialog_body);
            builder.setNeutralButton(R.string.nothing_ok_button, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    getActivity().finish();
                }
            });

            return builder.create();
        }
    }

}
