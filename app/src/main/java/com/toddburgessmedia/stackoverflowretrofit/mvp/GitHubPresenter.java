package com.toddburgessmedia.stackoverflowretrofit.mvp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabSelectedListener;
import com.toddburgessmedia.stackoverflowretrofit.GitHubLink;
import com.toddburgessmedia.stackoverflowretrofit.ListQuestionsActivity;
import com.toddburgessmedia.stackoverflowretrofit.MainActivity;
import com.toddburgessmedia.stackoverflowretrofit.MeetupActivity;
import com.toddburgessmedia.stackoverflowretrofit.NoLanguageFoundDialog;
import com.toddburgessmedia.stackoverflowretrofit.PreferencesActivity;
import com.toddburgessmedia.stackoverflowretrofit.PrivacyPolicyActivity;
import com.toddburgessmedia.stackoverflowretrofit.R;
import com.toddburgessmedia.stackoverflowretrofit.RecyclerViewGitHub;
import com.toddburgessmedia.stackoverflowretrofit.TechDive;
import com.toddburgessmedia.stackoverflowretrofit.eventbus.NoLanguageFoundMessage;
import com.toddburgessmedia.stackoverflowretrofit.retrofit.GitHubProjectAPI;
import com.toddburgessmedia.stackoverflowretrofit.retrofit.GitHubProjectCollection;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Response;
import retrofit2.Retrofit;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Todd Burgess (todd@toddburgessmedia.com on 04/10/16.
 */

public class GitHubPresenter extends Fragment implements TechDiveMVP {

    String TAG = MainActivity.TAG;
    String searchTag;
    String searchsite;
    boolean searchLanguage = true;
    int page = 1;

    GitHubLink gitHubLink;
    NoLanguageFoundDialog nothing;
    RecyclerViewGitHub adapter;
    GitHubProjectCollection projects;
    GitHubProjectCollection newprojects;
    Response<GitHubProjectCollection> response;
    ProgressDialog progress;

    BottomBar bottomBar;
    int TABPOS = 1;

    @Inject
    @Named("githubrx")
    Retrofit retrofit;

    @Inject
    Context context;

    @BindView(R.id.github_fragment_recycleview)
    RecyclerView rv;

    @BindString(R.string.github_activity_loading) String loadingMsg;


    @Override
    public void onStart() {
        super.onStart();

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((TechDive) getActivity().getApplication()).getOkHttpComponent().inject(this);

        searchTag = getArguments().getString("searchtag");
        searchsite = getArguments().getString("searchsite");

        setHasOptionsMenu(true);
        createBottomBar(savedInstanceState);
        bottomBar.selectTabAtPosition(TABPOS,false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_github, container,false);
        ButterKnife.bind(this, view);

        if (rv != null) {
            rv.setHasFixedSize(true);
        }
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        createScrollChangeListener();

        if (savedInstanceState != null) {
            projects = (GitHubProjectCollection) savedInstanceState.getSerializable("savedprojects");
            searchTag = savedInstanceState.getString("searchtag");
            searchsite = savedInstanceState.getString("searchsite");
            searchLanguage = savedInstanceState.getBoolean("search_language");
            page = savedInstanceState.getInt("page");
            gitHubLink = (GitHubLink) savedInstanceState.getSerializable("githublink");
            createBottomBar(savedInstanceState);
            if (projects != null) {
                adapter = new RecyclerViewGitHub(projects.getProjects(), context);
                if (searchLanguage) {
                    adapter.setSearchType(RecyclerViewGitHub.LANGUAGESEARCH);
                } else {
                    adapter.setSearchType(RecyclerViewGitHub.KEYWORDSEARCH);
                }
                adapter.setSearchword(searchTag);
                adapter.setHasMore(gitHubLink.hasMore());
                rv.setAdapter(adapter);
                bottomBar.selectTabAtPosition(TABPOS,false);
            }

            return view;
        }


        setSearch();
        fetchRestSource();
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("savedprojects",projects);
        outState.putString("searchtag",searchTag);
        outState.putString("searchsite",searchsite);
        outState.putBoolean("search_language",searchLanguage);
        outState.putInt("page", page);
        outState.putSerializable("githublink",gitHubLink);
        bottomBar.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);


    }

    private void setSearch() {

        String searchtype;
        if (searchLanguage) {
            searchtype = getString(R.string.github_language);
        } else {
            searchtype = getString(R.string.github_searchterm);
        }

        String text = searchtype + " " + searchTag;
    }


    public void fetchRestSource() {

        startProgressDialog();
        GitHubProjectAPI projectAPI;

        projectAPI = retrofit.create(GitHubProjectAPI.class);

        Log.d(TAG, "fetchRestSource: " + searchTag);
        final String qlanguage;
        if (searchLanguage) {
            qlanguage = "language:" + searchTag;
        } else {
            qlanguage = searchTag;
        }

        Observable<Response<GitHubProjectCollection>> call;
        if (page == 1) {
            call = projectAPI.getProjectsObservable(qlanguage);
        } else {
            call = projectAPI.getProjectsNextPageObservable(gitHubLink.getParamterHashMap());
        }

        call.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Response<GitHubProjectCollection>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(context, "Unable to retrieve projects", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(Response<GitHubProjectCollection> response) {
                        stopProgressDialog();
                        if  ((response.code() != 200) && (searchLanguage)) {

                            if (nothing == null) {
                                nothing = new NoLanguageFoundDialog();
                                nothing.show(getActivity().getFragmentManager(), "nothing");
                                return;
                            }
                        } else if ((response.code() != 200) && (!searchLanguage)) {
                            Toast.makeText(getActivity(), "No Github Projects Found", Toast.LENGTH_SHORT).show();
                            getActivity().finish();
                        }

                        gitHubLink = new GitHubLink(response.headers().get("Link"));
                        newprojects = response.body();

                        renderModel();

                    }
                });
    }

    public void renderModel () {


        if ((adapter != null) && (page > 1)) {
            newprojects.insertLastPlaceHolder();
            adapter.addItems(newprojects.getProjects());
            adapter.setHasMore(gitHubLink.hasMore());
            projects.mergeProjects(newprojects);
        } else if (adapter != null) {
            projects = newprojects;
            projects.insertPlaceHolders();
            adapter.removeAllItems();
            adapter.updateAdapter(projects.getProjects());
            adapter.setHasMore(gitHubLink.hasMore());
        } else {
            projects = newprojects;
            projects.insertPlaceHolders();
            adapter = new RecyclerViewGitHub(projects.getProjects(), context);
            adapter.setHasMore(gitHubLink.hasMore());
            if (searchLanguage) {
                adapter.setSearchType(RecyclerViewGitHub.LANGUAGESEARCH);
            } else {
                adapter.setSearchType(RecyclerViewGitHub.KEYWORDSEARCH);
            }
            adapter.setSearchword(searchTag);
            rv.setAdapter(adapter);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.github_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.github_menu_refresh:
                page = 1;
                //startProgressDialog();
                fetchRestSource();
                break;
            case R.id.github_menu_preferences:
                Intent i = new Intent(getActivity(),PreferencesActivity.class);
                startActivity(i);
                break;
            case R.id.github_menu_privacy:
                Intent pi = new Intent(getActivity(), PrivacyPolicyActivity.class);
                startActivity(pi);
                break;
        }
        return true;
    }

    private void createBottomBar(Bundle savedInstanceState) {

        NewTabListener listener = new NewTabListener();
        listener.setSearchsite(searchsite);
        listener.setSearchTab(searchTag);

        bottomBar = BottomBar.attach(getActivity(), savedInstanceState);
        bottomBar.setItemsFromMenu(R.menu.github_three_buttons, listener);
        bottomBar.selectTabAtPosition(TABPOS,false);
    }

    private void startProgressDialog() {

        if (progress == null) {
            progress = new ProgressDialog(getActivity());
        }
        progress.setMessage(loadingMsg);
        progress.show();
    }

    private void stopProgressDialog() {
        if (progress != null) {
            progress.dismiss();
        }
    }

    @Subscribe
    public void positiveClick(NoLanguageFoundMessage message) {

        if (!message.isTopicsearch()) {
            getActivity().finish();
            return;
        }

        nothing.dismiss();
        //startProgressDialog();
        searchLanguage = false;
        setSearch();
        fetchRestSource();
    }

    @Subscribe
    public void onClick(RecyclerViewGitHub.RecyclerViewGitHubMessage message) {

        page++;
        //startProgressDialog();
        fetchRestSource();
    }

    private void createScrollChangeListener() {
        rv.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {
                    bottomBar.hide();
                } else {
                    bottomBar.show();
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }

    protected class NewTabListener implements OnMenuTabSelectedListener {

        public void setSearchTab(String searchTab) {
            this.searchTab = searchTab;
        }

        private String searchTab;

        public void setSearchsite(String searchsite) {
            this.searchsite = searchsite;
        }

        private String searchsite;

        @Override
        public void onMenuItemSelected(@IdRes int menuItemId) {
            Log.d(MainActivity.TAG, "onMenuItemSelected: " + menuItemId);
            Intent i;
            switch (menuItemId) {
                case R.id.github_bottom_faq:
                    i = new Intent(getActivity(), ListQuestionsActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("sitename", searchsite);
                    i.putExtra("name", searchTag);
                    startActivity(i);
                    break;
                case R.id.gitbhub_bottom_meetup:
                    i = new Intent(getActivity(), MeetupActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("searchtag", searchTag);
                    i.putExtra("searchsite", searchsite);
                    startActivity(i);
                    break;
            }
        }
    }


}
