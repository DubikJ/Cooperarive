package com.avatlantik.cooperative.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.avatlantik.cooperative.R;
import com.avatlantik.cooperative.adapter.MembersSearchAdapter;
import com.avatlantik.cooperative.app.CooperativeApplication;
import com.avatlantik.cooperative.db.CooperativeContract;
import com.avatlantik.cooperative.model.db.Member;
import com.avatlantik.cooperative.model.db.Track;
import com.avatlantik.cooperative.repository.DataRepository;
import com.google.zxing.client.android.Intents;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.List;

import javax.inject.Inject;

import static com.google.zxing.integration.android.IntentIntegrator.QR_CODE_TYPES;

public class SearchMemberActivity  extends FragmentActivity{
    private static final int SEARCH_CODE = 1;

    @Inject
    DataRepository dataRepository;

    private MembersSearchAdapter membersSearchAdapter;

    private EditText searchNameView;
    private ListView searchListView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_member_search);

        ((CooperativeApplication) getApplication()).getComponent().inject(this);

        searchNameView = (EditText) findViewById(R.id.member_name_search);
        searchNameView.setOnTouchListener(new View.OnTouchListener() {
            final int DRAWABLE_LEFT = 0;
            final int DRAWABLE_TOP = 1;
            final int DRAWABLE_RIGHT = 2;
            final int DRAWABLE_BOTTOM = 3;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    int leftEdgeOfRightDrawable = searchNameView.getRight()
                            - searchNameView.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width();
                    if (event.getRawX() >= leftEdgeOfRightDrawable) {
                        searchNameView.setText("");
                        return true;
                    }
                }
                return false;
            }
        });
        searchNameView.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SearchMemberActivity.this.membersSearchAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        searchListView = (ListView) findViewById(R.id.search_member_ListView);

        Button backButton = (Button) findViewById(R.id.ret_main_searc_button);
        backButton.setFocusable(true);
        backButton.setFocusableInTouchMode(true);
        backButton.requestFocus();
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button searchButton = (Button) findViewById(R.id.member_search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(
                        new IntentIntegrator(SearchMemberActivity.this)
                                .setDesiredBarcodeFormats(QR_CODE_TYPES)
                                .setCaptureActivity(ScannerQrActivity.class)
                                .createScanIntent(),
                        SEARCH_CODE);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        Track track = dataRepository.getLatestTrack();
        String trackId = track != null ? track.getExternalId() : null;

        List<Member> members = dataRepository.getMembers(trackId);

        membersSearchAdapter = new MembersSearchAdapter(this, members);

        searchListView.setAdapter(membersSearchAdapter);

        String textSearch = String.valueOf(searchNameView.getText());
        if(textSearch != null && ! textSearch.isEmpty()){
            SearchMemberActivity.this.membersSearchAdapter.getFilter().filter(textSearch);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

            if (intentResult != null || requestCode == SEARCH_CODE){
                    startActivity(
                            new Intent(getBaseContext(), MemberActivity.class)
                                    .putExtra(CooperativeContract.MemberContract.QR_CODE,
                                            data.getStringExtra(Intents.Scan.RESULT)));
            }
        }
    }
}
