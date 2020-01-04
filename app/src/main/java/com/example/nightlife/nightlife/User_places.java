package com.example.nightlife.nightlife;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.nightlife.nightlife.model.Place_model;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseAppLifecycleListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class User_places extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    FirebaseUser user;
    DatabaseReference database;
    static final int RC_PERMISSION_READ_EXTERNAL_STORAGE = 1;
    static final int RC_IMAGE_GALLERY = 2;
    ArrayList<Place_model> placeModels=new ArrayList<>();

    SwipeRefreshLayout mSwipeRefreshLayout;

    RecyclerView recyclerView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_places);

        database=FirebaseDatabase.getInstance().getReference("place");
        android.support.v7.app.ActionBar actionBar=getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Places");
        mSwipeRefreshLayout =  findViewById(R.id.swipeuser);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, android.R.color.holo_orange_light);
        mSwipeRefreshLayout.post(new Runnable() {

            @Override
            public void run() {

                mSwipeRefreshLayout.setRefreshing(true);

                // Fetching data from server
                fetchplaces();
            }
        });


        recyclerView = findViewById(R.id.recylcerView);

        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);



        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

    }

    private void fetchplaces() {
        placeModels.clear();
        database.addListenerForSingleValueEvent(new ValueEventListener() {


            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("Count " ,""+dataSnapshot.getChildrenCount());
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Place_model post = postSnapshot.getValue(Place_model.class);
                    placeModels.add(post);
                    Log.e("Get Data", placeModels.get(0).name);


                }
                Place_Adapter adapterStores = new Place_Adapter(User_places.this,placeModels);
                recyclerView.setAdapter(adapterStores);
                Log.d("hey", "onComplete: adapter holder!");

                mSwipeRefreshLayout.setRefreshing(false);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                mSwipeRefreshLayout.setRefreshing(false);

            }
        });

    }

    public void uploadImage(View view) {

            Intent intent = new Intent(this,Place_form.class);
            startActivity(intent);
        }

    @Override
    public void onRefresh() {
        fetchplaces();

    }
}



