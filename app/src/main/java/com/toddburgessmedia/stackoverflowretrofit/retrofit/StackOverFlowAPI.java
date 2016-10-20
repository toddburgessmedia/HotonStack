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

import rx.Observable;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by tburgess on 04/06/16.
 */
public interface StackOverFlowAPI {

    @GET("/2.2/tags?order=desc&sort=popular")
    Call<StackOverFlowTags> loadquestions(@Query("pagesize") int pagesize,
                                            @Query("site") String site,
                                            @Query("page") int page);

    @GET("/2.2/tags?order=desc&sort=activity")
    Call<StackOverFlowTags> loadquestionsActivity(@Query("pagesize") int pagesize,
                                          @Query("site") String site,
                                          @Query("page") int page);

    @GET("/2.2/tags?order=desc")
    Call<StackOverFlowTags> loadsquestionsByDate(@Query("pagesize") int pagesize,
                                            @Query("sort") String sort,
                                            @Query("site") String site,
                                            @Query("page") int page,
                                            @Query("fromdate") long fromDate);

    @GET("/2.2/tags/{tag}/related")
    Call<StackOverFlowTags> loadSynonyms(@Path("tag") String tag, @Query("site") String site);

    @GET("/2.2/tags?order=desc&sort=popular")
    Observable<Response<StackOverFlowTags>> loadquestionsObservable(@Query("pagesize") int pagesize,
                                                         @Query("site") String site,
                                                         @Query("page") int page);

    @GET("/2.2/tags?order=desc&sort=activity")
    Observable<Response<StackOverFlowTags>> loadquestionsActivityObservable(@Query("pagesize") int pagesize,
                                                  @Query("site") String site,
                                                  @Query("page") int page);

    @GET("/2.2/tags?order=desc")
    Observable<Response<StackOverFlowTags>> loadsquestionsByDateObservable(@Query("pagesize") int pagesize,
                                                 @Query("sort") String sort,
                                                 @Query("site") String site,
                                                 @Query("page") int page,
                                                 @Query("fromdate") long fromDate);

    @GET("/2.2/tags/{tag}/related")
    Observable<Response<StackOverFlowTags>> loadSynonymsObservable(@Path("tag") String tag,
                                                                   @Query("site") String site);

}
