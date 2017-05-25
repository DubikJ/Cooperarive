package com.avatlantik.cooperative.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.avatlantik.cooperative.R;
import com.avatlantik.cooperative.adapter.ServicesListAdapter;
import com.avatlantik.cooperative.adapter.treelistview.Element;
import com.avatlantik.cooperative.adapter.treelistview.TreeViewItemClickListener;
import com.avatlantik.cooperative.app.CooperativeApplication;
import com.avatlantik.cooperative.model.db.Member;
import com.avatlantik.cooperative.model.db.ServiceCode;
import com.avatlantik.cooperative.model.db.ServiceDemand;
import com.avatlantik.cooperative.model.db.Visit;
import com.avatlantik.cooperative.repository.DataRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static com.avatlantik.cooperative.adapter.treelistview.Element.NO_PARENT;
import static com.avatlantik.cooperative.adapter.treelistview.Element.TOP_LEVEL;
import static com.avatlantik.cooperative.common.Consts.CLEAR_GUID;
import static com.avatlantik.cooperative.common.Consts.TAGLOG;

public class ServiceActivity extends FragmentActivity {
    @Inject
    DataRepository dataRepository;

    private TextView nameServMembTextView;
    private ListView serviceListView;
    private ServicesListAdapter adapter;
    private Member member;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services);
        ((CooperativeApplication) getApplication()).getComponent().inject(this);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            finish();
            return;
        }

        member = getIntent().getExtras() != null
                ? (Member) getIntent().getExtras().get("member")
                : null;

        Visit visit = getIntent().getExtras() != null
                ? (Visit) getIntent().getExtras().get("visit")
                : null;

        initElementsForm();
        initData(visit, member);
    }

    private void initElementsForm() {

        nameServMembTextView = (TextView) findViewById(R.id.name_member_service_TextView);
        serviceListView = (ListView) findViewById(R.id.service_ListView);

        Button retMemberButton = (Button) findViewById(R.id.ret_member_button);
        retMemberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button saveServButton = (Button) findViewById(R.id.save_service_button);
        saveServButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAGLOG, "Press button 'save services'");
                saveDataInBase();
                finish();
            }
        });

    }

    private void saveDataInBase(){
        for (int i = 0; i < adapter.getCount(); i++) {
            ServiceDemand item = (ServiceDemand) adapter.getItemServiceDemand(i);
            dataRepository.saveServiceDemand(item);
        }
        dataRepository.setMemberChanged(member.getExternalId());
    }

    private void initData(Visit visit, Member member) {

        nameServMembTextView.setText(getResources().getString(R.string.request_for_services) + ": " + member.getName());

        List<ServiceDemand> serviceDemands = dataRepository.getDemandServices(visit.getId());


        ArrayList<Element> serviceCodesParent = new ArrayList<Element>();
        ArrayList<Element> serviceCodesChild = new ArrayList<Element>();

        List<ServiceCode> serviceCodes = dataRepository.getServiceCodes();

        addingElement(CLEAR_GUID, serviceCodesParent, serviceCodesChild, serviceCodes, TOP_LEVEL);

        adapter = new ServicesListAdapter(this, visit, serviceCodesParent, serviceCodesChild, serviceDemands);
        TreeViewItemClickListener treeViewItemClickListener = new TreeViewItemClickListener(this, adapter);
        serviceListView.setAdapter(adapter);
        serviceListView.setOnItemClickListener(treeViewItemClickListener);

    }

    private void addingElement(String externalId, ArrayList<Element> serviceCodesParent, ArrayList<Element> serviceCodesChild,
                               List<ServiceCode> serviceCodesAll, int level){

        List<ServiceCode> serviceCodes = dataRepository.getServiceCodesByParent(externalId);

        if(serviceCodes.size() == 0) return;

        for(ServiceCode mServCode : serviceCodes){
            Boolean notHaveParent = mServCode.getParentId().equals(CLEAR_GUID);
            Boolean haveChild = dataRepository.getServiceCodesByParent(mServCode.getExternalId()).size() > 0;

            int idParent = getIdInServiceCodesAllByElement(serviceCodesAll, mServCode.getParentId());
            int idElement = getIdInServiceCodesAllByElement(serviceCodesAll, mServCode.getExternalId());
            Element element = new Element(mServCode.getName(), TOP_LEVEL + level, idElement, idParent,
                    haveChild, false, mServCode.getExternalId(), mServCode.getParentId());
            if(notHaveParent) {
                serviceCodesParent.add(element);
            }else {
                serviceCodesChild.add(element);
            }
            addingElement(mServCode.getExternalId(), serviceCodesParent, serviceCodesChild, serviceCodesAll,  (level+1));
        }

    }

    private int getIdInServiceCodesAllByElement(List<ServiceCode> serviceCodesAll, String externalId) {
        for (ServiceCode mServCodeParent : serviceCodesAll) {
            if (externalId.equals(mServCodeParent.getExternalId())) {
                return serviceCodesAll.indexOf(mServCodeParent);
            }
        }

        return NO_PARENT;
    }

}
