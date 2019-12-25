package com.example.nightlife.nightlife;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface ApiInterface {
    @GET
        //Call<PlacesPOJO.Root> doPlaces(@Query(value = "location", encoded = true) String location,@Query(value = "radius", encoded = true) String radius,@Query(value = "type", encoded = true) String type,@Query(value = "sensor", encoded = true) String sensor,@Query(value = "key", encoded = true) String key);
    Call<PlacesPOJO.Root>doplaces(@Url String url);
    @GET
    Call<PlacesPOJO.Root1>phone(@Url String url);

    @GET() // origins/destinations:  LatLng as string
    Call<ResultDistanceMatrix.Root> getDistance(@Url String url);
}
