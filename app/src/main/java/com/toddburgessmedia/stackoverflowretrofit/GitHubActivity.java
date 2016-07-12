package com.toddburgessmedia.stackoverflowretrofit;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.toddburgessmedia.stackoverflowretrofit.retrofit.GitHubProjectAPI;
import com.toddburgessmedia.stackoverflowretrofit.retrofit.GitHubProjectCollection;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GitHubActivity extends AppCompatActivity implements NoLanguageFoundDialog.NothingFoundListener {

    String TAG = MainActivity.TAG;
    String searchTag;
    GitHubProjectCollection projects;
    ProgressDialog progress;

    RecyclerView rv;
    RecyclerViewGitHub adapter;

    boolean searchLanguage = true;

    TextView search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_git_hub);

        search = (TextView) findViewById(R.id.github_search);
        rv = (RecyclerView) findViewById(R.id.github_recycleview);
        if (rv != null) {
            rv.setHasFixedSize(true);
        }
        rv.setLayoutManager(new LinearLayoutManager(this));

        if (savedInstanceState != null) {
            projects = (GitHubProjectCollection) savedInstanceState.getSerializable("savedprojects");
            searchTag = savedInstanceState.getString("searchtag");
            searchLanguage = savedInstanceState.getBoolean("search_language");
            if (projects != null) {
                adapter = new RecyclerViewGitHub(projects.getProjects(), getBaseContext());
                rv.setAdapter(adapter);
                setSearch();
                return;
            }
        }

        searchTag = getIntent().getStringExtra("name");
        setSearch();
        startProgressDialog();
        getProjects(searchTag,searchLanguage);
    }

    private void startProgressDialog() {

        if (progress == null) {
            progress = new ProgressDialog(this);
            progress.setMessage(getString(R.string.github_activity_loading));
        }
        progress.show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putSerializable("savedprojects",projects);
        outState.putString("searchtag",searchTag);
        outState.putBoolean("search_language",searchLanguage);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflate = getMenuInflater();
        inflate.inflate(R.menu.github_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.github_menu_meetup) {
            Intent i = new Intent(this, MeetupActivity.class);
            i.putExtra("searchtag", searchTag);
            startActivity(i);
            return true;
        } else if (item.getItemId() == R.id.github_menu_refresh) {
            startProgressDialog();
            getProjects(searchTag,searchLanguage);
        }

        return true;
    }

    private void setSearch() {

        String searchtype;
        if (searchLanguage) {
            searchtype = getString(R.string.github_language);
        } else {
            searchtype = getString(R.string.github_searchterm);
        }

        String text = searchtype + " " + searchTag;
        search.setText(text);
    }

    private void getProjects(String language,boolean langugesearch) {

        int cachesize =  10 * 1024 * 1024;
        final Cache cache = new Cache(new File(getApplicationContext().getCacheDir(), "http"), cachesize);
        OkHttpClient client = new OkHttpClient.Builder()
                .cache(cache)
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        GitHubProjectAPI projectAPI = retrofit.create(GitHubProjectAPI.class);

        String qlanguage;
        if (langugesearch) {
            qlanguage = "language:" + language;
        } else {
            qlanguage = language;
        }

        Call<GitHubProjectCollection> call = projectAPI.getProjects(qlanguage);

        call.enqueue(new Callback<GitHubProjectCollection>() {
            @Override
            public void onResponse(Call<GitHubProjectCollection> call, Response<GitHubProjectCollection> response) {
                projects = response.body();

                stopProgressDialog();
                if  ((response.code() != 200) || (projects==null)) {
                    NoLanguageFoundDialog nothing = new NoLanguageFoundDialog();
                    nothing.show(getFragmentManager(),"nothing");
                    return;
                }

                adapter = new RecyclerViewGitHub(projects.getProjects(),getBaseContext());
                rv.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<GitHubProjectCollection> call, Throwable t) {
                stopProgressDialog();
                Toast.makeText(GitHubActivity.this, "No Network Connection", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure: it failed horribly. how embarassing");
            }
        });
    }

    private void stopProgressDialog() {
        if (progress != null) {
            progress.dismiss();
        }
    }

    @Override
    public void positiveClick(DialogFragment dialog) {
        Log.d(TAG, "positiveClick: yo yo yo ");

        startProgressDialog();
        searchLanguage = false;
        setSearch();
        getProjects(searchTag,searchLanguage);
    }

    @Override
    public void negativeClick(DialogFragment dialog) {
        finish();
    }

}
