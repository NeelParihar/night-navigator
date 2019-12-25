package com.example.nightlife.nightlife;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static com.example.nightlife.nightlife.RecyclerViewAdapter.EXTRA_ID;
import static com.example.nightlife.nightlife.RecyclerViewAdapter.EXTRA_LAT;
import static com.example.nightlife.nightlife.RecyclerViewAdapter.EXTRA_LON;
import static com.example.nightlife.nightlife.RecyclerViewAdapter.EXTRA_NAME;

public class hotel extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    private final static int ALL_PERMISSIONS_RESULT = 101;
    List<StoreModel> storeModels;
    ApiInterface apiService;
    SwipeRefreshLayout mSwipeRefreshLayout;
    String latLngString;
    LatLng latLng;

    RecyclerView recyclerView;
    EditText editText;
    Button button;
    List<PlacesPOJO.CustomA> results,finalresult;
    Location currentLocation;
    LocationTrack locationTrack;
    TextView i;



    double lat,lon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclerview);
        Log.d("hey", "onComplete: google place!");
        android.support.v7.app.ActionBar actionBar=getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Lodge");

        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);

        permissionsToRequest = findUnAskedPermissions(permissions);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, android.R.color.holo_orange_light);
        mSwipeRefreshLayout.post(new Runnable() {

            @Override
            public void run() {

                mSwipeRefreshLayout.setRefreshing(true);

                // Fetching data from server
                fetchStores();
            }
        });

        i=findViewById(R.id.no);

        apiService = APIClient.getClient().create(ApiInterface.class);

        recyclerView = findViewById(R.id.recylcerView);

        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);



        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


            if (permissionsToRequest.size() > 0)
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
            else {
                fetchStores();

            }
        } else {
            fetchStores();

        }


    }

    private void fetchStores() {

        /**
         * For Locations In India McDonalds stores aren't returned accurately
         */
        locationTrack = new LocationTrack(this,i);


        if (locationTrack.canGetLocation()) {


            lon = locationTrack.getLongitude();
            lat = locationTrack.getLatitude();
            //Call<PlacesPOJO.Root> call = apiService.doPlaces(placeType, latLngString,"\""+ businessName +"\"", true, "distance", APIClient.GOOGLE_PLACE_API_KEY);
            //Call<PlacesPOJO.Root> call = apiService.doPlaces(currentLocation.getLatitude()+","+currentLocation.getLongitude(),"1000","hotel","true","AIzaSyBlbRC-4r5x73rTsTMlAH9PJ8eptz3nwU4");
            Call<PlacesPOJO.Root> call = apiService.doplaces("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+lat+","+lon+"&radius=1500&type=lodging&sensor=true&key=AIzaSyBlbRC-4r5x73rTsTMlAH9PJ8eptz3nwU4");
            call.enqueue(new Callback<PlacesPOJO.Root>() {
                @Override
                public void onResponse(Call<PlacesPOJO.Root> call, Response<PlacesPOJO.Root> response) {
                    PlacesPOJO.Root root = response.body();



                    if (response.isSuccessful()) {

                        if (root.status.equals("OK")) {


                            results = root.customA;
                            RecyclerViewAdapter adapterStores = new RecyclerViewAdapter(hotel.this,results);
                            recyclerView.setAdapter(adapterStores);
                            Log.d("hey", "onComplete: adapter holder!");
                            locationTrack.stopListener();
                            mSwipeRefreshLayout.setRefreshing(false);




                        } else {
                            Toast.makeText(getApplicationContext(), "No matches found near you", Toast.LENGTH_SHORT).show();
                            mSwipeRefreshLayout.setRefreshing(false);
                        }

                    } else if (response.code() != 200) {
                        Toast.makeText(getApplicationContext(), "Error " + response.code() + " found.", Toast.LENGTH_SHORT).show();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }else {
                        Log.d("hey", "not: adapter");
                    }


                }

                @Override
                public void onFailure(Call<PlacesPOJO.Root> call, Throwable t) {
                    // Log error here since request failed
                    call.cancel();
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });


        } else {

            locationTrack.showSettingsAlert();
        }




    }

    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<>();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (String perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }

                } else {
                    fetchStores();
                }

                break;
        }

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        fetchStores();
    }

    /*private void fetchLocation() {

        FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try{
            Task location = mFusedLocationProviderClient.getLastLocation();
            final Task task = location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Log.d("hey", "onComplete: found location!");
                        currentLocation = (Location) task.getResult();
                        latLngString=currentLocation.getLatitude() + "," + currentLocation.getLongitude();
                        fetchStores("hotel");
                    } else {
                        Toast.makeText(googleplace.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        Log.e("execption location", "getDeviceLocation: SecurityException: ");
                    }
                }
            });
        }catch (SecurityException e){
            Log.d("execption location", "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
    }*/

   /*private void fetchDistance(final PlacesPOJO.CustomA info) {

        Call<ResultDistanceMatrix.Root> call = apiService.getDistance("https://maps.googleapis.com/maps/api/distancematrix/json?key=AIzaSyBlbRC-4r5x73rTsTMlAH9PJ8eptz3nwU4&origins="+lat+"%2C"+lon+"&destinations="+info.geo.locationA.lat+"%2C"+info.geo.locationA.lng);
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
                        String totalDistance = String.valueOf(itemDistance.text);
                        String totalDuration = String.valueOf(itemDuration.text);


                            RecyclerViewAdapter adapterStores = new RecyclerViewAdapter(googleplace.this,results,infoDistanceMatrix);
                            recyclerView.setAdapter(adapterStores);
                            Log.d("hey", "onComplete: adapter holder!");
                            progressBar.setVisibility(View.GONE);
                        }

                    }

                }
                @Override
            public void onFailure(Call<ResultDistanceMatrix.Root> call, Throwable t) {
                call.cancel();
            }
        });

    }*/
   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
       getMenuInflater().inflate(R.menu.item,menu);
       return true;
   }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return true;
            case R.id.Signout:
                // User chose the "Settings" item, show the app settings UI...
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                // ...
                                Intent intent=new Intent(hotel.this,Login.class);
                                startActivity(intent);
                            }
                        });
                return true;
            case R.id.search:
                // User chose the "Settings" item, show the app settings UI...

                try {
                    Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(hotel.this);
                    startActivityForResult(intent, 200);


                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }

                return true;


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode==200)
        {
            if (resultCode==RESULT_OK) {

                Place place = PlaceAutocomplete.getPlace(hotel.this, data);
                String name = place.getName().toString();
                Intent detailIntent = new Intent(this, detail_place.class);
                //detailIntent.putExtra(EXTRA_URL, icon);
                detailIntent.putExtra(EXTRA_NAME, place.getName());
                detailIntent.putExtra("EXTRA_ADD",place.getAddress());
                detailIntent.putExtra(EXTRA_ID, place.getId());
                detailIntent.putExtra("EXTRA_RATING",place.getRating());
                detailIntent.putExtra(EXTRA_LAT,String.valueOf(place.getLatLng().latitude));
                detailIntent.putExtra(EXTRA_LON,String.valueOf(place.getLatLng().longitude));


                startActivity(detailIntent);


            }

        }
    }

}
