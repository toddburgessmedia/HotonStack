package com.toddburgessmedia.stackoverflowretrofit.retrofit;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by tburgess on 04/06/16.
 */
public interface StackOverFlowAPI {

    @GET("/2.2/tags?order=desc&sort=popular")
    Call<StackOverFlowTags> loadquestions(@Query("pagesize") String pagesize,
                                            @Query("site") String site);

    @GET("/2.2/tags?order=desc&sort=activity")
    Call<StackOverFlowTags> loadquestionsActivity(@Query("pagesize") String pagesize,
                                          @Query("site") String site);

    @GET("/2.2/tags/{tag}/related")
    Call<StackOverFlowTags> loadSynonyms(@Path("tag") String tag, @Query("site") String site);


}
