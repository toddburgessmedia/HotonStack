package com.toddburgessmedia.stackoverflowretrofit;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
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
import com.toddburgessmedia.stackoverflowretrofit.retrofit.GitHubProjectAPI;
import com.toddburgessmedia.stackoverflowretrofit.retrofit.GitHubProjectCollection;

import java.io.File;
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

public class GitHubActivity extends AppCompatActivity implements NoLanguageFoundDialog.NothingFoundListener {

    String TAG = MainActivity.TAG;
    String searchTag;
    String searchsite;
    GitHubProjectCollection projects;
    ProgressDialog progress;
    BottomBar bottomBar;
    NoLanguageFoundDialog nothing;

    final int TABPOS = 1;

    @BindView(R.id.github_recycleview) RecyclerView rv;
    RecyclerViewGitHub adapter;

    boolean searchLanguage = true;

    @BindView(R.id.github_search) TextView search;

//    @BindView(R.id.github_co_layout) CoordinatorLayout colayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: starting up");
        setContentView(R.layout.activity_git_hub);
        ButterKnife.bind(this);


        if (rv != null) {
            rv.setHasFixedSize(true);
        }
        rv.setLayoutManager(new LinearLayoutManager(this));
        createScrollChangeListener();

        if (savedInstanceState != null) {
            Log.d(TAG, "onCreate: we are here");
            projects = (GitHubProjectCollection) savedInstanceState.getSerializable("savedprojects");
            searchTag = savedInstanceState.getString("searchtag");
            searchsite = savedInstanceState.getString("searchsite");
            searchLanguage = savedInstanceState.getBoolean("search_language");
            createBottomBar(savedInstanceState);
            if (projects != null) {
                adapter = new RecyclerViewGitHub(projects.getProjects(), getBaseContext());
                rv.setAdapter(adapter);
                setSearch();
                bottomBar.selectTabAtPosition(TABPOS,false);
                return;
            }
        }

        searchTag = getIntent().getStringExtra("name");
        Log.d(TAG, "onCreate: " + searchTag);
        searchsite = getIntent().getStringExtra("searchsite");
        createBottomBar(savedInstanceState);
        bottomBar.selectTabAtPosition(TABPOS,false);
        setSearch();
        startProgressDialog();
        getProjects(searchTag,searchLanguage);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }


    @Override
    protected void onResume() {

        super.onResume();

        Log.d(MainActivity.TAG, "onResume: in the on Rescume!! :)");

        bottomBar.selectTabAtPosition(TABPOS,false);


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
        outState.putString("searchsite",searchsite);
        outState.putBoolean("search_language",searchLanguage);
        bottomBar.onSaveInstanceState(outState);
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

        if (item.getItemId() == R.id.github_menu_refresh) {
            startProgressDialog();
            getProjects(searchTag,searchLanguage);
        }

        return true;
    }

    private void createBottomBar(Bundle savedInstanceState) {

        NewTabListener listener = new NewTabListener();
        listener.setSearchsite(searchsite);
        listener.setSearchTab(searchTag);


        bottomBar = BottomBar.attach(this, savedInstanceState);

        bottomBar.setItemsFromMenu(R.menu.github_three_buttons, listener);
        bottomBar.selectTabAtPosition(TABPOS,false);
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

                stopProgressDialog();
                if  ((response.code() != 200) && (searchLanguage)) {

                    if (nothing == null) {
                        nothing = new NoLanguageFoundDialog();
                        nothing.show(getFragmentManager(), "nothing");
                    }
                    return;
                } else if ((response.code() != 200) && (!searchLanguage)) {
                    Toast.makeText(GitHubActivity.this, "No Github Projects Found", Toast.LENGTH_SHORT).show();
                }

                projects = response.body();
                if (projects != null) {
                    adapter = new RecyclerViewGitHub(projects.getProjects(), getBaseContext());
                    rv.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<GitHubProjectCollection> call, Throwable t) {
                stopProgressDialog();
                Toast.makeText(GitHubActivity.this, "No Network Connection", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure: it failed horribly. oh the humanity");
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

    private void stopProgressDialog() {
        if (progress != null) {
            progress.dismiss();
        }
    }

    @Override
    public void positiveClick(DialogFragment dialog) {
        nothing.dismiss();
        startProgressDialog();
        searchLanguage = false;
        setSearch();
        getProjects(searchTag,searchLanguage);
    }

    @Override
    public void negativeClick(DialogFragment dialog) {
        finish();
    }


    protected class NewTabListener implements OnMenuTabSelectedListener {

        public String getSearchTab() {
            return searchTab;
        }

        public void setSearchTab(String searchTab) {
            this.searchTab = searchTab;
        }

        private String searchTab;

        public String getSearchsite() {
            return searchsite;
        }

        public void setSearchsite(String searchsite) {
            this.searchsite = searchsite;
        }

        private String searchsite;

        @Override
        public void onMenuItemSelected(@IdRes int menuItemId) {
            Log.d(MainActivity.TAG, "onMenuItemSelected: " + menuItemId);
            Intent i;
            switch (menuItemId) {
                case R.id.github_bottom_faq:
                    i = new Intent(GitHubActivity.this, ListQuestionsActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("sitename", searchsite);
                    i.putExtra("name", searchTag);
                    //i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    break;
                case R.id.gitbhub_bottom_meetup:
                    i = new Intent(GitHubActivity.this, MeetupActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("searchtag", searchTag);
                    i.putExtra("searchsite", searchsite);
                    startActivity(i);
                    break;
            }
        }
    }
}
