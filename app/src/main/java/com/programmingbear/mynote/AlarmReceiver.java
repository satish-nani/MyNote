package com.programmingbear.mynote;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import static com.programmingbear.mynote.NotifyService.INTENT_NOTIFY;

/**
 * Created by satish on 13/2/2017.
 */

public class AlarmReceiver extends BroadcastReceiver {

    Bundle extras;
    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getBooleanExtra(INTENT_NOTIFY, false))
            extras=intent.getExtras();
        String name=extras.getString("name");
        String content=extras.getString("content");
        showNotification(context,name,content);
    }

    public void showNotification(Context context,String name,String content) {

        Intent intent = new Intent(context, MyNotes.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setContentTitle(name)
                .setContentText(content)
                .setSound(defaultSoundUri)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                .setAutoCancel(true)
                .setSmallIcon(android.R.drawable.ic_secure)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }
}
