package com.avatlantik.cooperative.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.avatlantik.cooperative.R;
import com.avatlantik.cooperative.app.CooperativeApplication;
import com.avatlantik.cooperative.db.CooperativeContract.MemberContract;
import com.avatlantik.cooperative.db.CooperativeContract.TrackMemberContract;
import com.avatlantik.cooperative.model.db.Member;
import com.avatlantik.cooperative.repository.DataRepository;

import java.util.UUID;

import javax.inject.Inject;

import static com.avatlantik.cooperative.common.Consts.TAGLOG;

public class RegistrationActivity extends FragmentActivity {

    @Inject
    DataRepository dataRepository;

    private TextView mDtNameView, mDtAdressView, mDtPhoneView, mDtQRcodeView;
    private String qr_code;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_details);
        ((CooperativeApplication) getApplication()).getComponent().inject(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            finish();
            return;
        }

        qr_code = extras.getString(MemberContract.QR_CODE);

        final String trackId = extras.getString(TrackMemberContract.TRACK_EXTERNAL_ID);

        Log.d(TAGLOG, "qr_code is: " + qr_code);

        Member member = null;

        if (qr_code != null && !qr_code.isEmpty()) {
            member = dataRepository.getMemberByQrCode(qr_code);
        }

        if (member != null) {
            Toast.makeText(this, "Member is registered", Toast.LENGTH_SHORT).show();
            startActivity(
                    new Intent(getBaseContext(), MemberActivity.class)
                            .putExtra(MemberContract.QR_CODE, qr_code));
            finish();
            return;
        }

        mDtNameView = (EditText) findViewById(R.id.member_detail_name);
        mDtQRcodeView = (EditText) findViewById(R.id.member_detail_qrcode);
        mDtQRcodeView.setText(qr_code);

        FloatingActionButton getQRcodeButton = (FloatingActionButton) findViewById(R.id.member_detail_getQRcode);
        getQRcodeButton.setVisibility(View.GONE);

        mDtAdressView = (EditText) findViewById(R.id.member_detail_adress);
        mDtPhoneView = (EditText) findViewById(R.id.member_detail_phone);
        mDtPhoneView.addTextChangedListener(new TextWatcher() {
            int length_before = 0;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                length_before = s.length();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (length_before < s.length()) {
                    if (s.length() == 1) {
                        if (Character.isDigit(s.charAt(0)))
                            s.insert(0, "(");
                    }
                    if (s.length() == 4) {
                        s.append(")");
                        if (s.length() > 4) {
                            if (Character.isDigit(s.charAt(4)))
                                s.insert(4, ")");
                        }
                    }
                    if (s.length() == 8 || s.length() == 11) {
                        s.append("-");
                        if (s.length() > 8) {
                            if (Character.isDigit(s.charAt(8)))
                                s.insert(8, "-");
                        }
                        if (s.length() > 11) {
                            if (Character.isDigit(s.charAt(11)))
                                s.insert(11, "-");
                        }
                    }
                }
            }
        });

        Button saveBt = (Button) findViewById(R.id.member_detail_save_button);
        saveBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData(trackId);
            }
        });

        Button retBt = (Button) findViewById(R.id.ret_member_detail_button);
        retBt.setFocusable(true);
        retBt.setFocusableInTouchMode(true);
        retBt.requestFocus();
        retBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationActivity.this);
                builder.setMessage(getString(R.string.questions_member_datail_save));

                builder.setPositiveButton(getString(R.string.questions_answer_yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        saveData(trackId);
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
        });


    }

    private void saveData(String trackId) {

        mDtNameView.setError(null);
        mDtPhoneView.setError(null);

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(mDtNameView.getText().toString())) {
            mDtNameView.setError(getString(R.string.error_field_required));
            focusView = mDtNameView;
            cancel = true;
        }

        if (TextUtils.isEmpty(mDtPhoneView.getText().toString())) {
            mDtPhoneView.setError(getString(R.string.error_field_required));
            focusView = mDtPhoneView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            String externalId = "app-" + UUID.randomUUID().toString();
            dataRepository.insertMember(Member.builder()
                    .externalId(externalId)
                    .name(mDtNameView.getText().toString())
                    .address(mDtAdressView.getText().toString())
                    .phone(mDtPhoneView.getText().toString())
                    .qrcode(qr_code)
                    .build());
            dataRepository.insertTrackMember(trackId, dataRepository.getMemberByExternalId(externalId));
            dataRepository.setMemberChanged(externalId);
            finish();
        }
    }
}
