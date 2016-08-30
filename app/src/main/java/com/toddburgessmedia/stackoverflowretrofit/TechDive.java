package com.toddburgessmedia.stackoverflowretrofit;

import android.app.Application;

import com.toddburgessmedia.stackoverflowretrofit.dagger.DaggerOkHttpComponent;
import com.toddburgessmedia.stackoverflowretrofit.dagger.OKHttpModule;
import com.toddburgessmedia.stackoverflowretrofit.dagger.OkHttpComponent;

/**
 * Created by Todd Burgess (todd@toddburgessmedia.com on 30/08/16.
 */
public class TechDive extends Application {

    private OkHttpComponent okHttpComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        okHttpComponent = DaggerOkHttpComponent.builder()
                .oKHttpModule(new OKHttpModule(this))
                .build();
    }

    public OkHttpComponent getOkHttpComponent() {
        return okHttpComponent;
    }
}
