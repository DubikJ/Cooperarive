package com.avatlantik.cooperative.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.avatlantik.cooperative.R;
import com.avatlantik.cooperative.adapter.InfoMemberAdapter;
import com.avatlantik.cooperative.app.CooperativeApplication;
import com.avatlantik.cooperative.db.CooperativeContract.MemberContract;
import com.avatlantik.cooperative.model.ParameterInfo;
import com.avatlantik.cooperative.model.db.DocumentCode;
import com.avatlantik.cooperative.model.db.DocumentStats;
import com.avatlantik.cooperative.model.db.Member;
import com.avatlantik.cooperative.model.db.MemberStats;
import com.avatlantik.cooperative.model.db.MilkParam;
import com.avatlantik.cooperative.model.db.ServiceCode;
import com.avatlantik.cooperative.model.db.ServiceDelivery;
import com.avatlantik.cooperative.model.db.Track;
import com.avatlantik.cooperative.model.db.Visit;
import com.avatlantik.cooperative.repository.DataRepository;
import com.avatlantik.cooperative.util.PhoneUtils;

import org.joda.time.LocalDate;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import static android.os.Environment.DIRECTORY_DCIM;
import static android.os.Environment.getExternalStoragePublicDirectory;
import static com.avatlantik.cooperative.R.id.foto_doc_button;
import static com.avatlantik.cooperative.common.Consts.PHONE_USING;
import static com.avatlantik.cooperative.common.Consts.ROOT_DIR;
import static com.avatlantik.cooperative.common.Consts.TAGLOG;

public class MemberActivity extends FragmentActivity {

