package com.toddburgessmedia.stackoverflowretrofit;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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

public class GitHubActivity extends AppCompatActivity {

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

        searchTag = getIntent().getStringExtra("title");

        progress = new ProgressDialog(this);
        progress.setMessage("Loading GitHub Projects");
        progress.show();
        getProjects(searchTag);

    }


    private void getProjects(String language) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GitHubProjectAPI projectAPI = retrofit.create(GitHubProjectAPI.class);

        String qlanguage = "language:" + language;
        Call<GitHubProjectCollection> call = projectAPI.getProjects(qlanguage);

        call.enqueue(new Callback<GitHubProjectCollection>() {
            @Override
            public void onResponse(Call<GitHubProjectCollection> call, Response<GitHubProjectCollection> response) {
                projects = response.body();
                Log.d(TAG, "onResponse: code" + response.headers());

                Log.d(TAG, "onResponse: it worked");

                progress.dismiss();
                if  (response.code() != 200) {
                    NothingFoundDialog nothing = new NothingFoundDialog();
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
