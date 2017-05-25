package com.avatlantik.cooperative.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.avatlantik.cooperative.R;
import com.avatlantik.cooperative.adapter.InfoMilkAdapter;
import com.avatlantik.cooperative.app.CooperativeApplication;
import com.avatlantik.cooperative.ekomilk.EkoMilk;
import com.avatlantik.cooperative.model.ParameterInfo;
import com.avatlantik.cooperative.model.db.Member;
import com.avatlantik.cooperative.model.db.Milk;
import com.avatlantik.cooperative.model.db.MilkParam;
import com.avatlantik.cooperative.model.db.Visit;
import com.avatlantik.cooperative.repository.DataRepository;
import com.avatlantik.cooperative.service.SettingsService;
import com.avatlantik.cooperative.service.SettingsService.ConnectionType;
import com.avatlantik.cooperative.service.SettingsService.DeviceType;
import com.avatlantik.cooperative.util.ActivityUtils;
import com.avatlantik.cooperative.util.Consumer;
import com.avatlantik.cooperative.util.NetworkUtils;
import com.avatlantik.cooperative.util.PhoneUtils;

import org.joda.time.LocalDate;

import java.util.ArrayList;

import javax.inject.Inject;

import static com.avatlantik.cooperative.common.Consts.ECOMILK_SYNC;
import static com.avatlantik.cooperative.common.Consts.EKOMILK_CONNECTION_TYPE;
import static com.avatlantik.cooperative.common.Consts.EKOMILK_DEVICE_TYPE;
import static com.avatlantik.cooperative.common.Consts.EKOMILK_USING;
import static com.avatlantik.cooperative.common.Consts.FLOWMETER_USING;
import static com.avatlantik.cooperative.common.Consts.MILK_ADDEDWATER;
import static com.avatlantik.cooperative.common.Consts.MILK_CONDUCTIVITY;
import static com.avatlantik.cooperative.common.Consts.MILK_DENCITY;
import static com.avatlantik.cooperative.common.Consts.MILK_FAT;
import static com.avatlantik.cooperative.common.Consts.MILK_FP;
import static com.avatlantik.cooperative.common.Consts.MILK_PROTEIN;
import static com.avatlantik.cooperative.common.Consts.MILK_SNF;
import static com.avatlantik.cooperative.common.Consts.MILK_VOLUME;
import static com.avatlantik.cooperative.common.Consts.PHONE_USING;
import static com.avatlantik.cooperative.common.Consts.TAGLOG;

public class MilkReceptionActivity extends FragmentActivity {

    private EkoMilk ekomilk;

    @Inject
    DataRepository dataRepository;
    @Inject
    SettingsService settingsService;
    @Inject
    NetworkUtils networkUtils;
    @Inject
    ActivityUtils activityUtils;
    @Inject
    PhoneUtils phoneUtils;

