package utilities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

import models.Knot;

public class BootCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ArrayList<Knot> knotList = tools.getKnotsArrayList(context,KnotitOpenHelper.KNOTS_TABLE_NAME);
        for (Knot knot : knotList) {
            tools.setReminder(context,knot);
        }
    }
}
