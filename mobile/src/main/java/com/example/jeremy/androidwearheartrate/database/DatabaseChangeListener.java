package com.example.jeremy.androidwearheartrate.database;

public interface DatabaseChangeListener {

    void onSuccess(String value);

    void onFail(String value);
}
