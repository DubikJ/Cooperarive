package com.avatlantik.cooperative.component;

import com.avatlantik.cooperative.activity.DocumentDetailsActivity;
import com.avatlantik.cooperative.activity.DocumentsActivity;
import com.avatlantik.cooperative.activity.LandingActivity;
import com.avatlantik.cooperative.activity.LoginActivity;
import com.avatlantik.cooperative.activity.MemberActivity;
import com.avatlantik.cooperative.activity.MemberDetailActivity;
import com.avatlantik.cooperative.activity.MilkReceptionActivity;
import com.avatlantik.cooperative.activity.RegistrationActivity;
import com.avatlantik.cooperative.activity.SearchMemberActivity;
import com.avatlantik.cooperative.activity.ServiceActivity;
import com.avatlantik.cooperative.modules.ActivityUtilsApiModule;
import com.avatlantik.cooperative.modules.DataApiModule;
import com.avatlantik.cooperative.modules.ErrorUtilsApiModule;
import com.avatlantik.cooperative.modules.NetworkUtilsApiModule;
import com.avatlantik.cooperative.modules.PhoneUtilsApiModule;
import com.avatlantik.cooperative.modules.ServiceApiModule;
import com.avatlantik.cooperative.task.SyncIntentService;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {DataApiModule.class, ServiceApiModule.class,
        NetworkUtilsApiModule.class, ActivityUtilsApiModule.class, ErrorUtilsApiModule.class, PhoneUtilsApiModule.class})
public interface DIComponent {
    void inject(LandingActivity activity);

    void inject(LoginActivity activity);

    void inject(MemberActivity memberActivity);

    void inject(SyncIntentService syncIntentService);

    void inject(MemberDetailActivity memberDetailActivity);

    void inject(MilkReceptionActivity manualMilkReceptionActivity);

    void inject(RegistrationActivity registrationActivity);

    void inject(ServiceActivity registrationActivity);

    void inject(DocumentsActivity documentsActivity);

    void inject(SearchMemberActivity searchMemberActivity);

    void inject(DocumentDetailsActivity documentDetailsActivity);
}
