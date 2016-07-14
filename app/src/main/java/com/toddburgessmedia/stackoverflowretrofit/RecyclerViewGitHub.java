package com.toddburgessmedia.stackoverflowretrofit;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.toddburgessmedia.stackoverflowretrofit.retrofit.GitHubProject;

import java.util.List;

/**
 * Created by Todd Burgess (todd@toddburgessmedia.com on 05/06/16.
 */
public class RecyclerViewGitHub extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<GitHubProject> projects;

    private final int VIEWTYPE = 1;

    Context context;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {

        View v = null;

        View.OnClickListener click = new View.OnClickListener() {
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

        switch (viewType) {
            case VIEWTYPE:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_github,parent,false);
                v.setOnClickListener(click);
                return new ViewHolder(v);
        }
        return new ViewHolder(v);
    }


    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        GitHubProject t = projects.get(position);

        String description;
        if (t.getDescription().equals("")) {
            description = context.getString(R.string.no_github_desc);
        }
        else {
            description = t.getDescription();
        }

        switch (getItemViewType(position)) {
            case VIEWTYPE:
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
        }
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

        return VIEWTYPE;
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

        TextView title;
        TextView description;
        TextView link;
        TextView language;
        TextView createdate;
        TextView updatedate;
        TextView forks;
        TextView watchers;

        public ViewHolder (View v) {
            super(v);

            title = (TextView) v.findViewById(R.id.rv_github_title);
            description = (TextView) v.findViewById(R.id.rv_github_desc);
            link = (TextView) v.findViewById(R.id.rv_github_link);
            language = (TextView) v.findViewById(R.id.rv_github_language);
            createdate = (TextView) v.findViewById(R.id.rv_github_createdate);
            updatedate = (TextView) v.findViewById(R.id.rv_github_updatedate);
            forks = (TextView) v.findViewById(R.id.rv_github_forks);
            watchers = (TextView) v.findViewById(R.id.rv_github_watchers);
        }

    }

}
