package com.example.joseph.webapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

import static android.R.attr.id;
import static android.R.attr.name;
import static com.example.joseph.webapp.R.drawable.email;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    EditText regNo;
    EditText password;
    Button loginBtn;
    LocalDb localDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        localDb = new LocalDb(this);

        regNo = (EditText) findViewById(R.id.regNo);
        password = (EditText) findViewById(R.id.password);
        loginBtn = (Button) findViewById(R.id.login_button);

        loginBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.login_button:

                String rn = regNo.getText().toString();
                String pw = password.getText().toString();


                BackgroundTask bgtask = new BackgroundTask();
                bgtask.execute(rn,pw);

                break;

        }
    }



    class BackgroundTask extends AsyncTask<String,String,String> {

        String fetch_url, JSON_STRING;
        @Override
        protected void onPreExecute() {
            //fetch_url = "http://192.168.43.196/my_project/android/get_user.php";
            fetch_url = "http://www.peacosentertainment.com/get_user.php";
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
                String data = URLEncoder.encode("regNo","UTF-8")+"="+URLEncoder.encode(params[0],"UTF-8")
                        +"&"+URLEncoder.encode("password","UTF-8")+"="+URLEncoder.encode(params[1],"UTF-8");



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

                if(result != "" && result != null){

                    JSONObject object = new JSONObject(result);
                    JSONArray array = object.getJSONArray("response");

                    JSONObject neededUserDetails = array.getJSONObject(0);


                    Boolean status = Boolean.valueOf(neededUserDetails.getString("status"));

                    //Toast.makeText(getApplicationContext(), status.toString(), Toast.LENGTH_LONG).show();

                    if(status){

                        //Toast.makeText(getApplicationContext(), "ghyuu", Toast.LENGTH_LONG).show();

                        String name = neededUserDetails.getString("Name");
                        String rn = neededUserDetails.getString("RegNo");
                        String pw = neededUserDetails.getString("Password");


                        int id = Integer.parseInt(neededUserDetails.getString("id"));
                        String phone = neededUserDetails.getString("Phone");


                        User user = new User(id,name,phone,rn,pw);

                        logUserIn(user);
//
//
                        //Toast.makeText(getApplicationContext(), name, Toast.LENGTH_LONG).show();
//
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Sorry, User not Found!", Toast.LENGTH_LONG).show();
                    }


                }
                else{

                    Toast.makeText(getApplicationContext(), "Could not login. Check your internet connection", Toast.LENGTH_LONG).show();


                }



            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

    private void logUserIn(User user) {

        localDb.storeUser(user);
        localDb.logUserIn(true);
        Toast.makeText(getApplicationContext(), "Yes", Toast.LENGTH_LONG).show();
        Intent i = new Intent(this,MainActivity.class);
        startActivity(i);

    }




}




