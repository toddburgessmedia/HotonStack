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

import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by Todd Burgess (todd@toddburgessmedia.com on 24/06/16.
 */
public interface GitHubProjectAPI {

    @GET("/search/repositories?sort=stars&order=desc")
    Call<GitHubProjectCollection> getProjects (@Query("q") String language);

    @GET("/search/repositories")
    Call<GitHubProjectCollection> getProjectsNextPage(@QueryMap Map<String, String> queryMap);

    @GET("/search/repositories?sort=stars&order=desc")
    Observable<Response<GitHubProjectCollection>> getProjectsObservable (@Query("q") String language);

    @GET("/search/repositories")
    Observable<Response<GitHubProjectCollection>> getProjectsNextPageObservable (@QueryMap Map<String, String> queryMap);
}
