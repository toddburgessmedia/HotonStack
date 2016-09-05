package com.toddburgessmedia.stackoverflowretrofit;

import java.util.StringTokenizer;

/**
 * Created by Todd Burgess (todd@toddburgessmedia.com on 05/09/16.
 */
public class GitHubLink {

    public final String HASMORE = "next";
    public final String END = "last";
    public final String START = "first";

    private boolean hasMore = false;

    private String nextPage;
    private String lastPage;

    public GitHubLink (String linkheader) throws Exception {

        StringTokenizer st = new StringTokenizer(linkheader, ",");

        if (st.countTokens() != 2) {
            throw new Exception("Something is wrong with Link header");
        }

        processNextLink(st.nextToken());

    }

    private void processNextLink(String token) {

        StringTokenizer st = new StringTokenizer(token, ";");

        nextPage = st.nextToken();
        nextPage = nextPage.substring(1, nextPage.length() - 1);

        if (st.nextToken().contains(HASMORE)) {
            hasMore = true;
        } else {
            hasMore = false;
        }
    }

    public boolean hasMore() {
        return hasMore;
    }

    public String getNextLink() {
        return nextPage;
    }

}
