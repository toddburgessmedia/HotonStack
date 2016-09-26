package com.toddburgessmedia.stackoverflowretrofit;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.f2prateek.rx.preferences.Preference;
import com.f2prateek.rx.preferences.RxSharedPreferences;
import com.toddburgessmedia.stackoverflowretrofit.retrofit.StackOverFlowAPI;
import com.toddburgessmedia.stackoverflowretrofit.retrofit.StackOverFlowTags;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import rx.functions.Action1;
import rx.functions.Func1;


public class MainActivity extends AppCompatActivity implements
        SearchDialog.SearchDialogListener, SiteSelectDialog.SiteSelectDialogListener,
        TagsLongPressDialog.TagsLongPressDialogListener, RecyclerViewTagsAdapter.TagsAdapterListener,
        StackExchangeRankingDialog.StackExchangeRankingDialogListener,
        StackExchangeSortDialog.StackExchangeSortDialogListener,
        TimeFrameDialog.TimeFrameDialogListener
        {

    public final static String TAG = "StackOverFlow";

    public static final int ALLTIME = 0;
    public static final int TODAY = 4;
    public static final int YESTERDAY = 3;
    public static final int THISMONTH = 2;
    public static final int THISYEAR = 1;

    int searchtime = ALLTIME;

    @BindView(R.id.recycleview) RecyclerView rv;

    @Inject @Named("stackexchange") Retrofit retrofit;

    RecyclerViewTagsAdapter adapter;
    StackOverFlowTags tags;

    ProgressDialog progress;
    int tagcount;
    int pagecount = 1;


    String searchsite;
    String sitename;

    String searchtag = "";

    String searchtype = "Popularity";
    String sorttype = "Usage";

    RxSharedPreferences rxPrefs;
    Preference<String> rxDefaultsite;

    Menu menu;

    boolean tagsearch = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        ((TechDive) getApplication()).getOkHttpComponent().inject(this);

        if (rv != null) {
            rv.setHasFixedSize(true);
            rv.setLayoutManager(new LinearLayoutManager(this));
            getSwipeHandler();
        }


        if (savedInstanceState != null) {
            tags = (StackOverFlowTags) savedInstanceState.getSerializable("taglist");
            searchsite = savedInstanceState.getString("searchsite");
            tagcount = savedInstanceState.getInt("tagcount");
            pagecount = savedInstanceState.getInt("pagecount");
            setSiteName();
            if (tags != null) {
                adapter = new RecyclerViewTagsAdapter(tags.tags, getBaseContext(),searchsite,this);
                adapter.setDisplaySiteName(sitename);
                adapter.setHasmore(tags.isHasMore());
                rv.setAdapter(adapter);
                return;
            }
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        tagcount = Integer.valueOf(prefs.getString("tagcount", "100"));
        searchsite = prefs.getString("defaultsite","StackOverflow");
        startPrefsObservables(prefs);

        setSiteName();
        startProgressDialog();
        getTags(tagcount,tagsearch);
    }

    private void startProgressDialog() {

        if (progress == null) {
            progress = new ProgressDialog(this);
            progress.setMessage("Loading Tags");
        }
        progress.show();
    }

    private void startPrefsObservables(SharedPreferences prefs) {
        rxPrefs = RxSharedPreferences.create(prefs);
        rxDefaultsite = rxPrefs.getString("defaultsite");
        rxDefaultsite.asObservable()
                .filter(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String s) {
                        if (s == null) {
                            return false;
                        } else {
                            return true;
                        }
                    }
                })
                .subscribe(new Action1<String>() {
                @Override
                public void call(String s) {
                    searchsite = s;
                    setSiteName();
                    if (adapter != null) {
                        adapter.setDisplaySiteName(sitename);
                    }
                    startProgressDialog();
                    getTags(tagcount,tagsearch);
                }
            });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putSerializable("taglist",tags);
        outState.putString("searchsite",searchsite);
        outState.putInt("tagcount", tagcount);
        outState.putInt("pagecount", pagecount);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);

                SearchDialog dialog = new SearchDialog();
                dialog.show(getFragmentManager(),"search");
                break;
            case R.id.menu_refresh:
                startProgressDialog();
                pagecount = 1;
                getTags(tagcount,tagsearch);
                break;
            case R.id.menu_preferences:
                Intent i = new Intent(this,PreferencesActivity.class);
                startActivity(i);
                break;
            case R.id.menu_siteselect:
                SiteSelectDialog siteSelectDialog = new SiteSelectDialog();
                siteSelectDialog.show(getFragmentManager(),"sitesearch");
                break;
            case R.id.menu_privacy:
                Intent pi = new Intent(MainActivity.this, PrivacyPolicyActivity.class);
                startActivity(pi);
                break;
            case R.id.menu_ranking:
                StackExchangeRankingDialog rankingDialog = new StackExchangeRankingDialog();
                rankingDialog.show(getFragmentManager(),"rankingdialog");
                break;
            case R.id.timeframe:
                TimeFrameDialog timeFrameDialog = new TimeFrameDialog();
                timeFrameDialog.show(getFragmentManager(), "timeframedialog");
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflate = getMenuInflater();
        inflate.inflate(R.menu.actionbar,menu);
        this.menu = menu;

        MenuItem item = menu.findItem(R.id.menu_ranking);
        String rankby = getString(R.string.tags_dialog_ranking) + searchtype;
        item.setTitle(rankby);

