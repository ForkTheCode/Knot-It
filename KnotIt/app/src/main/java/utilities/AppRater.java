package utilities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.forkthecode.knotit.R;

public class AppRater {
    private final static String APP_PNAME = "com.forkthecode.knotit";

    private final static int DAYS_UNTIL_PROMPT = 9;
    private final static int LAUNCHES_UNTIL_PROMPT = 11;

    public static void app_launched(Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("apprater", 0);
        if (prefs.getBoolean("dontshowagain", false)) { return ; }

        SharedPreferences.Editor editor = prefs.edit();

        // Increment launch counter
        long launch_count = prefs.getLong("launch_count", 0) + 1;
        editor.putLong("launch_count", launch_count);

        // Get date of first launch
        Long date_firstLaunch = prefs.getLong("date_firstlaunch", 0);
        if (date_firstLaunch == 0) {
            date_firstLaunch = System.currentTimeMillis();
            editor.putLong("date_firstlaunch", date_firstLaunch);
        }

        // Wait at least n days before opening
        if (launch_count >= LAUNCHES_UNTIL_PROMPT) {
            if (System.currentTimeMillis() >= date_firstLaunch +
                    (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000)) {
                showRateDialog(mContext, editor);
            }
        }

        editor.commit();
    }

    public static void showRateDialog(final Context mContext,
                                      final SharedPreferences.Editor editor) {
        final Dialog dialog = new Dialog(mContext);
        dialog.setTitle(mContext.getString(R.string.rate) + " "
                + mContext.getString(R.string.app_name));
        dialog.setCancelable(false);
        LinearLayout ll = new LinearLayout(mContext);
        ll.setOrientation(LinearLayout.VERTICAL);



        TextView tv = new TextView(mContext);
        tv.setText(mContext.getString(R.string.rate_message));

        tv.setPadding(16, 0, 16, 0);
        ll.addView(tv);

        Button b1 = new Button(mContext);
        b1.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        b1.setText(mContext.getString(R.string.rate) + mContext.getString(R.string.app_name));
        b1.setTextColor(mContext.getResources().getColor(R.color.primary_dark));
        b1.setBackground(null);
        b1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (editor != null) {
                    editor.putBoolean("dontshowagain", true);
                    editor.commit();
                }
                mContext.startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=" + APP_PNAME)));
                dialog.dismiss();
            }
        });
        ll.addView(b1);

        Button b2 = new Button(mContext);
        b2.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        b2.setBackground(null);
        b2.setText(mContext.getString(R.string.remind_me_later));
        b2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
                if(editor!=null){
                    editor.putLong("launch_count", 0);
                    editor.commit();
                }
            }

        });
        ll.addView(b2);

        Button b3 = new Button(mContext);
        b3.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        b3.setBackground(null);
        b3.setText(mContext.getString(R.string.no_thanks));
        b3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (editor != null) {
                    editor.putBoolean("dontshowagain", true);
                    editor.commit();
                }
                dialog.dismiss();
            }
        });
        ll.addView(b3);

        dialog.setContentView(ll);
        dialog.show();
    }
}