package com.taxibookingdriver.Controller;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;


public class Connection {

    public static String getconnection(String url) {
        Log.i("url : ", url);
        String str_response;
        HttpURLConnection client;
        int MY_SOCKET_TIMEOUT_MS = 40000;
        try {
            URL url1 = new URL(url);
            client = (HttpURLConnection) url1.openConnection();
            client.addRequestProperty("username", Config.headunm);
          //  client.addRequestProperty("apikey", Config.headkey);
            int code = client.getResponseCode();
            Log.i("status code ", "" + code);
            InputStream in = new BufferedInputStream(client.getInputStream());
            str_response = Parsing.convertStreamToString(in);
            client.disconnect();
            Log.i("response", str_response);
        } catch (Exception e) {
            str_response = "";
            e.printStackTrace();
        }

        return str_response;
    }

    public static void postconnection(String url, final Map<String, String> param, final Map<String, String> header,
                                      Context context, Response.Listener<String> lis_res, Response.ErrorListener lis_error) {
        try {
            Log.e("url", url);
            RequestQueue requestQueue = null;
            int MY_SOCKET_TIMEOUT_MS = 40000;
            StringRequest postRequest = new StringRequest(Request.Method.POST, url, lis_res, lis_error
            ) {

                @Override
                protected Map<String, String> getParams() {
                    Log.e("parameter", param + "");
                    return param;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Log.e("header", header + "");
                    return header;
                }
            };

            if (requestQueue == null) {
                requestQueue = Volley.newRequestQueue(context);
            }

            requestQueue.add(postRequest);
            postRequest.setRetryPolicy(new DefaultRetryPolicy(
                    MY_SOCKET_TIMEOUT_MS,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void postconnection_without_header(String url, final Map<String, String> param,
                                                     Context context, Response.Listener<String> lis_res, Response.ErrorListener lis_error) {
        try {
            Log.e("url", url);
            RequestQueue requestQueue = null;
            int MY_SOCKET_TIMEOUT_MS = 40000;
            StringRequest postRequest = new StringRequest(Request.Method.POST, url, lis_res, lis_error
            ) {

                @Override
                protected Map<String, String> getParams() {
                    Log.e("parameter", param + "");
                    return param;
                }

            };

            if (requestQueue == null) {
                requestQueue = Volley.newRequestQueue(context);
            }

            requestQueue.add(postRequest);
            postRequest.setRetryPolicy(new DefaultRetryPolicy(
                    MY_SOCKET_TIMEOUT_MS,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getconnectionVolley(String url, final Map<String, String> param, final Map<String, String> header,
                                           final Context context, Response.Listener<String> lis_res, Response.ErrorListener lis_error) {
        try {

            Log.e("url", " " + url);
            RequestQueue requestQueue = null;
            StringRequest getRequest = new StringRequest(Request.Method.GET, url, lis_res, lis_error

            ) {
                @Override
                protected Map<String, String> getParams() {
                    Log.e("parameter", param + "");
                    return param;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Log.e("header", header + "");
                    return header;
                }
            };

            if (requestQueue == null) {
                requestQueue = Volley.newRequestQueue(context);
            }

            requestQueue.add(getRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isconnection(Context c) {
        ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void run_api(Context c, AsyncTask<String, Void, String> cl) {
        if (!Connection.isconnection(c)) {
            Log.e("connection", "not available");
            DialogBox.dialog_connection(c);
        } else {
            cl.execute();
        }
    }
}
