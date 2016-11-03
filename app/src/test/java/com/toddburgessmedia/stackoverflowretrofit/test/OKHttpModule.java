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

package com.toddburgessmedia.stackoverflowretrofit.test;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.mockito.Mockito;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * Created by Todd Burgess (todd@toddburgessmedia.com on 30/08/16.
 */

@Module
public class OKHttpModule {

    Application app;

    public OKHttpModule(Application application) {
        app = application;
    }

    @Provides
    @Singleton
    public SharedPreferences getPreferences () {
        return PreferenceManager.getDefaultSharedPreferences(app);
    }

    @Provides
    public Context getContext () {
        return app.getApplicationContext();
    }

    @Provides
    @Singleton
    public OkHttpClient getHttpCliient () {

        return Mockito.mock(OkHttpClient.class);
    }


    @Provides
    @Named("stackexchangerx")
    @Singleton
    public Retrofit getRetrofitStackExchangeRx (OkHttpClient client) {

        return Mockito.mock(Retrofit.class);
    }

    @Provides
    @Named("githubrx")
    @Singleton
    public Retrofit getRetrofitGitHubRx (OkHttpClient client) {

        return Mockito.mock(Retrofit.class);
    }

    @Provides
    @Named("meetuprx")
    @Singleton
    public Retrofit getRetrofitMeetupRx (OkHttpClient client) {

        return Mockito.mock(Retrofit.class);
    }
}
