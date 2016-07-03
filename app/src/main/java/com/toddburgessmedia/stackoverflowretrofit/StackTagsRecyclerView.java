package com.toddburgessmedia.stackoverflowretrofit;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.toddburgessmedia.stackoverflowretrofit.retrofit.Tag;

import org.jsoup.Jsoup;

import java.util.List;

/**
 * Created by Todd Burgess (todd@toddburgessmedia.com on 05/06/16.
 */
public class StackTagsRecyclerView extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Tag> tagList;

    private final int VIEWTYPE = 1;
    private final int VIEWTYPE_EVEN = 0;

    int lastPosition = -1;
    Context context;

    String sitename;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {

        View v;

        View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String tag;
                if (getItemViewType(viewType) == VIEWTYPE) {
                    ViewHolder vh = new ViewHolder(v);
                    tag = vh.tagname.getText().toString();
                } else {
                    ViewHolderEven vh = new ViewHolderEven(v);
                    tag = vh.tagname.getText().toString();
                }

                Intent i = new Intent(v.getContext(),ListQuestionsActivity.class);
                i.putExtra("name",tag);
                i.putExtra("sitename",sitename);
                v.getContext().startActivity(i);
            }
        };

        View.OnLongClickListener longClickListener = new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                Log.d(MainActivity.TAG, "onLongClick: long click!");
                return false;
            }
        };

        switch (viewType) {
            case VIEWTYPE:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_tags_odd,parent,false);
                v.setOnClickListener(click);
                v.setOnLongClickListener(longClickListener);
                return new ViewHolder(v);
            case VIEWTYPE_EVEN:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_tags_even,parent,false);
                v.setOnClickListener(click);
                v.setOnLongClickListener(longClickListener);
                return new ViewHolderEven(v);
            default:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_tags_odd,parent,false);
                break;
        }
        return new ViewHolder(v);
    }


    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        int viewType;
        if (position == 0) {
            viewType = VIEWTYPE_EVEN;
        } else {
            viewType = position % 2;
        }

        Tag t = tagList.get(position);

        String r = Integer.valueOf(position+1).toString();

        switch (viewType) {
            case VIEWTYPE:
                ViewHolder v = (ViewHolder) holder;
                v.tagname.setText(Jsoup.parse(t.getName()).text());
                v.tagcount.setText("Tag count: " + t.getCount());
                v.tagrank.setText(r);
                break;
            case VIEWTYPE_EVEN:
                ViewHolderEven ve = (ViewHolderEven) holder;
                ve.tagname.setText(Jsoup.parse(t.getName()).text());
                ve.tagcount.setText("Tag count: " + t.getCount());
                ve.tagrank.setText(r);
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
        return tagList.size();
    }

    public void removeAllItems() {
        for (int i = 0; i < tagList.size(); i++) {
            tagList.remove(i);
            notifyItemRemoved(i);
            notifyItemRangeChanged(i,tagList.size());
        }
    }

    public StackTagsRecyclerView (List<Tag> tags, Context con, String site) {

        this.tagList = tags;
        this.context = con;
        this.sitename = site;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tagname;
        TextView tagcount;
        TextView tagrank;

        public ViewHolder (View v) {
            super(v);

            tagname = (TextView) v.findViewById(R.id.tag_name);
            tagcount = (TextView) v.findViewById(R.id.tag_count);
            tagrank = (TextView) v.findViewById(R.id.tag_rank);

        }

        @Override
        public void onClick(View v) {

            Intent i = new Intent(v.getContext(),ListQuestionsActivity.class);
            v.getContext().startActivity(i);

        }
    }

    public class ViewHolderEven extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tagname;
        TextView tagcount;
        TextView tagrank;

        public ViewHolderEven (View v) {
            super(v);

            tagname = (TextView) v.findViewById(R.id.tag_name_even);
            tagcount = (TextView) v.findViewById(R.id.tag_count_even);
            tagrank = (TextView) v.findViewById(R.id.tag_rank_even);
        }

        @Override
        public void onClick(View v) {

            Intent i = new Intent(v.getContext(),ListQuestionsActivity.class);
            v.getContext().startActivity(i);
        }
    }


}
