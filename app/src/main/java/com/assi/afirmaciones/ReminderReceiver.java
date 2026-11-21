package com.assi.afirmaciones;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.Manifest;
import android.content.pm.PackageManager;

public class ReminderReceiver extends BroadcastReceiver {
    public void onReceive(Context c,Intent in) {
        String action=in.getAction();
        if(Intent.ACTION_BOOT_COMPLETED.equals(action)||Intent.ACTION_MY_PACKAGE_REPLACED.equals(action)||"android.intent.action.TIME_SET".equals(action)||"android.intent.action.TIMEZONE_CHANGED".equals(action)) {
            ReminderScheduler.updateAll(c);return;
        }
        int id=in.getIntExtra("session",-1);
        if(id<0||id>2)return;
        if(ReminderScheduler.ACTION_SNOOZE.equals(action)) {
            int minutes=in.getIntExtra("minutes",10);
            ReminderScheduler.snooze(c,id,minutes);
            NotificationManager nm=(NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE);
            if(nm!=null)nm.cancel(400+id);
            return;
        }
        if(!ReminderScheduler.ACTION_REMIND.equals(action)||!ReminderScheduler.enabled(c,id))return;
        ReminderScheduler.update(c,id);
        NotificationManager nm=(NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE);
        if(nm==null)return;
        String channel="assi_affirmations";
        if(Build.VERSION.SDK_INT>=26)nm.createNotificationChannel(new NotificationChannel(channel,"Recordatorios de afirmaciones",NotificationManager.IMPORTANCE_DEFAULT));
        Intent open=new Intent(c,MainActivity.class).putExtra("open_session",id).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent openPi=PendingIntent.getActivity(c,500+id,open,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        Notification.Builder b=Build.VERSION.SDK_INT>=26?new Notification.Builder(c,channel):new Notification.Builder(c);
        b.setSmallIcon(android.R.drawable.ic_popup_reminder).setContentTitle("ASSI Afirmaciones · "+ReminderScheduler.NAMES[id])
         .setContentText("Es momento de tus afirmaciones.").setAutoCancel(true).setContentIntent(openPi)
         .addAction(new Notification.Action.Builder(android.R.drawable.ic_media_play,"Iniciar",openPi).build());
        for(int minutes:new int[]{5,10,15,30}) {
            Intent snooze=new Intent(c,ReminderReceiver.class).setAction(ReminderScheduler.ACTION_SNOOZE).putExtra("session",id).putExtra("minutes",minutes);
            PendingIntent p=PendingIntent.getBroadcast(c,600+id*100+minutes,snooze,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
            b.addAction(new Notification.Action.Builder(android.R.drawable.ic_lock_idle_alarm,"+"+minutes+" min",p).build());
        }
        if(Build.VERSION.SDK_INT<33||c.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)==PackageManager.PERMISSION_GRANTED)nm.notify(400+id,b.build());
    }
}