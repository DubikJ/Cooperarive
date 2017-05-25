package com.avatlantik.cooperative.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.avatlantik.cooperative.R;
import com.avatlantik.cooperative.adapter.MembersListAdapter;
import com.avatlantik.cooperative.app.CooperativeApplication;
import com.avatlantik.cooperative.db.CooperativeContract.MemberContract;
import com.avatlantik.cooperative.db.CooperativeContract.TrackMemberContract;
import com.avatlantik.cooperative.model.LandingMember;
import com.avatlantik.cooperative.model.db.Member;
import com.avatlantik.cooperative.model.db.MilkParam;
import com.avatlantik.cooperative.model.db.Track;
import com.avatlantik.cooperative.model.db.Visit;
import com.avatlantik.cooperative.repository.DataRepository;
import com.avatlantik.cooperative.service.SettingsService;
import com.avatlantik.cooperative.task.SyncIntentService;
import com.avatlantik.cooperative.task.SyncReceiver;
import com.avatlantik.cooperative.util.ActivityUtils;
import com.google.zxing.client.android.Intents;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.joda.time.LocalDate;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static android.os.Environment.DIRECTORY_DCIM;
import static android.os.Environment.getExternalStoragePublicDirectory;
import static com.avatlantik.cooperative.common.Consts.CLEAR_DATABASE_IN_LOGOUT;
import static com.avatlantik.cooperative.common.Consts.EXITAPPLICATION;
import static com.avatlantik.cooperative.common.Consts.LOGIN;
import static com.avatlantik.cooperative.common.Consts.ROOT_DIR;
import static com.avatlantik.cooperative.common.Consts.SETTINGSACTIVITYLOGIN;
import static com.avatlantik.cooperative.common.Consts.STARTACTIVITYLOGIN;
import static com.avatlantik.cooperative.common.Consts.STATUS_ERROR_SYNC;
import static com.avatlantik.cooperative.common.Consts.STATUS_FINISHED_SYNC;
import static com.avatlantik.cooperative.common.Consts.STATUS_STARTED_SYNC;
import static com.avatlantik.cooperative.task.SyncIntentService.SYNC_RECEIVER;
import static com.google.zxing.integration.android.IntentIntegrator.QR_CODE_TYPES;

public class LandingActivity extends AppCompatActivity implements SyncReceiver.Receiver {

    private static final int REGISTRATION_CODE = 1;
    private static final int MIN_NUMBER_LOADED_MEMBERS = 50;
    private boolean doubleBackToExitPressedOnce = false;

    @Inject
    DataRepository dataRepository;
    @Inject
    SettingsService cm;
    @Inject
    ActivityUtils activityUtils;

    private TextView collectorView, trackView, landCollLitersView;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;
    private List<LandingMember> landingMembers;
    private List<String> memberBlackList;
    private Runnable refresher;
    private View bodyLayout, mSyncView, mSyncLauout;
    private MembersListAdapter membersListAdapter;
    private ListView membersListView;

    private Track track;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing_activity);

        ((CooperativeApplication) getApplication()).getComponent().inject(this);

        refresher = new Runnable() {
            @Override
            public void run() {
                ListView membersListView = (ListView) findViewById(R.id.landing_members);
                ((BaseAdapter) membersListView.getAdapter()).notifyDataSetChanged();
            }
        };


        bodyLayout = findViewById(R.id.body_layout);
        mSyncView = findViewById(R.id.sync_progress);
        mSyncLauout = findViewById(R.id.sync_progress_layout);
        collectorView = (TextView) findViewById(R.id.landing_collector_name);
        trackView = (TextView) findViewById(R.id.landing_track_name);
        landCollLitersView = (TextView) findViewById(R.id.landing_collected_liters);
        membersListView = (ListView) findViewById(R.id.landing_members);
        membersListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView arg0, int arg1) {}

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (visibleItemCount > 0 && firstVisibleItem + visibleItemCount == totalItemCount) {
                    updateMembersList();
                    membersListAdapter.notifyDataSetChanged();
                }
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        FloatingActionButton fab_search = (FloatingActionButton) findViewById(R.id.landing_search);
        fab_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getBaseContext(), SearchMemberActivity.class);
                startActivity(i);
            }
        });

        FloatingActionButton fab_registration = (FloatingActionButton) findViewById(R.id.landing_registration);
        fab_registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registration();
            }
        });

        initNavigationView();
    }

    @Override
    protected void onResume() {
        super.onResume();

        initDataToCap();

        landingMembers = new ArrayList<>();

        memberBlackList = new ArrayList<>();

        updateMembersList();

        membersListAdapter = new MembersListAdapter(this, landingMembers);

        membersListView.setAdapter(membersListAdapter);

        double totalLiters = dataRepository.getTotalLitresByTrack(LocalDate.now().toDate());

        landCollLitersView.setText(getResources().getString(R.string.landing_collected_liters) + " " +
                String.valueOf(totalLiters) + " " + getResources().getString(R.string.landing_liters_name_short));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

            if (intentResult != null || REGISTRATION_CODE == requestCode) {
                startActivity(new Intent(getBaseContext(), RegistrationActivity.class)
                        .putExtra(MemberContract.QR_CODE,
                                data.getStringExtra(Intents.Scan.RESULT))
                        .putExtra(TrackMemberContract.TRACK_EXTERNAL_ID,
                                track.getExternalId()));
            } else {
                startActivity(new Intent(getBaseContext(), MemberActivity.class)
                        .putExtra(MemberContract.QR_CODE,
                                data.getStringExtra(Intents.Scan.RESULT)));
            }
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
                initDataToCap();
                landingMembers.clear();
                memberBlackList.clear();
                updateMembersList();
                runOnUiThread(refresher);
                showProgress(false);
                Toast.makeText(this, getString(R.string.finish_dowload_data), Toast.LENGTH_SHORT).show();
                break;
            case STATUS_ERROR_SYNC:
                String error = resultData.getString(Intent.EXTRA_TEXT);
                activityUtils.showMessage(error, this);
                showProgress(false);
                break;
        }
    }

    private void initDataToCap(){

        collectorView.setText(dataRepository.getUserSetting(LOGIN));

        track = dataRepository.getLatestTrack();

        if (track != null) {
            trackView.setText(track.getName());
            if (Integer.valueOf(track.getDate()) > 0){
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
                getSupportActionBar().setTitle(getString(R.string.app_name)+": "+getString(R.string.landing_route_date)+
                        " "+simpleDateFormat.format(new java.util.Date((long)Integer.valueOf(track.getDate())*1000)).toString());
            }else {
                getSupportActionBar().setTitle(mActivityTitle);
            }
        }

    }

    private void updateMembersList() {
        String trackId = track != null ? track.getExternalId() : null;

        List<Member> members = dataRepository.getMembersByTrackAndPosition(trackId,
                                             memberBlackList, MIN_NUMBER_LOADED_MEMBERS);//getMembers(trackId);
        for (Member member : members) {
            Visit visit = dataRepository.getVisit(member.getId(), LocalDate.now().toDate());
            MilkParam milkParam;
            if (visit != null) {
                milkParam = dataRepository.getMilkParams(
                        visit.getId());
                landingMembers.add(
                        new LandingMember(
                                member.getId(),
                                member.getName(),
                                milkParam == null ? null : milkParam.getFat(),
                                milkParam == null ? null : milkParam.getVolume()
                        ));
            } else {
                landingMembers.add(
                        new LandingMember(
                                member.getId(),
                                member.getName(),
                                null,
                                null
                        ));
            }
            memberBlackList.add(member.getExternalId());
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);//Menu Resource, Menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        if (item.getItemId() == R.id.nav_sync) {
            sync();
            return true;
        }

        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void sync() {
        SyncReceiver mReceiver = new SyncReceiver(new Handler(), this);
        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, SyncIntentService.class);
        intent.putExtra(SYNC_RECEIVER, mReceiver);
        startService(intent);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(SETTINGSACTIVITYLOGIN, EXITAPPLICATION);
                startActivity(intent);
