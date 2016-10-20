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

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.toddburgessmedia.stackoverflowretrofit.mvp.MeetupPresenter;

import butterknife.ButterKnife;

public class MeetupActivity extends AppCompatActivity {

    String TAG = MainActivity.TAG;

    String searchTag;
    String searchsite;

    MeetupPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meetup);
        ButterKnife.bind(this);

        ((TechDive) getApplication()).getOkHttpComponent().inject(this);

        if (savedInstanceState != null) {
            presenter = (MeetupPresenter) getSupportFragmentManager().getFragment(savedInstanceState, "presenter");
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.meetup_Framelayout, presenter);
            transaction.commit();
            return;
        }

        searchTag = getIntent().getStringExtra("searchtag");
        searchsite = getIntent().getStringExtra("searchsite");

        if (checkLocationPermission()) {
            return;
        }
        createPresenter();

    }

    private void createPresenter() {
        Bundle bundle = new Bundle();
        bundle.putString("searchtag",searchTag);
        bundle.putString("searchsite",searchsite);
        presenter = new MeetupPresenter();
        presenter.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.meetup_Framelayout, presenter);
        transaction.commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        if (presenter != null) {
            super.onSaveInstanceState(outState);
            getSupportFragmentManager().putFragment(outState, "presenter", presenter);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case 1:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                    createPresenter();
                }
                else {
                    finish();
                }
                break;
        }
    }

    private boolean checkLocationPermission() {
        if ( ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions( this, new String[] {  Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION },
                    1);

            return true;
        }
        return false;
    }

}
