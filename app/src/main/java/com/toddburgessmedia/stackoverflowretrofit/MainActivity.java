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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.toddburgessmedia.stackoverflowretrofit.retrofit.StackOverFlowAPI;
import com.toddburgessmedia.stackoverflowretrofit.retrofit.StackOverFlowTags;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity implements
        SearchDialog.SearchDialogListener {

    public final static String TAG = "StackOverFlow";

    RecyclerView rv;
    RecyclerView.Adapter adapter;
    StackOverFlowTags tags;

    String tagcount;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rv = (RecyclerView) findViewById(R.id.recycleview);
        if (rv != null) {
            rv.setHasFixedSize(true);
        }
        rv.setLayoutManager(new LinearLayoutManager(this));
        //rv.addItemDecoration(new SimpleDividerItemDecoration(this));

        if (savedInstanceState != null) {
            tags = (StackOverFlowTags) savedInstanceState.getSerializable("taglist");
            if (tags != null) {
                adapter = new StackTagsRecyclerView(tags.tags, getBaseContext());
                rv.setAdapter(adapter);
                return;
            }
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        tagcount = prefs.getString("tagcount","100");

        progress = new ProgressDialog(this);
        progress.setMessage("Loading Tags");
        progress.show();

        getTags(tagcount);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putSerializable("taglist",tags);

        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:
                Log.d(TAG, "onOptionsItemSelected: click!");

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);

                SearchDialog dialog = new SearchDialog();

                dialog.show(getFragmentManager(),"search");

                break;
            case R.id.menu_refresh:
                getTags(tagcount);
                break;
            case R.id.menu_preferences:
                Intent i = new Intent(this,PreferencesActivity.class);
                startActivity(i);
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflate = getMenuInflater();
        inflate.inflate(R.menu.actionbar,menu);
        return true;

        //return super.onCreateOptionsMenu(menu);
    }


    private void getTags(String tagcount) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.stackexchange.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        StackOverFlowAPI stackOverFlowAPI = retrofit.create(StackOverFlowAPI.class);

        Call<StackOverFlowTags> call = stackOverFlowAPI.loadquestions(tagcount);

        call.enqueue(new Callback<StackOverFlowTags>() {
            @Override
            public void onResponse(Call<StackOverFlowTags> call, Response<StackOverFlowTags> response) {

                tags = response.body();

                adapter = new StackTagsRecyclerView(tags.tags,getBaseContext());
                rv.setAdapter(adapter);
                progress.dismiss();
            }

            @Override
            public void onFailure(Call<StackOverFlowTags> call, Throwable t) {
                Log.d(TAG, "onFailure: we failed. this is terrible!!!");
                Toast.makeText(MainActivity.this, "No Network Connection!", Toast.LENGTH_SHORT).show();
                progress.dismiss();
            }
        });
    }

    @Override
    public void positiveClick(DialogFragment fragment) {
        String tag;
        SearchDialog search = (SearchDialog) fragment;

        EditText text = (EditText) search.view.findViewById(R.id.search_tag);
        tag = text.getText().toString();
        tag = tag.replace(' ','-');

        Intent i = new Intent(this,ListQuestionsActivity.class);
        i.putExtra("title",tag);
        startActivity(i);
    }

    @Override
    public void negativeClick(DialogFragment fragment) {

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Log.d(TAG, "negativeClick: ");
    }

}
