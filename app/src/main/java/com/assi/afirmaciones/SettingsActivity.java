package com.assi.afirmaciones;

import android.app.*;
import android.os.*;
import android.Manifest;
import android.content.*;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.widget.*;
import java.util.Locale;

public class SettingsActivity extends Activity {
    LinearLayout root;
    int fg,bg,card,accent;
    int dp(float n){return (int)(getResources().getDisplayMetrics().density*n+.5f);}
    boolean dark(){String theme=getSharedPreferences("appearance",0).getString("theme","system");return theme.equals("dark")||(theme.equals("system")&&((getResources().getConfiguration().uiMode&48)==32));}
    GradientDrawable shape(int color,int radius){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(dp(radius));return d;}
    TextView label(String s,int size,boolean bold){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(fg);if(bold)t.setTypeface(null,Typeface.BOLD);t.setPadding(dp(6),dp(8),dp(6),dp(8));return t;}
    void permission(){if(Build.VERSION.SDK_INT>=33&&checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)!=PackageManager.PERMISSION_GRANTED)requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS},10);}
    @Override public void onCreate(Bundle b){super.onCreate(b);show();}
    void show(){
        boolean d=dark();bg=d?Color.rgb(14,24,39):Color.rgb(246,248,252);fg=d?Color.WHITE:Color.rgb(23,39,61);card=d?Color.rgb(28,43,62):Color.WHITE;accent=Color.rgb(40,111,216);
        root=new LinearLayout(this);root.setOrientation(1);root.setPadding(dp(20),dp(24),dp(20),dp(24));root.setBackgroundColor(bg);
        ScrollView scroll=new ScrollView(this);scroll.addView(root);setContentView(scroll);getWindow().setStatusBarColor(bg);getWindow().getDecorView().setSystemUiVisibility(d?0:View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        root.addView(label("Preferencias",29,true));root.addView(label("Personaliza tu experiencia y tus avisos.",15,false));
        root.addView(label("Apariencia",20,true));
        RadioGroup themes=new RadioGroup(this);themes.setOrientation(0);String[] names={"Sistema","Claro","Oscuro"};String[] keys={"system","light","dark"};String current=getSharedPreferences("appearance",0).getString("theme","system");
        for(int i=0;i<3;i++){final String key=keys[i];RadioButton rb=new RadioButton(this);rb.setText(names[i]);rb.setTextColor(fg);rb.setTextSize(13);rb.setChecked(key.equals(current));themes.addView(rb);rb.setOnClickListener(v->{getSharedPreferences("appearance",0).edit().putString("theme",key).apply();show();});}
        root.addView(themes);
        root.addView(label("Recordatorios diarios",20,true));
        root.addView(label("Los avisos funcionan sin conexión. Android puede retrasar las alarmas cuando restringe el uso de batería.",13,false));
        for(int i=0;i<3;i++){final int id=i;
            LinearLayout box=new LinearLayout(this);box.setOrientation(1);box.setPadding(dp(14),dp(10),dp(14),dp(12));box.setBackground(shape(card,18));
            LinearLayout.LayoutParams bp=new LinearLayout.LayoutParams(-1,-2);bp.setMargins(0,dp(8),0,dp(8));root.addView(box,bp);
            Switch toggle=new Switch(this);toggle.setText(ReminderScheduler.NAMES[i]);toggle.setTextSize(18);toggle.setTextColor(fg);toggle.setChecked(ReminderScheduler.enabled(this,i));box.addView(toggle);
            Button time=new Button(this);time.setAllCaps(false);time.setText(String.format(Locale.getDefault(),"Horario: %02d:%02d",ReminderScheduler.hour(this,i),ReminderScheduler.minute(this,i)));box.addView(time);
            time.setOnClickListener(v->{TimePickerDialog dlg=new TimePickerDialog(this,(picker,h,m)->{ReminderScheduler.prefs(this).edit().putInt("hour_"+id,h).putInt("minute_"+id,m).apply();ReminderScheduler.update(this,id);time.setText(String.format(Locale.getDefault(),"Horario: %02d:%02d",h,m));},ReminderScheduler.hour(this,id),ReminderScheduler.minute(this,id),true);dlg.show();});
            toggle.setOnCheckedChangeListener((v,checked)->{ReminderScheduler.prefs(this).edit().putBoolean("enabled_"+id,checked).apply();ReminderScheduler.update(this,id);if(checked)permission();});
        }
        root.addView(label("Posposición desde la notificación: 5, 10, 15 o 30 minutos.",14,false));
        Button back=new Button(this);back.setText("Volver");back.setAllCaps(false);root.addView(back);back.setOnClickListener(v->finish());
    }
}