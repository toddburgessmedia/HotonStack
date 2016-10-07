package com.toddburgessmedia.stackoverflowretrofit.retrofit;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Todd Burgess (todd@toddburgessmedia.com on 07/06/16.
 */
public interface StackOverFlowFaqAPI {

        @GET("/2.2/questions?sort=votes&order=desc")
        Call<StackOverFlowFAQ> loadQuestions (@Query("tagged") String tag,
                                              @Query("site") String site,
                                              @Query("page") int page,
                                              @Query("pagesize") int pagesize);

        @GET("/2.2/questions?order=desc&sort=votes")
        Call<StackOverFlowFAQ> loadQuestionsToday (@Query("fromdate") long fromdate,
                                                   @Query("tagged") String tagged,
                                                   @Query("site") String site,
                                                   @Query("page") int page,
                                                   @Query("pagesize") int pagesize);

        @GET("/2.2/questions?sort=votes&order=desc")
        Observable<Response<StackOverFlowFAQ>> loadQuestionsObservable (@Query("tagged") String tag,
                                                             @Query("site") String site,
                                                             @Query("page") int page,
                                                             @Query("pagesize") int pagesize);

        @GET("/2.2/questions?order=desc&sort=votes")
        Observable<Response<StackOverFlowFAQ>> loadQuestionsByDate (@Query("fromdate") long fromdate,
                                                   @Query("tagged") String tagged,
                                                   @Query("site") String site,
                                                   @Query("page") int page,
                                                   @Query("pagesize") int pagesize);

}
