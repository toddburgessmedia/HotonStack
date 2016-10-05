package com.toddburgessmedia.stackoverflowretrofit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.toddburgessmedia.stackoverflowretrofit.mvp.GitHubPresenter;

import butterknife.BindString;
import butterknife.ButterKnife;

public class GitHubActivity extends AppCompatActivity {

    String TAG = MainActivity.TAG;
    ProgressDialog progress;

    GitHubPresenter presenter;

    @BindString(R.string.github_activity_loading) String loadingMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_git_hub);

        ButterKnife.bind(this);

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

    private void startProgressDialog() {

        if (progress == null) {
            progress = new ProgressDialog(this);
        }
        progress.setMessage(loadingMsg);
        progress.show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        getSupportFragmentManager().putFragment(outState,"presenter", presenter);
        super.onSaveInstanceState(outState);
    }

    private void stopProgressDialog() {
        if (progress != null) {
            progress.dismiss();
        }
    }
}
