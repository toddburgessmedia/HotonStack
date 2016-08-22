package com.toddburgessmedia.stackoverflowretrofit;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.toddburgessmedia.stackoverflowretrofit.retrofit.Tag;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Todd Burgess (todd@toddburgessmedia.com on 05/06/16.
 */
public class RecyclerViewTagsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Tag> tagList;

    private final int VIEWTYPE = 1;
    private final int VIEWTYPESTART = 0;

    Context context;

    public String getSitename() {
        return sitename;
    }

    public void setSitename(String sitename) {
        this.sitename = sitename;
    }

    String sitename;

    public void setDisplaySiteName(String displaySiteName) {
        this.displaySiteName = displaySiteName;
    }

    String displaySiteName;

    OnLongPressListener longClickListener;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {

        View v;

        switch (viewType) {
            case VIEWTYPE:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_tags,parent,false);
                v.setOnClickListener(getOnClickListener(viewType));
                v.setOnLongClickListener(getLongClickListener());
                return new ViewHolder(v);
            case VIEWTYPESTART:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_tags_start,parent,false);
                return new ViewHolderStart(v);
            default:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_tags,parent,false);
                break;
        }
        return new ViewHolder(v);
    }

    @NonNull
    private View.OnClickListener getOnClickListener(final int viewType) {
        return new View.OnClickListener() {
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
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    v.getContext().startActivity(i);
                }
            };
    }

    @NonNull
    private View.OnLongClickListener getLongClickListener() {
        return new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {

                    String tag = "";
                    ViewHolder vh = new ViewHolder(v);
                    tag = vh.tagname.getText().toString();
                    longClickListener.onLongClick(v,tag);

                    return true;
                }
            };
    }

    public void onItemDismiss(int position) {
        tagList.remove(position);
        notifyItemRemoved(position);
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        switch (getItemViewType(position)) {
            case VIEWTYPE:
                Tag t = tagList.get(position-1);
                ViewHolder v = (ViewHolder) holder;
                v.tagname.setText(t.getName());
                v.tagcount.setText("Tag count: " + t.getCount());
                break;
            case VIEWTYPESTART:
                ViewHolderStart vs = (ViewHolderStart) holder;
                vs.sitename.setText(displaySiteName);
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

    public RecyclerViewTagsAdapter(List<Tag> tags, Context con, String site, OnLongPressListener listener) {

        this.tagList = tags;
        this.context = con;
        this.sitename = site;
        this.longClickListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tag_name) TextView tagname;
        @BindView(R.id.tag_count) TextView tagcount;
        @BindView(R.id.tag_rank) TextView tagrank;

        public ViewHolder (View v) {
            super(v);

            ButterKnife.bind(this,v);
        }

    }

    public class ViewHolderStart extends RecyclerView.ViewHolder {

        @BindView(R.id.rv_tags_start_sitename) TextView sitename;

        public ViewHolderStart (View view) {

            super(view);

            ButterKnife.bind(this, view);
        }
    }

    public interface OnLongPressListener {

        public void onLongClick(View view, String tag);
    }


}
