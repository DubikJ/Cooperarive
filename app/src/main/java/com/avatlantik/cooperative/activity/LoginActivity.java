package com.avatlantik.cooperative.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.avatlantik.cooperative.R;
import com.avatlantik.cooperative.app.CooperativeApplication;
import com.avatlantik.cooperative.model.ParameterInfo;
import com.avatlantik.cooperative.repository.DataRepository;
import com.avatlantik.cooperative.service.SettingsService;
import com.avatlantik.cooperative.service.SettingsService.ConnectionType;
import com.avatlantik.cooperative.service.SyncService;
import com.avatlantik.cooperative.task.SyncIntentService;
import com.avatlantik.cooperative.task.SyncReceiver;
import com.avatlantik.cooperative.util.ActivityUtils;
import com.avatlantik.cooperative.util.PropertyUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.inject.Inject;

import static android.R.id.message;
import static com.avatlantik.cooperative.R.id.login;
import static com.avatlantik.cooperative.R.id.login_sign_in_button;
import static com.avatlantik.cooperative.common.Consts.EKOMILK_AUTOMATIC_START;
import static com.avatlantik.cooperative.common.Consts.EKOMILK_CONNECTION_TYPE;
import static com.avatlantik.cooperative.common.Consts.EKOMILK_USING;
import static com.avatlantik.cooperative.common.Consts.EXITAPPLICATION;
import static com.avatlantik.cooperative.common.Consts.FLOWMETER_USING;
import static com.avatlantik.cooperative.common.Consts.LOGIN;
import static com.avatlantik.cooperative.common.Consts.PASSWORD;
import static com.avatlantik.cooperative.common.Consts.PHONE_USING;
import static com.avatlantik.cooperative.common.Consts.SERVER;
import static com.avatlantik.cooperative.common.Consts.SETTINGSACTIVITYLOGIN;
import static com.avatlantik.cooperative.common.Consts.STARTACTIVITYLANDING;
import static com.avatlantik.cooperative.common.Consts.STATUS_ERROR_SYNC;
import static com.avatlantik.cooperative.common.Consts.STATUS_FINISHED_SYNC;
import static com.avatlantik.cooperative.common.Consts.STATUS_STARTED_SYNC;
import static com.avatlantik.cooperative.task.SyncIntentService.SYNC_RECEIVER;

public class LoginActivity extends AppCompatActivity implements SyncReceiver.Receiver {

    private int settingsActivity = STARTACTIVITYLANDING;

    @Inject
    SyncService syncService;
    @Inject
    SettingsService cm;
    @Inject
    DataRepository dataRepository;
    @Inject
    ActivityUtils activityUtils;

    private EditText mLoginView, mAddressView, mPasswordView;
    private Spinner mSpinner;
    private View mProgressView, mRegistrationFormView, signInButton, returnButton, mProgressLauout;
    private String mConnectionToEcomilk;
    private List<String> listSettongsConnection = new ArrayList<String>();
    private Switch usingEcomilkSwitch, usingFlowSwitch, usingPhoneSwitch, ecomilkAutoStartSwitch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ((CooperativeApplication) getApplication()).getComponent().inject(this);

