package com.bilue.board;

import android.app.Application;

/**
 * Created by bilue on 17/3/13.
 */

public class BApplication extends Application {
    private static BApplication application;
    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
    }

    public static BApplication getApp(){
        return application;
    }
}
