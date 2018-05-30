package com.walinns.walinnsapi;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

/**
 * Created by walinnsinnovation on 04/04/18.
 */

public class WAApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
