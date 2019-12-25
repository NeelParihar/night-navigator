package com.example.nightlife.nightlife.remote;


import com.example.nightlife.nightlife.model.my_places;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface GoogleApiServices  {

    @GET
    Call<my_places> getNearbyPlaces(@Url String url);

}
