package com.toddburgessmedia.stackoverflowretrofit.eventbus;

/**
 * Created by Todd Burgess (todd@toddburgessmedia.com on 29/09/16.
 */

public class SelectSiteMessage {

    private int position;

    public SelectSiteMessage (int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
