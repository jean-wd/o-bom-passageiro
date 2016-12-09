package br.edu.ufabc.padm.jean.obompassageiro.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.location.Location;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import br.edu.ufabc.padm.jean.obompassageiro.MainActivity;
import br.edu.ufabc.padm.jean.obompassageiro.R;

public class ShareIntentService extends IntentService {

    public static final String ACTION_SEND = "br.edu.ufabc.padm.jean.obompassageiro.action.SEND";

    public static final String PARAM_DESTINATION = "br.edu.ufabc.padm.jean.obompassageiro.extra.DESTINATION";
    public static final String PARAM_LOCATION = "br.edu.ufabc.padm.jean.obompassageiro.extra.LOCATION";
    public static final String PARAM_LOCATION_LAT = "br.edu.ufabc.padm.jean.obompassageiro.extra.LOCATION_LAT";
    public static final String PARAM_LOCATION_LON = "br.edu.ufabc.padm.jean.obompassageiro.extra.LOCATION_LON";

    public ShareIntentService() {
        super("ShareIntentService");
    }

    public static void startActionSend(Context context, String param1, Location param2) {
        Intent intent = new Intent(context, ShareIntentService.class);
        intent.setAction(ACTION_SEND);

        intent.putExtra(PARAM_DESTINATION, param1);
        intent.putExtra(PARAM_LOCATION_LAT, String.valueOf(param2.getLatitude()));
        intent.putExtra(PARAM_LOCATION_LON, String.valueOf(param2.getLongitude()));

        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_SEND.equals(action)) {
                final String param1 = intent.getStringExtra(PARAM_DESTINATION);
                final String param2 = intent.getStringExtra(PARAM_LOCATION_LAT);
                final String param3 = intent.getStringExtra(PARAM_LOCATION_LON);
                handleActionSend(param1, param2, param3);
            }
        }
    }

    private void handleActionSend(String param1, String param2, String param3) {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_stat_name)
                        .setContentTitle("O Bom Passageiro")
                        .setContentText("Esse cara é você!");

        Intent resultIntent = new Intent(this, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());
    }

}
