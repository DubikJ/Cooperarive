package com.avatlantik.cooperative.modules;


import android.app.Application;

import com.avatlantik.cooperative.util.PhoneUtils;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class PhoneUtilsApiModule {


    public PhoneUtilsApiModule(Application application) {
    }

    @Provides
    @Singleton
    public PhoneUtils getPhoneUtils() {
        return new PhoneUtils();
    }
}
