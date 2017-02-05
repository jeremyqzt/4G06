package com.example.jeremy.androidwearheartrate.database;

public interface DatabaseChangeListener {

    void onSuccess(Object value);

    void onFail(String value);
}
