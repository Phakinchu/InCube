package com.cwm.incube;

import android.app.Application;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Phakin on 2/14/2018.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initFont();
        // do something when app start.
    }
    private void initFont() {
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("font/notosansthailight.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }
}
