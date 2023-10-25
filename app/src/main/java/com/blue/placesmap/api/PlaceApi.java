package com.blue.placesmap.api;

import retrofit2.Call;

import com.blue.placesmap.model.PlaceList;

import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PlaceApi {

    @GET("/maps/api/place/findplacefromtext/json")
    Call<PlaceList> getPlaceList(@Query("language")String language,
                                 @Query("location")String location,
                                 @Query("radius")int radius,
                                 @Query("key")String key,
                                 @Query("keyword")String keyword);
}
