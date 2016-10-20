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
