package com.example.jeremy.androidwearheartrate;

/**
 * Created by Jeremy on 2017-03-28.
 */

public class Person {
    String Gender, Conditions, Name;
    Integer Age;
    public void setGender(String igender){
        this.Gender = igender;
    }
    public void setName(String iname){
        this.Name = iname;
    }
    public void setConditions(String iconditions){
        this.Conditions = iconditions;
    }
    public void setAge(Integer iage){
        this.Age = iage;
    }

    public String getConditions(){
        return Conditions;
    }
    public String getGender(){
        return Gender;
    }
    public String getName(){
        return Name;
    }
    public Integer getAge(){
        return Age;
    }
}
