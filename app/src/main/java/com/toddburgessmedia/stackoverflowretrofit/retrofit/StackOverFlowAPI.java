package com.toddburgessmedia.stackoverflowretrofit.retrofit;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by tburgess on 04/06/16.
 */
public interface StackOverFlowAPI {

//    @GET("https://api.stackexchange.com/2.2/tags?order=desc&sort=popular&site=serverfault")
    @GET("/2.2/tags?order=desc&sort=popular")
    Call<StackOverFlowTags> loadquestions(@Query("pagesize") String pagesize,
                                            @Query("site") String site);


}
