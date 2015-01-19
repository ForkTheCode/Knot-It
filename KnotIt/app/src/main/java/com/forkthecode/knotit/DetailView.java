package com.forkthecode.knotit;

import java.io.File;

import models.Knot;
import utilities.FloatingActionButton;
import utilities.LoadLargePics;
import utilities.LoadLargePics.onImageLoaded;
import utilities.tools;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;



public class DetailView extends ActionBarActivity implements onImageLoaded{
	
	String title;
	String description;
	String image_path;
	Long timestamp;
	Long reminder_timestamp;
	Knot mKnot;
	int type;
    int isRepeating;
    Long repeatingTime;

	FloatingActionButton fabButton;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail_view);
		ActionBar bar = getSupportActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.pink)));
		bar.setDisplayHomeAsUpEnabled(true);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            if(getWindow()!=null)
			getWindow().setStatusBarColor(getResources().getColor(R.color.pink_700));
		}
        fabButton = new FloatingActionButton.Builder(this)
                .withDrawable(getResources().getDrawable(R.drawable.ic_action_edit))
                .withButtonColor(getResources().getColor(R.color.accent))
                .withGravity(Gravity.BOTTOM | Gravity.END)
                .withMargins(0, 0, 16, 24)
                .create();
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               edit(v);
            }
        });
        fabButton.showFloatingActionButton();
		Intent i = getIntent();
		type = i.getIntExtra("type", 1);
		if(type != 1 && fabButton!=null){
            //Display fab only for Knots in "Knots" Section
			fabButton.setEnabled(false);
			fabButton.hideFloatingActionButton();
		}
		else{
            if(fabButton!=null) {
                fabButton.setEnabled(true);
                fabButton.showFloatingActionButton();
            }
		}
		title = i.getStringExtra("title");
		description = i.getStringExtra("description");
		image_path = i.getStringExtra("image_path");
		timestamp = i.getLongExtra("timestamp", tools.getCurrentTime());
		reminder_timestamp = i.getLongExtra("reminder_timestamp", tools.getCurrentTime());
        isRepeating = i.getIntExtra("isRepeating",0);
        repeatingTime = i.getLongExtra("repeating_time", 0);
		mKnot = new Knot(title, description, image_path, timestamp,
                reminder_timestamp ,isRepeating,repeatingTime );
		if(title != null)
		bar.setTitle(title);
		else
			bar.setTitle(getString(R.string.app_name));
		setData();
		
	}


    ProgressBar bar;
	ScrollView sv ;
	ImageView imageView;


    private void setData() {
		sv = (ScrollView) findViewById(R.id.scrollView);
		sv.setVisibility(View.GONE);
        if(fabButton!=null)
             fabButton.hideFloatingActionButton();
		bar = (ProgressBar) findViewById(R.id.progressBar2);
		bar.setVisibility(View.VISIBLE);
		TextView titleView = (TextView) findViewById(R.id.detail_textView_title);
		TextView descView = (TextView) findViewById(R.id.detail_textView_desc);
		TextView timeView = (TextView) findViewById(R.id.detail_settime);
		TextView reminderView = (TextView) findViewById(R.id.detail_reminderdate);
		imageView = (ImageView) findViewById(R.id.viewImage);
		titleView.setText(title);
		descView.setText(description);
        CheckBox checkBox = (CheckBox) findViewById(R.id.checkBoxdetail);
        if(isRepeating==1){
            checkBox.setVisibility(View.VISIBLE);
            if(repeatingTime == 3600000l) {
                    checkBox.setText(getString(R.string.repeat_every_hour));
            }
             else if(repeatingTime == 86400000l) {
                checkBox.setText(getString(R.string.repeat_every_day));
            }
             else if(repeatingTime == 604800000l) {
                checkBox.setText(getString(R.string.repeat_every_week));
            }

        }
        else{
            checkBox.setVisibility(View.GONE);
        }
		timeView.setText(getString(R.string.edited_on) + " " + tools.epochToTime(this, timestamp));
		reminderView.setText(tools.epochToTime(this, reminder_timestamp));
        LoadLargePics loadPics = new LoadLargePics(this);
        loadPics.execute(image_path);
        loadPics.delegate = this;

		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		if(type == 1){
		getMenuInflater().inflate(R.menu.detail_view, menu);
		}
		else if(type == 2){
			getMenuInflater().inflate(R.menu.detail_view_archive,menu);
		}
		else if(type == 3){
			getMenuInflater().inflate(R.menu.detail_view_delete, menu);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.delete) {
			tools.moveToTrash(this, mKnot);
			Toast t = Toast.makeText(this, getString(R.string.moved_to_trash), Toast.LENGTH_SHORT);
			t.show();
			this.finish();

			return true;
		}
		else if(id==android.R.id.home){
			this.finish();
		        return true;
		}
		else if(id == R.id.archive){
			tools.archive(this, mKnot);
			Toast t = Toast.makeText(this, getString(R.string.Done_for_now), Toast.LENGTH_SHORT);
			t.show();
			this.finish();
			return true;
		}
		else if(id == R.id.deleteArchive){
			tools.moveToTrashFromArchived(this, mKnot);
			Toast t = Toast.makeText(this, getString(R.string.moved_to_trash), Toast.LENGTH_SHORT);
			t.show();
			this.finish();
			return true;
		}
		else if(id == R.id.unarchive){
			tools.unArchive(this, mKnot);
			Toast t = Toast.makeText(this, getString(R.string.moved_to_Knots), Toast.LENGTH_SHORT);
			t.show();
			this.finish();
			return true;
		}
		else if(id == R.id.movebackfromtrash){
			tools.moveFromTrashToArchived(this, mKnot);
			Toast t = Toast.makeText(this, getString(R.string.Done_for_now), Toast.LENGTH_SHORT);
			t.show();
			this.finish();
			return true;
		}
		else if (id == R.id.permDelt){
			final Context context = this;
			final ActionBarActivity activity = this;
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        builder.setMessage(getString(R.string.delete_forever_question))
	               .setPositiveButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                           tools.permDelt(context, mKnot);
                           if (mKnot.imageSource != null) {
                               if (mKnot.imageSource.contains("com.forkthecode.knotit")) {
                                   File file = new File(mKnot.imageSource);
                                   file.delete();
                               }
                           }
                           dialog.dismiss();
                           Toast t = Toast.makeText(context, getString(R.string.deleted_forever), Toast.LENGTH_SHORT);
                           t.show();
                           activity.finish();
                       }
                   })
	               .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                           dialog.dismiss();
                       }
                   });
	        // Create the AlertDialog object and return it
	        builder.show();
	       
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void edit(View view){
		Intent edit = new Intent();
		edit.setClass(this, AddNew.class);
		edit.putExtra("type", 2);
		edit.putExtra("title", title);
		edit.putExtra("description", description);
		edit.putExtra("image_path", image_path);
		edit.putExtra("timestamp", timestamp);
		edit.putExtra("reminder_timestamp", reminder_timestamp);
        edit.putExtra("isRepeating",isRepeating);
        edit.putExtra("repeating_time",repeatingTime);
		startActivity(edit);
		this.finish();
	}

	@Override
	public void setImage(Drawable d) {
        if (type == 1 && fabButton != null) {
            fabButton.showFloatingActionButton();
        }
        bar.setVisibility(View.GONE);
        sv.setVisibility(View.VISIBLE);
        imageView.setImageDrawable(d);

    }

}
