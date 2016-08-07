package com.toddburgessmedia.stackoverflowretrofit;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.toddburgessmedia.stackoverflowretrofit.retrofit.GitHubProject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Todd Burgess (todd@toddburgessmedia.com on 05/06/16.
 */
public class RecyclerViewGitHub extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<GitHubProject> projects;

    private final int VIEWTYPE = 1;
    private final int VIEWTYPESTART = 0;

    public static final int LANGUAGESEARCH = 0;
    public static final int KEYWORDSEARCH = 1;
    private int searchType;

    public void setSearchword(String searchword) {
        this.searchword = searchword;
    }

    private String searchword;

    public void setHeader(String header) {
        this.header = header;
    }

    private String header;

    public void setSearchType(int searchType) {
        this.searchType = searchType;
    }

    Context context;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {

        View v = null;

//        View.OnClickListener click = getOnClickListener(viewType);

        switch (viewType) {
            case VIEWTYPE:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_github,parent,false);
                v.setOnClickListener(getOnClickListener(viewType));
                return new ViewHolder(v);
            case VIEWTYPESTART:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_github_start, parent,false);
                return new ViewHolderStart(v);
        }
        return new ViewHolder(v);
    }

    @NonNull
    private View.OnClickListener getOnClickListener(final int viewType) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String link = "";
                if (getItemViewType(viewType) == VIEWTYPE) {
                    ViewHolder vh = new ViewHolder(v);
                    link = vh.link.getText().toString();
                }

                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                v.getContext().startActivity(i);
            }
        };
    }


    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {



        switch (getItemViewType(position)) {
            case VIEWTYPE:
                GitHubProject t = projects.get(position-1);
                String description = getDescription(t);
                ViewHolder v = (ViewHolder) holder;
                v.title.setText(t.getFullName());
                v.description.setText(description);
                v.link.setText(t.getHtmlURL());
                v.language.setText(setLanguage(t.getLanguage()));
                v.createdate.setText(formatGitHubDate(t.getCreatedAt()));
                v.updatedate.setText(formatGitHubDate(t.getUpdatedAt()));
                v.forks.setText(t.getForks());
                v.watchers.setText(t.getWatchers());
                break;
            case VIEWTYPESTART:
                ViewHolderStart vs = (ViewHolderStart) holder;
                vs.searchTitle.setText(createHeader());
                break;
        }
    }

    private String createHeader() {

        String header;

        if (searchType == LANGUAGESEARCH) {
            header = "Language: ";
        } else {
            header = "Keyword: ";
        }

        return header + searchword;
    }

    private String getDescription(GitHubProject t) {
        String description;
        if ((t==null) || (t.getDescription().equals(""))) {
            description = context.getString(R.string.no_github_desc);
        }
        else {
            description = t.getDescription();
        }
        return description;
    }

    public String setLanguage (String language) {

        if (language == null) {
            return context.getString(R.string.rv_github_no_language);
        } else {
            return language;
        }

    }

    public String formatGitHubDate(String githubdate) {

        String newdate = githubdate.replace('T',' ');
        return newdate.substring(0,newdate.lastIndexOf(':'));

    }

    @Override
    public int getItemViewType(int position) {

        if (position == 0) {
            return VIEWTYPESTART;
        } else {
            return VIEWTYPE;
        }
    }

    @Override
    public int getItemCount() {
        return projects.size();
    }

    public void removeAllItems() {
        for (int i = 0; i < projects.size(); i++) {
            projects.remove(i);
            notifyItemRemoved(i);
            notifyItemRangeChanged(i,projects.size());
        }
    }

    public RecyclerViewGitHub (List<GitHubProject> projects, Context con) {

        this.projects = projects;
        this.context = con;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.rv_github_title) TextView title;
        @BindView(R.id.rv_github_desc) TextView description;
        @BindView(R.id.rv_github_link) TextView link;
        @BindView(R.id.rv_github_language) TextView language;
        @BindView(R.id.rv_github_createdate) TextView createdate;
        @BindView(R.id.rv_github_updatedate) TextView updatedate;
        @BindView(R.id.rv_github_forks) TextView forks;
        @BindView(R.id.rv_github_watchers) TextView watchers;

        public ViewHolder (View v) {
            super(v);

            ButterKnife.bind(this, v);

        }

    }

    public class ViewHolderStart extends RecyclerView.ViewHolder {

        @BindView(R.id.github_start_search) TextView searchTitle;

        public  ViewHolderStart (View v) {
            super(v);

            ButterKnife.bind(this, v);
        }


    }
}
