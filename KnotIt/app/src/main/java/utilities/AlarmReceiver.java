package utilities;

import com.forkthecode.knotit.DetailView;
import com.forkthecode.knotit.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {

    long[] mario = {125,75,125,275,200,275,125,75,125,275,200,600,200,600};
    long[] teenageTurtle = {75,75,75,75,75,75,75,75,150,150,150,450,75,75,75,75,75,525};
    long[] voltron = {250,200,150,150,100,50,450,450,150,150,100,50,900,2250};
    long[] finalFantasy ={50,100,50,100,50,100,400,100,300,100,350,50,200,100,100,50,600};
    long[] starWars = {500,110,500,110,450,110,200,110,170,40,450,110,200,110,170,40,500};
    long[] powerRangers = {150,150,150,150,75,75,150,150,150,150,450};
    long[] jamesBond = {200,100,200,275,425,100,200,100,200,275,425,100,75,25,75,125,75,25,75,
            125,100,100};
    long[] defaultL = { 100, 150, 200, 250, 200, 150 , 100 , 0 , 0 , 0 ,100, 150, 200, 250, 200,
            150 , 100};
    long[] mortalKombat ={100,200,100,200,100,200,100,200,100,100,100,100,100,200,100,200,100,200,
            100,200,100,100,100,100,100,200,100,200,100,200,100,200,100,100,100,100,100,100,100,100,
            100,100,50,50,100,800};
    long[] michaelJ = {0,300,100,50,100,50,100,50,100,50,100,50,100,50,150,150,150,450,100,50,100,
            50,150,150,150,450,100,50,100,50,150,150,150,450,150,150};

    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("title");
        String desc = intent.getStringExtra("description");
        String image_path  = intent.getStringExtra("image_path");
        Long time = intent.getLongExtra("timestamp", tools.getCurrentTime());
        Long reminderTime = intent.getLongExtra("reminder_timestamp", tools.getCurrentTime());
        int isRepeating = intent.getIntExtra("isRepeating",0);
        Long repeatingTime = intent.getLongExtra("repeating_time",0);
        int timeInt = (int)(time/1000);
        Intent resultIntent = new Intent(context, DetailView.class);
        resultIntent.putExtra("type", 1);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        resultIntent.putExtra("title", title);
        resultIntent.putExtra("description", desc);
        resultIntent.putExtra("image_path", image_path);
        resultIntent.putExtra("timestamp", time);
        resultIntent.putExtra("reminder_timestamp", reminderTime);
        resultIntent.putExtra("isRepeating",isRepeating);
        resultIntent.putExtra("repeating_time",repeatingTime);
        PendingIntent contentIntent = PendingIntent.getActivity(context, timeInt, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT );
        SharedPreferences prefs_sound = context.getSharedPreferences("sound",Context.MODE_PRIVATE);
        String sound = prefs_sound.getString("sound_uri",RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString());
        Uri alarmSound = Uri.parse(sound);
        SharedPreferences prefs = context.getSharedPreferences("vibrate",Context.MODE_PRIVATE);
        long[] pattern = defaultL;
        switch ( prefs.getInt("vibrate_key",0)){
            case 0:
                pattern = defaultL;
                break;
            case 1:
                pattern = mario;
                break;
            case 2:
                pattern = teenageTurtle;
                break;
            case  3:
                pattern = voltron;
                break;
            case 4:
                pattern = finalFantasy;
                break;
            case 5:
                pattern = starWars;
                break;
            case 6:
                pattern = jamesBond;
                break;
            case 7:
                pattern = powerRangers;
                break;
            case 8:
                pattern = mortalKombat;
                break;
            case 9:
                pattern = michaelJ;
                break;
            case 10:
                pattern = null;
                break;
        }
        Intent i = new Intent(context, OnDone.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("title", title);
        i.putExtra("description", desc);
        i.putExtra("image_path", image_path);
        i.putExtra("timestamp", time);
        i.putExtra("reminder_timestamp", reminderTime);
        PendingIntent onDone = PendingIntent.getBroadcast(context, timeInt, i,
                PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)

                .setContentTitle(title)
                .setContentText(desc)
                .setSmallIcon(R.drawable.ic_action_done_small)
                .setPriority(Notification.PRIORITY_MAX)
                .setSound(alarmSound, AudioManager.STREAM_NOTIFICATION)
                .setColor(context.getResources().getColor(R.color.primary))
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .addAction(R.drawable.ic_material_archive_light, context.getString(R.string.archive), onDone);
        if(image_path != null){
            Bitmap bm = tools.decodeSampledBitmapFromSource(image_path,
                    (int)tools.getWidth(context), (int)tools.convertDpToPixel(150, context));
            builder.setLargeIcon(bm);
            builder.setStyle(new NotificationCompat.BigPictureStyle().setBigContentTitle(title)
                    .setSummaryText(desc)
                    .bigPicture(bm));
        }
        else{
            Bitmap background = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.bt_il_events_threadlist);
            NotificationCompat.WearableExtender wearableExtender =
                    new NotificationCompat.WearableExtender()
                            .setHintHideIcon(true)
                            .setBackground(background);
            builder.extend(wearableExtender);

        }

        NotificationManager mNotificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification noti = builder.build();
        if(pattern == null){
            noti.defaults = Notification.DEFAULT_VIBRATE;
        }
        else{
            noti.vibrate = pattern;
        }
        mNotificationManager.notify(timeInt,noti);
    }
}
