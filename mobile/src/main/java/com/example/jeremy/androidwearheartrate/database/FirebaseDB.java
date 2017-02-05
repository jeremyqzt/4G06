package com.example.jeremy.androidwearheartrate.database;


import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.ContentValues.TAG;

public class FirebaseDB {

    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static DatabaseReference myRef;

    private static FirebaseDB instance;

    private FirebaseDB(){
    }

    public static FirebaseDB getInstance(String reference){
        myRef = database.getReference(reference);
        if(instance == null){
            instance = new FirebaseDB();
        }
        return instance;
    }

    public void onChange(final DatabaseChangeListener listener){

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
//                ArrayList value = dataSnapshot.getValue(ArrayList.class);
//                listener.onSuccess(value.toString());

                listener.onSuccess(dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
                listener.onFail(error.getMessage());
            }
        });

    }
}
