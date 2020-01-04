package com.example.nightlife.nightlife;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MapsActivity2 extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    ArrayList<taxi> taxi1 = new ArrayList<>();

    double distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        adddata();
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


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;



        Intent intent = getIntent();
        String pickupadd = intent.getStringExtra("pick");
        String destinationadd = intent.getStringExtra("dest");
        distance = Double.valueOf(intent.getStringExtra("distance").substring(0, 3));
        double picklat = intent.getDoubleExtra("picklat", 0);
        double picklng = intent.getDoubleExtra("picklng", 0);
        double deslat = intent.getDoubleExtra("deslat", 0);
        double deskng = intent.getDoubleExtra("deslng", 0);
        Button b1 = findViewById(R.id.bt1);
        Button b2 = findViewById(R.id.bt2);
        LatLng pickup = new LatLng(picklat, picklng);
        LatLng destination = new LatLng(deslat, deskng);


        b1.setText(pickupadd);
        b2.setText(destinationadd);
        // Add a marker and move the camera
        final Marker marker1 = mMap.addMarker(new MarkerOptions().position(pickup).title("Pickup"));
        final Marker marker2 = mMap.addMarker(new MarkerOptions().position(destination).title("Destination"));

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        builder.include(marker1.getPosition());
        builder.include(marker2.getPosition());

        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 17, 17, 0));
        TextView t1 = findViewById(R.id.textViewShortDesc);
        TextView t2 = findViewById(R.id.textViewShortDesc1);
        Date currentTime = Calendar.getInstance().getTime();
        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();
        for (int i=0;i<taxi1.size();i++)
        {
            if(distance<=taxi1.get(i).km)
            {
                if(distance==taxi1.get(i).km)
                {
                    if(Double.valueOf(today.format("%k"))>=0 && Double.valueOf(today.format("%k"))<=5)
                    {
                        t1.setText("₹"+taxi1.get(i).Price2);
                        break;
                    }
                    else
                    {
                        t1.setText("₹"+taxi1.get(i).price1);
                        break;
                    }

                }
                else {
                    if(Double.valueOf(today.format("%k"))>=0 && Double.valueOf(today.format("%k"))<=5)
                    {
                        t1.setText("₹"+taxi1.get(i).Price2);
                        break;
                    }
                    else
                    {
                        t1.setText("₹"+taxi1.get(i).price1);
                        break;
                    }
                }
            }
        }
    }



}

