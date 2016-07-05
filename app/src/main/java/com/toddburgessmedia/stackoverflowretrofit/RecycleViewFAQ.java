package com.toddburgessmedia.stackoverflowretrofit;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.toddburgessmedia.stackoverflowretrofit.retrofit.FAQTag;

import org.jsoup.Jsoup;

import java.util.List;

/**
 * Created by Todd Burgess (todd@toddburgessmedia.com on 13/06/16.
 */
public class RecycleViewFAQ extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public final int VIEWFAQODD = 1;
    public final int VIEWFAQEVEN = 0;

    private List<FAQTag> faqTAGs;
    protected int lastPosition = -1;
    protected Context context;

    public RecycleViewFAQ (List<FAQTag> tags, Context con) {

        this.faqTAGs = tags;
        this.context = con;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {

        View v;

        View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String link;
                if (getItemViewType(viewType) == VIEWFAQODD) {
                    ViewHolderOdd vo = new ViewHolderOdd(v);
                    link = vo.link.getText().toString();
                } else {
                    ViewHolderEven ve = new ViewHolderEven(v);
                    link = ve.link.getText().toString();
                }

                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                v.getContext().startActivity(i);
            }
        };

        switch (viewType) {
            case VIEWFAQODD:
               v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_faq_odd,parent,false);
                v.setOnClickListener(click);
                return new ViewHolderOdd(v);
            case VIEWFAQEVEN:
                v = LayoutInflater.from((parent.getContext())).inflate(R.layout.recycle_faq_even,parent,false);
                v.setOnClickListener(click);
                return new ViewHolderEven(v);
        }
        return null;
    }

    public void removeAllItems() {
        for (int i = 0; i < faqTAGs.size(); i++) {
            faqTAGs.remove(i);
            notifyItemRemoved(i);
            notifyItemRangeChanged(i,faqTAGs.size());
        }
    }

    public void updateAdapter (List<FAQTag> tags) {

        this.faqTAGs = tags;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        //int holdposition = holder.getAdapterPosition();
        int viewType = getItemViewType(position);
        FAQTag tag = faqTAGs.get(position);

//        Animation animation = AnimationUtils.loadAnimation(context,
//                (position > lastPosition) ? R.anim.up_from_bottom
//                        : R.anim.down_from_top);
//        holder.itemView.startAnimation(animation);
        lastPosition = position;

        switch (viewType) {
            case VIEWFAQODD:
                ViewHolderOdd vh = (ViewHolderOdd) holder;
                vh.question.setText(Jsoup.parse(tag.title).text());
                vh.link.setText(tag.link);
                vh.score.setText(tag.getScore());
                vh.views.setText(tag.getViewCount());
                vh.answers.setText(tag.getAnswerCount());
                break;
            case VIEWFAQEVEN:
                ViewHolderEven vhe = (ViewHolderEven) holder;
                vhe.question.setText(Jsoup.parse(tag.title).text());
                vhe.link.setText(tag.link);
                vhe.score.setText(tag.getScore());
                vhe.views.setText(tag.getViewCount());
                vhe.answers.setText(tag.getAnswerCount());
                break;
        }

//        holder.itemView.startAnimation(animation);

    }

    @Override
    public int getItemViewType(int position) {
        if ((position % 2 ) == 0) {
            return VIEWFAQEVEN;
        } else {
            return VIEWFAQODD;
        }
    }

    @Override
    public int getItemCount() {
        return faqTAGs.size();
    }

    public class ViewHolderOdd extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView question;
        TextView link;
        TextView score;
        TextView views;
        TextView answers;

        public ViewHolderOdd(View view) {

            super(view);
            question = (TextView) view.findViewById(R.id.faq_odd);
            link = (TextView) view.findViewById(R.id.faq_link_odd);
            score = (TextView) view.findViewById(R.id.faq_odd_score);
            views = (TextView) view.findViewById(R.id.faq_odd_views);
            answers = (TextView) view.findViewById(R.id.faq_odd_answers);

        }

        @Override
        public void onClick(View v) {

        }
    }

    public class ViewHolderEven extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView question;
        TextView link;
        TextView score;
        TextView views;
        TextView answers;


        public ViewHolderEven(View view) {

            super(view);
            question = (TextView) view.findViewById(R.id.faq_even);
            link = (TextView) view.findViewById(R.id.faq_link_even);
            score = (TextView) view.findViewById(R.id.faq_even_score);
            views = (TextView) view.findViewById(R.id.faq_even_views);
            answers = (TextView) view.findViewById(R.id.faq_even_answers);

        }

        @Override
        public void onClick(View v) {

        }
    }


}
