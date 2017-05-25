package com.avatlantik.cooperative.modules;


import com.avatlantik.cooperative.util.ActivityUtils;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ActivityUtilsApiModule {

    public ActivityUtilsApiModule() {
    }

    @Provides
    @Singleton
    public ActivityUtils getActivityUtils() {
        return new ActivityUtils();
    }
}
