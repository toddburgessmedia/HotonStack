/*
 * Copyright 2016 Todd Burgess Media
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
