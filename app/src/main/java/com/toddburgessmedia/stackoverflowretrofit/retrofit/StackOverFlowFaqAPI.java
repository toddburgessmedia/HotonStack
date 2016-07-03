package com.toddburgessmedia.stackoverflowretrofit.retrofit;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Todd Burgess (todd@toddburgessmedia.com on 07/06/16.
 */
public interface StackOverFlowFaqAPI {

        @GET("/2.2/tags/{tag}/faq?pagesize=100")
        Call<StackOverFlowFAQ> loadQuestions (@Path("tag") String tag, @Query("site") String site);

        @GET("/2.2/questions?order=desc&sort=activity")
        Call<StackOverFlowFAQ> loadQuestionsToday (@Query("fromdate") long fromdate,
                                                   @Query("tagged") String tagged,
                                                   @Query("site") String site);

        @GET("/2.2/questions?order=desc&sort=activity")
        Call<StackOverFlowFAQ> loadQuestionsYesterday (@Query("fromdate") long fromdate,
                                                   @Query("tagged") String tagged,
                                                       @Query("site") String site);



}
