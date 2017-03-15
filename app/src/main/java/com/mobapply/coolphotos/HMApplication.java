package com.mobapply.coolphotos;

import android.app.Application;

import com.google.android.gms.ads.MobileAds;

public class HMApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-6973932400636393~3338523065");
    }
}
