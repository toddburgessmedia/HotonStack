package com.toddburgessmedia.stackoverflowretrofit.retrofit;

import com.google.gson.annotations.SerializedName;

/**
 * Created by tburgess on 07/06/16.
 */
public class FAQTag {

    @SerializedName("title")
    public String title;

    @SerializedName("link")
    public String link;

    @Override
    public String toString() {
        return title;
    }
}
