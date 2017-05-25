package com.avatlantik.cooperative.activity;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import com.avatlantik.cooperative.model.db.Member;
import com.avatlantik.cooperative.repository.DataRepository;
import com.google.zxing.client.android.Intents;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import javax.inject.Inject;

import static com.avatlantik.cooperative.common.Consts.TAGLOG;
import static com.google.zxing.integration.android.IntentIntegrator.QR_CODE_TYPES;

public class MemberDetailActivity extends FragmentActivity {

    private static final int GET_CODE = 1;

    @Inject
    DataRepository dataRepository;

    private Member member;
    private EditText mDetailNameView, mDetailAdressView,
            mDetailQRcodeView, mDetailPhoneView;
    private String qrcodeMember = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        member = (Member) extras.get("member");
        if (member == null) {
            finish();
            return;
        }

        mDetailNameView = (EditText) findViewById(R.id.member_detail_name);
        mDetailNameView.setFocusableInTouchMode(false);
        mDetailNameView.setText(member.getName());

        mDetailQRcodeView = (EditText) findViewById(R.id.member_detail_qrcode);
        mDetailQRcodeView.setFocusableInTouchMode(false);

        FloatingActionButton getQRcodeButton = (FloatingActionButton) findViewById(R.id.member_detail_getQRcode);
        getQRcodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(
                        new IntentIntegrator(MemberDetailActivity.this)
                                .setDesiredBarcodeFormats(QR_CODE_TYPES)
                                .setCaptureActivity(ScannerQrActivity.class)
                                .createScanIntent(),
                        GET_CODE);
            }
        });


        if (qrcodeMember == null || qrcodeMember.isEmpty()) {
            qrcodeMember = member.getQrcode();
        }
        if (qrcodeMember != null && !qrcodeMember.isEmpty()) {
            mDetailQRcodeView.setText(qrcodeMember);
            getQRcodeButton.setVisibility(View.GONE);

        }

        mDetailAdressView = (EditText) findViewById(R.id.member_detail_adress);
        mDetailAdressView.setText(member.getAddress());

        mDetailPhoneView = (EditText) findViewById(R.id.member_detail_phone);
        mDetailPhoneView.setText(member.getPhone());
        mDetailPhoneView.addTextChangedListener(new TextWatcher() {
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

        Button saveButton = (Button) findViewById(R.id.member_detail_save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAGLOG, "Press button 'save member detail'");
                saveData();
            }
        });

        Button retMemberDet = (Button) findViewById(R.id.ret_member_detail_button);
        retMemberDet.setFocusable(true);
        retMemberDet.setFocusableInTouchMode(true);
        retMemberDet.requestFocus();
        retMemberDet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MemberDetailActivity.this);
                builder.setMessage(getString(R.string.questions_member_datail_save));

                builder.setPositiveButton(getString(R.string.questions_answer_yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        saveData();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

            if (intentResult != null || requestCode == GET_CODE) {
                qrcodeMember = data.getStringExtra(Intents.Scan.RESULT).toString();
                if (qrcodeMember != null && !qrcodeMember.isEmpty()) {
                    member = dataRepository.getMemberByQrCode(qrcodeMember);
                    if (member != null) {
                        qrcodeMember = null;
                        Toast.makeText(this,
                                getResources().getString(R.string.member_qr_code_attached) + " " +
                                        member.getName(), Toast.LENGTH_LONG).
                                show();
                    }
                }
            }
        }
    }

    private void saveData() {

        mDetailNameView.setError(null);
        mDetailPhoneView.setError(null);

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(mDetailNameView.getText().toString())) {
            mDetailNameView.setError(getString(R.string.error_field_required));
            focusView = mDetailNameView;
            cancel = true;
        }

        if (TextUtils.isEmpty(mDetailPhoneView.getText().toString())) {
            mDetailPhoneView.setError(getString(R.string.error_field_required));
            focusView = mDetailPhoneView;
            cancel = true;
        }

        if (cancel) {

            focusView.requestFocus();

        } else {

            dataRepository.insertMember(Member.builder()
                    .id(member.getId())
                    .name(mDetailNameView.getText().toString())
                    .externalId(member.getExternalId())
                    .address(mDetailAdressView.getText().toString())
                    .phone(mDetailPhoneView.getText().toString())
                    .qrcode(qrcodeMember).build());

            dataRepository.setMemberChanged(member.getExternalId());

            finish();
        }
    }

}
