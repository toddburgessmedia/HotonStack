package com.toddburgessmedia.stackoverflowretrofit;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
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

    Context context;

    String sitename;

    OnLongPressListener longClickListener;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {

        View v;

        View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String tag = "";
                if (getItemViewType(viewType) == VIEWTYPE) {
                    ViewHolder vh = new ViewHolder(v);
                    tag = vh.tagname.getText().toString();
                }

                Intent i = new Intent(v.getContext(),ListQuestionsActivity.class);
                i.putExtra("name",tag);
                i.putExtra("sitename",sitename);
                v.getContext().startActivity(i);
            }
        };

        final View.OnLongClickListener longClick = new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {

                String tag = "";
                ViewHolder vh = new ViewHolder(v);
                tag = vh.tagname.getText().toString();
                longClickListener.onLongClick(v,tag);

                return true;
            }
        };

        switch (viewType) {
            case VIEWTYPE:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_tags_odd,parent,false);
                v.setOnClickListener(click);
                v.setOnLongClickListener(longClick);
                return new ViewHolder(v);
            default:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_tags_odd,parent,false);
                break;
        }
        return new ViewHolder(v);
    }


    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        Tag t = tagList.get(position);

        String r = Integer.valueOf(position+1).toString();

        switch (getItemViewType(position)) {
            case VIEWTYPE:
                ViewHolder v = (ViewHolder) holder;
                v.tagname.setText(Jsoup.parse(t.getName()).text());
                v.tagcount.setText("Tag count: " + t.getCount());
                v.tagrank.setText(r);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {

        return VIEWTYPE;

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

    public void updateAdapter (List<Tag> tags) {

        this.tagList = tags;
        notifyDataSetChanged();
    }

    public StackTagsRecyclerView (List<Tag> tags, Context con, String site, OnLongPressListener listener) {

        this.tagList = tags;
        this.context = con;
        this.sitename = site;
        this.longClickListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tagname;
        TextView tagcount;
        TextView tagrank;

        public ViewHolder (View v) {
            super(v);

            tagname = (TextView) v.findViewById(R.id.tag_name);
            tagcount = (TextView) v.findViewById(R.id.tag_count);
            tagrank = (TextView) v.findViewById(R.id.tag_rank);

        }

    }

    public interface OnLongPressListener {
        void onLongClick(View v, String tag);
    }


}
