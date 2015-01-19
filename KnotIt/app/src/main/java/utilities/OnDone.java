package utilities;

import models.Knot;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class OnDone extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		String title = intent.getStringExtra("title");
		String desc = intent.getStringExtra("description");
		String image_path  = intent.getStringExtra("image_path");
		Long time = intent.getLongExtra("timestamp", tools.getCurrentTime());
		Long reminderTime = intent.getLongExtra("reminder_timestamp", tools.getCurrentTime());
        int isRepeating = intent.getIntExtra("isRepeating",0);
        Long repeatingTime = intent.getLongExtra("repeating_time",0);
		Knot knot = new Knot(title, desc, image_path, time, reminderTime, isRepeating,repeatingTime);
		int timeInt = (int)(time/1000); 
		NotificationManager notificationManager = (NotificationManager) 
                context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(timeInt);
		tools.archive(context, knot);

	}

}
