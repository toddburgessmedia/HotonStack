package com.toddburgessmedia.stackoverflowretrofit;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.toddburgessmedia.stackoverflowretrofit.retrofit.FAQTag;

import org.greenrobot.eventbus.EventBus;
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
    public final int VIEWSTART = 0;
    public final int VIEWHASMORE = 2;

    String sitename;
    String timeframe;


    boolean hasmore = false;

    public void setSitename(String sitename) {
        this.sitename = sitename;
    }

    public void setTimeframe(String timeframe) {
        this.timeframe = timeframe;
    }

    public void setHasmore(boolean hasmore) {
        this.hasmore = hasmore;
    }

    private List<FAQTag> faqTAGs;

    protected Context context;

    public RecycleViewFAQ (List<FAQTag> tags, Context con) {

        this.faqTAGs = tags;
        this.context = con;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {

        View v;

        switch (viewType) {
            case VIEWFAQ:
               v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_faq,parent,false);
                v.setOnClickListener(getOnClickListener(viewType));
                return new ViewHolderOdd(v);
            case VIEWSTART:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_faq_start, parent, false);
                return new ViewHolderStart(v);
            case VIEWHASMORE:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_faq_hasmore, parent, false);
                v.setOnClickListener(getHasMoreClickListener(viewType));
                return new ViewHolderHasMore(v);
        }
        return null;
    }

    @NonNull
    private View.OnClickListener getOnClickListener(final int viewType) {
        return new View.OnClickListener() {
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
    }

    @NonNull View.OnClickListener getHasMoreClickListener(final int viewType) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new RecycleViewFAQMessage());
            }
        };
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

    public void onItemDismiss(int position) {
        faqTAGs.remove(position);
        notifyItemRemoved(position);
    }

    public void addItems (List<FAQTag> tags) {

        int position = faqTAGs.size();
        onItemDismiss(position-1);
        position--;

        for (int i = 0; i < tags.size(); i++) {
            faqTAGs.add(tags.get(i));
            position++;
            notifyItemInserted(position);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        int viewType = getItemViewType(position);

        switch (viewType) {
            case VIEWFAQ:
                FAQTag tag = faqTAGs.get(position);
                if ((tag.title == null) || (tag.isPlaceholder())) {
                    break;
                }
                ViewHolderOdd vh = (ViewHolderOdd) holder;
                vh.question.setText(Html.fromHtml(tag.title));
                vh.link.setText(tag.link);
                vh.score.setText(tag.getScore());
                vh.views.setText(tag.getViewCount());
                vh.answers.setText(tag.getAnswerCount());
                vh.createdate.setText(convertDate(tag.getCreationDate()));
                break;
            case VIEWSTART:
                ViewHolderStart vs = (ViewHolderStart) holder;
                vs.sitename.setText(sitename);
                vs.timeframe.setText(timeframe);
        }
    }

    private String convertDate(long utcDate) {

        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd KK:mm a");
        DateTime dt = new DateTime(utcDate*1000);

        return dt.toString(fmt);
    }

    @Override
    public int getItemViewType(int position) {

        if (position == 0) {
            return VIEWSTART;
        } else if ((position == faqTAGs.size()-1) && (hasmore)) {
            return VIEWHASMORE;
        } else {
            return VIEWFAQ;
        }
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

    public class ViewHolderStart extends RecyclerView.ViewHolder {

        @BindView(R.id.questions_start_sitename) TextView sitename;
        @BindView(R.id.questions_start_timeframe) TextView timeframe;

        public ViewHolderStart(View view) {
            super(view);

            ButterKnife.bind(this, view);
        }

    }

    public class ViewHolderHasMore extends RecyclerView.ViewHolder {

        @BindView(R.id.rv_faq_hasmore_title) TextView hasmore;

        public ViewHolderHasMore (View view) {
            super(view);

            ButterKnife.bind(this, view);
        }
    }

    public class RecycleViewFAQMessage {

        public RecycleViewFAQMessage() {

        }
    }
}
