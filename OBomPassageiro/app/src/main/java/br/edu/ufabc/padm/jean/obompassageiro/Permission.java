package br.edu.ufabc.padm.jean.obompassageiro;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;

public class Permission {

    public static void checkAllUsePermission(Fragment f) {
        if (ActivityCompat.checkSelfPermission(f.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(f.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            f.requestPermissions(new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        }
    }

    public static void checkInternet(final Context c, View v) {
        ConnectivityManager cm =
                (ConnectivityManager)c.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean enabled = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if(!enabled) {
            Snackbar snackbar = Snackbar
                    .make(v, c.getString(R.string.share_switch_fail), Snackbar.LENGTH_LONG)
                    .setAction(c.getString(R.string.connect_internet), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            c.startActivity(new Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS));
                        }
                    });

            snackbar.show();
        }
    }

    public static void checkGPS(final Context c, View v) {
        LocationManager service = (LocationManager) c.getSystemService(c.LOCATION_SERVICE);
        boolean enabled = service
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        if(!enabled) {
            Snackbar snackbar = Snackbar
                    .make(v, c.getString(R.string.gps_fail), Snackbar.LENGTH_LONG)
                    .setAction(c.getString(R.string.connect_gps), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            c.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    });

            snackbar.show();
        }
    }
}
