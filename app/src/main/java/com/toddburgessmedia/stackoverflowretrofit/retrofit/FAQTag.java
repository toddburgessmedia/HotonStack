package com.toddburgessmedia.stackoverflowretrofit.retrofit;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by tburgess on 07/06/16.
 */
public class FAQTag implements Serializable {

    @SerializedName("title")
    public String title;

    @SerializedName("link")
    public String link;

    public String getAnswerCount() {
        return answerCount;
    }

    public void setAnswerCount(String answerCount) {
        this.answerCount = answerCount;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getViewCount() {
        return viewCount;
    }

    public void setViewCount(String viewCount) {
        this.viewCount = viewCount;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    @SerializedName("score")
    private String score;

    @SerializedName("view_count")
    private String viewCount;

    @SerializedName("answer_count")
    private String answerCount;

    @SerializedName("creation_date")
    private long creationDate;

    @Override
    public String toString() {
        return title;
    }
}
