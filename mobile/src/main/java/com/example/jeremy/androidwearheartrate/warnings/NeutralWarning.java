package com.example.jeremy.androidwearheartrate.warnings;

import com.google.android.gms.nearby.Nearby;

/**
 * Created by kevind on 2017-01-25.
 */

public class NeutralWarning extends Warning {

    public NeutralWarning(){
        super();
        this.id = WarningIdKeys.neutral;
        this.response = WarningMessageKeys.neutralMessage;
    }

    @Override
    void Compare() {
        //implement later
    }

}
