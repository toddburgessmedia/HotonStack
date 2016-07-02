package com.toddburgessmedia.stackoverflowretrofit.retrofit;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Todd Burgess (todd@toddburgessmedia.com on 29/06/16.
 */
public class MeetUpGroup implements Serializable {

    @SerializedName("name")
    private String name;

    @SerializedName("link")
    private String link;

    @SerializedName("description")
    private String description;

    @SerializedName("city")
    private String city;

    @SerializedName("members")
    private String members;

    @SerializedName("plain_text_description")
    private String plainText;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getMembers() {
        return members;
    }

    public void setMembers(String members) {
        this.members = members;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlainText() {
        return plainText;
    }

    public void setPlainText(String plainText) {
        this.plainText = plainText;
    }
}

