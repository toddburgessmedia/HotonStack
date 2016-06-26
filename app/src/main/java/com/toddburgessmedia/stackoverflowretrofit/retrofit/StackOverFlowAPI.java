package com.toddburgessmedia.stackoverflowretrofit.retrofit;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by tburgess on 04/06/16.
 */
public interface StackOverFlowAPI {

//    @GET("https://api.stackexchange.com/2.2/tags?order=desc&sort=popular&site=stackoverflow")
    @GET("/2.2/tags?order=desc&sort=popular&site=stackoverflow")
    Call<StackOverFlowTags> loadquestions(@Query("pagesize") String pagesize);


}
