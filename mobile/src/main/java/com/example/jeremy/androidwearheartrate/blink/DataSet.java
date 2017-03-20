package com.example.jeremy.androidwearheartrate.blink;

import java.util.Date;

/**
 * Created by kevind on 2017-01-30.
 */

public class DataSet {

    private String type;
    private boolean avail;
    private Date latest;

    public DataSet(String type, boolean avail){
        this.type = type;
        this.avail = avail;
    }

    public String getType(){
        return type;
    }

    public void setType(String type){
        this.type = type;
    }

    public boolean getAvailable(){
        return avail;
    }

    public void setAvailable(boolean avail){
        this.avail = avail;
    }

    public Date getLastestDate(){
        return latest;
    }

    public void setLatestDate(Date Last){
        latest = Last;
    }
}
