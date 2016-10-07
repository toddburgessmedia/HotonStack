package com.toddburgessmedia.stackoverflowretrofit;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.toddburgessmedia.stackoverflowretrofit.mvp.MainActivityPresenter;

import javax.inject.Inject;

import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {

    public final static String TAG = "StackOverFlow";

    int tagcount;
    String searchsite;

    @Inject
    SharedPreferences prefs;

    MainActivityPresenter presenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        ((TechDive) getApplication()).getOkHttpComponent().inject(this);

        if (savedInstanceState != null) {
            presenter = (MainActivityPresenter) getSupportFragmentManager().getFragment(savedInstanceState, "presenter");
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.mainactivity_framelayout, presenter);
            transaction.commit();
            return;
        }

        tagcount = Integer.valueOf(prefs.getString("tagcount", "100"));
        searchsite = prefs.getString("defaultsite", "StackOverflow");

        Bundle bundle = new Bundle();
        bundle.putInt("tagcount", tagcount);
        bundle.putString("searchsite", searchsite);

        presenter = new MainActivityPresenter();
        presenter.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.mainactivity_framelayout, presenter);
        transaction.commit();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        getSupportFragmentManager().putFragment(outState,"presenter",presenter);
        super.onSaveInstanceState(outState);
    }


}