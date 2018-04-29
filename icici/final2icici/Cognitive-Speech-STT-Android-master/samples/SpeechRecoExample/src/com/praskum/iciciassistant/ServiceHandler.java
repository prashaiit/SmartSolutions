package com.praskum.iciciassistant;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by praskum on 4/14/2017.
 */

public class ServiceHandler {

    public String makeServiceCall(String url) {
        String response = null;

        try {
            /*Log.i("test", "url = " + url);
            URL url1 = new URL(url);
            HttpURLConnection connection = (HttpURLConnection)url1.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.i("test", "responst != OK " + connection.getResponseCode());
                return null;
            }
            Log.i("test", "response is 200");
            Reader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            StringBuilder sb = new StringBuilder();

            for (int i; (i = in.read()) > 0;) {
                sb.append((char)i);
            }
            response = sb.toString();*/

            HttpGet httpGet = new HttpGet(url);
            HttpClient client = new DefaultHttpClient();
            HttpResponse httpResponse = client.execute(httpGet);

            int status = httpResponse.getStatusLine().getStatusCode();
            Log.i("test", "status = " + status);

            if (status == 200) {
                HttpEntity entity = httpResponse.getEntity();
                response = EntityUtils.toString(entity);
                return response;
            }
        } catch (Exception e) {
            Log.i("test", "exception " + e.getMessage());
        }

        return response;
    }
}
