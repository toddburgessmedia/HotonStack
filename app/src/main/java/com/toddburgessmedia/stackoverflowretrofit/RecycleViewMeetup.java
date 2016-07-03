package com.toddburgessmedia.stackoverflowretrofit;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.toddburgessmedia.stackoverflowretrofit.retrofit.MeetUpGroup;

import java.util.List;

/**
 * Created by Todd Burgess (todd@toddburgessmedia.com on 05/06/16.
 */
public class RecycleViewMeetup extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<MeetUpGroup> groups;

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

        Log.d(MainActivity.TAG, "onCreateViewHolder: making viewholders");

        switch (viewType) {
            case VIEWTYPE:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_meetup_odd,parent,false);
                v.setOnClickListener(click);
                return new ViewHolder(v);
            case VIEWTYPE_EVEN:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_meetup_even,parent,false);
                v.setOnClickListener(click);
                return new ViewHolderEven(v);
        }
        return new ViewHolder(v);
    }


    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        Log.d(MainActivity.TAG, "onBindViewHolder: hello");

        MeetUpGroup t = groups.get(position);

//        Animation animation = AnimationUtils.loadAnimation(context,
//                (position > lastPosition) ? R.anim.up_from_bottom
//                        : R.anim.down_from_top);
//        holder.itemView.startAnimation(animation);
//        lastPosition = position;

        switch (getItemViewType(position)) {
            case VIEWTYPE:
                ViewHolder v = (ViewHolder) holder;
                v.name.setText(t.getName());
                v.description.setText(Html.fromHtml(t.getDescription()));
                v.link.setText(t.getLink());
                v.city.setText(t.getCity());
                v.members.setText(t.getMembers());
                break;
            case VIEWTYPE_EVEN:
                ViewHolderEven ve = (ViewHolderEven) holder;
                ve.name.setText(t.getName());
                ve.description.setText(Html.fromHtml(t.getDescription()));
                ve.link.setText(t.getLink());
                ve.city.setText(t.getCity());
                ve.members.setText(t.getMembers());
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if ((position %2) == 0) {
            return VIEWTYPE_EVEN;
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

        Log.d(MainActivity.TAG, "RecycleViewMeetup: starting up " + projects.toString());
        this.groups = projects;
        this.context = con;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name;
        TextView description;
        TextView link;
        TextView city;
        TextView members;

        public ViewHolder (View v) {
            super(v);

            name = (TextView) v.findViewById(R.id.rv_meetup_odd_title);
            description = (TextView) v.findViewById(R.id.rv_meetup_odd_desciption);
            link = (TextView) v.findViewById(R.id.rv_meetup_odd_link);
            city = (TextView) v.findViewById(R.id.rv_meetup_odd_city);
            members = (TextView) v.findViewById(R.id.rv_meetup_odd_members);
        }
        @Override
        public void onClick(View v) {

        }
    }

    public class ViewHolderEven extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name;
        TextView description;
        TextView link;
        TextView city;
        TextView members;


        public ViewHolderEven (View v) {
            super(v);

            name = (TextView) v.findViewById(R.id.rv_meetup_even_title);
            description = (TextView) v.findViewById(R.id.rv_meetup_even_desciption);
            link = (TextView) v.findViewById(R.id.rv_meetup_even_link);
            city = (TextView) v.findViewById(R.id.rv_meetup_even_city);
            members = (TextView) v.findViewById(R.id.rv_meetup_even_members);
        }

        @Override
        public void onClick(View v) {


        }

    }


}
