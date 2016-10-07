package com.toddburgessmedia.stackoverflowretrofit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.toddburgessmedia.stackoverflowretrofit.mvp.ListQuestionsPresenter;

import javax.inject.Inject;

import butterknife.ButterKnife;

public class ListQuestionsActivity extends AppCompatActivity {

    String searchTag;
    String searchsite;
    int faqpagesize;

    @Inject SharedPreferences prefs;

    ListQuestionsPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list_questions);
        ButterKnife.bind(this);

        ((TechDive) getApplication()).getOkHttpComponent().inject(this);

        if (savedInstanceState != null) {
            presenter = (ListQuestionsPresenter) getSupportFragmentManager().getFragment(savedInstanceState, "presenter");
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.questions_framelayout, presenter);
            transaction.commit();
            return;
        }

        Intent i = getIntent();
        searchTag = i.getStringExtra("name");
        searchsite = i.getStringExtra("sitename");
        faqpagesize = Integer.parseInt(prefs.getString("faqpagesize", "30"));

        Bundle bundle = new Bundle();
        bundle.putString("searchtag",searchTag);
        bundle.putString("searchsite",searchsite);
        bundle.putInt("faqpagesize",faqpagesize);

        presenter = new ListQuestionsPresenter();
        presenter.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.questions_framelayout, presenter);
        transaction.commit();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);


    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        getSupportFragmentManager().putFragment(outState,"presenter",presenter);
        super.onSaveInstanceState(outState);
    }

}
