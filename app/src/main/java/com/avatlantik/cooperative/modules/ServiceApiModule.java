package com.avatlantik.cooperative.modules;


import android.app.Application;

import com.avatlantik.cooperative.service.SettingsService;
import com.avatlantik.cooperative.service.SettingsServiceImpl;
import com.avatlantik.cooperative.service.SyncService;
import com.avatlantik.cooperative.task.SyncServiceFactory;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;


@Module
public class ServiceApiModule {

    private Application application;

    public ServiceApiModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    public SyncService getSyncService() {

        return SyncServiceFactory.createService(
                SyncService.class,
                application.getBaseContext());
    }

    @Provides
    @Singleton
    public SettingsService getSettingService() {
        return new SettingsServiceImpl(application.getBaseContext());
    }
}
