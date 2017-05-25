package com.avatlantik.cooperative.app;


import android.app.Application;

import com.avatlantik.cooperative.component.DIComponent;
import com.avatlantik.cooperative.component.DaggerDIComponent;
import com.avatlantik.cooperative.modules.ActivityUtilsApiModule;
import com.avatlantik.cooperative.modules.DataApiModule;
import com.avatlantik.cooperative.modules.ErrorUtilsApiModule;
import com.avatlantik.cooperative.modules.NetworkUtilsApiModule;
import com.avatlantik.cooperative.modules.PhoneUtilsApiModule;
import com.avatlantik.cooperative.modules.ServiceApiModule;

import net.danlew.android.joda.JodaTimeAndroid;

public class CooperativeApplication extends Application {

    DIComponent diComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        JodaTimeAndroid.init(this);
        diComponent = DaggerDIComponent.builder()
                .dataApiModule(new DataApiModule(this))
                .serviceApiModule(new ServiceApiModule(this))
                .networkUtilsApiModule(new NetworkUtilsApiModule(this))
                .activityUtilsApiModule(new ActivityUtilsApiModule())
                .errorUtilsApiModule(new ErrorUtilsApiModule(this))
                .phoneUtilsApiModule(new PhoneUtilsApiModule(this))
                .build();
    }

    public DIComponent getComponent() {
        return diComponent;
    }
}
