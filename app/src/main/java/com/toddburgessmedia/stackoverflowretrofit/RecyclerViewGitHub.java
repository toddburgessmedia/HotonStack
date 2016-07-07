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
    private final int VIEWTYPE_EVEN = 0;

    int lastPosition = -1;
    Context context;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {

        View v = null;

        View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String link;
                if (getItemViewType(viewType) == VIEWTYPE) {
                    ViewHolder vh = new ViewHolder(v);
                    link = vh.link.getText().toString();
                } else {
                    ViewHolderEven vh = new ViewHolderEven(v);
                    link = vh.link.getText().toString();
                }

                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                v.getContext().startActivity(i);
            }
        };

        switch (viewType) {
            case VIEWTYPE:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleview_github_odd,parent,false);
                v.setOnClickListener(click);
                return new ViewHolder(v);
            case VIEWTYPE_EVEN:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleview_github_even,parent,false);
                v.setOnClickListener(click);
                return new ViewHolderEven(v);
        }
        return new ViewHolder(v);
    }


    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        GitHubProject t = projects.get(position);

        String description;
        if (t.getDescription().equals(""))
            description = context.getString(R.string.no_github_desc);
        else
            description = t.getDescription();

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
            case VIEWTYPE_EVEN:
                ViewHolderEven ve = (ViewHolderEven) holder;

                ve.title.setText(t.getFullName());
                ve.description.setText(description);
                ve.link.setText(t.getHtmlURL());
                break;
        }
    }

    public String setLanguage (String language) {

        if (language == null) {
            return "No Language Specified";
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
//        if ((position %2) == 0) {
//            return VIEWTYPE_EVEN;
//        } else {
//            return VIEWTYPE;
//        }

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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

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

            title = (TextView) v.findViewById(R.id.rv_github_odd_title);
            description = (TextView) v.findViewById(R.id.rv_github_odd_desc);
            link = (TextView) v.findViewById(R.id.rv_github_odd_link);
            language = (TextView) v.findViewById(R.id.rv_github_odd_language);
            createdate = (TextView) v.findViewById(R.id.rv_github_odd_createdate);
            updatedate = (TextView) v.findViewById(R.id.rv_github_odd_updatedate);
            forks = (TextView) v.findViewById(R.id.rv_github_odd_forks);
            watchers = (TextView) v.findViewById(R.id.rv_github_odd_watchers);
        }
        @Override
        public void onClick(View v) {

            Intent i = new Intent(v.getContext(),ListQuestionsActivity.class);
            v.getContext().startActivity(i);

        }
    }

    public class ViewHolderEven extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView title;
        TextView description;
        TextView link;
        TextView language;

        public ViewHolderEven (View v) {
            super(v);

            title = (TextView) v.findViewById(R.id.rv_github_even_title);
            description = (TextView) v.findViewById(R.id.rv_github_even_desc);
            link = (TextView) v.findViewById(R.id.rv_github_even_link);

        }

        @Override
        public void onClick(View v) {

            Intent i = new Intent(v.getContext(),ListQuestionsActivity.class);
            v.getContext().startActivity(i);
        }
    }


}
