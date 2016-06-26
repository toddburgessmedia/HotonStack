package com.toddburgessmedia.stackoverflowretrofit.retrofit;

import com.google.gson.annotations.SerializedName;

import org.jsoup.Jsoup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tburgess on 07/06/16.
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

    public String[] makeStringArray() {
        List<String> questions = new ArrayList<>();
        for (FAQTag f : faq) {
            questions.add(Jsoup.parse(f.title).text());
        }

        String[] q = new String[questions.size()];
        questions.toArray(q);
        return q;
    }

    public boolean isEmpty() {
        if (faq.size() == 0) {
            return true;
        } else {
            return false;
        }
    }
}
