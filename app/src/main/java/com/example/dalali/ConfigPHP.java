package com.example.dalali;


import android.net.Uri;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ConfigPHP {
    public static final String conn="http:// 192.168.100.21/LoginRegister";

    public static String addDetailPhp(Uri buildUri){
        //initializing variables
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String jsonResponse = null;
        int mConnectFeedback = 0;

        try {
            URL url = new URL(buildUri.toString()); // initializng url with bulded uri

            urlConnection = (HttpURLConnection) url.openConnection(); //connect to the builded url
            urlConnection.setReadTimeout(15000);
            urlConnection.setConnectTimeout(12000);
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.connect(); //build the connection

            mConnectFeedback = urlConnection.getResponseCode(); //feedback of connection

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null){
                jsonResponse = null;
                return null;
            }

            reader = new BufferedReader( new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null ){
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0){
                jsonResponse = null;
                return null;
            }

            jsonResponse = buffer.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }catch (IOException ioex){

        } finally {
            if (urlConnection != null){
                urlConnection.disconnect();
            }

            if (reader != null ){
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }

        try{
            if (mConnectFeedback == 200){
                return jsonResponse;
            }else {
                return "error";
            }
        }catch (Exception ex){

        }
        return null;
    }


}

