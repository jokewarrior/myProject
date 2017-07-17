package com.example.joseph.webapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.zxing.Result;
import static android.view.View.Z;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends AppCompatActivity  implements ZXingScannerView.ResultHandler, View.OnClickListener {
    private ZXingScannerView mScannerView;
    private DrawerLayout mDrawer;
    FloatingActionButton fab;
    Context ctx;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ctx = this;
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawer.openDrawer(Gravity.START);
            }
        });

        toolbar.setTitle(R.string.nav_dashboard);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_holder, new DashboardFragment()).commit();

        NavigationView nv = (NavigationView) findViewById(R.id.Navigation_view);

        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.dashboard:
                        fragment = new DashboardFragment();
                        toolbar.setTitle(R.string.nav_dashboard);
                        break;

                    case R.id.profile:
                        fragment = new ProfileFragment();
                        toolbar.setTitle(R.string.nav_profile);
                        break;

                    case R.id.contact_admin:
                        fragment = new ContactAdmin();
                        toolbar.setTitle(R.string.nav_contact);
                        break;

                    case R.id.attendance:
                        fragment = new RecordsFragment();
                        toolbar.setTitle(R.string.nav_attendance);
                        break;

                    default:
                        fragment = new DashboardFragment();
                        toolbar.setTitle(R.string.nav_dashboard);
                        break;
                }

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_holder, fragment)
                        .commit();

                mDrawer.closeDrawer(Gravity.START);

                return true;
            }
        });
    }
    @Override
    public void onClick (View v){

        Toast.makeText(this,"Yo",Toast.LENGTH_LONG).show();
        mScannerView = new ZXingScannerView(getApplicationContext());
        setContentView(mScannerView);
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();

    }
    @Override
    protected void onPause(){
        super.onPause();
        mScannerView.stopCamera();

    }
    @Override
    public void handleResult(Result result){
        //result handling code
        Log.v("handleResult", result.getText());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Scan result");
        builder.setMessage(result.getText());
        AlertDialog alertDialogue = builder.create();
        alertDialogue.show();


        mScannerView.resumeCameraPreview(this);
    }
}