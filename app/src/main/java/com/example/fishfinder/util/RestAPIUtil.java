package com.example.fishfinder.util;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;

public class RestAPIUtil {

    /* Variables */
    // An ExecutorService is an object that allows you to run methods asynchronously on a separate Thread
    ExecutorService service = Executors.newFixedThreadPool(1);

    /**
     * Basic GET Request to the specified URL
     * @param urlString
     * @return String contents of the result from the URL
     */
    public static String get(String urlString) throws IOException {

        // Variable for reading URL stream data line by line
        String input;

        // Connecting to URL
        URL url = new URL(urlString);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setConnectTimeout(5000);

        // Reading the data from the URL line by line
        BufferedReader in      = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuffer content   = new StringBuffer();
        while ((input = in.readLine()) != null){
            content.append(input);
        }

        // CLosing the connection
        in.close();
        con.disconnect();

        return content.toString();

    }

    // TODO: Add another abstract way of calling fetch that doesn't rely on Function Java Class
    // Function Java Class requires API 24, so try making a class dedicated to store data from REST API requests
    // Or, just require the user to do the API request plus their own callback
    public <T,R> R fetch(String urlString, Function<String,String> RESTCall, Function<String,R> func) {
        service.execute(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                try {
                    String result = RESTCall.apply(urlString);
                    func.apply(result);
                } catch (Exception e){
                    e.printStackTrace();
                    System.out.println(e.getLocalizedMessage());
                }
            }
        });

        return null;
    }

}
