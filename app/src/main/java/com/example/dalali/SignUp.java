package com.example.dalali;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SignUp extends AppCompatActivity  {
    public static final String PREFS_NAME = "LoginPrefs";
    private ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setSubtitle("Register");

        /*check login before*/
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        if (settings.getString("logged", "").toString().equals("logged")) {
            Intent i = new Intent(SignUp.this, Login.class);
            startActivity(i);
        } else {
            getSupportActionBar().setSubtitle("Log In");

            //Direct to register activity
            TextView regi = (TextView) findViewById(R.id.signup);
            regi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(SignUp.this, Login.class);
                    startActivity(i);
                }
            });

            Button login = (Button) findViewById(R.id.ButtonLogin);
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText uname = (EditText)findViewById(R.id.editTextUsername);
                    EditText pass = (EditText)findViewById(R.id.editTexTPassword);

                    String username = uname.getText().toString();
                    String password = pass.getText().toString();
                    //check empty space
                    if (username.isEmpty() || password.isEmpty()) {
                        Toast.makeText(SignUp.this, "Fill the blank space", Toast.LENGTH_LONG).show();
                    } else {
                        new SendDataPHP().execute(username, password);
                    }
                }
            });
        }
    }
    class SendDataPHP extends AsyncTask<String,Void,String> {
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = progressDialog.show(SignUp.this,"LOG IN","Loading..........",true,true);
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            if (result.equalsIgnoreCase("erro")){
                progressDialog.dismiss();
                Toast.makeText(SignUp.this, "Check your Data Connection", Toast.LENGTH_LONG).show();
            }
            else {
                if (result.startsWith("fail")){
                    progressDialog.dismiss();
                    Toast.makeText(SignUp.this, "Invalid Username or Passowrd", Toast.LENGTH_LONG).show();
                }
                else{
                    //Declare JSON array which will help to open data from php
                    JSONArray jsonArray = null;
                    try {
                        jsonArray = new JSONArray(result);
                        JSONObject o;

                        //capturing data by using json  from php
                        o=jsonArray.getJSONObject(0);
                        String pass = o.getString("password");
                        String userID = o.getString("userid");
                        String fname = o.getString("fullname");
                        String username = o.getString("username");

                        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("logged", "logged");
                        editor.putString("phone", username);
                        editor.putString("userId", userID);
                        editor.putString("fullname", fname);
                        editor.putString("password", pass);
                        editor.commit();
                        progressDialog.dismiss();
                        Intent i = new Intent(SignUp.this,MainActivity.class);
                        startActivity(i);
                    } catch (JSONException e) {
                        progressDialog.dismiss();
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        protected String doInBackground(String... params){
            String base_url= ConfigPHP.conn+"login.php";
            Uri buildUri;
            buildUri = Uri.parse(base_url).buildUpon().
                    appendQueryParameter("username", params[0])
                    .appendQueryParameter("password", params[1])
                    .appendQueryParameter("process", "LogIn").build();
            return ConfigPHP.addDetailPhp(buildUri);
        }
    }

}



