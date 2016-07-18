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
}
