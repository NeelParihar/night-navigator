package com.example.nightlife.nightlife;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.nightlife.nightlife.model.Place_model;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static com.example.nightlife.nightlife.R.id.rootlayout;

public class Place_form extends AppCompatActivity {


    FirebaseUser user;
    DatabaseReference database;
    EditText name,type,phone;
    Button btnloc,btnadd;
    double lat=0;
    double lon=0;



    // ...

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_form);
        // ...

        android.support.v7.app.ActionBar actionBar=getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Add Place");
        name=findViewById(R.id.input_name);
        type=findViewById(R.id.input_type);
        phone=findViewById(R.id.input_phone);
        btnloc=findViewById(R.id.btn_SelectLocation);
        btnadd=findViewById(R.id.btn_signup);
        btnloc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Place_form.this,MapsActivity3.class),2);
            }
        });


        btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    signup();
                } catch (IOException e) {
                    Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });
    }
    public void signup() throws IOException {
        Log.d("TAG", "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        btnadd.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(Place_form.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Uploading...");
        progressDialog.show();

        String name1 = name.getText().toString();
        String type1 = type.getText().toString();
        String phone1 = phone.getText().toString();
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());


            addresses = geocoder.getFromLocation(Double.valueOf(lat), Double.valueOf(lon), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5


        String address = addresses.get(0).getAddressLine(0);



        // TODO: Implement your own signup logic here.
        database = FirebaseDatabase.getInstance().getReference();

        String key = database.child("place").push().getKey();
        Place_model place_model=new Place_model(name1,address,phone1, Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(),lat,lon,type1);
        database.child("place").child(key).setValue(place_model).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                onSignupSuccess();
                progressDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                onSignupFailed();
            }
        });

        /*new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        onSignupSuccess();
                        // onSignupFailed();

                    }
                }, 3000);*/
    }


    public void onSignupSuccess() {
        btnadd.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Upload failed", Toast.LENGTH_LONG).show();

        btnadd.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name1 = name.getText().toString();
        String type1 = type.getText().toString();
        String phone1 = phone.getText().toString();

        if (name1.isEmpty() || name1.length() < 3) {
            name.setError("at least 3 characters");
            valid = false;
        } else {
            name.setError(null);
        }

        if (type1.isEmpty()) {
            type.setError("enter a valid email address");
            valid = false;
        } else {
            type.setError(null);
        }

        if (phone1.isEmpty() || phone1.length() > 10||phone1.length() < 10) {
            phone.setError("Enter valid phone number");
            valid = false;
        } else {
            phone.setError(null);
        }
        if (lat==0&&lon==0)
        {
            Toast.makeText(getBaseContext(), "Select Location", Toast.LENGTH_LONG).show();
            valid = false;
        }


        return valid;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==2){
            if (data != null) {
                lat = data.getDoubleExtra("LAT", 0);
                lon = data.getDoubleExtra("LON", 0);
                btnloc.setText("Location selected");
            }
        }else {

        }
    }
}



