package com.example.jeremy.androidwearheartrate.warnings;
/**
 * Created by kevind on 2017-01-24.
 */

abstract class Warning{

    protected int id;
    protected String response;

    public int getId(){
        return id;
    }

    public void setId(int newid){
        id = newid;
    }

    public String getResponse(){
        return response;
    }

    public void setResponse(String newResp){
        response = newResp;
    }

    //implement a comparision method to show how one might want to improve
    abstract void Compare();

}