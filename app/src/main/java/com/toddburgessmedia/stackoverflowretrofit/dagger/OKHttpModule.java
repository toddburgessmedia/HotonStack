package com.toddburgessmedia.stackoverflowretrofit.dagger;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.schedulers.Schedulers;

/**
 * Created by Todd Burgess (todd@toddburgessmedia.com on 30/08/16.
 */

@Module
public class OKHttpModule {

    Application app;

    public OKHttpModule (Application application) {
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

        int cachesize = 10 * 1024 * 1024;
        final Cache cache = new Cache(new File(app.getApplicationContext().getCacheDir(), "http"), cachesize);

        OkHttpClient client = new OkHttpClient.Builder()
                .cache(cache)
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();
        return client;
    }

    @Provides
    @Named("stackexchange")
    @Singleton
    public Retrofit getRetrofitStackExchange (OkHttpClient client) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.stackexchange.com")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        return retrofit;
    }

    @Provides
    @Named("stackexchangerx")
    @Singleton
    public Retrofit getRetrofitStackExchangeRx (OkHttpClient client) {
        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io()))
                .baseUrl("https://api.stackexchange.com")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        return retrofit;
    }

    @Provides
    @Named("githubrx")
    @Singleton
    public Retrofit getRetrofitGitHubRx (OkHttpClient client) {

        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io()))
                .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        return retrofit;
    }

    @Provides
    @Named("meetup")
    @Singleton
    public Retrofit getRetrofitMeetup (OkHttpClient client) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.meetup.com")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        return retrofit;
    }

    @Provides
    @Named("meetuprx")
    @Singleton
    public Retrofit getRetrofitMeetupRx (OkHttpClient client) {

        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io()))
                .baseUrl("https://api.meetup.com")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        return retrofit;
    }
}
