package com.toddburgessmedia.stackoverflowretrofit.retrofit;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Todd Burgess (todd@toddburgessmedia.com on 07/06/16.
 */
public class StackOverFlowFAQ implements Serializable {

    @SerializedName("items")
    public List<FAQTag> faq;

    @SerializedName("has_more")
    boolean hasmore;

    @Override
    public String toString() {

        StringBuffer sb = new StringBuffer();
        for (FAQTag f: faq) {
            sb.append(f.toString() + "\n");
        }

        return sb.toString();
    }

    public boolean isEmpty() {
        if (faq.size() == 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isHasmore() {
        return hasmore;
    }

    public void setHasmore(boolean hasmore) {
        this.hasmore = hasmore;
    }

    public void mergeTags (StackOverFlowFAQ newtags) {

        for (int i = 0; i < newtags.faq.size(); i++) {
            faq.add(newtags.faq.get(i));
        }
    }

    public void insertPlaceHolders() {

        FAQTag tag = new FAQTag();
        tag.setPlaceholder(true);

        faq.add(0,tag);
        faq.add(faq.size(), tag);

    }

    public void insertLastPlaceHolder() {

        FAQTag tag = new FAQTag();
        tag.setPlaceholder(true);

        faq.add(faq.size(), tag);
    }
}
