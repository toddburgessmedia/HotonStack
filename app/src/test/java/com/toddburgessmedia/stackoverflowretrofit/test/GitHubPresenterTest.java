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

import com.toddburgessmedia.stackoverflowretrofit.BuildConfig;
import com.toddburgessmedia.stackoverflowretrofit.GitHubActivity;
import com.toddburgessmedia.stackoverflowretrofit.eventbus.NoLanguageFoundMessage;
import com.toddburgessmedia.stackoverflowretrofit.mvp.GitHubPresenter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertNotNull;

/**
 * Created by Todd Burgess (todd@toddburgessmedia.com on 27/10/16.
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class,
        sdk = 21,
        application = com.toddburgessmedia.stackoverflowretrofit.test.TechDive.class)
public class GitHubPresenterTest {

    private GitHubActivity gitHubActivity;
    private GitHubPresenter presenter;
    private com.toddburgessmedia.stackoverflowretrofit.TechDive techDive;

    @Before
    public void setUp() throws Exception {

//        techDive = (TechDive) RuntimeEnvironment.application;
        gitHubActivity = Robolectric.buildActivity(GitHubActivity.class).create().get();
        presenter = (GitHubPresenter) gitHubActivity.getSupportFragmentManager().findFragmentByTag("presenter");
        //startFragment(presenter);

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void shouldNotBeNull() throws Exception {

        assertNotNull(gitHubActivity);
        assertNotNull(presenter);
    }

    @Test
    public void positiveClick() throws Exception {

        NoLanguageFoundMessage message = new NoLanguageFoundMessage(false);
        presenter.positiveClick(message);
    }

    @Test
    public void fetchData() throws Exception {

    }

}