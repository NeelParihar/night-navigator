package com.example.nightlife.nightlife;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.NetworkOnMainThreadException;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.example.nightlife.nightlife.model.Results;
import com.example.nightlife.nightlife.model.my_places;
import com.example.nightlife.nightlife.remote.GoogleApiServices;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.nightlife.nightlife.MainActivity.extra_permission;


public class recyclerview extends AppCompatActivity  {
    private static final String URL_PRODUCTS = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362&radius=1500&type=restaurant&keyword=cruise&key=AIzaSyBlbRC-4r5x73rTsTMlAH9PJ8eptz3nwU4";

    //a list to store all the products
    List<Results> productList;

    //the recyclerview
    RecyclerView recyclerView;




    private GoogleApiServices mService;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private double latitude;
    private double longitude;
    protected PlaceDetectionClient placeDetectionClient;
    protected GeoDataClient geoDataClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclerview);

        //getting the recyclerview from xml
        recyclerView = findViewById(R.id.recylcerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mService = common.getGoogleAPIService();
        //initializing the productlist
        productList = new ArrayList<>();

        //this method will fetch and parse json
        //to display it in recyclerview
       // placeDetectionClient = Places.getPlaceDetectionClient(this, null);
        getDeviceLocation();
    }

    private  void loadProducts(double latitude, double longitude) throws NetworkOnMainThreadException, IOException {
        String url =geturl(latitude,longitude);
        mService.getNearbyPlaces(url)
                .enqueue(new Callback<my_places>() {
                    @Override
                    public void onResponse(@NonNull Call<my_places> call, @NonNull Response<my_places> response) {
                        if (response.isSuccessful()&&response.body().getResults() != null) {
                            for (int i = 0; i < response.body().getResults().length; i++) {

                                Results googlePlace = response.body().getResults()[i];
                                productList.add(googlePlace);

                                Log.d("hey", "onComplete: found products!");


                            }
                            placeadapter adapter = new placeadapter(recyclerview.this, productList);
                            recyclerView.setAdapter(adapter);


                        }
                        else {
                            Log.d("hey", "onComplete: not found products!");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<my_places> call, @NonNull Throwable t) {
                        Log.d("onfail", "onComplete: not found products!");

                    }
                });
       /*@SuppressLint("MissingPermission")
        Task<PlaceLikelihoodBufferResponse> placeResult = placeDetectionClient.getCurrentPlace(null);
        placeResult.addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                Log.d("heeynew", "current location places info");
                List<Place> placesList = new ArrayList<Place>();
                PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();
                for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                    placesList.add(placeLikelihood.getPlace().freeze());
                }
                likelyPlaces.release();

                placeadapter recyclerViewAdapter = new placeadapter( recyclerview.this,placesList);
                recyclerView.setAdapter(recyclerViewAdapter);
            }
        });*/







    }

    private String geturl(double latitude, double longitude) {
        StringBuilder googleplacesurl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googleplacesurl.append("location="+latitude+","+longitude);
        googleplacesurl.append("&radius=" + 2000);
        googleplacesurl.append("&type="+"restaurant");
        googleplacesurl.append("&sensor=true");
        googleplacesurl.append("&key=AIzaSyD6QsXNKo6JKxMwT1oIIck-1iXa_2qTr0M");
        Log.d("geturl", googleplacesurl.toString());
        return googleplacesurl.toString();
    }

    private void getDeviceLocation() {

        FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);



        try{


                Task location = mFusedLocationProviderClient.getLastLocation();
            final Task task = location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Log.d("hey", "onComplete: found location!");
                        Location currentLocation = (Location) task.getResult();
                        try {
                            loadProducts(currentLocation.getLatitude(), currentLocation.getLongitude());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(recyclerview.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        Log.e("execption location", "getDeviceLocation: SecurityException: ");
                    }
                }
            });
        }catch (SecurityException e){
            Log.d("execption location", "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
    }




   /*private synchronized void buildGoogleApiClient() {
        mGoogleApiClient=new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest =new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest, (LocationListener) this);
        }


    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override

    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }*/
}





