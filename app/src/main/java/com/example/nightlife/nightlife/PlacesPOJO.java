package com.example.nightlife.nightlife;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PlacesPOJO {
    public class Root implements Serializable {

        @SerializedName("results")
        public List<CustomA> customA = new ArrayList<>();
        @SerializedName("status")
        public String status;

    }
    public class Root1 implements Serializable {
        @SerializedName("result")
        public phonenumber phone;
        @SerializedName("status")
        public String status;


    }

    public class CustomA implements Serializable {

        @SerializedName("vicinity")
        public String vicinity;
        @SerializedName("name")
        public String name;
        @SerializedName("icon")
        public String icon;
        @SerializedName("place_id")
        public String placeid;
        @SerializedName("geometry")
        public Geometry geo;
        @SerializedName("opening_hours")
        public Opennow opennow;
        @SerializedName("rating")
        public String rating;


    }
    public class photos implements Serializable{

        @SerializedName("photo_reference")
        public String photoref;

    }

    public class phonenumber implements Serializable{

        @SerializedName("formatted_phone_number")
        public String phoneno;
        @SerializedName("photos")
        public List<photos> photo=new ArrayList<>();


    }

      public class Geometry implements Serializable{

        @SerializedName("location")
        public LocationA locationA;

    }

    public class LocationA implements Serializable {

        @SerializedName("lat")
        public double lat;
        @SerializedName("lng")
        public double lng;


    }
    public class Opennow implements Serializable{

        @SerializedName("open_now")
        public boolean open_now;

    }


}