        mAddressView = (EditText) findViewById(R.id.serverAddress);
        mLoginView = (EditText) findViewById(login);
        mPasswordView = (EditText) findViewById(R.id.password);

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptData();
                    return true;
                }
                return false;
            }
        });

        returnButton = findViewById(R.id.ret_login_button);
        returnButton.setFocusable(true);
        returnButton.setFocusableInTouchMode(true);
        returnButton.requestFocus();
        returnButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        signInButton = findViewById(login_sign_in_button);
        signInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptData();
            }
        });

        mRegistrationFormView = findViewById(R.id.registration_form);
        mProgressView = findViewById(R.id.registration_progress);
        mProgressLauout = findViewById(R.id.progress_layout);
        mSpinner = (Spinner) findViewById(R.id.connect_eco_spinner);
        listSettongsConnection.add(ConnectionType.WIFI.name());
        listSettongsConnection.add(ConnectionType.USB.name());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.item_with_spinner,listSettongsConnection);

        adapter.setDropDownViewResource(R.layout.item_with_spinner);
        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mConnectionToEcomilk = listSettongsConnection.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        usingEcomilkSwitch = (Switch) findViewById(R.id.using_ecomilk_switch);
        usingEcomilkSwitch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!usingEcomilkSwitch.isChecked()){
                    ecomilkAutoStartSwitch.setChecked(false);
                }
                setVisibilityElements();
            }
        });
        usingFlowSwitch = (Switch) findViewById(R.id.using_flowmeter_switch);
        usingPhoneSwitch = (Switch) findViewById(R.id.using_phone_switch);
        ecomilkAutoStartSwitch = (Switch) findViewById(R.id.ecomilk_autostart_switch);
        ecomilkAutoStartSwitch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setVisibilityElements();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        initData();

        if (settingsActivity == STARTACTIVITYLANDING) {
            checkSavingSettings();
        } else if (settingsActivity == EXITAPPLICATION) {
            finish();
        }

    }

    private void initData() {

        getSupportActionBar().setTitle(getString(R.string.item_settings));

        Bundle extras = getIntent().getExtras();
        if (extras != null && !extras.isEmpty()) {
            settingsActivity = extras.getInt(SETTINGSACTIVITYLOGIN, STARTACTIVITYLANDING);
        }

        String adressServer = dataRepository.getUserSetting(SERVER);
        if (adressServer == null || adressServer.isEmpty()) {
            final String APPLICATION_PROPERTIES = "application.properties";
            try {
                Properties properties = PropertyUtils.getProperties(APPLICATION_PROPERTIES, this.getBaseContext());
                adressServer = properties.getProperty("server") + ":" + properties.getProperty("port");
            } catch (IOException e) {
                adressServer = "";
            }
        }


        mAddressView.setText(adressServer);
        mLoginView.setText(dataRepository.getUserSetting(LOGIN));
        mPasswordView.setText(dataRepository.getUserSetting(PASSWORD));

        mConnectionToEcomilk = dataRepository.getUserSetting(EKOMILK_CONNECTION_TYPE);
        if(mConnectionToEcomilk == null || mConnectionToEcomilk.isEmpty()){
            mConnectionToEcomilk = ConnectionType.WIFI.name();
        }

        mSpinner.setSelection(listSettongsConnection.indexOf(mConnectionToEcomilk));

        usingEcomilkSwitch.setChecked(Boolean.valueOf(dataRepository.getUserSetting(EKOMILK_USING)));
        usingFlowSwitch.setChecked(Boolean.valueOf(dataRepository.getUserSetting(FLOWMETER_USING)));
        usingPhoneSwitch.setChecked(Boolean.valueOf(dataRepository.getUserSetting(PHONE_USING)));
        ecomilkAutoStartSwitch.setChecked(Boolean.valueOf(dataRepository.getUserSetting(EKOMILK_AUTOMATIC_START)));

        setVisibilityElements();
    }

    private void attemptData() {

        mLoginView.setError(null);
        mPasswordView.setError(null);

        String address = mAddressView.getText().toString();
        String login = mLoginView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(login)) {
            mLoginView.setError(getString(R.string.error_field_required));
            focusView = mLoginView;
            cancel = true;
        }

        if (TextUtils.isEmpty(address)) {
            mAddressView.setError(getString(R.string.error_field_required));
            focusView = mAddressView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            saveSettings();
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRegistrationFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            signInButton.setVisibility(show ? View.GONE : View.VISIBLE);
            mRegistrationFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegistrationFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                    signInButton.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressLauout.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressLauout.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });

            InputMethodManager inputMethodManager =
                    (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            if (getCurrentFocus() == null) {
                inputMethodManager.hideSoftInputFromWindow(mAddressView.getWindowToken(), 0);
            } else {
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }

        } else {
            mProgressLauout.setVisibility(show ? View.VISIBLE : View.GONE);
            mRegistrationFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            signInButton.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case STATUS_STARTED_SYNC:
                showProgress(true);
                Toast.makeText(this, getString(R.string.sync_start), Toast.LENGTH_SHORT).show();
                break;
            case STATUS_FINISHED_SYNC:
                if (settingsActivity == STARTACTIVITYLANDING) {
                    startActivity(new Intent(this, LandingActivity.class));
                }
                showProgress(false);
                Toast.makeText(this, getString(R.string.finish_dowload_data), Toast.LENGTH_SHORT).show();
                finish();
                break;
            case STATUS_ERROR_SYNC:
                activityUtils.showMessage(resultData.getString(Intent.EXTRA_TEXT), this);
//                if (settingsActivity == STARTACTIVITYLANDING) {
//                    startActivity(new Intent(this, LandingActivity.class));
//                }
                showProgress(false);
//                finish();
                break;
        }
    }

    private void saveSettings() {

        dataRepository.insertUserSetting(new ParameterInfo(SERVER, String.valueOf(mAddressView.getText())));
        dataRepository.insertUserSetting(new ParameterInfo(LOGIN, String.valueOf(mLoginView.getText())));
        dataRepository.insertUserSetting(new ParameterInfo(PASSWORD, String.valueOf(mPasswordView.getText())));
        dataRepository.insertUserSetting(new ParameterInfo(EKOMILK_CONNECTION_TYPE, mConnectionToEcomilk));
        dataRepository.insertUserSetting(new ParameterInfo(EKOMILK_USING, String.valueOf(usingEcomilkSwitch.isChecked())));
        dataRepository.insertUserSetting(new ParameterInfo(FLOWMETER_USING, String.valueOf(usingFlowSwitch.isChecked())));
        dataRepository.insertUserSetting(new ParameterInfo(PHONE_USING, String.valueOf(usingPhoneSwitch.isChecked())));
        dataRepository.insertUserSetting(new ParameterInfo(EKOMILK_AUTOMATIC_START, String.valueOf(ecomilkAutoStartSwitch.isChecked())));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.questions_Sync));

        builder.setPositiveButton(getString(R.string.questions_answer_yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                sync();
            }
        });

        builder.setNegativeButton(getString(R.string.questions_answer_no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (settingsActivity == STARTACTIVITYLANDING) {
                    startActivity(new Intent(getBaseContext(), LandingActivity.class));
                } else {
                    finish();
                }

            }

        });
        AlertDialog alert = builder.create();
        alert.show();

        // TODO (start stub): to set size text in AlertDialog
        TextView textView = (TextView) alert.findViewById(message);
        textView.setTextSize(getResources().getDimension(R.dimen.text_size_medium));
        Button button1 = (Button) alert.findViewById(android.R.id.button1);
        button1.setTextSize(getResources().getDimension(R.dimen.text_size_medium));
        Button button2 = (Button) alert.findViewById(android.R.id.button2);
        button2.setTextSize(getResources().getDimension(R.dimen.text_size_medium));
        // TODO: (end stub) ------------------
    }

    private void sync() {
        SyncReceiver mReceiver = new SyncReceiver(new Handler(), this);
        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, SyncIntentService.class);
        intent.putExtra(SYNC_RECEIVER, mReceiver);
        startService(intent);
    }

    private void checkSavingSettings() {
        String adress = dataRepository.getUserSetting(SERVER);
        String login = dataRepository.getUserSetting(LOGIN);
        if (adress != null && !adress.isEmpty() && login != null && !login.isEmpty()) {
            startActivity(new Intent(getBaseContext(), LandingActivity.class));
        }
    }

    private void setVisibilityElements() {
        if(usingEcomilkSwitch.isChecked()){
            ecomilkAutoStartSwitch.setVisibility(View.VISIBLE);
        }else{
            ecomilkAutoStartSwitch.setVisibility(View.GONE);
        }
    }
}

