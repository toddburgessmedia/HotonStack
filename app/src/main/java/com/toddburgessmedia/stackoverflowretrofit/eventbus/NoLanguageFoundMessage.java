package com.toddburgessmedia.stackoverflowretrofit.eventbus;

/**
 * Created by Todd Burgess (todd@toddburgessmedia.com on 30/09/16.
 */

public class NoLanguageFoundMessage {

    private boolean topicsearch;

    public NoLanguageFoundMessage (boolean topicsearch) {

        this.topicsearch = topicsearch;
    }

    public boolean isTopicsearch() {
        return topicsearch;
    }

    public void setTopicsearch(boolean topicsearch) {
        this.topicsearch = topicsearch;
    }
}
