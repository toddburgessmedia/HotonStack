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

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Todd Burgess (todd@toddburgessmedia.com on 29/06/16.
 */
public interface MeetupAPI {

    @GET("/find/groups?&sign=true&photo-host=public&fields=plain_text_description&page=20&key=3e791918462f262f52137b404f112a1b")
    Call<List<MeetUpGroup>> getMeetupGroups (@Query("lon") String lon,
                                             @Query("lat") String lat,
                                             @Query("text") String text);

    @GET("/find/groups?&sign=true&photo-host=public&fields=plain_text_description&page=20&key=3e791918462f262f52137b404f112a1b")
    Observable<Response<List<MeetUpGroup>>> getMeetupGroupsObservable (@Query("lon") String lon,
                                                                      @Query("lat") String lat,
                                                                      @Query("text") String text);
}
