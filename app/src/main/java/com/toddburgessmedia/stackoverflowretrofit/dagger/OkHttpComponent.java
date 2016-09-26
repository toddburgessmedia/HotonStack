package com.toddburgessmedia.stackoverflowretrofit.dagger;

import com.toddburgessmedia.stackoverflowretrofit.GitHubActivity;
import com.toddburgessmedia.stackoverflowretrofit.ListQuestionsActivity;
import com.toddburgessmedia.stackoverflowretrofit.MainActivity;
import com.toddburgessmedia.stackoverflowretrofit.MeetupActivity;
import com.toddburgessmedia.stackoverflowretrofit.RecyclerViewTagsAdapter;

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

    void inject(GitHubActivity gitHubActivity);

    void inject(MeetupActivity meetupActivity);

    void inject(RecyclerViewTagsAdapter recyclerViewTagsAdapter);
}
