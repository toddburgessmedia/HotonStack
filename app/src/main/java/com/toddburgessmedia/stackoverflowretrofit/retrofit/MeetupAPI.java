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
