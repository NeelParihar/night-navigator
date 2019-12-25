package com.example.nightlife.nightlife;

import com.example.nightlife.nightlife.remote.GoogleApiServices;
import com.example.nightlife.nightlife.remote.retrofitclient;

public class common {
    private static final String GOOGLE_API_URL="https://maps.googleapis.com/";
    public static GoogleApiServices getGoogleAPIService()
    {
        return retrofitclient.getClient(GOOGLE_API_URL).create(GoogleApiServices.class);
    }
}
