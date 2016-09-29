package com.toddburgessmedia.stackoverflowretrofit.eventbus;

/**
 * Created by Todd Burgess (todd@toddburgessmedia.com on 29/09/16.
 */

public class SearchDialogMessage {

    private String search;
    private boolean negativeClick;

    public SearchDialogMessage(String search) {
        this.search = search;
        negativeClick = false;
    }

    public SearchDialogMessage(boolean negativeClick) {
        this.negativeClick = negativeClick;
    }

    public boolean isNegativeClick() {
        return negativeClick;
    }

    public void setNegativeClick(boolean negativeClick) {
        this.negativeClick = negativeClick;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
