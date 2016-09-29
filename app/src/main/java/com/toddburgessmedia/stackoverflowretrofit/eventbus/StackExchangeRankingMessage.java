package com.toddburgessmedia.stackoverflowretrofit.eventbus;

/**
 * Created by Todd Burgess (todd@toddburgessmedia.com on 29/09/16.
 */

public class StackExchangeRankingMessage {

    private int position;

    public StackExchangeRankingMessage (int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
