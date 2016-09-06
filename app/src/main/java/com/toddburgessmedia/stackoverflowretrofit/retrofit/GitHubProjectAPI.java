package com.toddburgessmedia.stackoverflowretrofit.retrofit;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by Todd Burgess (todd@toddburgessmedia.com on 24/06/16.
 */
public interface GitHubProjectAPI {

    @GET("/search/repositories?sort=stars&order=desc")
    Call<GitHubProjectCollection> getProjects (@Query("q") String language);

    @GET("/search/repositories")
    Call<GitHubProjectCollection> getProjectsNextPage(@QueryMap Map<String, String> queryMap);
}
