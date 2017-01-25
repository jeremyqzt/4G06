package com.example.jeremy.androidwearheartrate.warnings;

/**
 * Created by kevind on 2017-01-24.
 */

public abstract class Warning {

    private int id;
    private String message;
    private String type;

    public Warning(String type){
        this.type = type;
    }

    /**
     *
     * @return message corresponding to Warning type
     */
    public String getResponseMessage(){
        String respMessage;
        switch(id){
            case 0 :
                respMessage = ""
        }
    }
}
