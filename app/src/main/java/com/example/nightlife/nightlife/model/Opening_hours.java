package com.example.nightlife.nightlife.model;

public class Opening_hours {
    private Boolean open_now;

    public Boolean getOpen_now ()
    {
        return open_now;
    }

    public void setOpen_now (Boolean open_now)
    {
        this.open_now = open_now;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [open_now = "+open_now+"]";
    }
}
