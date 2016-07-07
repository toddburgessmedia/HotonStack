package com.toddburgessmedia.stackoverflowretrofit;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.f2prateek.rx.preferences.Preference;
import com.f2prateek.rx.preferences.RxSharedPreferences;
import com.toddburgessmedia.stackoverflowretrofit.retrofit.StackOverFlowAPI;
import com.toddburgessmedia.stackoverflowretrofit.retrofit.StackOverFlowTags;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.functions.Action1;


public class MainActivity extends AppCompatActivity implements
        SearchDialog.SearchDialogListener, SiteSelectDialog.SiteSelectDialogListener {

    public final static String TAG = "StackOverFlow";

    RecyclerView rv;
    RecyclerView.Adapter adapter;
    StackOverFlowTags tags;

    String tagcount;
    ProgressDialog progress;

    String searchsite = "stackoverflow";

    RxSharedPreferences rxPrefs;
    Preference<String> rxDefaultsite;

    TextView sitename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        sitename = (TextView) findViewById(R.id.main_sitename);
        rv = (RecyclerView) findViewById(R.id.recycleview);
        if (rv != null) {
            rv.setHasFixedSize(true);
        }
        rv.setLayoutManager(new LinearLayoutManager(this));

        //rv.addItemDecoration(new SimpleDividerItemDecoration(this));

        if (savedInstanceState != null) {
            tags = (StackOverFlowTags) savedInstanceState.getSerializable("taglist");
            searchsite = savedInstanceState.getString("searchsite");
            setSiteName();
            if (tags != null) {
                adapter = new StackTagsRecyclerView(tags.tags, getBaseContext(),searchsite);
                rv.setAdapter(adapter);
                return;
            }
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        tagcount = prefs.getString("tagcount","100");
        searchsite = prefs.getString("defaultsite","StackOverflow");

        rxPrefs = RxSharedPreferences.create(prefs);
        rxDefaultsite = rxPrefs.getString("defaultsite");
        rxDefaultsite.asObservable().subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                searchsite = s;
                setSiteName();
                startProgressDialog();
                getTags(tagcount);
            }
        });

        setSiteName();
        startProgressDialog();
        getTags(tagcount);
    }

    private void startProgressDialog() {

        if (progress == null) {
            progress = new ProgressDialog(this);
            progress.setMessage("Loading Tags");
        }
        progress.show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putSerializable("taglist",tags);
        outState.putString("searchsite",searchsite);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);

                SearchDialog dialog = new SearchDialog();
                dialog.show(getFragmentManager(),"search");
                break;
            case R.id.menu_refresh:
                startProgressDialog();
                getTags(tagcount);
                break;
            case R.id.menu_preferences:
                Intent i = new Intent(this,PreferencesActivity.class);
                startActivity(i);
                break;
            case R.id.menu_siteselect:
                SiteSelectDialog siteSelectDialog = new SiteSelectDialog();
                siteSelectDialog.show(getFragmentManager(),"sitesearch");
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflate = getMenuInflater();
        inflate.inflate(R.menu.actionbar,menu);
        return true;
    }

    private void getTags(String tagcount) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.stackexchange.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        StackOverFlowAPI stackOverFlowAPI = retrofit.create(StackOverFlowAPI.class);

        Call<StackOverFlowTags> call = stackOverFlowAPI.loadquestions(tagcount,searchsite);

        call.enqueue(new Callback<StackOverFlowTags>() {
            @Override
            public void onResponse(Call<StackOverFlowTags> call, Response<StackOverFlowTags> response) {

                tags = response.body();
                if (tags == null) {
                    return;
                }
                adapter = new StackTagsRecyclerView(tags.tags,getBaseContext(),searchsite);
                rv.setAdapter(adapter);
                progress.dismiss();
            }

            @Override
            public void onFailure(Call<StackOverFlowTags> call, Throwable t) {
                Log.d(TAG, "onFailure: we failed. this is terrible!!!");
                Toast.makeText(MainActivity.this, "No Network Connection!", Toast.LENGTH_SHORT).show();
                progress.dismiss();
            }
        });
    }

    void setSiteName() {

        String[] display = getResources().getStringArray(R.array.select_select_display);
        String[] values = getResources().getStringArray(R.array.site_select_array);
        boolean found = false;
        int i = 0;

        while (!found) {
            Log.d(TAG, "setSiteName: " + values[i]);
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
        sitename.setText(display[i]);
    }

    @Override
    public void positiveClick(DialogFragment fragment) {
        String tag;
        SearchDialog search = (SearchDialog) fragment;

        EditText text = (EditText) search.view.findViewById(R.id.search_tag);
        tag = text.getText().toString();

        tag = tag.replace(' ','-');

        Intent i = new Intent(this,ListQuestionsActivity.class);
        i.putExtra("name",tag);
        i.putExtra("sitename",searchsite);
        startActivity(i);
    }

    @Override
    public void negativeClick(DialogFragment fragment) {

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Log.d(TAG, "negativeClick: ");
    }

    @Override
    public void siteSelectpositiveClick(DialogFragment fragment, int which) {

        String[] sites = getResources().getStringArray(R.array.site_select_array);
        searchsite = sites[which];

        setSiteName();
        startProgressDialog();
        getTags(tagcount);


    }

    @Override
    public void siteSelectnegativeClick(DialogFragment fragment, int which) {

    }
}
