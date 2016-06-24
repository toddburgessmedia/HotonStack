package com.toddburgessmedia.stackoverflowretrofit;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
                i.putExtra("title",tag);
                v.getContext().startActivity(i);
            }
        };

        switch (viewType) {
            case VIEWTYPE:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview,parent,false);
                v.setOnClickListener(click);
                return new ViewHolder(v);
            case VIEWTYPE_EVEN:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_even,parent,false);
                v.setOnClickListener(click);
                return new ViewHolderEven(v);
            default:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview,parent,false);
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
//                Log.d(MainActivity.TAG, "onBindViewHolder: " + position);
        Tag t = tagList.get(position);

        String r = Integer.valueOf(position+1).toString();

        Animation animation = AnimationUtils.loadAnimation(context,
                (position > lastPosition) ? R.anim.up_from_bottom
                        : R.anim.down_from_top);
        holder.itemView.startAnimation(animation);
        lastPosition = position;

        switch (viewType) {
            case VIEWTYPE:
                //Log.d(MainActivity.TAG, "onBindViewHolder: in VIEWTYPE " + viewType);
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

    public StackTagsRecyclerView (List<Tag> tags, Context con) {

        this.tagList = tags;
        this.context = con;
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
