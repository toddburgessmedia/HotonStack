/*
 * Copyright 2016 Todd Burgess Media
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.toddburgessmedia.stackoverflowretrofit.retrofit;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;
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

        @GET("/2.2/tags/{tag}/faq")
        Observable<Response<StackOverFlowFAQ>> loadFAQ (@Path("tag") String tag,
                                                        @Query("page") int page,
                                                        @Query("pagesize") int pagesize,
                                                        @Query("site") String site);

}