//                super.onBackPressed();
//                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, getString(R.string.double_press_exit), Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    private void initNavigationView() {

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mActivityTitle = getTitle().toString();

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, 0, 0) {


            @Override
            public boolean onOptionsItemSelected(MenuItem item) {
                return super.onOptionsItemSelected(item);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
                if (track != null) {
                   if (Integer.valueOf(track.getDate()) > 0){
                      SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
                      getSupportActionBar().setTitle(getString(R.string.app_name)+": "+getString(R.string.landing_route_date)+
                            " "+simpleDateFormat.format(new java.util.Date((long)Integer.valueOf(track.getDate())*1000)).toString());
                   }else {
                      getSupportActionBar().setTitle(mActivityTitle);
                   }
                } else{
                    getSupportActionBar().setTitle(mActivityTitle);
                }
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                 /* hide keyboard */
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(R.string.item_settings);
                invalidateOptionsMenu();
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerToggle.syncState();

        //Initializing NavigationView
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                mDrawerLayout.closeDrawers();

                item.setChecked(true);

                switch (item.getItemId()) {
                    case R.id.nav_settings:
                        startActivity(new Intent(getBaseContext(), LoginActivity.class).putExtra(SETTINGSACTIVITYLOGIN, STARTACTIVITYLOGIN));
                        break;
                    case R.id.nav_exit:
                        logout();
                        break;
                    default:
                        return false;
                }
                return true;
            }


        });
    }

    /**
     * Shows the progress UI and hides the form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            bodyLayout.setVisibility(show ? View.GONE : View.VISIBLE);
            bodyLayout.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    bodyLayout.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mSyncLauout.setVisibility(show ? View.VISIBLE : View.GONE);
            mSyncView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mSyncLauout.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mSyncLauout.setVisibility(show ? View.VISIBLE : View.GONE);
            bodyLayout.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void logout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.item_exit));
        builder.setMessage(getString(R.string.questions_exit_clear));

        builder.setPositiveButton(getString(R.string.questions_answer_yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                cm.clearData();

                File cooperativeDir = new File(getExternalStoragePublicDirectory(DIRECTORY_DCIM).getPath()
                        + File.separator + ROOT_DIR);
                deleteRecursive(cooperativeDir);

                if (CLEAR_DATABASE_IN_LOGOUT) {
                    dataRepository.clearDataBase();
                }

                dialog.dismiss();
                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
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

    private void registration() {
        if (track == null) {
            Toast.makeText(this, getString(R.string.no_sync_track), Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.questions_enbled_qrcode));

        builder.setPositiveButton(getString(R.string.questions_answer_yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                startActivityForResult(
                        new IntentIntegrator(LandingActivity.this)
                                .setDesiredBarcodeFormats(QR_CODE_TYPES)
                                .setCaptureActivity(ScannerQrActivity.class)
                                .createScanIntent(),
                        REGISTRATION_CODE);

            }
        });

        builder.setNegativeButton(getString(R.string.questions_answer_no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                startActivity(
                        new Intent(getBaseContext(), RegistrationActivity.class)
                                .putExtra(MemberContract.QR_CODE, "")
                                .putExtra(TrackMemberContract.TRACK_EXTERNAL_ID,
                                        track.getExternalId()));
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

    void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
    }
}
