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
import android.view.View;
import android.widget.Toast;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabSelectedListener;
import com.toddburgessmedia.stackoverflowretrofit.retrofit.GitHubProjectAPI;
import com.toddburgessmedia.stackoverflowretrofit.retrofit.GitHubProjectCollection;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class GitHubActivity extends AppCompatActivity implements NoLanguageFoundDialog.NothingFoundListener,
                RecyclerViewGitHub.GitHubOnClickListener {

    String TAG = MainActivity.TAG;
    String searchTag;
    String searchsite;
    GitHubProjectCollection projects;
    ProgressDialog progress;
    BottomBar bottomBar;
    NoLanguageFoundDialog nothing;
    GitHubLink gitHubLink;

    final int TABPOS = 1;

    @BindView(R.id.github_recycleview) RecyclerView rv;
    RecyclerViewGitHub adapter;

    @Inject @Named("github") Retrofit retrofit;
    @Inject OkHttpClient httpClient;

    boolean searchLanguage = true;

    boolean loadmore = false;
    int page = 1;


    @BindString(R.string.github_activity_loading) String loadingMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: starting up");
        setContentView(R.layout.activity_git_hub);
        ButterKnife.bind(this);

        ((TechDive) getApplication()).getOkHttpComponent().inject(this);

        if (rv != null) {
            rv.setHasFixedSize(true);
        }
        rv.setLayoutManager(new LinearLayoutManager(this));
        createScrollChangeListener();

        if (savedInstanceState != null) {
            projects = (GitHubProjectCollection) savedInstanceState.getSerializable("savedprojects");
            searchTag = savedInstanceState.getString("searchtag");
            searchsite = savedInstanceState.getString("searchsite");
            searchLanguage = savedInstanceState.getBoolean("search_language");
            createBottomBar(savedInstanceState);
            if (projects != null) {
                adapter = new RecyclerViewGitHub(projects.getProjects(), getBaseContext(),GitHubActivity.this);
                rv.setAdapter(adapter);
                bottomBar.selectTabAtPosition(TABPOS,false);
                return;
            }
        }

        searchTag = getIntent().getStringExtra("name");
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
        bottomBar.selectTabAtPosition(TABPOS,false);
    }

    private void startProgressDialog() {

        if (progress == null) {
            progress = new ProgressDialog(this);
        }
        progress.setMessage(loadingMsg);
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
            page = 1;
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
    }

    private void getProjects(final String language, final boolean langugesearch) {

        GitHubProjectAPI projectAPI;

        projectAPI = retrofit.create(GitHubProjectAPI.class);

        final String qlanguage;
        if (langugesearch) {
            qlanguage = "language:" + language;
        } else {
            qlanguage = language;
        }

        Call<GitHubProjectCollection> call;
        if (page == 1) {
            call = projectAPI.getProjects(qlanguage);
        } else {
            call = projectAPI.getProjectsNextPage(gitHubLink.getParamterHashMap());
        }

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

                GitHubProjectCollection newprojects = response.body();

                if (newprojects == null) {
                    Toast.makeText(GitHubActivity.this, "Unable to retrieve projects", Toast.LENGTH_SHORT).show();
                    return;
                }

                Headers headers = response.headers();
                Log.d(TAG, "onResponse: " + headers.get("Link"));

                gitHubLink = new GitHubLink(headers.get("Link"));

                if ((adapter != null) && (page > 1)) {
                    newprojects.insertLastPlaceHolder();
                    adapter.addItems(newprojects.getProjects());
                    adapter.setHasMore(gitHubLink.hasMore());
                    projects.mergeProjects(newprojects);
                } else if (adapter != null) {
                    projects = newprojects;
                    projects.insertPlaceHolders();
                    adapter.removeAllItems();
                    adapter.updateAdapter(projects.getProjects());
                    adapter.setHasMore(gitHubLink.hasMore());
                } else {
                    projects = newprojects;
                    projects.insertPlaceHolders();
                    adapter = new RecyclerViewGitHub(projects.getProjects(), getBaseContext(),GitHubActivity.this);
                    adapter.setHasMore(gitHubLink.hasMore());
                    if (searchLanguage) {
                        adapter.setSearchType(RecyclerViewGitHub.LANGUAGESEARCH);
                    } else {
                        adapter.setSearchType(RecyclerViewGitHub.KEYWORDSEARCH);
                    }
                    adapter.setSearchword(language);
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

        public void setSearchTab(String searchTab) {
            this.searchTab = searchTab;
        }

        private String searchTab;

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

    @Override
    public void onClick(View view) {

        page++;
        startProgressDialog();
        getProjects(searchTag,searchLanguage);
    }

}
