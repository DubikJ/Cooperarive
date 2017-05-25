package com.avatlantik.cooperative.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.avatlantik.cooperative.R;
import com.avatlantik.cooperative.db.CooperativeContract;

public class TestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity);

    }

    public void but_Click(View v) {
        switch (v.getId()) {
            case R.id.button1:
                startActivity(new Intent(getBaseContext(), LandingActivity.class));
                break;
            case R.id.button2:
                startActivity(new Intent(getBaseContext(), LoginActivity.class));
                break;
            case R.id.button3:
                Intent intent = new Intent(getBaseContext(), MemberActivity.class);
                intent.putExtra(CooperativeContract.MemberContract._ID, 12);
                startActivity(intent);
                break;
            case R.id.button4:
                startActivity(new Intent(getBaseContext(), ServiceActivity.class));
                break;
            case R.id.button5:
                startActivity(new Intent(getBaseContext(), MilkReceptionActivity.class));
                break;
        }
    }
}