package com.forkthecode.knotit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.widget.DrawerLayout;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import models.Knot;
import utilities.AppRater;
import utilities.FloatingActionButton;
import utilities.KnotitOpenHelper;
import utilities.tools;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {
    String chosenRingtone;

    long[] mario = {125,75,125,275,200,275,125,75,125,275,200,600,200,600};
    long[] teenageTurtle = {75,75,75,75,75,75,75,75,150,150,150,450,75,75,75,75,75,525};
    long[] voltron = {250,200,150,150,100,50,450,450,150,150,100,50,900,2250};
    long[] finalFantasy ={50,100,50,100,50,100,400,100,300,100,350,50,200,100,100,50,600};
    long[] starWars = {500,110,500,110,450,110,200,110,170,40,450,110,200,110,170,40,500};
    long[] powerRangers = {150,150,150,150,75,75,150,150,150,150,450};
    long[] jamesBond = {200,100,200,275,425,100,200,100,200,275,425,100,75,25,75,125,75,25,75,
            125,100,100};
    long[] defaultL = { 100, 150, 200, 250, 200, 150 , 100 , 0 , 0 , 0 ,100, 150, 200, 250,
            200, 150 , 100};
    long[] mortalKombat ={100,200,100,200,100,200,100,200,100,100,100,100,100,200,100,200,100
            ,200,100,200,100,100,100,100,100,200,100,200,100,200,100,200,100,100,100,100,100,100
            ,100,100,100,100,50,50,100,800};
    long[] michaelJ = {0,300,100,50,100,50,100,50,100,50,100,50,100,50,150,150,150,450,100,50,100
            ,50,150,150,150,450,100,50,100,50,150,150,150,450,150,150};

    FloatingActionButton fabButton;
    public static HashMap<Long,Drawable> hashMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppRater.app_launched(this);
        hashMap = new HashMap<Long,Drawable>();
        /*
      Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
        NavigationDrawerFragment mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
            fabButton = new FloatingActionButton.Builder(this)
                    .withDrawable(getResources().getDrawable(R.drawable.ic_action_new))
                    .withButtonColor(getResources().getColor(R.color.accent))
                    .withGravity(Gravity.BOTTOM | Gravity.END)
                    .withMargins(0, 0, 16, 24)
                    .create();
            fabButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    add(v);
                }
            });

        fabButton.showFloatingActionButton();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(type == 2 || type == 1){
            fabButton.hideFloatingActionButton();
            fabButton.setEnabled(false);
        }
        else{
            fabButton.showFloatingActionButton();
            fabButton.setEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.knot_info) {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("message/rfc822");
            i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"knotit@forkthecode.com"});
            i.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                startActivity(Intent.createChooser(i, "Send mail..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(this, getString(R.string.no_email_client),
                        Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        else if(id == R.id.knot_delt){
            final Context context = this;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.clear_all_question))
                    .setPositiveButton(getString(R.string.clear_data), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            KnotitOpenHelper openHelper = KnotitOpenHelper.getInstance(context);
                            SQLiteDatabase db = openHelper.getWritableDatabase();
                            db.delete(KnotitOpenHelper.TRASH_KNOTS_TABLE_NAME, null, null);
                            db.delete(KnotitOpenHelper.ARCHIVED_KNOTS_TABLE_NAME, null, null);
                            ArrayList<Knot> list = tools.getKnotsArrayList(context,
                                    KnotitOpenHelper.KNOTS_TABLE_NAME);
                            for(int i =0;i<list.size();i++){
                                Knot knot = list.get(i);
                                tools.cancelReminder(context, knot);
                            }
                            db.delete(KnotitOpenHelper.KNOTS_TABLE_NAME, null, null);

                            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                            deleteDir(storageDir);
                            dialog.dismiss();
                            NotificationManager nManager = ((NotificationManager)
                                    context.getSystemService(Context.NOTIFICATION_SERVICE));
                            nManager.cancelAll();
                            try {
                                tools.firstRun(context);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            recreate();
                            Toast t = Toast.makeText(context, getString(R.string.data_clear), Toast.LENGTH_SHORT);
                            t.show();
                        }
                    })
                    .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
            builder.show();
            return true;
        }
        else if(id == R.id.menuSortTitle){
            SharedPreferences prefs = getSharedPreferences("sort",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("sort_by",KnotitOpenHelper.COLUMN_TITLE);
            editor.apply();
            recreate();
        }
        else if(id == R.id.menuSortReminder){
            SharedPreferences prefs = getSharedPreferences("sort",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("sort_by",KnotitOpenHelper.COLUMN_REMINDER_TIMESTAMP);
            editor.apply();
            recreate();
        }
        else if(id == R.id.menuSortEdited){
            SharedPreferences prefs = getSharedPreferences("sort",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("sort_by",KnotitOpenHelper.COLUMN_TIMESTAMP + " DESC");
            editor.apply();
            recreate();
        }
        else if(id==R.id.vib0){
            SharedPreferences prefs = getSharedPreferences("vibrate",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("vibrate_key", 0);
            editor.apply();
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(defaultL, -1);
        }
        else if(id==R.id.vib1){
            SharedPreferences prefs = getSharedPreferences("vibrate",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("vibrate_key", 1);
            editor.apply();
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

            v.vibrate(mario, -1);

        }
        else if(id==R.id.vib2){
            SharedPreferences prefs = getSharedPreferences("vibrate",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("vibrate_key", 2);
            editor.apply();
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

            v.vibrate(teenageTurtle, -1);
        }
        else if(id==R.id.vib3){
            SharedPreferences prefs = getSharedPreferences("vibrate",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("vibrate_key", 3);
            editor.apply();
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

            v.vibrate(voltron, -1);
        }
        else if(id==R.id.vib4){
            SharedPreferences prefs = getSharedPreferences("vibrate",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("vibrate_key", 4);
            editor.apply();
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(finalFantasy, -1);
        }
        else if(id==R.id.vib5){
            SharedPreferences prefs = getSharedPreferences("vibrate",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("vibrate_key", 5);
            editor.apply();
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(starWars, -1);
        }
        else if(id==R.id.vib6){
            SharedPreferences prefs = getSharedPreferences("vibrate",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("vibrate_key", 6);
            editor.apply();
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(jamesBond, -1);
        }
        else if(id==R.id.vib7){
            SharedPreferences prefs = getSharedPreferences("vibrate",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("vibrate_key", 7);
            editor.apply();
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

            v.vibrate(powerRangers, -1);
        }
        else if(id==R.id.vib8){
            SharedPreferences prefs = getSharedPreferences("vibrate",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("vibrate_key", 8);
            editor.apply();
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(mortalKombat, -1);
        }
        else if(id==R.id.vib9){
            SharedPreferences prefs = getSharedPreferences("vibrate",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("vibrate_key", 9);
            editor.apply();
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(michaelJ, -1);
        }
        else if(id==R.id.vib10){
            SharedPreferences prefs = getSharedPreferences("vibrate",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("vibrate_key", 10);
            editor.apply();
        }
        else if(id == R.id.setSound){
            Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
            this.startActivityForResult(intent, 5);
        }
        else if(id == R.id.about){
            Intent about = new Intent();
            about.setClass(this,About.class);
            startActivity(about);
        }

        return super.onOptionsItemSelected(item);
    }
    private static int type = 0;
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent intent)
    {
        if (resultCode == Activity.RESULT_OK && requestCode == 5)
        {
            Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

            if (uri != null)
            {
                this.chosenRingtone = uri.toString();
                SharedPreferences prefs = getSharedPreferences("sound",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("sound_uri", chosenRingtone);
                editor.apply();
            }
            else
            {
                this.chosenRingtone = null;
            }
        }
    }
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        final MainActivityFragment fragment = new MainActivityFragment();
        ActionBar bar = getSupportActionBar();

        type = position;
        if(position == 0){
            Bundle b = new Bundle();
            b.putString("table_name", KnotitOpenHelper.KNOTS_TABLE_NAME);
            fragment.setArguments(b);
            bar.setTitle(getString(R.string.app_name));
            bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.primary)));
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                if(getWindow()!=null)
              getWindow().setStatusBarColor(getResources().getColor(R.color.primary_dark));

            }
            if(fabButton!=null) {
                fabButton.showFloatingActionButton();
                fabButton.setEnabled(true);
            }
        }
        else if(position == 1) {
            Bundle b = new Bundle();
            b.putString("table_name", KnotitOpenHelper.ARCHIVED_KNOTS_TABLE_NAME);
            fragment.setArguments(b);
            bar.setTitle(getString(R.string.archive));
            bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.archive)));
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                if(getWindow()!=null)
                getWindow().setStatusBarColor(getResources().getColor(R.color.archive_dark));
            }
            if (fabButton != null) {
                fabButton.hideFloatingActionButton();
                fabButton.setEnabled(false);
            }
        }
        else if(position == 2) {
            Bundle b = new Bundle();
            b.putString("table_name", KnotitOpenHelper.TRASH_KNOTS_TABLE_NAME);
            fragment.setArguments(b);
            bar.setTitle(getString(R.string.trash));
            bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.trash)));
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                if(getWindow()!=null)
                getWindow().setStatusBarColor(getResources().getColor(R.color.trash_dark));
            }
            if (fabButton != null) {
                fabButton.hideFloatingActionButton();
                fabButton.setEnabled(false);
            }
        }
        fragmentManager.beginTransaction().replace(R.id.container , fragment ).commit();

    }
    public void add(View view){
        Intent add = new Intent();
        add.putExtra("type", 1);
        add.setClass(this,AddNew.class);
        startActivity(add);
    }
    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                boolean success = deleteDir(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }
}