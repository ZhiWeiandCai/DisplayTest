package com.liftcore.android.displaytest;

import android.app.Application;

import com.liftcore.android.displaytest.model.Constants;
import com.liftcore.android.displaytest.util.Utils;

/**
 * Created by Harry on 7/25/2017.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Constants.SDCardPath = Utils.getInnerSDCardPath();
    }

}
