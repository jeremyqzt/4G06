package com.example.jeremy.androidwearheartrate.warnings;
/**
 * Created by kevind on 2017-01-24.
 */

public class Warning {

    private int id;

    public Warning(int initId){
        id = initId;
    }

    public int getId(){
        return id;
    }

    public void setId(int idVal){
        id = idVal;
    }

    /**
     *
     * @return message corresponding to Warning type
     */
    public String getResponseMessage(){
        String respMessage = "";
        switch(id){
            case WarningIdKeys.BAD:
                respMessage = WarningMessageKeys.badRESPONSE;
                break;
            case WarningIdKeys.NEUTRAL:
                respMessage = WarningMessageKeys.neutralRESPONSE;
                break;
            case WarningIdKeys.VERYBAD:
                respMessage = WarningMessageKeys.verbadRESPONSE;
                break;
        }
        return respMessage;
    }
}
