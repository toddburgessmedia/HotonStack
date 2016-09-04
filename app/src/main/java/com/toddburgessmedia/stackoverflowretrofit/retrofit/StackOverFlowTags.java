package com.toddburgessmedia.stackoverflowretrofit.retrofit;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by tburgess on 04/06/16.
 */
public class StackOverFlowTags implements Serializable {

    @SerializedName("items")
    public List<Tag> tags;

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

        for (int i = 0; i < newtags.tags.size(); i++) {
            tags.add(newtags.tags.get(i));
        }
    }
}
