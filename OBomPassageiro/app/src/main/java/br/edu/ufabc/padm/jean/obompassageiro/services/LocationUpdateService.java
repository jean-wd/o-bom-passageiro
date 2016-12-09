package br.edu.ufabc.padm.jean.obompassageiro.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationResult;

public class LocationUpdateService extends IntentService {

    private final String TAG = "LocationUpdateService";
    Location location;

    public LocationUpdateService() {
        super("LocationUpdateService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (LocationResult.hasResult(intent)) {
            LocationResult locationResult = LocationResult.extractResult(intent);
            Location location = locationResult.getLastLocation();

            if (location != null) {
                Log.d(TAG, "accuracy: " + location.getAccuracy() + " lat: " + location.getLatitude() + " lon: " + location.getLongitude());

                RequestQueue queue = Volley.newRequestQueue(this);
                String url = "http://10.0.2.2:8080/server/?lat="+location.getLatitude()+"&lon="+location.getLongitude();

                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                               Log.d(TAG, response);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "Fail connecting");
                    }
                });
                queue.add(stringRequest);
            }
        }
    }

    public static Location callServer(String line, Context context, Intent intent) {
        final Location location = new Location("");

        if (line != null) {
            Log.d("TAG", "Line " + line);

            RequestQueue queue = Volley.newRequestQueue(context);
            String url = "http://10.0.2.2:8080/server/line.html?line="+line;

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("TAG", "response: " + response);

                            int gap = response.indexOf(',');

                            CharSequence lat = response.subSequence(0, gap);

                            CharSequence lon = response.subSequence(gap+1, response.length());


                            Log.d("TAG", "response: " + lat.toString() + "| " + lon.toString());

                            double latitude = Double.valueOf(lat.toString().trim());
                            double longitute = Double.valueOf(lon.toString().trim());

                            location.setLatitude(latitude);
                            location.setLongitude(longitute);

                            Log.d("TAG", "accuracy: " + location.getAccuracy() + " lat: " + location.getLatitude() + " lon: " + location.getLongitude());
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("TAG", "Fail connecting");
                }
            });
            queue.add(stringRequest);
        }
        return location;
    }

}