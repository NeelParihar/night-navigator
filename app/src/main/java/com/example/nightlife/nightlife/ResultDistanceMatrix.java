package com.example.nightlife.nightlife;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ResultDistanceMatrix {

    public class Root implements Serializable {

        @SerializedName("rows")
        public List<InfoDistanceMatrix> rows=new ArrayList<>();
        @SerializedName("status")
        public String status;
        @SerializedName("destination_addresses")
        public List<String> destination=new ArrayList<>();
        @SerializedName("origin_addresses")
        public List<String> pickup=new ArrayList<>();
    }


        public class InfoDistanceMatrix implements Serializable {
            @SerializedName("elements")
            public List<DistanceElement> elements=new ArrayList<>();


            public class DistanceElement implements Serializable {

                @SerializedName("duration")
                public ValueItem duration;
                @SerializedName("distance")
                public ValueItem distance;
                @SerializedName("status")
                public String status;


            }

            public class ValueItem implements Serializable {
                @SerializedName("value")
                public long value;
                @SerializedName("text")
                public String text;

            }
        }

}