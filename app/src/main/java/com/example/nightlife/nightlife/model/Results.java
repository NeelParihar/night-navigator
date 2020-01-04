package com.example.nightlife.nightlife.model;


public class Results {
    private String icon;

    private String place_id;

    private String scope;

    private String reference;

    private Geometry geometry;

    private Opening_hours opening_hours;

    private String id;

    private Photos[] photos;

    private String vicinity;

    private String name;

    private Plus_code plus_code;

    private String rating;

    private String[] types;

    public Results(String icon, String place_id, String scope, String reference, Geometry geometry, Opening_hours opening_hours, String id, Photos[] photos, String vicinity, String name, Plus_code plus_code, String rating, String[] types) {
        this.icon = icon;
        this.place_id = place_id;
        this.scope = scope;
        this.reference = reference;
        this.geometry = geometry;
        this.opening_hours = opening_hours;
        this.id = id;
        this.photos = photos;
        this.vicinity = vicinity;
        this.name = name;
        this.plus_code = plus_code;
        this.rating = rating;
        this.types = types;
    }

    /*public Results() {
        this.icon = icon;
        this.place_id = place_id;
        this.scope = scope;
        this.reference = reference;
        this.geometry = geometry;
        this.opening_hours = opening_hours;
        this.id = id;
        this.photos = photos;
        this.vicinity = vicinity;
        this.name = name;
        this.plus_code = plus_code;
        this.rating = rating;
        this.types = types;
    }*/

    public Results(String icon, Opening_hours opening_hours, String vicinity, String name, String rating) {
        this.icon = icon;
        this.opening_hours = opening_hours;
        this.vicinity = vicinity;
        this.name = name;
        this.rating = rating;
    }

    public Results() {
    }

    public String getIcon ()
    {
        return icon;
    }

    public void setIcon (String icon)
    {
        this.icon = icon;
    }

    public String getPlace_id ()
    {
        return place_id;
    }

    public void setPlace_id (String place_id)
    {
        this.place_id = place_id;
    }

    public String getScope ()
    {
        return scope;
    }

    public void setScope (String scope)
    {
        this.scope = scope;
    }

    public String getReference ()
    {
        return reference;
    }

    public void setReference (String reference)
    {
        this.reference = reference;
    }

    public Geometry getGeometry ()
    {
        return geometry;
    }

    public void setGeometry (Geometry geometry)
    {
        this.geometry = geometry;
    }

    public Opening_hours getOpening_hours ()
    {
        return opening_hours;
    }

    public void setOpening_hours (Opening_hours opening_hours)
    {
        this.opening_hours = opening_hours;
    }

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public Photos[] getPhotos ()
    {
        return photos;
    }

    public void setPhotos (Photos[] photos)
    {
        this.photos = photos;
    }

    public String getVicinity ()
    {
        return vicinity;
    }

    public void setVicinity (String vicinity)
    {
        this.vicinity = vicinity;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public Plus_code getPlus_code ()
    {
        return plus_code;
    }

    public void setPlus_code (Plus_code plus_code)
    {
        this.plus_code = plus_code;
    }

    public String getRating ()
    {
        return rating;
    }

    public void setRating (String rating)
    {
        this.rating = rating;
    }

    public String[] getTypes ()
    {
        return types;
    }

    public void setTypes (String[] types)
    {
        this.types = types;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [icon = "+icon+", place_id = "+place_id+", scope = "+scope+", reference = "+reference+", geometry = "+geometry+", opening_hours = "+opening_hours+", id = "+id+", photos = "+photos+", vicinity = "+vicinity+", name = "+name+", plus_code = "+plus_code+", rating = "+rating+", types = "+types+"]";
    }
}
