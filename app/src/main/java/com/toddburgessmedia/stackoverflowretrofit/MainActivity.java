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
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.f2prateek.rx.preferences.Preference;
import com.f2prateek.rx.preferences.RxSharedPreferences;
import com.toddburgessmedia.stackoverflowretrofit.retrofit.StackOverFlowAPI;
import com.toddburgessmedia.stackoverflowretrofit.retrofit.StackOverFlowTags;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.functions.Action1;


public class MainActivity extends AppCompatActivity implements
        SearchDialog.SearchDialogListener, SiteSelectDialog.SiteSelectDialogListener,
        TagsLongPressDialog.TagsLongPressDialogListener, StackTagsRecyclerView.OnLongPressListener {

    public final static String TAG = "StackOverFlow";

    RecyclerView rv;
    StackTagsRecyclerView adapter;
    StackOverFlowTags tags;

    String tagcount;
    ProgressDialog progress;

    String searchsite = "stackoverflow";

    String searchtag = "";

    RxSharedPreferences rxPrefs;
    Preference<String> rxDefaultsite;

    TextView sitename;
    TextView relatedtags;

    boolean tagsearch = false;

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
                adapter = new StackTagsRecyclerView(tags.tags, getBaseContext(),searchsite,this);
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
                getTags(tagcount,tagsearch);
            }
        });

        setSiteName();
        startProgressDialog();
        getTags(tagcount,tagsearch);
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
                getTags(tagcount,tagsearch);
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

    private void getTags(final String tagcount, final boolean synonymsearch) {

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

        StackOverFlowAPI stackOverFlowAPI = retrofit.create(StackOverFlowAPI.class);

        Call<StackOverFlowTags> call;
        if (!synonymsearch) {
            call = stackOverFlowAPI.loadquestions(tagcount, searchsite);
        } else {
            call = stackOverFlowAPI.loadSynonyms(searchtag, searchsite);
        }

        call.enqueue(new Callback<StackOverFlowTags>() {
            @Override
            public void onResponse(Call<StackOverFlowTags> call, Response<StackOverFlowTags> response) {

                tags = response.body();
                if (tags == null) {
                    return;
                }

                if (tags.tags.size() == 0) {
                    Toast.makeText(MainActivity.this, "Tag Not Found", Toast.LENGTH_SHORT).show();
                    tagsearch = false;
                    setSiteName();
                    return;
                }

                if (adapter != null) {
                    adapter.removeAllItems();
                    adapter.updateAdapter(tags.tags);
                } else {
                    adapter = new StackTagsRecyclerView(tags.tags, getBaseContext(), searchsite, MainActivity.this);
                    rv.setAdapter(adapter);
                }
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

    // Search Dialog positive click
    @Override
    public void positiveClick(DialogFragment fragment) {
        String tag;
        SearchDialog search = (SearchDialog) fragment;

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        EditText text = (EditText) search.view.findViewById(R.id.search_tag);
        tag = text.getText().toString();

        tag = tag.replace(' ', '-');
        searchtag = tag;
        String newsite = sitename.getText() + " / " + searchtag;
        sitename.setText(newsite);
        tagsearch = true;

        startProgressDialog();
        getTags(tagcount, tagsearch);

    }


    // Search Dialog negative click
    @Override
    public void negativeClick(DialogFragment fragment) {

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Log.d(TAG, "negativeClick: ");
    }

    // Change Site positive click
    @Override
    public void siteSelectpositiveClick(DialogFragment fragment, int which) {

        String[] sites = getResources().getStringArray(R.array.site_select_array);
        searchsite = sites[which];

        setSiteName();
        startProgressDialog();
        getTags(tagcount,tagsearch);


    }

    // Change site name negative click
    @Override
    public void siteSelectnegativeClick(DialogFragment fragment, int which) {

    }

    // LongPress Dialog positive click handler
    @Override
    public void longPresspositiveClick(DialogFragment fragment, int which) {

        String[] values;

        if (!searchtag.equals("")) {
            values = getResources().getStringArray(R.array.tag_longpress_dialog);

            if (values[which].equals("Load Related Tags")) {
                String newsite = sitename.getText() + " / " + searchtag;
                sitename.setText(newsite);
                tagsearch = true;
            }
            startProgressDialog();
            getTags(tagcount, tagsearch);

        } else {
            setSiteName();
            tagsearch = false;
            startProgressDialog();
            getTags(tagcount,tagsearch);
        }


    }

    // Long Press negative handler
    @Override
    public void longPresstnegativeClick(DialogFragment fragment, int which) {

    }

    
    @Override
    public void onLongClick(View v, String tag) {
        Log.d(TAG, "onLongClick: ");


        TagsLongPressDialog dialog = new TagsLongPressDialog();
        if (searchtag.equals("")) {
            searchtag = tag;
            dialog.setTagsearch(true);
        } else {
            searchtag = "";
            dialog.setTagsearch(false);
        }
        dialog.show(getFragmentManager(),"long press");
    }

}
