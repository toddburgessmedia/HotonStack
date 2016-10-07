package com.toddburgessmedia.stackoverflowretrofit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.toddburgessmedia.stackoverflowretrofit.mvp.GitHubPresenter;

public class GitHubActivity extends AppCompatActivity {

    String TAG = MainActivity.TAG;

    GitHubPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_git_hub);

        if (savedInstanceState != null) {
            presenter = (GitHubPresenter) getSupportFragmentManager().getFragment(savedInstanceState, "presenter");
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.github_presenter, presenter);
            transaction.commit();
            return;
        }

        presenter = new GitHubPresenter();
        Bundle bundle = new Bundle();
        bundle.putString("searchtag",getIntent().getStringExtra("name"));
        bundle.putString("searchsite",getIntent().getStringExtra("searchsite"));
        presenter.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.github_presenter, presenter);
        transaction.commit();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {

        getSupportFragmentManager().putFragment(outState,"presenter", presenter);
        super.onSaveInstanceState(outState);
    }


}
