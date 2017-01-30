package com.example.jeremy.androidwearheartrate.warnings;

/**
 * Created by kevind on 2017-01-25.
 */

public class BadWarning extends Warning{

    public BadWarning(){
        super();
        this.id = WarningIdKeys.bad;
        this.response = WarningMessageKeys.badMessage;
    }

    @Override
    public void Compare(){

    }
}
