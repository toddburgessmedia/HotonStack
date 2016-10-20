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

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.toddburgessmedia.stackoverflowretrofit.mvp.TechDiveWebActivity;
import com.toddburgessmedia.stackoverflowretrofit.retrofit.MeetUpGroup;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Todd Burgess (todd@toddburgessmedia.com on 05/06/16.
 */
public class RecycleViewMeetup extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<MeetUpGroup> groups;

    private final int VIEWTYPE = 1;
    private final int VIEWTYPESTART = 0;
    Context context;

    private String location;
    private String searchterm;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {

        View v = null;

        switch (viewType) {
            case VIEWTYPE:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_meetup,parent,false);
                v.setOnClickListener(getOnClickListener(viewType));
                return new ViewHolder(v);
            case VIEWTYPESTART:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_meetup_start,parent,false);
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

                    Intent i = new Intent(v.getContext(), TechDiveWebActivity.class);
                    i.putExtra("url", link);
                    v.getContext().startActivity(i);
                }
            };
    }

    public void setHeader (String loc, String search) {

        location = loc;
        searchterm = search;
    }


    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {


        switch (getItemViewType(position)) {
            case VIEWTYPE:
                MeetUpGroup t = groups.get(position);
                if ((t.getName() == null) || (t.isPlaceholder())) {
                    break;
                 }
                ViewHolder v = (ViewHolder) holder;
                v.name.setText(t.getName());
                v.description.setText(Html.fromHtml(t.getDescription()));
                v.link.setText(t.getLink());
                v.city.setText(t.getCity());
                v.members.setText(t.getMembers());
                break;
            case VIEWTYPESTART:
                ViewHolderStart vs = (ViewHolderStart) holder;
                vs.location.setText(location);
                vs.searchTerm.setText(searchterm);
                break;
        }
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
        return groups.size();
    }

    public void removeAllItems() {
        for (int i = 0; i < groups.size(); i++) {
            groups.remove(i);
            notifyItemRemoved(i);
            notifyItemRangeChanged(i, groups.size());
        }
    }

    public RecycleViewMeetup (List<MeetUpGroup> projects, Context con) {

        this.groups = projects;
        this.context = con;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.rv_meetup_odd_title) TextView name;
        @BindView(R.id.rv_meetup_odd_desciption) TextView description;
        @BindView(R.id.rv_meetup_odd_link) TextView link;
        @BindView(R.id.rv_meetup_odd_city) TextView city;
        @BindView(R.id.rv_meetup_odd_members) TextView members;

        public ViewHolder (View v) {
            super(v);

            ButterKnife.bind(this, v);
        }

    }

    public class ViewHolderStart extends RecyclerView.ViewHolder {

        @BindView(R.id.rv_start_location) TextView location;
        @BindView(R.id.rv_start_searchterm) TextView searchTerm;

        public ViewHolderStart (View v) {
            super(v);
            ButterKnife.bind(this, v);
        }

    }

}
