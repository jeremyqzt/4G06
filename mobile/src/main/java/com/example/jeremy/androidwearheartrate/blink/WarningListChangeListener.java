package com.example.jeremy.androidwearheartrate.blink;

import com.example.jeremy.androidwearheartrate.warnings.Warning;

import java.util.List;

/**
 * Created by kevind on 2017-01-31.
 */

public interface WarningListChangeListener {

    void onWarningChange(List<Warning> wList);

}
