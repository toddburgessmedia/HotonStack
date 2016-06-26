package com.toddburgessmedia.stackoverflowretrofit;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.toddburgessmedia.stackoverflowretrofit.retrofit.GitHubProjectAPI;
import com.toddburgessmedia.stackoverflowretrofit.retrofit.GitHubProjectCollection;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_git_hub);

        rv = (RecyclerView) findViewById(R.id.github_recycleview);
        if (rv != null) {
            rv.setHasFixedSize(true);
        }
        rv.setLayoutManager(new LinearLayoutManager(this));

        if (savedInstanceState != null) {
            projects = (GitHubProjectCollection) savedInstanceState.getSerializable("savedprojects");
            if (projects != null) {
                adapter = new RecyclerViewGitHub(projects.getProjects(), getBaseContext());
                rv.setAdapter(adapter);
                return;
            }
        }

        searchTag = getIntent().getStringExtra("title");
        progress = new ProgressDialog(this);
        progress.setMessage("Loading GitHub Projects");
        progress.show();
        getProjects(searchTag,true);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putSerializable("savedprojects",projects);
        super.onSaveInstanceState(outState);
    }

    private void getProjects(String language,boolean langugesearch) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create())
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

                progress.dismiss();
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
                progress.dismiss();
                Toast.makeText(GitHubActivity.this, "No Network Connection", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure: it failed horribly. how embarassing");
            }
        });
    }

    @Override
    public void positiveClick(DialogFragment dialog) {
        Log.d(TAG, "positiveClick: yo yo yo ");

        progress.show();
        getProjects(searchTag,false);
    }

    @Override
    public void negativeClick(DialogFragment dialog) {
        finish();
    }

}
