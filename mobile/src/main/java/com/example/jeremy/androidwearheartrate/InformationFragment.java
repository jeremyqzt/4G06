package com.example.jeremy.androidwearheartrate;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.jeremy.androidwearheartrate.database.DatabaseChangeListener;
import com.example.jeremy.androidwearheartrate.database.FirebaseDB;
import com.firebase.client.Firebase;


public class InformationFragment extends Fragment {
    final String TAG = "InformationPage";
    private Bundle bundle;
    TextView Summary, SummaryHead;
    String Name, Condition, Gender;
    Integer mAge;
    Person person = new Person();
    Button button;
    Firebase ref;
    Spinner cond;
    Spinner gender;
    EditText nom, age;

    private void doSomethingLocal(){
        String inputAge = "45";
        try{
            inputAge = age.getText().toString();
        }catch (Exception e){

        }
        SummaryHead.setText("Hello, " + nom.getText().toString());
        String out = "According to Our Database, you're a " +gender.getSelectedItem().toString()+ ", " + inputAge + " years of age with " + cond.getSelectedItem().toString();
        Summary.setText(out);
    }
    private void doSomething(){
        SummaryHead.setText("Hello, " +Name);
        String out = "According to Our Database, you're a " +Gender+ ", " + String.valueOf(mAge) + " years of age with " + Condition;
        Summary.setText(out);
    }

    private void doTheOtherThing(){
        String gend = gender.getSelectedItem().toString();
        String condition = cond.getSelectedItem().toString();
        String aname = nom.getText().toString();
        try {
            mAge = Integer.parseInt(age.getText().toString());
        }catch (Exception e){
            mAge = 45;
        }
        person.setAge(mAge);
        person.setConditions(condition);
        person.setName(aname);
        person.setGender(gend);
        ref.child("Person").setValue(person);
        doSomethingLocal();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.information, container, false);
        bundle = this.getArguments();
        ref = new Firebase(Config.FIREBASE_URL);
        Summary = (TextView) view.findViewById(R.id.summary);
        SummaryHead = (TextView) view.findViewById(R.id.summaryhead);
        button = (Button) view.findViewById(R.id.updatebtn);
        cond = (Spinner) view.findViewById(R.id.cond);
        gender = (Spinner) view.findViewById(R.id.gender);
        nom = (EditText) view.findViewById(R.id.nom);
        age = (EditText) view.findViewById(R.id.age);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                doTheOtherThing();
            }
        });

        Runnable r = new Runnable() {
            @Override
            public void run(){
                doSomething(); //<-- put your code in here.
            }
        };
        Handler h = new Handler();
        h.postDelayed(r, 3000);

        FirebaseDB.getInstance("Person/Name").onChange(new DatabaseChangeListener() {
            @Override
            public void onSuccess(Object value) {
                Name = value.toString();
            }

            @Override
            public void onFail(String value) {
                Log.e(TAG, value);
            }
        });

        FirebaseDB.getInstance("Person/Gender").onChange(new DatabaseChangeListener() {
            @Override
            public void onSuccess(Object value) {
                Gender = value.toString();
            }

            @Override
            public void onFail(String value) {
                Log.e(TAG, value);
            }
        });
        FirebaseDB.getInstance("Person/Conditions").onChange(new DatabaseChangeListener() {
            @Override
            public void onSuccess(Object value) {
                Condition = value.toString();
            }

            @Override
            public void onFail(String value) {
                Log.e(TAG, value);
            }
        });

        FirebaseDB.getInstance("Person/Age").onChange(new DatabaseChangeListener() {
            @Override
            public void onSuccess(Object value) {
                mAge = Integer.parseInt(value.toString());
            }

            @Override
            public void onFail(String value) {
                Log.e(TAG, value);
            }
        });



        return view;
    }
}

