package com.toddburgessmedia.stackoverflowretrofit.retrofit;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by tburgess on 04/06/16.
 */
public class StackOverFlowTags implements Serializable {

    @SerializedName("items")
    public List<Tag> tags;

    private int rankCount = 0;

    public boolean isHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    @SerializedName("has_more")
    private boolean hasMore;

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (Tag t : tags) {
            sb.append(t.toString() + " ");
        }
        return sb.toString();
    }

    public Tag getTag (int position) {
        return tags.get(position);
    }

    public void mergeTags(StackOverFlowTags newtags) {

        Tag t;
        for (int i = 0; i < newtags.tags.size(); i++) {
            t = newtags.tags.get(i);
            if (!t.isPlaceholder()) {
                rankCount++;
                t.setRank(rankCount);
            }
            tags.add(t);
        }
    }

    public void insertPlaceHolders() {

        Tag holder = new Tag();
        holder.setPlaceholder(true);

        tags.add(0, holder);
        tags.add(tags.size(), holder);
    }

    public void insertLastPlaceHolder() {

        Tag holder = new Tag();
        holder.setPlaceholder(true);

        tags.add(tags.size(), holder);
    }

    public void insertFirstPlaceHolder() {

        Tag holder = new Tag();
        holder.setPlaceholder(true);

        tags.add(0,holder);

    }

    public void rankTags() {

        int start = rankCount;

        for (int i = start; i < tags.size(); i++) {
            if (!tags.get(i).isPlaceholder()) {
                rankCount++;
                tags.get(i).setRank(rankCount);
            }
        }

    }

    public void sortTagNames() {

        Collections.sort(tags, new Comparator<Tag>() {
            @Override
            public int compare(Tag tag, Tag t1) {
                return tag.getName().compareTo(t1.getName());
            }
        });

    }

    public void sortTagRank() {

        Collections.sort(tags, new Comparator<Tag>() {
            @Override
            public int compare(Tag tag, Tag t1) {
                return Integer.compare(tag.getRank(),t1.getRank());
            }
        });
    }
}
