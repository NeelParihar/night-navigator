package com.example.nightlife.nightlife;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.nightlife.nightlife.RecyclerViewAdapter.EXTRA_ID;
import static com.example.nightlife.nightlife.RecyclerViewAdapter.EXTRA_LAT;
import static com.example.nightlife.nightlife.RecyclerViewAdapter.EXTRA_LON;
import static com.example.nightlife.nightlife.RecyclerViewAdapter.EXTRA_photoref;
import static com.example.nightlife.nightlife.placeadapter.EXTRA_ADD;
import static com.example.nightlife.nightlife.placeadapter.EXTRA_NAME;
import static com.example.nightlife.nightlife.placeadapter.EXTRA_URL;


public class detail_place extends AppCompatActivity {

    private ApiInterface apiService;
    PlacesPOJO.phonenumber result;
    List<PlacesPOJO.photos> result1;
    String  phoneno;
    PlacesPOJO.photos photoref=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.support.v7.app.ActionBar actionBar=getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("");
        final Intent intent = getIntent();
        apiService = APIClient.getClient().create(ApiInterface.class);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        Call<PlacesPOJO.Root1> call1 = apiService.phone("https://maps.googleapis.com/maps/api/place/details/json?placeid="+intent.getStringExtra(EXTRA_ID)+"&fields=photos,formatted_phone_number&key=AIzaSyBlbRC-4r5x73rTsTMlAH9PJ8eptz3nwU4");
        call1.enqueue(new Callback<PlacesPOJO.Root1>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<PlacesPOJO.Root1> call, @NonNull Response<PlacesPOJO.Root1> response) {
                PlacesPOJO.Root1 root = response.body();


                if (response.isSuccessful()) {

                    if (root.status.equals("OK")) {

                            result = root.phone;
                            phoneno = result.phoneno;
                            result1= result.photo;
                            if (result1!=null){
                                photoref=result1.get(0);
                            }

                        setContentView(R.layout.activity_detail_place);



                        String creatorName = intent.getStringExtra(EXTRA_NAME);
                        String address = intent.getStringExtra("EXTRA_ADD");



                        ImageView imageView = findViewById(R.id.image_view);
                        TextView textViewCreator = findViewById(R.id.placename);
                        TextView textViewLikes = findViewById(R.id.placeadd);
                        TextView rate=findViewById(R.id.ratingdetail);


                        textViewCreator.setText(creatorName);
                        textViewLikes.setText("Address: "+address);
                        rate.setText(intent.getStringExtra("EXTRA_RATING"));


                        Button dir=(Button)findViewById(R.id.gotomap);
                        dir.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Uri.Builder builder = new Uri.Builder();
                                builder.scheme("https")
                                        .authority("www.google.com").appendPath("maps").appendPath("dir").appendPath("").appendQueryParameter("api", "1")
                                        .appendQueryParameter("destination",intent.getStringExtra(EXTRA_LAT)+ "," + intent.getStringExtra(EXTRA_LON));
                                String url = builder.build().toString();
                                Log.d("Directions", url);
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(url));
                                startActivity(i);
                            }
                        });
                        Button dial=(Button)  findViewById(R.id.call);
                        dial.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                // Send phone number to intent as data
                                intent.setData(Uri.parse("tel:" + phoneno));
                                // Start the dialer app activity with number
                                startActivity(intent);
                            }
                        });
                        Button cab=findViewById(R.id.gotouber);
                        cab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent1=new Intent(detail_place.this,MapsActivity.class);
                                intent1.setAction("detail");
                                intent1.putExtra("EXTRA_LAT",intent.getStringExtra(EXTRA_LAT));
                                intent1.putExtra("EXTRA_LON",intent.getStringExtra(EXTRA_LON));
                                startActivity(intent1);
                            }
                        });
                        if (photoref!=null)
                        {
                            Glide.with(detail_place.this).load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="+photoref.photoref+"&key=AIzaSyBlbRC-4r5x73rTsTMlAH9PJ8eptz3nwU4").into(imageView);
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "No matches found near you", Toast.LENGTH_SHORT).show();
                    }

                } else if (response.code() != 200) {
                    Toast.makeText(getApplicationContext(), "Error " + response.code() + " found.", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call<PlacesPOJO.Root1> call, Throwable t) {
                // Log error here since request failed
                call.cancel();
            }
        });




    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
