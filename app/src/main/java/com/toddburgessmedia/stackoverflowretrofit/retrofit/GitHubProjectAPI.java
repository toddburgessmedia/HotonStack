package com.toddburgessmedia.stackoverflowretrofit.retrofit;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Todd Burgess (todd@toddburgessmedia.com on 24/06/16.
 */
public interface GitHubProjectAPI {

    @GET("/search/repositories?sort=stars&order=desc")
    Call<GitHubProjectCollection> getProjects (@Query("q") String language);
}
