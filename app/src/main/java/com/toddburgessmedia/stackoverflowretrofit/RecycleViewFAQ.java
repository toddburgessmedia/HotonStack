package com.toddburgessmedia.stackoverflowretrofit;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.toddburgessmedia.stackoverflowretrofit.retrofit.FAQTag;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Todd Burgess (todd@toddburgessmedia.com on 13/06/16.
 */
public class RecycleViewFAQ extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public final int VIEWFAQ = 1;


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
                if (getItemViewType(viewType) == VIEWFAQ) {
                    ViewHolderOdd vo = new ViewHolderOdd(v);
                    link = vo.link.getText().toString();
                }
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                v.getContext().startActivity(i);
            }
        };

        switch (viewType) {
            case VIEWFAQ:
               v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_faq,parent,false);
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
            case VIEWFAQ:
                ViewHolderOdd vh = (ViewHolderOdd) holder;
                vh.question.setText(Html.fromHtml(tag.title));
                vh.link.setText(tag.link);
                vh.score.setText(tag.getScore());
                vh.views.setText(tag.getViewCount());
                vh.answers.setText(tag.getAnswerCount());
                vh.createdate.setText(convertDate(tag.getCreationDate()));
                break;
        }
    }

    private String convertDate(long utcDate) {

        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd KK:mm a");
        DateTime dt = new DateTime(utcDate*1000);

        return dt.toString(fmt);
    }

    @Override
    public int getItemViewType(int position) {

        return VIEWFAQ;
    }

    @Override
    public int getItemCount() {
        return faqTAGs.size();
    }

    public class ViewHolderOdd extends RecyclerView.ViewHolder {

        @BindView(R.id.faq_odd) TextView question;
        @BindView(R.id.faq_link) TextView link;
        @BindView(R.id.faq_score) TextView score;
        @BindView(R.id.faq_odd_views) TextView views;
        @BindView(R.id.faq_answers) TextView answers;
        @BindView(R.id.faq_date) TextView createdate;

        public ViewHolderOdd(View view) {

            super(view);

            ButterKnife.bind(this,view);
        }
   }

}
