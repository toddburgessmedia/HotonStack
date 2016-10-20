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

package com.toddburgessmedia.stackoverflowretrofit.dagger;

import com.toddburgessmedia.stackoverflowretrofit.ListQuestionsActivity;
import com.toddburgessmedia.stackoverflowretrofit.MainActivity;
import com.toddburgessmedia.stackoverflowretrofit.MeetupActivity;
import com.toddburgessmedia.stackoverflowretrofit.RecyclerViewTagsAdapter;
import com.toddburgessmedia.stackoverflowretrofit.mvp.GitHubPresenter;
import com.toddburgessmedia.stackoverflowretrofit.mvp.ListQuestionsPresenter;
import com.toddburgessmedia.stackoverflowretrofit.mvp.MainActivityPresenter;
import com.toddburgessmedia.stackoverflowretrofit.mvp.MeetupPresenter;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Todd Burgess (todd@toddburgessmedia.com on 30/08/16.
 */

@Singleton
@Component(modules={OKHttpModule.class})
public interface OkHttpComponent {

    void inject(MainActivity mainActivity);

    void inject(ListQuestionsActivity listQuestionsActivity);

    void inject(MeetupActivity meetupActivity);

    void inject(RecyclerViewTagsAdapter recyclerViewTagsAdapter);

    void inject(GitHubPresenter gitHubPresenter);

    void inject(MainActivityPresenter mainActivityPresenter);

    void inject(ListQuestionsPresenter listQuestionsPresenter);

    void inject(MeetupPresenter meetupPresenter);
}