    @Inject
    DataRepository dataRepository;
    @Inject
    PhoneUtils phoneUtils;
    private Visit visit;
    private MemberStats memberStats;
    private Track track;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);
        ((CooperativeApplication) getApplication()).getComponent().inject(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        track = dataRepository.getLatestTrack();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            finish();
            return;
        }
        final Member member;
        String qrCode = extras.getString(MemberContract.QR_CODE);
        int id = extras.getInt(MemberContract._ID);
        if (qrCode != null && !qrCode.isEmpty()) {
            member = dataRepository.getMemberByQrCode(qrCode);
            if (member == null) {
                openRegistrationActivity(qrCode);
                return;
            }
        } else if (id > 0) {
            member = dataRepository.getMemberById(id);
        } else {
            finish();
            return;
        }

        if ((visit = dataRepository.getVisit(member.getId(), LocalDate.now().toDate())) == null) {
            dataRepository.saveVisit(createVisit(member, LocalDate.now().toDate()));
            visit = dataRepository.getVisit(member.getId(), LocalDate.now().toDate());
        }

        memberStats = dataRepository.getMemberStats(member.getExternalId());


        TextView memberNameView = (TextView) findViewById(R.id.name_member_TextView);
        memberNameView.setText(member.getName());

        Button receptionMilkButton = (Button) findViewById(R.id.reception_milk_button);
        receptionMilkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAGLOG, "Press button 'reception_milk_button'");
                Intent i = new Intent(getBaseContext(), MilkReceptionActivity.class);
                i.putExtra("member", member);
                i.putExtra("visit", visit);
                startActivity(i);
            }
        });

        Button servicesButton = (Button) findViewById(R.id.services_button);
        servicesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAGLOG, "Press button 'services_button'");
                Intent i = new Intent(getBaseContext(), ServiceActivity.class);
                i.putExtra("member", member);
                i.putExtra("visit", visit);
                startActivity(i);
            }
        });

        FloatingActionButton membDetailButton = (FloatingActionButton) findViewById(R.id.open_member_detail_button);
        membDetailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAGLOG, "Press button 'open member details'");
                Intent i = new Intent(getBaseContext(), MemberDetailActivity.class);
                i.putExtra("member", member);
                startActivity(i);
            }
        });

        Button backButton = (Button) findViewById(R.id.ret_main_menu_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button fotoDocButton = (Button) findViewById(foto_doc_button);
        fotoDocButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), DocumentsActivity.class);
                i.putExtra("member", member);
                startActivity(i);
            }
        });

        Button phoneCallButton = (Button) findViewById(R.id.phonecall_button);
        if(Boolean.valueOf(dataRepository.getUserSetting(PHONE_USING))) {
            phoneCallButton.setVisibility(View.VISIBLE);
        }else{
            phoneCallButton.setVisibility(View.GONE);
        }
        phoneCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAGLOG, "Press button 'send sms'");
                phoneUtils.call(MemberActivity.this, member.getPhone());
            }
        });

        loadMemberData(member, (ListView) findViewById(R.id.info_member_ListView));
    }

    private void openRegistrationActivity(String qrCode) {

        final String qr_code = qrCode;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.questions_member_not_found));

        builder.setPositiveButton(getString(R.string.questions_answer_yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                startActivity(
                        new Intent(getBaseContext(), RegistrationActivity.class)
                                .putExtra(MemberContract.QR_CODE, qr_code));
                dialog.dismiss();
                finish();
            }
        });

        builder.setNegativeButton(getString(R.string.questions_answer_no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
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

    private void loadMemberData(Member member, ListView dataView) {

        ArrayList<ParameterInfo> listInfo = new ArrayList<>();

        chekFotoDocumentList(member.getExternalId(), listInfo);

        addServices(member, listInfo);

        Visit visit = dataRepository.getVisit(member.getId(), LocalDate.now().toDate());
        Double visitVolume = 0.0;
        if (visit != null) {
            MilkParam milkParam = dataRepository.getMilkParams(visit.getId());
            visitVolume = milkParam == null ? 0.0 : milkParam.getVolume();
        }

        listInfo.add(new ParameterInfo(getString(R.string.milk_volume_month),
                String.valueOf(memberStats!=null ? memberStats.getMilkVolume() : 0.0 + visitVolume) + " "
                        + getResources().getString(R.string.landing_liters_name_short)));

        if (memberStats != null) {
//            listInfo.add(new ParameterInfo(getString(R.string.credit_member),
//                    String.valueOf(memberStats.getMemberLoan()) + " "
//                            + getResources().getString(R.string.landing_money_name_short)));
//            listInfo.add(new ParameterInfo(getString(R.string.credit_company),
//                    String.valueOf(memberStats.getCompanyLoan()) + " "
//                            + getResources().getString(R.string.landing_money_name_short)));
        }
        InfoMemberAdapter adapter = new InfoMemberAdapter(this, listInfo);
        dataView.setAdapter(adapter);

    }

    private Visit createVisit(Member member, Date date) {
        return Visit.builder().memberId(member.getId()).date(date).build();
    }

    private void chekFotoDocumentList(String memberId, ArrayList<ParameterInfo> listInfo) {


        List<DocumentCode> allDocumentCodes = dataRepository.getDocumentCodes();

        List<DocumentStats> ExistedMemberDocs = dataRepository.getDocStatsByMemberId(memberId);

        boolean dirDcimNotExsist = false;
        File directory = getExternalStoragePublicDirectory(DIRECTORY_DCIM);
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                dirDcimNotExsist = true;
            }
        }

        boolean fileDirNotExist = false;
        File dir = new File(directory.getPath() + File.separator
                + ROOT_DIR + File.separator
                + memberId);
        if (!dir.exists()) fileDirNotExist = true;

        int numDocs = 0;
        for (DocumentCode doc : allDocumentCodes) {

            boolean breakParentLoop = false;
            for (DocumentStats docStat : ExistedMemberDocs) {
                if (!docStat.isValue()) continue;
                if (doc.getExternalId().equals(docStat.getCode())) {
                    numDocs++;
                    breakParentLoop = true;
                    break;
                }
            }

            if (breakParentLoop) continue;

            if (dirDcimNotExsist) continue;

            if (fileDirNotExist) continue;

            File file = new File(dir, File.separator + doc.getExternalId() + ".jpg");

            if (file.exists()) numDocs++;
        }

        if (numDocs == allDocumentCodes.size()) {
            listInfo.add(new ParameterInfo(getString(R.string.with_photo_documents), getString(R.string.sull_list_foto), false));
        } else {
            listInfo.add(new ParameterInfo(getString(R.string.with_photo_documents), getString(R.string.not_sull_list_foto), false));
        }
    }

    private void addServices(Member member, ArrayList<ParameterInfo> listInfo) {

        List<ServiceCode> serviceCodes = dataRepository.getServiceCodes();
        List<ServiceDelivery> servicesDelivery = dataRepository.getDeliveryServices(track.getExternalId(), member.getExternalId());

        if(servicesDelivery == null || serviceCodes == null) return;

        String services = "";
        for (ServiceCode serviceCode : serviceCodes) {
            for (ServiceDelivery serviceDelivery : servicesDelivery) {
                if (serviceCode.getExternalId().equals(serviceDelivery.getCode())) {
                    services = services + serviceCode.getName() + ", ";
                    break;
                }
            }
        }

        if (!services.isEmpty()) {
            listInfo.add(new ParameterInfo(getString(R.string.services), services.substring(0, services.length() - 2), false));
        }

        if(member.getQrcode() == null || member.getQrcode().isEmpty() ||
                member.getPhone() == null || member.getPhone().isEmpty()) {

            listInfo.add(new ParameterInfo(getString(R.string.not_full_info),
                    String.valueOf(member.getQrcode() == null || member.getQrcode().isEmpty() ?
                            getString(R.string.member_qr_code)+ ", " : "")
                            + String.valueOf(member.getPhone() == null || member.getPhone().isEmpty() ?
                            getString(R.string.member_phone) : "")));

        }
    }

}
