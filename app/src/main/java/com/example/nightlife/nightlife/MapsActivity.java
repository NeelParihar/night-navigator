package com.example.nightlife.nightlife;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nightlife.nightlife.model.Opening_hours;
import com.example.nightlife.nightlife.model.Results;
import com.example.nightlife.nightlife.model.my_places;
import com.example.nightlife.nightlife.remote.GoogleApiServices;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.uber.sdk.android.core.UberButton;
import com.uber.sdk.android.rides.RideParameters;
import com.uber.sdk.android.rides.RideRequestButton;
import com.uber.sdk.android.rides.RideRequestButtonCallback;
import com.uber.sdk.rides.client.ServerTokenSession;
import com.uber.sdk.rides.client.SessionConfiguration;
import com.uber.sdk.rides.client.error.ApiError;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import java.io.IOException;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks ,
        GoogleApiClient.OnConnectionFailedListener,LocationListener,Serializable {


    private static final int MY_PERMISSION_CODE = 1000;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private double latitude, longitude;
    private Location mLastLocation;
    private Marker pickupmarker,destinationmarker;
    private LocationRequest mLocationRequest;
    GoogleApiServices mService;
    LatLng center, pickup, destination;

    ApiInterface apiService;
    TextView t1;
    String pickupadd,destinationadd,totalDistance,totaltime;

    ImageView img;
    Button set,bt1,bt2;
    CardView cardlabel,cardbottom,cardtop;

    ArrayList<taxi> taxi1 = new ArrayList<>();
    ArrayList<Auto> auto1=new ArrayList<>();

    RideRequestButton button;

    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //init Service
        mService = common.getGoogleAPIService();

        apiService = APIClient.getClient().create(ApiInterface.class);
        // Request runtime permission

        //t1 = findViewById(R.id.textView);

        button=findViewById(R.id.uberbtn);





        img=findViewById(R.id.imageView2);
        //cardlabel=findViewById(R.id.card1);
        cardbottom=findViewById(R.id.card2);
        cardtop=findViewById(R.id.card);


        bt1=findViewById(R.id.bt1);
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(MapsActivity.this);
                    startActivityForResult(intent, 200);


                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
            }
        });
        bt2=findViewById(R.id.bt2);
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(MapsActivity.this);
                    startActivityForResult(intent, 400);


                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (mGoogleApiClient == null)
                            buildGoogleApiClient();
                        mMap.setMyLocationEnabled(true);


                    }
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();
        adddata();
        addauto();


        //init google play services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);



            } else {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);

            }
        }
    }

    private void addauto() {
        auto1.add(new Auto( 1.5,18,33));
        auto1.add(new Auto( 1.6,19,33));
        auto1.add(new Auto( 1.7,20,33));
        auto1.add(new Auto( 1.8,22,33));
        auto1.add(new Auto( 1.9,23,33));
        auto1.add(new Auto( 2.0,24,33));
        auto1.add(new Auto( 2.1,25,33));
        auto1.add(new Auto( 2.2,27,33));
        auto1.add(new Auto( 2.3,28,33));
        auto1.add(new Auto( 2.4,29,33));
        auto1.add(new Auto( 2.5,30,33));
        auto1.add(new Auto( 2.6,31,33));
        auto1.add(new Auto( 2.7,33,33));
        auto1.add(new Auto( 2.8,34,33));
        auto1.add(new Auto( 2.9,35,33));
        auto1.add(new Auto( 3.0,36,33));
        auto1.add(new Auto( 3.1,38,33));
        auto1.add(new Auto( 3.2,39,33));
        auto1.add(new Auto( 3.3,40,33));
        auto1.add(new Auto( 3.4,41,33));
        auto1.add(new Auto( 3.5,42,33));
        auto1.add(new Auto( 3.6,44,33));
        auto1.add(new Auto( 3.7,45,33));
        auto1.add(new Auto( 3.8,46,33));
        auto1.add(new Auto( 3.9,47,33));
        auto1.add(new Auto( 4.0,48,33));
        auto1.add(new Auto( 4.1,50,33));
        auto1.add(new Auto( 4.2,51,33));
        auto1.add(new Auto( 4.3,52,33));
        auto1.add(new Auto( 4.4,53,33));
        auto1.add(new Auto( 4.5,55,33));
        auto1.add(new Auto( 4.6,56,33));
        auto1.add(new Auto( 4.7,57,33));
        auto1.add(new Auto( 4.8,58,33));
        auto1.add(new Auto( 4.9,59,33));
        auto1.add(new Auto( 5.0,61,33));

    }

    private void adddata() {
        taxi1.add(new taxi( 1.5,22,33));
        taxi1.add(new taxi( 1.6f,23,33));
        taxi1.add(new taxi( 1.7f,25,33));
        taxi1.add(new taxi( 1.8f,26,33));
        taxi1.add(new taxi( 1.9f,28,33));
        taxi1.add(new taxi( 2.0f,29,33));
        taxi1.add(new taxi( 2.1f,31,33));
        taxi1.add(new taxi( 2.2f,32,33));
        taxi1.add(new taxi( 2.3f,34,33));
        taxi1.add(new taxi( 2.4f,35,33));
        taxi1.add(new taxi( 2.5f,37,33));
        taxi1.add(new taxi( 2.6f,38,33));
        taxi1.add(new taxi( 2.7f,40,33));
        taxi1.add(new taxi( 2.8f,41,33));
        taxi1.add(new taxi( 2.9f,43,33));
        taxi1.add(new taxi( 3.0f,44,33));
        taxi1.add(new taxi( 3.1f,46,33));
        taxi1.add(new taxi( 3.2f,47,33));
        taxi1.add(new taxi( 3.3f,49,33));
        taxi1.add(new taxi( 3.4f,50,33));
        taxi1.add(new taxi( 3.5f,52,33));
        taxi1.add(new taxi( 3.6f,53,33));
        taxi1.add(new taxi( 3.7f,55,33));
        taxi1.add(new taxi( 3.8f,56,33));
        taxi1.add(new taxi( 3.9f,58,33));
        taxi1.add(new taxi( 4.0f,59,33));
        taxi1.add(new taxi( 4.1f,61,33));
        taxi1.add(new taxi( 4.2f,62,33));
        taxi1.add(new taxi( 4.3f,64,33));
        taxi1.add(new taxi( 4.4f,65,33));
        taxi1.add(new taxi( 4.5f,67,33));
        taxi1.add(new taxi( 4.6f,68,33));
        taxi1.add(new taxi( 4.7f,69,33));
        taxi1.add(new taxi( 4.8f,71,33));
        taxi1.add(new taxi( 4.9f,72,33));
        taxi1.add(new taxi( 5f,22,33));
        taxi1.add(new taxi( 1.5f,22,33));
        taxi1.add(new taxi( 1.5f,22,33));
        taxi1.add(new taxi( 1.5f,22,33));
        taxi1.add(new taxi( 1.5f,22,33));
    }

    private  void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }


    @SuppressLint("RestrictedApi")
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {


    }

    @Override
    public void onLocationChanged(Location location)   {


        mLastLocation = location;

        latitude = location.getLatitude();
        longitude = location.getLongitude();

        final LatLng latLng = new LatLng(latitude, longitude);
        //move camera
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
        pickup=latLng;

        if (pickupmarker==null){
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(pickup)
                    .title("Pick up")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            pickupmarker = mMap.addMarker(markerOptions);
            pickupmarker.showInfoWindow();
        }
        if ((Objects.equals(getIntent().getAction(), "detail")))
        {
            destination=new LatLng(Double.valueOf(getIntent().getStringExtra("EXTRA_LAT")),Double.valueOf(getIntent().getStringExtra("EXTRA_LON")));
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(destination)
                    .title("Destination")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            destinationmarker = mMap.addMarker(markerOptions);
            destinationmarker.setPosition(destination);
            destinationmarker.showInfoWindow();
            Call<ResultDistanceMatrix.Root> call = apiService.getDistance("https://maps.googleapis.com/maps/api/distancematrix/json?key=AIzaSyBlbRC-4r5x73rTsTMlAH9PJ8eptz3nwU4&origins=" + pickup.latitude + "%2C" + pickup.longitude + "&destinations=" + getIntent().getStringExtra("EXTRA_LAT")+ "%2C" + getIntent().getStringExtra("EXTRA_LON"));

            call.enqueue(new Callback<ResultDistanceMatrix.Root>() {
                @Override
                public void onResponse(Call<ResultDistanceMatrix.Root> call, Response<ResultDistanceMatrix.Root> response) {

                    ResultDistanceMatrix.Root resultDistance = response.body();
                    if ("OK".equalsIgnoreCase(resultDistance.status)) {

                        ResultDistanceMatrix.InfoDistanceMatrix infoDistanceMatrix = resultDistance.rows.get(0);
                        ResultDistanceMatrix.InfoDistanceMatrix.DistanceElement distanceElement = infoDistanceMatrix.elements.get(0);
                        if ("OK".equalsIgnoreCase(distanceElement.status)) {
                            ResultDistanceMatrix.InfoDistanceMatrix.ValueItem itemDuration = distanceElement.duration;
                            ResultDistanceMatrix.InfoDistanceMatrix.ValueItem itemDistance = distanceElement.distance;


                            pickupadd = String.valueOf(resultDistance.pickup);
                            destinationadd = String.valueOf(resultDistance.destination);
                            totalDistance = itemDistance.text;
                            totaltime = itemDuration.text;


                            set.setVisibility(View.GONE);
                            cardbottom.setVisibility(View.VISIBLE);
                            double distance = Double.valueOf(totalDistance.substring(0, 3));
                            LatLngBounds.Builder builder = new LatLngBounds.Builder();

                            builder.include(pickupmarker.getPosition());
                            builder.include(destinationmarker.getPosition());
                            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 11, 11, 0));

                            TextView t1 = findViewById(R.id.textViewShortDesc);
                            TextView t2 = findViewById(R.id.textViewShortDesc1);










                            RideParameters rideParams = new RideParameters.Builder()
                                    .setPickupLocation(pickup.latitude, pickup.longitude, "Pick Up", "")
                                    .setDropoffLocation(Double.valueOf(getIntent().getStringExtra("EXTRA_LAT")), Double.valueOf(getIntent().getStringExtra("EXTRA_LON")), "Drop Off", "") // Price estimate will only be provided if this is provided.
                                     // Optional. If not provided, the cheapest product will be used.
                                    .build();

                            SessionConfiguration config = new SessionConfiguration.Builder()
                                    .setClientId("aVoNLUyR0EtVV3-38KZnBOHrMOT1Wx-s")
                                    .setServerToken("SXj4BdTsL8Fzpp0BUWgUWbDWnYqEb_fIVSRgppiv")
                                    .build();
                            ServerTokenSession session = new ServerTokenSession(config);

                            RideRequestButtonCallback callback = new RideRequestButtonCallback() {

                                @Override
                                public void onRideInformationLoaded() {

                                }

                                @Override
                                public void onError(ApiError apiError) {

                                }

                                @Override
                                public void onError(Throwable throwable) {

                                }
                            };

                            button.setRideParameters(rideParams);
                            button.setSession(session);
                            button.setCallback(callback);
                            button.loadRideInformation();

                            Time today = new Time(Time.getCurrentTimezone());
                            today.setToNow();
                            for (int i = 0; i < taxi1.size(); i++) {
                                if (distance <= taxi1.get(i).km) {
                                    if (distance == taxi1.get(i).km) {
                                        if (Double.valueOf(today.format("%k")) >= 0 && Double.valueOf(today.format("%k")) <= 5) {
                                            t1.setText("₹" + taxi1.get(i).Price2);
                                            t2.setText("₹" + auto1.get(i).Price2);

                                            break;
                                        } else {
                                            t1.setText("₹" + taxi1.get(i).price1);
                                            t2.setText("₹" + auto1.get(i).price1);

                                            break;
                                        }

                                    } else {
                                        if (Double.valueOf(today.format("%k")) >= 0 && Double.valueOf(today.format("%k")) <= 5) {
                                            t1.setText("₹" + taxi1.get(i).Price2);
                                            t2.setText("₹" + auto1.get(i).Price2);
                                            break;
                                        } else {
                                            t1.setText("₹" + taxi1.get(i).price1);
                                            t2.setText("₹" + auto1.get(i).price1);
                                            break;
                                        }
                                    }
                                }
                            }


                        }

                    }

                }

                @Override
                public void onFailure(Call<ResultDistanceMatrix.Root> call, Throwable t) {
                    call.cancel();
                }
            });

        }
        if ((Objects.equals(getIntent().getAction(), "feed")))
        {
            destination=new LatLng(getIntent().getDoubleExtra("EXTRA_LAT",0),getIntent().getDoubleExtra("EXTRA_LON",0));
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(destination)
                    .title("Destination")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            destinationmarker = mMap.addMarker(markerOptions);
            destinationmarker.setPosition(destination);
            destinationmarker.showInfoWindow();
            Call<ResultDistanceMatrix.Root> call = apiService.getDistance("https://maps.googleapis.com/maps/api/distancematrix/json?key=AIzaSyBlbRC-4r5x73rTsTMlAH9PJ8eptz3nwU4&origins=" + pickup.latitude + "%2C" + pickup.longitude + "&destinations=" + getIntent().getDoubleExtra("EXTRA_LAT",0)+ "%2C" + getIntent().getDoubleExtra("EXTRA_LON",0));

            call.enqueue(new Callback<ResultDistanceMatrix.Root>() {
                @Override
                public void onResponse(Call<ResultDistanceMatrix.Root> call, Response<ResultDistanceMatrix.Root> response) {

                    ResultDistanceMatrix.Root resultDistance = response.body();
                    if ("OK".equalsIgnoreCase(resultDistance.status)) {

                        ResultDistanceMatrix.InfoDistanceMatrix infoDistanceMatrix = resultDistance.rows.get(0);
                        ResultDistanceMatrix.InfoDistanceMatrix.DistanceElement distanceElement = infoDistanceMatrix.elements.get(0);
                        if ("OK".equalsIgnoreCase(distanceElement.status)) {
                            ResultDistanceMatrix.InfoDistanceMatrix.ValueItem itemDuration = distanceElement.duration;
                            ResultDistanceMatrix.InfoDistanceMatrix.ValueItem itemDistance = distanceElement.distance;


                            pickupadd = String.valueOf(resultDistance.pickup);
                            destinationadd = String.valueOf(resultDistance.destination);
                            totalDistance = itemDistance.text;
                            totaltime = itemDuration.text;


                            set.setVisibility(View.GONE);
                            cardbottom.setVisibility(View.VISIBLE);
                            double distance = Double.valueOf(totalDistance.substring(0, 3));
                            LatLngBounds.Builder builder = new LatLngBounds.Builder();

                            builder.include(pickupmarker.getPosition());
                            builder.include(destinationmarker.getPosition());
                            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 11, 11, 0));

                            TextView t1 = findViewById(R.id.textViewShortDesc);
                            TextView t2 = findViewById(R.id.textViewShortDesc1);










                            RideParameters rideParams = new RideParameters.Builder()
                                    .setPickupLocation(pickup.latitude, pickup.longitude, "Pick Up", "")
                                    .setDropoffLocation(getIntent().getDoubleExtra("EXTRA_LAT",0), getIntent().getDoubleExtra("EXTRA_LON",0), "Drop Off", "") // Price estimate will only be provided if this is provided.
                                    // Optional. If not provided, the cheapest product will be used.
                                    .build();

                            SessionConfiguration config = new SessionConfiguration.Builder()
                                    .setClientId("aVoNLUyR0EtVV3-38KZnBOHrMOT1Wx-s")
                                    .setServerToken("SXj4BdTsL8Fzpp0BUWgUWbDWnYqEb_fIVSRgppiv")
                                    .build();
                            ServerTokenSession session = new ServerTokenSession(config);

                            RideRequestButtonCallback callback = new RideRequestButtonCallback() {

                                @Override
                                public void onRideInformationLoaded() {

                                }

                                @Override
                                public void onError(ApiError apiError) {

                                }

                                @Override
                                public void onError(Throwable throwable) {

                                }
                            };

                            button.setRideParameters(rideParams);
                            button.setSession(session);
                            button.setCallback(callback);
                            button.loadRideInformation();

                            Time today = new Time(Time.getCurrentTimezone());
                            today.setToNow();
                            for (int i = 0; i < taxi1.size(); i++) {
                                if (distance <= taxi1.get(i).km) {
                                    if (distance == taxi1.get(i).km) {
                                        if (Double.valueOf(today.format("%k")) >= 0 && Double.valueOf(today.format("%k")) <= 5) {
                                            t1.setText("₹" + taxi1.get(i).Price2);
                                            t2.setText("₹" + auto1.get(i).Price2);

                                            break;
                                        } else {
                                            t1.setText("₹" + taxi1.get(i).price1);
                                            t2.setText("₹" + auto1.get(i).price1);

                                            break;
                                        }

                                    } else {
                                        if (Double.valueOf(today.format("%k")) >= 0 && Double.valueOf(today.format("%k")) <= 5) {
                                            t1.setText("₹" + taxi1.get(i).Price2);
                                            t2.setText("₹" + auto1.get(i).Price2);
                                            break;
                                        } else {
                                            t1.setText("₹" + taxi1.get(i).price1);
                                            t2.setText("₹" + auto1.get(i).price1);
                                            break;
                                        }
                                    }
                                }
                            }


                        }

                    }

                }

                @Override
                public void onFailure(Call<ResultDistanceMatrix.Root> call, Throwable t) {
                    call.cancel();
                }
            });

        }

        set = findViewById(R.id.button2);
        set.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               if (destinationmarker==null){
                   Toast.makeText(MapsActivity.this, "Set Destination", Toast.LENGTH_SHORT).show();

               }else if (pickupmarker==null)
               {
                   Toast.makeText(MapsActivity.this, "Set SOURCE", Toast.LENGTH_SHORT).show();
               }


               else {


                       Call<ResultDistanceMatrix.Root> call = apiService.getDistance("https://maps.googleapis.com/maps/api/distancematrix/json?key=AIzaSyBlbRC-4r5x73rTsTMlAH9PJ8eptz3nwU4&origins=" + pickup.latitude + "%2C" + pickup.longitude + "&destinations=" + destination.latitude + "%2C" + destination.longitude);

                       call.enqueue(new Callback<ResultDistanceMatrix.Root>() {
                           @Override
                           public void onResponse(Call<ResultDistanceMatrix.Root> call, Response<ResultDistanceMatrix.Root> response) {

                               ResultDistanceMatrix.Root resultDistance = response.body();
                               if ("OK".equalsIgnoreCase(resultDistance.status)) {

                                   ResultDistanceMatrix.InfoDistanceMatrix infoDistanceMatrix = resultDistance.rows.get(0);
                                   ResultDistanceMatrix.InfoDistanceMatrix.DistanceElement distanceElement = infoDistanceMatrix.elements.get(0);
                                   if ("OK".equalsIgnoreCase(distanceElement.status)) {
                                       ResultDistanceMatrix.InfoDistanceMatrix.ValueItem itemDuration = distanceElement.duration;
                                       ResultDistanceMatrix.InfoDistanceMatrix.ValueItem itemDistance = distanceElement.distance;


                                       pickupadd = String.valueOf(resultDistance.pickup);
                                       destinationadd = String.valueOf(resultDistance.destination);
                                       totalDistance = itemDistance.text;
                                       totaltime = itemDuration.text;
                                   /*Intent intent = new Intent(MapsActivity.this, MapsActivity2.class);
                                   intent.putExtra("pick", pickupadd);
                                   intent.putExtra("dest", destinationadd);
                                   intent.putExtra("time", totaltime);
                                   intent.putExtra("distance", totalDistance);
                                   intent.putExtra("picklat", pickup.latitude);
                                   intent.putExtra("picklng", pickup.longitude);
                                   intent.putExtra("deslat", destination.latitude);
                                   intent.putExtra("deslng", destination.longitude);
                                   startActivity(intent);*/

                                       set.setVisibility(View.GONE);
                                       cardbottom.setVisibility(View.VISIBLE);
                                       double distance = Double.valueOf(totalDistance.substring(0, 3));
                                       LatLngBounds.Builder builder = new LatLngBounds.Builder();

                                       builder.include(pickupmarker.getPosition());
                                       builder.include(destinationmarker.getPosition());
                                       mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 11, 11, 0));

                                       TextView t1 = findViewById(R.id.textViewShortDesc);
                                       TextView t2 = findViewById(R.id.textViewShortDesc1);


                                       RideParameters rideParams = new RideParameters.Builder()
                                               .setPickupLocation(pickup.latitude, pickup.longitude, "Uber HQ", "1455 Market Street, San Francisco")
                                               .setDropoffLocation(destination.latitude, destination.longitude, "Embarcadero", "One Embarcadero Center, San Francisco") // Price estimate will only be provided if this is provided.
                                               // Optional. If not provided, the cheapest product will be used.
                                               .build();

                                       SessionConfiguration config = new SessionConfiguration.Builder()
                                               .setClientId("aVoNLUyR0EtVV3-38KZnBOHrMOT1Wx-s")
                                               .setServerToken("SXj4BdTsL8Fzpp0BUWgUWbDWnYqEb_fIVSRgppiv")
                                               .build();
                                       ServerTokenSession session = new ServerTokenSession(config);

                                       RideRequestButtonCallback callback = new RideRequestButtonCallback() {

                                           @Override
                                           public void onRideInformationLoaded() {

                                           }

                                           @Override
                                           public void onError(ApiError apiError) {

                                           }

                                           @Override
                                           public void onError(Throwable throwable) {

                                           }
                                       };

                                       button.setRideParameters(rideParams);
                                       button.setSession(session);
                                       button.setCallback(callback);
                                       button.loadRideInformation();

                                       Time today = new Time(Time.getCurrentTimezone());
                                       today.setToNow();
                                       for (int i = 0; i < taxi1.size(); i++) {
                                           if (distance <= taxi1.get(i).km) {
                                               if (distance == taxi1.get(i).km) {
                                                   if (Double.valueOf(today.format("%k")) >= 0 && Double.valueOf(today.format("%k")) <= 5) {
                                                       t1.setText("₹" + taxi1.get(i).Price2);
                                                       t2.setText("₹" + auto1.get(i).Price2);

                                                       break;
                                                   } else {
                                                       t1.setText("₹" + taxi1.get(i).price1);
                                                       t2.setText("₹" + auto1.get(i).price1);

                                                       break;
                                                   }

                                               } else {
                                                   if (Double.valueOf(today.format("%k")) >= 0 && Double.valueOf(today.format("%k")) <= 5) {
                                                       t1.setText("₹" + taxi1.get(i).Price2);
                                                       t2.setText("₹" + auto1.get(i).Price2);
                                                       break;
                                                   } else {
                                                       t1.setText("₹" + taxi1.get(i).price1);
                                                       t2.setText("₹" + auto1.get(i).price1);
                                                       break;
                                                   }
                                               }
                                           }
                                       }


                                   }

                               }

                           }

                           @Override
                           public void onFailure(Call<ResultDistanceMatrix.Root> call, Throwable t) {
                               call.cancel();
                           }
                       });
                   }

               }

       });


                /*if (pickupmarker == null) {
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(center)
                            .title("Pick up")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    pickupmarker = mMap.addMarker(markerOptions);
                    pickupmarker.showInfoWindow();


                    pickup = center;
                    t1.setText("SET DESTINATION");

                } else if (pickupmarker != null) {

                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(center)
                            .title("Destination")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                            );
                    destinationmarker = mMap.addMarker(markerOptions);
                    destinationmarker.showInfoWindow();
                    destination = center;

                    Call<ResultDistanceMatrix.Root> call = apiService.getDistance( "https://maps.googleapis.com/maps/api/distancematrix/json?key=AIzaSyBlbRC-4r5x73rTsTMlAH9PJ8eptz3nwU4&origins="+pickup.latitude+"%2C"+pickup.longitude+"&destinations="+destination.latitude+"%2C"+destination.longitude);
                    call.enqueue(new Callback<ResultDistanceMatrix.Root>() {
                        @Override
                        public void onResponse(Call<ResultDistanceMatrix.Root> call, Response<ResultDistanceMatrix.Root> response) {

                            ResultDistanceMatrix.Root resultDistance = response.body();
                            if ("OK".equalsIgnoreCase(resultDistance.status)) {

                                ResultDistanceMatrix.InfoDistanceMatrix infoDistanceMatrix = resultDistance.rows.get(0);
                                ResultDistanceMatrix.InfoDistanceMatrix.DistanceElement distanceElement = infoDistanceMatrix.elements.get(0);
                                if ("OK".equalsIgnoreCase(distanceElement.status)) {
                                    ResultDistanceMatrix.InfoDistanceMatrix.ValueItem itemDuration = distanceElement.duration;
                                    ResultDistanceMatrix.InfoDistanceMatrix.ValueItem itemDistance = distanceElement.distance;


                                     pickupadd=String.valueOf(resultDistance.pickup);
                                     destinationadd=String.valueOf(resultDistance.destination);
                                     totalDistance = itemDistance.text;
                                     totaltime = itemDuration.text;
                                    Intent intent = new Intent(MapsActivity.this, MapsActivity2.class);
                                    intent.putExtra("pick", pickupadd);
                                    intent.putExtra("dest", destinationadd);
                                    intent.putExtra("time", totaltime);
                                    intent.putExtra("distance", totalDistance);
                                    intent.putExtra("picklat", pickup.latitude);
                                    intent.putExtra("picklng", pickup.longitude);
                                    intent.putExtra("deslat", destination.latitude);
                                    intent.putExtra("deslng", destination.longitude);
                                    startActivity(intent);
                                    img.setVisibility(View.GONE);
                                    set.setVisibility(View.GONE);
                                    cardlabel.setVisibility(View.GONE);
                                    cardbottom.setVisibility(View.VISIBLE);


                                }

                            }

                        }

                        @Override
                        public void onFailure(Call<ResultDistanceMatrix.Root> call, Throwable t) {
                            call.cancel();
                        }
                    });



                }


            }

        });
        */
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }



    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode==200)
        {
            if (resultCode==RESULT_OK) {
                pickupmarker.remove();
                Place place = PlaceAutocomplete.getPlace(MapsActivity.this, data);
                String name = place.getName().toString();
                pickup = place.getLatLng();
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(pickup)
                        .title("Pick Up")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                pickupmarker = mMap.addMarker(markerOptions);
                pickupmarker.setPosition(pickup);
                pickupmarker.showInfoWindow();
                bt1.setText(name);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pickup, 17));
                set.setVisibility(View.VISIBLE);
                cardbottom.setVisibility(View.GONE);

            }

        }
        else if (requestCode==400)
        {
            if (resultCode==RESULT_OK) {
                if (destinationmarker!=null) {destinationmarker.remove();}
                Place place = PlaceAutocomplete.getPlace(MapsActivity.this, data);
                String name = place.getName().toString();
                destination = place.getLatLng();
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(destination)
                        .title("Destination")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                destinationmarker = mMap.addMarker(markerOptions);
                destinationmarker.setPosition(destination);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(destination, 17));
                destinationmarker.showInfoWindow();
                bt2.setText(name);
                set.setVisibility(View.VISIBLE);
                cardbottom.setVisibility(View.GONE);
            }
        }
    }
}
