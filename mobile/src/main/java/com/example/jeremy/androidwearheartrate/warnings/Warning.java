package com.example.jeremy.androidwearheartrate.warnings;
/**
 * Created by kevind on 2017-01-24.
 */

public class Warning{

    public static final int Caution = 0;
    public static final int Alert = 1;
    public static final int Emergency = 2;

    private int type;
    private String response;

    public Warning(int type, String response){
        this.response = response;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getResponse(){
        return response;
    }

    public void setResponse(String newResp){
        response = newResp;
    }

    public String toString(){
        return response;
    }

}