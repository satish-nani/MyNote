package com.programmingbear.mynote;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
/**
 * Set an alarm for the date passed into the constructor
 * When the alarm is raised it will start the NotifyService
 *
 * This uses the android build in alarm manager *NOTE* if the phone is turned off this alarm will be cancelled
 *
 * This will run on it's own thread.
 *
 * @author paul.blundell
 */
public class AlarmTask implements Runnable{
    // The date selected for the alarm
    private final Calendar date;
    // The android system alarm manager
    private final AlarmManager am;
    // Your context to retrieve the alarm manager from
    private final Context context;

    String name,content;


    public AlarmTask(Context context, Calendar date,String name,String content) {
        this.context = context;
        this.am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        this.date = date;
        this.name=name;
        this.content=content;
    }

    @Override
    public void run() {
        // Request to start are service when the alarm date is upon us
        // We don't start an activity as we just want to pop up a notification into the system bar not a full activity
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(NotifyService.INTENT_NOTIFY, true);
        intent.putExtra("name",name);
        intent.putExtra("content",content);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        // Sets an alarm - note this alarm will be lost if the phone is turned off and on again
        am.set(AlarmManager.RTC_WAKEUP, date.getTimeInMillis(), pendingIntent);
    }
}