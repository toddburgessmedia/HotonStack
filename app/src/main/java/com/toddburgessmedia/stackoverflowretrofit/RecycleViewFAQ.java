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

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jsoup.Jsoup;

import java.util.List;

/**
 * Created by Todd Burgess (todd@toddburgessmedia.com on 13/06/16.
 */
public class RecycleViewFAQ extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public final int VIEWFAQODD = 1;


    private List<FAQTag> faqTAGs;

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
                String link = "";
                if (getItemViewType(viewType) == VIEWFAQODD) {
                    ViewHolderOdd vo = new ViewHolderOdd(v);
                    link = vo.link.getText().toString();
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

        int viewType = getItemViewType(position);
        FAQTag tag = faqTAGs.get(position);

        switch (viewType) {
            case VIEWFAQODD:
                ViewHolderOdd vh = (ViewHolderOdd) holder;
                vh.question.setText(Jsoup.parse(tag.title).text());
                vh.link.setText(tag.link);
                vh.score.setText(tag.getScore());
                vh.views.setText(tag.getViewCount());
                vh.answers.setText(tag.getAnswerCount());
                vh.createdate.setText(convertDate(tag.getCreationDate()));
                break;
        }

//        holder.itemView.startAnimation(animation);

    }

    private String convertDate(long utcDate) {

        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd KK:mm a");
        DateTime dt = new DateTime(utcDate*1000);

        return dt.toString(fmt);
    }

    @Override
    public int getItemViewType(int position) {

        return VIEWFAQODD;
    }

    @Override
    public int getItemCount() {
        return faqTAGs.size();
    }

    public class ViewHolderOdd extends RecyclerView.ViewHolder {

        TextView question;
        TextView link;
        TextView score;
        TextView views;
        TextView answers;
        TextView createdate;

        public ViewHolderOdd(View view) {

            super(view);
            question = (TextView) view.findViewById(R.id.faq_odd);
            link = (TextView) view.findViewById(R.id.faq_link_odd);
            score = (TextView) view.findViewById(R.id.faq_odd_score);
            views = (TextView) view.findViewById(R.id.faq_odd_views);
            answers = (TextView) view.findViewById(R.id.faq_odd_answers);
            createdate = (TextView) view.findViewById(R.id.faq_odd_date);

        }
   }

}