//        MenuItem sortItem = menu.findItem(R.id.menu_sortby);
//        String sortby = getString(R.string.tags_dialog_sort) + sorttype;
//        sortItem.setTitle(sortby);

        return true;
    }

    private void getTags(final int tagcount, final boolean synonymsearch) {

        StackOverFlowAPI stackOverFlowAPI = retrofit.create(StackOverFlowAPI.class);

        Call<StackOverFlowTags> call;
        if (!synonymsearch) {
            call = getStackOverFlowFAQCall(stackOverFlowAPI);
        } else {
            call = stackOverFlowAPI.loadSynonyms(searchtag, searchsite);
        }

        call.enqueue(new Callback<StackOverFlowTags>() {
            @Override
            public void onResponse(Call<StackOverFlowTags> call, Response<StackOverFlowTags> response) {

                StackOverFlowTags callTags = response.body();

                if (progress != null) {
                    progress.dismiss();
                }
                if (callTags == null) {
                    Toast.makeText(MainActivity.this, "No tags found", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (callTags.tags.size() == 0) {
                    Toast.makeText(MainActivity.this, "No more tags to load!", Toast.LENGTH_SHORT).show();
                    tagsearch = false;
                    setSiteName();
                    return;
                }

                if ((adapter != null) && pagecount > 1) {
                    callTags.insertLastPlaceHolder();
                    adapter.addItems(callTags.tags);
                    adapter.setHasmore(tags.isHasMore());
                    tags.mergeTags(callTags);
                } else if (adapter != null) {
                    tags = callTags;
                    tags.insertPlaceHolders();
                    adapter.removeAllItems();
                    tags.rankTags();
                    setTimeFrame();
                    adapter.updateAdapter(callTags.tags);
                    if (tags.tags.size() <= tagcount) {
                        adapter.setHasmore(false);
                    } else {
                        adapter.setHasmore(tags.isHasMore());
                    }
                } else {
                    tags = callTags;
                    tags.rankTags();
                    tags.insertPlaceHolders();
                    adapter = new RecyclerViewTagsAdapter(tags.tags, getBaseContext(), searchsite, MainActivity.this);
                    if (tags.tags.size() <= tagcount) {
                        adapter.setHasmore(false);
                    } else {
                        adapter.setHasmore(tags.isHasMore());
                    }
                    adapter.setDisplaySiteName(sitename);
                    setTimeFrame();
                    rv.setAdapter(adapter);
                }

            }

            @Override
            public void onFailure(Call<StackOverFlowTags> call, Throwable t) {
                Log.d(TAG, "onFailure: we failed. this is terrible!!!");
                Toast.makeText(MainActivity.this, "No Network Connection!", Toast.LENGTH_SHORT).show();
                progress.dismiss();
            }
        });
    }

    private Call<StackOverFlowTags> getStackOverFlowFAQCall(StackOverFlowAPI faqAPI) {

        long secondsPassed;
        TimeDelay delay = new TimeDelay();

        String sort;
        if (searchtype.equals("Popularity")) {
            sort = "popular";
        } else {
            sort = "activity";
        }

        switch (searchtime) {
            case TODAY:
                secondsPassed = delay.getTimeDelay(TimeDelay.TODAY);
                break;
            case YESTERDAY:
                secondsPassed = delay.getTimeDelay(TimeDelay.YESTERDAY);
                break;
            case THISMONTH:
                secondsPassed = delay.getTimeDelay(TimeDelay.THISMONTH);
                break;
            case THISYEAR:
                secondsPassed = delay.getTimeDelay(TimeDelay.THISYEAR);
                break;
            default:
                if (searchtype.equals("Popularity")) {
                    return faqAPI.loadquestions(tagcount, searchsite, pagecount);
                } else {
                    return faqAPI.loadquestionsActivity(tagcount, searchsite, pagecount);
                }
        }
        return faqAPI.loadsquestionsByDate(tagcount, sort, searchsite, pagecount, secondsPassed);
    }

    void setSiteName() {

        String[] display = getResources().getStringArray(R.array.select_select_display);
        String[] values = getResources().getStringArray(R.array.site_select_array);
        boolean found = false;
        int i = 0;

        while (!found) {
            if (values[i].equals(searchsite)) {
                found = true;
            } else {
                i++;
                if (i == values.length) { // this is to catch errors
                    i--;
                    found = true;
                }
            }
        }

        sitename = display[i];
        if (adapter != null) {
            adapter.setSitename(searchsite);
        }

    }

    // Search Dialog positive click
    @Override
    public void positiveClick(DialogFragment fragment) {
        String tag;
        SearchDialog search = (SearchDialog) fragment;

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        EditText text = (EditText) search.view.findViewById(R.id.search_tag);
        tag = text.getText().toString();

        tag = tag.replace(' ', '-');
        searchtag = tag;
        String newsite = sitename + " / " + searchtag;
        tagsearch = true;
        adapter.setDisplaySiteName(sitename);
        startProgressDialog();
        getTags(tagcount, tagsearch);

    }


    // Search Dialog negative click
    @Override
    public void negativeClick(DialogFragment fragment) {

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    // Change Site positive click
    @Override
    public void siteSelectpositiveClick(DialogFragment fragment, int which) {

        String[] sites = getResources().getStringArray(R.array.site_select_array);
        searchsite = sites[which];

        setSiteName();
        if (adapter != null) {
            adapter.setDisplaySiteName(sitename);
        }
        startProgressDialog();
        getTags(tagcount,tagsearch);


    }

    // LongPress Dialog positive click handler
    @Override
    public void longPresspositiveClick(DialogFragment fragment, int which) {

        String[] values;

        if (!searchtag.equals("")) {
            values = getResources().getStringArray(R.array.tag_longpress_dialog);

            if (values[which].equals("Load Related Tags")) {
                String newsite = sitename + " / " + searchtag;
                adapter.setDisplaySiteName(newsite);
                tagsearch = true;
            }
            startProgressDialog();
            getTags(tagcount, tagsearch);

        } else {
            setSiteName();
            adapter.setDisplaySiteName(sitename);
            tagsearch = false;
            startProgressDialog();
            getTags(tagcount,tagsearch);
        }


    }

    // Long Press negative handler
    @Override
    public void longPresstnegativeClick(DialogFragment fragment, int which) {

    }

    
    @Override
    public void onLongClick(View v, String tag) {
        Log.d(TAG, "onLongClick: ");


        TagsLongPressDialog dialog = new TagsLongPressDialog();
        if (searchtag.equals("")) {
            searchtag = tag;
            dialog.setTagsearch(true);
        } else {
            searchtag = "";
            dialog.setTagsearch(false);
        }
        dialog.show(getFragmentManager(),"long press");
    }

    @Override
    public void loadMoreTags(View view) {

        pagecount++;
        startProgressDialog();
        getTags(tagcount,tagsearch);

    }

    void setTimeFrame() {

        String[] times = getResources().getStringArray(R.array.time_dialog);
        adapter.setTimeframe(times[searchtime]);

    }

    private void getSwipeHandler() {

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                adapter.onItemDismiss(viewHolder.getAdapterPosition());
            }
        };

        ItemTouchHelper helper = new ItemTouchHelper(simpleCallback);
        helper.attachToRecyclerView(rv);

    }

    @Override
    public void stackExchangeRankingpositiveClick(DialogFragment fragment, int which) {

        String[] ranking = getResources().getStringArray(R.array.tags_ranking);

        if (searchtype.equals(ranking[which])) {
            return;
        }

        pagecount = 1;
        searchtype = ranking[which];

        MenuItem item = menu.findItem(R.id.menu_ranking);
        String sortby = getString(R.string.tags_dialog_ranking) + searchtype;
        item.setTitle(sortby);

        if (progress != null) {
            progress.show();
        }
        getTags(tagcount,tagsearch);

    }

    @Override
    public void stackExchangeSortpositiveClick(DialogFragment fragment, int which) {

        String[] sortOptions = getResources().getStringArray(R.array.tags_sortby);

        if (sorttype.equals(sortOptions[which])) {
            return;
        }

        sorttype = sortOptions[which];



    }

    @Override
    public void positiveClick(DialogFragment fragment, int which) {
        String[] what = getResources().getStringArray(R.array.time_dialog);

        switch (what[which]) {
            case "All Time":
                searchtime = ALLTIME;
                break;
            case "Today":
                searchtime = TODAY;
                break;
            case "Since Yesterday":
                searchtime = YESTERDAY;
                break;
            case "This Month":
                searchtime = THISMONTH;
                break;
            case "This Year":
                searchtime = THISYEAR;
                break;

        }
        pagecount = 1;
        startProgressDialog();
        getTags(tagcount,tagsearch);

    }

    @Override
    public void negativeClick(DialogFragment fragment, int which) {

    }

}
