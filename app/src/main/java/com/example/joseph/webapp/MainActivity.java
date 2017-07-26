package com.example.joseph.webapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import static android.R.attr.data;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static android.view.View.Z;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler, View.OnClickListener {

    private ZXingScannerView mScannerView;
    private DrawerLayout mDrawer;
    FloatingActionButton fab;
    Context ctx;
    User user;
    LocalDb localDb;
    TextView name, reg, phone;

    private boolean isScannerOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //check user login here
        localDb = new LocalDb(this);
        if(!localDb.checkLogin()){

            Intent loginIntent = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(loginIntent);

        }
        else{
            user = localDb.getUser();

        }



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

                    case R.id.logout:
                        fragment = new RecordsFragment();

                        localDb.logout();
                        localDb.logUserIn(false);

                        Intent loginIntent = new Intent(MainActivity.this,LoginActivity.class);
                        startActivity(loginIntent);
                        finish();

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
    public void onClick(View view) {
        //Toast.makeText(this, "Yo", Toast.LENGTH_LONG).show();

        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
        //isScannerOpen = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
////        if (isScannerOpen) {
////            mScannerView.resumeCameraPreview(this);
////        }
//    }
//
//    @Override
//    public void onBackPressed() {
//        if (isScannerOpen) {
//            mScannerView.setVisibility(View.GONE);
//            isScannerOpen = false;
//            return;
//        }
//        super.onBackPressed();
//    }

    @Override
    public void handleResult(Result result) {
        //result handling code
        String qrResult = result.getText();
        BackgroundTask bgtask = new BackgroundTask();

        bgtask.execute(qrResult,String.valueOf(user.id));

        Log.v("handleResult", result.getText());


        //isScannerOpen = false;
//        mScannerView.stopCamera();
//        mScannerView.setVisibility(View.GONE);
        //mScannerView.resumeCameraPreview(this);

    }


    public void displayAlert(String alertMsg){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Scan result");
        builder.setMessage(alertMsg);
        AlertDialog alertDialogue = builder.create();
        builder.setPositiveButton("OK", null);
        alertDialogue.show();

    }

    public void redirectToHome(){

    }



    class BackgroundTask extends AsyncTask<String,String,String> {

        String fetch_url, JSON_STRING;
        @Override
        protected void onPreExecute() {
            //fetch_url = "http://192.168.43.196/my_project/android/get_user.php";
            fetch_url = "http://www.jetcitytechnologies.com.ng/android/get_user.php";
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                URL url = new URL(fetch_url);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                //posting to the server
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                OutputStream outputStream = conn.getOutputStream();
                BufferedWriter bwriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));

                //data to post
                String data = URLEncoder.encode("qrString","UTF-8")+"="+URLEncoder.encode(params[0],"UTF-8")
                        +"&"+URLEncoder.encode("user_id","UTF-8")+"="+URLEncoder.encode(params[1],"UTF-8");



                bwriter.write(data);
                bwriter.flush();
                bwriter.close();
                outputStream.close();





                //getting results from the server
                InputStream inputStream = conn.getInputStream();
                BufferedReader buffer = new BufferedReader(new InputStreamReader((inputStream)));
                StringBuilder stringBuilder = new StringBuilder();
                while ((JSON_STRING = buffer.readLine()) != null){

                    stringBuilder.append(JSON_STRING+"\n");

                }

                buffer.close();
                inputStream.close();
                conn.disconnect();
                return stringBuilder.toString().trim();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {

            //we will assign the user stuff here and do the image shii
            //User user = new User()

            try {

                //Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                String messageFromServer;
                if(result != "" && result != null){

                    JSONObject object = new JSONObject(result);
                    JSONArray array = object.getJSONArray("response");

                    JSONObject neededUserDetails = array.getJSONObject(0);


                    Boolean status = Boolean.valueOf(neededUserDetails.getString("status"));

                    //Toast.makeText(getApplicationContext(), status.toString(), Toast.LENGTH_LONG).show();

                    if(status){

                        //Toast.makeText(getApplicationContext(), "ghyuu", Toast.LENGTH_LONG).show();

                        messageFromServer = "Recorded Successfully. Time Recorded: "+neededUserDetails.getString("time_recorded");

//
                    }
                    else{
                        messageFromServer = "Attendance Already Recorded/Time Elapsed";
                    }


                }
                else{

                    messageFromServer = "Could Not Record Attendance. Check Your Internet Connection!";

                }


                displayAlert(messageFromServer);



            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }


}
