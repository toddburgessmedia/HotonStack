package com.toddburgessmedia.stackoverflowretrofit;

import android.net.Uri;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Created by Todd Burgess (todd@toddburgessmedia.com on 05/09/16.
 */
public class GitHubLink implements Serializable {

    public final String HASMORE = "next";
    public final String END = "last";
    public final String START = "first";

    private boolean hasMore = false;

    private String nextPage;
    private String lastPage;

    public GitHubLink (String linkheader) {

        if (linkheader == null) {
            hasMore = false;
            return;
        }

        StringTokenizer st = new StringTokenizer(linkheader, ",");

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

    public HashMap<String,String> getParamterHashMap() {

        Uri uri = Uri.parse(nextPage);
        Set<String> queryParameters = uri.getQueryParameterNames();
        HashMap<String, String> map = new HashMap<>();
        Iterator<String> iterator = queryParameters.iterator();

        String queryname, queryParamater;
        while (iterator.hasNext()) {
            queryname = iterator.next();
            queryParamater = uri.getQueryParameter(queryname);
            map.put(queryname, queryParamater);
        }
        return map;
    }

    public boolean hasMore() {
        return hasMore;
    }

    public String getNextLink() {
        return nextPage;
    }

}
