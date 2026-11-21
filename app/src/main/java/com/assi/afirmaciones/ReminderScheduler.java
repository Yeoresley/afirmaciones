package com.assi.afirmaciones;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import java.util.Calendar;

public final class ReminderScheduler {
    private ReminderScheduler() {}
    public static final String PREFS = "reminders";
    public static final String ACTION_REMIND = "com.assi.afirmaciones.REMIND";
    public static final String ACTION_SNOOZE = "com.assi.afirmaciones.SNOOZE";
    public static final int[] DEFAULT_HOURS = {8,13,21};
    public static final int[] DEFAULT_MINUTES = {0,0,30};
    public static final String[] NAMES = {"Mañana", "Mediodía", "Noche"};
    public static SharedPreferences prefs(Context c) { return c.getSharedPreferences(PREFS,Context.MODE_PRIVATE); }
    public static boolean enabled(Context c,int i) { return prefs(c).getBoolean("enabled_"+i,false); }
    public static int hour(Context c,int i) { return prefs(c).getInt("hour_"+i,DEFAULT_HOURS[i]); }
    public static int minute(Context c,int i) { return prefs(c).getInt("minute_"+i,DEFAULT_MINUTES[i]); }
    private static PendingIntent intent(Context c,int id) {
        Intent in=new Intent(c,ReminderReceiver.class).setAction(ACTION_REMIND).putExtra("session",id);
        return PendingIntent.getBroadcast(c,100+id,in,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
    }
    private static void schedule(Context c,int id,long at) {
        AlarmManager am=(AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
        if(am==null)return;
        PendingIntent pi=intent(c,id);
        if(Build.VERSION.SDK_INT>=31 && am.canScheduleExactAlarms()) am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,at,pi);
        else if(Build.VERSION.SDK_INT>=23) am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,at,pi);
        else am.set(AlarmManager.RTC_WAKEUP,at,pi);
    }
    public static void update(Context c,int id) {
        AlarmManager am=(AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
        if(am!=null)am.cancel(intent(c,id));
        if(!enabled(c,id))return;
        Calendar when=Calendar.getInstance();
        when.set(Calendar.HOUR_OF_DAY,hour(c,id));
        when.set(Calendar.MINUTE,minute(c,id));
        when.set(Calendar.SECOND,0);
        when.set(Calendar.MILLISECOND,0);
        if(when.getTimeInMillis()<=System.currentTimeMillis())when.add(Calendar.DAY_OF_YEAR,1);
        schedule(c,id,when.getTimeInMillis());
    }
    public static void updateAll(Context c) { for(int i=0;i<3;i++)update(c,i); }
    public static void snooze(Context c,int id,int minutes) {
        if(id<0||id>2||!enabled(c,id))return;
        Intent in=new Intent(c,ReminderReceiver.class).setAction(ACTION_REMIND).putExtra("session",id);
        PendingIntent pi=PendingIntent.getBroadcast(c,200+id,in,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        AlarmManager am=(AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
        if(am==null)return;
        long at=System.currentTimeMillis()+minutes*60000L;
        if(Build.VERSION.SDK_INT>=31&&am.canScheduleExactAlarms())am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,at,pi);
        else if(Build.VERSION.SDK_INT>=23)am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,at,pi);
        else am.set(AlarmManager.RTC_WAKEUP,at,pi);
    }
}