    private ListView infoMRListView;
    private boolean isEkomilk, isFlowMeter, isUsingDevices;
    private Member member;
    private Visit visit;
    private ProgressDialog progressDialog;
    private ConnectionType connectionType;
    private ListAdapter adapter;
    private  TextView milkParamTypeTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_milk_reception);
        ((CooperativeApplication) getApplication()).getComponent().inject(this);

        String connectionTypeName = dataRepository.getUserSetting(EKOMILK_CONNECTION_TYPE);

        connectionType = connectionTypeName == null || connectionTypeName.isEmpty()
                ? ConnectionType.WIFI
                : ConnectionType.valueOf(dataRepository.getUserSetting(EKOMILK_CONNECTION_TYPE));

        ekomilk = EkoMilk.via(connectionType, this);
        ekomilk.initLoaderManager(getLoaderManager(), new Consumer<Milk>() {
            @Override
            public void apply(Milk data) {
            }
        });

        infoMRListView = (ListView) findViewById(R.id.info_milk_rec_ListView);
        Bundle extras = getIntent().getExtras();
        if (extras == null || extras.get("member") == null || extras.get("visit") == null) {
            finish();
            return;
        }

        member = (Member) extras.get("member");
        visit = (Visit) extras.get("visit");

        isUsingDevices = Boolean.valueOf(dataRepository.getUserSetting(EKOMILK_USING)) ||
                Boolean.valueOf(dataRepository.getUserSetting(FLOWMETER_USING));

        TextView nameView = (TextView) findViewById(R.id.name_milk_rec_TextView);
        nameView.setText(getResources().getString(R.string.reception_milk) + ": " + member.getName());

        TextView connectionView = (TextView) findViewById(R.id.milk_sync_textView);
        connectionView.setText(connectionType.name().toString() + " " + getResources().getString(R.string.connection_name));

        LinearLayout connectionLL = (LinearLayout) findViewById(R.id.milk_sync_ll);
        connectionLL.setVisibility(isUsingDevices ? View.VISIBLE : View.INVISIBLE);

        Button retMemberButton = (Button) findViewById(R.id.ret_member_button);
        retMemberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        milkParamTypeTextView = (TextView) findViewById(R.id.milk_param_type_sync_textView);
        milkParamTypeTextView.setText(getResources().getString(R.string.params_from_database));

        refresh(dataRepository.getMilkStats(member.getExternalId()));

        isEkomilk = false;
        isFlowMeter = false;

        Button sendSmsButton = (Button) findViewById(R.id.send_sms_button);
        if(Boolean.valueOf(dataRepository.getUserSetting(PHONE_USING))) {
            sendSmsButton.setVisibility(View.VISIBLE);
        }else{
            sendSmsButton.setVisibility(View.INVISIBLE);
        }
        sendSmsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAGLOG, "Press button 'send sms'");
                phoneUtils.sendSMS(MilkReceptionActivity.this, member.getPhone(), getTextMessage());
            }
        });

        Button saveButton = (Button) findViewById(R.id.save_milk_rec_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAGLOG, "Press button 'save milk reception'");
                MilkParam milkParam = MilkParam.builder()
                        .visitId(visit.getId())
                        .fat(getValue(((ParameterInfo) adapter.getItem(6)).getValue()))
                        .snf(getValue(((ParameterInfo) adapter.getItem(0)).getValue()))
                        .dencity(getValue(((ParameterInfo) adapter.getItem(1)).getValue()))
                        .addedWater(getValue(((ParameterInfo) adapter.getItem(2)).getValue()))
                        .fp(getValue(((ParameterInfo) adapter.getItem(3)).getValue()))
                        .protein(getValue(((ParameterInfo) adapter.getItem(4)).getValue()))
                        .conductivity(getValue(((ParameterInfo) adapter.getItem(5)).getValue()))
                        .volume(getValue(((ParameterInfo) adapter.getItem(7)).getValue()))
                        .isEkomilk(isEkomilk)
                        .build();
                dataRepository.saveMilkParam(milkParam);
                dataRepository.setMemberChanged(member.getExternalId());

                if(isUsingDevices && !isCurrectlyConnection()){
                    printDialog(getString(R.string.сheck_not_received));
                    return;}

                ekomilk.print(member.getName(), milkParam);
                printDialog(getString(R.string.сheck_received));
            }
        });
        clearCash();

        Button startEcomilkButton = (Button) findViewById(R.id.start_ecomilk_button);
        if(Boolean.valueOf(dataRepository.getUserSetting(EKOMILK_USING))) {
            startEcomilkButton.setVisibility(View.VISIBLE);
        }else{
            startEcomilkButton.setVisibility(View.INVISIBLE);
        }
        startEcomilkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAGLOG, "Press button 'start ecomilk analyse'");
                if(!isCurrectlyConnection()){
                    return;
                }

                settingsService.saveData(ECOMILK_SYNC, String.valueOf(true));

                progressDialog.setTitle(getResources().getString(R.string.syncing_Ecomilk));
                showProgressDialog(true);

                ekomilk.start(
                        new Consumer<Milk>() {
                            @Override
                            public void apply(Milk milk) {
                                showProgressDialog(false);
                                clearCash();
                                if (milk != null) {
                                    milkParamTypeTextView.setText(getResources().getString(R.string.params_from_device));
                                    refresh(insertMilkParams(milk));
                                    isEkomilk = true;
                                } else {
                                    activityUtils.showMessage(getString(R.string.ekomilk_error), MilkReceptionActivity.this);
                                }
                            }
                        }, DeviceType.ECOMILK);
            }
        });

        Button startFlowmeterButton = (Button) findViewById(R.id.start_flowmeter_button);
        if(Boolean.valueOf(dataRepository.getUserSetting(FLOWMETER_USING))) {
            startFlowmeterButton.setVisibility(View.VISIBLE);
        }else{
            startFlowmeterButton.setVisibility(View.INVISIBLE);
        }
        startFlowmeterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAGLOG, "Press button 'start flowmeter analyse'");
                if(!isCurrectlyConnection()){
                    return;
                }
                settingsService.saveData(ECOMILK_SYNC, String.valueOf(true));

                progressDialog.setTitle(getResources().getString(R.string.syncing_flowmeter));
                showProgressDialog(true);

                ekomilk.start(
                        new Consumer<Milk>() {
                            @Override
                            public void apply(Milk milk) {
                                showProgressDialog(false);
                                clearCash();
                                if (milk != null) {
                                    refresh(insertMilkVolume(milk));
                                    isFlowMeter = true;
                                } else {
                                    activityUtils.showMessage(getString(R.string.ekomilk_error), MilkReceptionActivity.this);
                                }
                            }
                        }, DeviceType.FLOWMETER);
            }
        });

        initProgressDialog();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Milk milk = readFromCash();
        if (milk != null) {
            refresh(milk);
        }

        if(!Boolean.valueOf(settingsService.readData(ECOMILK_SYNC)) && progressDialog.isShowing()) {
            showProgressDialog(false);
        }

    }

    private boolean isCurrectlyConnection(){

        if(connectionType == ConnectionType.WIFI) {
            if (!networkUtils.WIFISwitch()) {
                activityUtils.showMessage(getResources().getString(R.string.error_wifi_switch), MilkReceptionActivity.this);
                return false;
            }
            if (!networkUtils.checkWIFIconnectionToEcomilk()) {
                activityUtils.showMessage(getResources().getString(R.string.error_wifi_ecomilk_connection), MilkReceptionActivity.this);
                return false;
            }
        }

//        if(connectionType == ConnectionType.USB) {
//            if (!networkUtils.checkUSBconnectionToEcomilk()) {
//                activityUtils.showMessage(getResources().getString(R.string.error_usb_ecomilk_connection), MilkReceptionActivity.this);
//                return false;
//            }
//        }

        return true;

    }

    private void printDialog(String textQuestion) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(textQuestion);

        builder.setPositiveButton(getString(R.string.questions_answer_yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });

        builder.setNegativeButton(getString(R.string.questions_answer_no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }

        });
        AlertDialog alert = builder.create();
        alert.show();

        // TODO (start stub): to set size text in AlertDialog
        TextView textView = (TextView) alert.findViewById(android.R.id.message);
        textView.setTextSize(getResources().getDimension(R.dimen.text_size_medium));
        Button button1 = (Button) alert.findViewById(android.R.id.button1);
        button1.setTextSize(getResources().getDimension(R.dimen.text_size_medium));
        Button button2 = (Button) alert.findViewById(android.R.id.button2);
        button2.setTextSize(getResources().getDimension(R.dimen.text_size_medium));
        // TODO: (end stub) ------------------
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ekomilk.stop();
        clearCash();
    }

    private void refresh(Milk milk) {
        Boolean usingEcomilk = Boolean.valueOf(dataRepository.getUserSetting(EKOMILK_USING));

        Boolean usingFlowmeter = Boolean.valueOf(dataRepository.getUserSetting(FLOWMETER_USING));

        ArrayList<ParameterInfo> listInfo = new ArrayList<>();

        listInfo.add(new ParameterInfo(
                getResources().getString(R.string.milk_snf),
                String.valueOf(milk == null ? 0.0 : milk.getSnf())));
        listInfo.add(new ParameterInfo(
                getResources().getString(R.string.milk_dencity),
                String.valueOf(milk == null ? 0.0 : milk.getDencity())));
        listInfo.add(new ParameterInfo(
                getResources().getString(R.string.milk_adw),
                String.valueOf(milk == null ? 0.0 : milk.getAddedWater())));
        listInfo.add(new ParameterInfo(
                getResources().getString(R.string.milk_fp),
                String.valueOf(milk == null ? 0.0 : milk.getFp())));
        listInfo.add(new ParameterInfo(
                getResources().getString(R.string.milk_protein),
                String.valueOf(milk == null ? 0.0 : milk.getProtein())));
        listInfo.add(new ParameterInfo(
                getResources().getString(R.string.milk_conductivity),
                String.valueOf(milk == null ? 0.0 : milk.getConductivity())));
        listInfo.add(new ParameterInfo(
                getResources().getString(R.string.milk_fat),
                String.valueOf(milk == null ? 0.0 : milk.getFat()),
                usingEcomilk ? false : true));
        listInfo.add(new ParameterInfo(
                getResources().getString(R.string.milk_volume),
                String.valueOf(milk == null ? 0.0 : milk.getVolume()),
                usingFlowmeter ? false : true));

        adapter = new InfoMilkAdapter(this, listInfo);
        infoMRListView.setAdapter(adapter);

    }

    private MilkParam insertMilkVolume(Milk milk) {
        return MilkParam.builder()
                .visitId(visit.getId())
                .fat(Double.valueOf(((ParameterInfo) adapter.getItem(6)).getValue()))
                .snf(Double.valueOf(((ParameterInfo) adapter.getItem(0)).getValue()))
                .dencity(Double.valueOf(((ParameterInfo) adapter.getItem(1)).getValue()))
                .addedWater(Double.valueOf(((ParameterInfo) adapter.getItem(2)).getValue()))
                .fp(Double.valueOf(((ParameterInfo) adapter.getItem(3)).getValue()))
                .protein(Double.valueOf(((ParameterInfo) adapter.getItem(4)).getValue()))
                .conductivity(Double.valueOf(((ParameterInfo) adapter.getItem(5)).getValue()))
                .volume(milk.getVolume())
                .isEkomilk(isEkomilk)
                .build();
    }

    private MilkParam insertMilkParams(Milk milk) {
        return MilkParam.builder()
                .visitId(visit.getId())
                .fat(milk.getFat())
                .snf(milk.getSnf())
                .dencity(milk.getDencity())
                .addedWater(milk.getAddedWater())
                .fp(milk.getFp())
                .protein(milk.getProtein())
                .conductivity(milk.getConductivity())
                .volume(Double.valueOf(((ParameterInfo) adapter.getItem(7)).getValue()))
                .isEkomilk(isEkomilk)
                .build();
    }

    private void initProgressDialog() {

        progressDialog = new ProgressDialog(this);

        progressDialog.setIcon(R.drawable.ic_milk);

        progressDialog.setTitle(getResources().getString(R.string.syncing_Ecomilk));

        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        progressDialog.setCanceledOnTouchOutside(false);

        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(R.string.cancel_name), new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int whichButton){
                ekomilk.cancel();
                showProgressDialog(false);
            }
        });

        progressDialog.setProgress(0);

    }

    private void showProgressDialog(final boolean show) {

        if (show) {
            progressDialog.show();
        } else {
            progressDialog.dismiss();
        }
    }

    private Milk readFromCash() {
        MilkParam milkParam;
        String fat = settingsService.readData(MILK_FAT);
        String snf = settingsService.readData(MILK_SNF);
        String dencity = settingsService.readData(MILK_DENCITY);
        String addedWater = settingsService.readData(MILK_ADDEDWATER);
        String fp = settingsService.readData(MILK_FP);
        String protein = settingsService.readData(MILK_PROTEIN);
        String conductivity = settingsService.readData(MILK_CONDUCTIVITY);
        String volume = settingsService.readData(MILK_VOLUME);
        String deviTypeName = settingsService.readData(EKOMILK_DEVICE_TYPE);
        DeviceType deviceType = deviTypeName == null || deviTypeName.isEmpty()
                ? DeviceType.ECOMILK
                : DeviceType.valueOf(settingsService.readData(EKOMILK_DEVICE_TYPE));

        if ((fat == null || fat.isEmpty()) && (snf == null || snf.isEmpty()) && (dencity == null || dencity.isEmpty())
                && (addedWater == null || addedWater.isEmpty()) && (fp == null || fp.isEmpty())
                && (protein == null || protein.isEmpty()) && (conductivity == null || conductivity.isEmpty())
                && (volume == null || volume.isEmpty())) {

            return null;

        }

        milkParam = MilkParam.builder()
                .visitId(visit.getId())
                .fat(fat == null || fat.isEmpty() ? 0.0 : Double.valueOf(fat))
                .snf(snf == null || snf.isEmpty() ? 0.0 : Double.valueOf(snf))
                .dencity(dencity == null || dencity.isEmpty() ? 0.0 : Double.valueOf(dencity))
                .addedWater(addedWater == null || addedWater.isEmpty() ? 0.0 : Double.valueOf(addedWater))
                .fp(fp == null || fp.isEmpty() ? 0.0 : Double.valueOf(fp))
                .protein(protein == null || protein.isEmpty() ? 0.0 : Double.valueOf(protein))
                .conductivity(conductivity == null || conductivity.isEmpty() ? 0.0 : Double.valueOf(conductivity))
                .volume(volume == null || volume.isEmpty() ? 0.0 : Double.valueOf(volume))
                .isEkomilk(isEkomilk)
                .build();


        if (deviceType==DeviceType.FLOWMETER){
            return insertMilkVolume(milkParam);
        }else {
            return insertMilkParams(milkParam);
        }

    }

    private void clearCash() {

        settingsService.saveData(MILK_FAT, "");
        settingsService.saveData(MILK_SNF, "");
        settingsService.saveData(MILK_DENCITY, "");
        settingsService.saveData(MILK_ADDEDWATER, "");
        settingsService.saveData(MILK_FP, "");
        settingsService.saveData(MILK_PROTEIN, "");
        settingsService.saveData(MILK_CONDUCTIVITY, "");
        settingsService.saveData(MILK_VOLUME, "");
        settingsService.saveData(EKOMILK_DEVICE_TYPE, "");

        settingsService.saveData(ECOMILK_SYNC, String.valueOf(false));

    }

    private String getTextMessage() {

        ListAdapter adapter = infoMRListView.getAdapter();

        return String.valueOf(LocalDate.now())+" vy zdaly "+
                Double.valueOf(((ParameterInfo) adapter.getItem(7)).getValue())+
                        "lytr moloka: zhir: "+Double.valueOf(((ParameterInfo) adapter.getItem(6)).getValue())+"%, " +
                        "somo: "+Double.valueOf(((ParameterInfo) adapter.getItem(0)).getValue())+"%, " +
                        "plotn.: "+(1000+Double.valueOf(((ParameterInfo) adapter.getItem(1)).getValue()))+", " +
                        "voda: "+Double.valueOf(((ParameterInfo) adapter.getItem(2)).getValue())+"%, " +
                        "temp.zam: "+Double.valueOf(((ParameterInfo) adapter.getItem(3)).getValue())+", " +
                        "belok: "+Double.valueOf(((ParameterInfo) adapter.getItem(4)).getValue())+"%, " +
                        "provod: "+Double.valueOf(((ParameterInfo) adapter.getItem(5)).getValue());

    }

    private Double getValue(String value) {

        if (value==null || value.isEmpty()) return 0.0;

        return Double.valueOf(value);

    }
}