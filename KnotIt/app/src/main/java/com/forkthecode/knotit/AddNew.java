package com.forkthecode.knotit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import models.Knot;

import utilities.DatePickerFragment;
import utilities.FloatingActionButton;
import utilities.KnotitOpenHelper;
import utilities.TimePickerFragment;
import utilities.tools;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Build;
import android.support.v4.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class AddNew extends ActionBarActivity  {

    public static final int ADD_NEW = 1; //Create New Knot
    public static final int EDIT = 2;//Edit a Knot
    boolean set = false;
    int year;
    int month;
    int day;
    int hour;
    int min;
    int sec;
    public static boolean datePicked = false;
    public static boolean timePicked = false;
    String imageSource = null;
    ImageView imageView;
    Knot mPreviousKnot;
    Knot mNewKnot;
    int type;
    Bitmap mBitmap;
    Uri mCapturedImageUrl;
    FloatingActionButton fabButton;
    Spinner spinner;
    CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new);
        imageView = (ImageView) findViewById(R.id.addimage);
        Intent i = getIntent();
        mNewKnot = new Knot();
        mPreviousKnot = new Knot();
        EditText descView = (EditText) findViewById(R.id.addDesc);
        if (i.getAction() != null) {
            if (i.getAction().equals(Intent.ACTION_SEND)) {
                //content is being shared
                //Image from other application
                if (i.getType().startsWith("image/")) {
                    Uri receivedUri = i.getParcelableExtra(Intent.EXTRA_STREAM);
                    if (receivedUri != null) {
                        imageSource = tools.getPath(this, receivedUri);
                        imageView.setVisibility(View.VISIBLE);
                        imageView.setImageURI(receivedUri);
                        set = true;//Flag to check whether image is shared from other app.
                    }
                    // Handle intents with image data ...
                } else if (i.getType().equals("text/plain")) {
                    //Text from other application
                    String receivedText = i.getStringExtra(Intent.EXTRA_TEXT);
                    descView.setText(receivedText);
                }
            }
        }
        type = i.getIntExtra("type", 1);
        fabButton = new FloatingActionButton.Builder(this)
                .withDrawable(getResources().getDrawable(R.drawable.ic_action_done_small))
                .withButtonColor(getResources().getColor(R.color.accent))
                .withGravity(Gravity.BOTTOM | Gravity.END)
                .withMargins(0, 0, 16, 24)
                .create();//Create fab
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeKnot(v);
            }
        });
        fabButton.showFloatingActionButton();
        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.teal)));
        bar.setDisplayHomeAsUpEnabled(true);
        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.add(0, "every minute");
        arrayList.add(1, "every hour");
        arrayList.add(2, "every day");
        arrayList.add(3, "every week");
        spinner = (Spinner) findViewById(R.id.spinnerAdd);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arrayList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        mNewKnot.repeatingTime=60000;
                        mPreviousKnot.repeatingTime = 60000;
                        break;
                    case 1:
                        mNewKnot.repeatingTime = 3600000;
                        mPreviousKnot.repeatingTime = 3600000;
                        break;
                    case 2:
                        mNewKnot.repeatingTime = 86400000;
                        mPreviousKnot.repeatingTime = 86400000;
                        break;
                    case 3:
                        mNewKnot.repeatingTime = 604800000;
                        mPreviousKnot.repeatingTime = 604800000;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mNewKnot.repeatingTime = 60000;
                mPreviousKnot.repeatingTime = 60000;
            }
        });
        checkBox = (CheckBox) findViewById(R.id.checkBoxadd);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    spinner.setVisibility(View.VISIBLE);
                } else {
                    spinner.setVisibility(View.GONE);
                }
            }
        });
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            if (getWindow() != null)
                getWindow().setStatusBarColor(getResources().getColor(R.color.teal_700));
        }
        if (type == ADD_NEW) {
            if (!set)
                imageView.setVisibility(View.GONE);
            bar.setTitle(getString(R.string.Add_Knot));
        } else if (type == EDIT) {
            datePicked = false;
            timePicked = false;
            bar.setTitle(getString(R.string.Edit_Knot));
            String title = i.getStringExtra("title");
            String description = i.getStringExtra("description");
            String image_path = i.getStringExtra("image_path");
            int prevIsRepeating = i.getIntExtra("isRepeating", 0);
            long prevRepeatingTime = i.getLongExtra("repeating_time", 0);
            Long timestamp = i.getLongExtra("timestamp", tools.getCurrentTime());
            Long reminder_timestamp = i.getLongExtra("reminder_timestamp", tools.getCurrentTime());
            mPreviousKnot = new Knot(title, description, image_path, timestamp, reminder_timestamp,
                    prevIsRepeating, prevRepeatingTime);
            imageView = (ImageView) findViewById(R.id.addimage);
            if (image_path != null) {
                imageView.setVisibility(View.VISIBLE);
            }
            setData(title, description, image_path, reminder_timestamp, prevIsRepeating,
                    prevRepeatingTime);
        }
    }

    private void setData(String title, String description, String image_path,
                         Long reminder_timestamp, int prevIsRepeating, Long prevRepeatingTime) {
        EditText titleView = (EditText) findViewById(R.id.addTitle);
        EditText descView = (EditText) findViewById(R.id.addDesc);
        TextView dateView = (TextView) findViewById(R.id.pickdate);
        TextView timeView = (TextView) findViewById(R.id.picktime);
        ImageView imageView = (ImageView) findViewById(R.id.addimage);
        titleView.setText(title);
        descView.setText(description);
        dateView.setText(new SimpleDateFormat("dd/MM/yyyy", getResources().getConfiguration()
                .locale).format(new Date(reminder_timestamp)));
        timeView.setText(new SimpleDateFormat("HH:mm", getResources().getConfiguration().locale)
                .format(new Date(reminder_timestamp)));
        if (image_path != null) {
            Bitmap b = tools.decodeSampledBitmapFromSource(image_path, (int) tools.getWidth(this),
                    (int) tools.convertDpToPixel(200, this));
            if (b != null) {
                imageView.setImageDrawable(new BitmapDrawable(getResources(), b));
            } else {
                imageView.setImageDrawable(getResources()
                        .getDrawable(R.drawable.img_not_available));
            }
        }
        if (prevIsRepeating == 1) {
            checkBox.setChecked(true);
            spinner.setVisibility(View.VISIBLE);
            switch ((int)(prevRepeatingTime/10000)){
                case 6:
                    spinner.setSelection(0);
                    break;
                case 360:
                    spinner.setSelection(1);
                    break;
                case 8640:
                    spinner.setSelection(2);
                    break;
                case 60480:
                    spinner.setSelection(3);
                    break;
            }

        } else {
            spinner.setVisibility(View.GONE);
            checkBox.setChecked(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_new, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_clearimage) {
            imageView.setImageDrawable(null);
            imageView.setVisibility(View.GONE);
            imageSource = null;
            mPreviousKnot.imageSource = null;
            return true;
        } else if (id == R.id.add_image) {
            selectImage();
            return true;
        } else if (id == android.R.id.home) {
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    String mCurrentPhotoPath;

    protected File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", getResources()
                .getConfiguration().locale).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (!storageDir.exists()) {
            if (!storageDir.mkdirs()) {
                Toast.makeText(this, getString(R.string.cannot_create_file), Toast.LENGTH_SHORT)
                        .show();
            }
        }
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents

        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    private static final int TAKE_PHOTO = 100;
    private static final int CHOOSE_PHOTO = 200;
    private static final int LOAD_IMAGE_RESULTS = 300;

    private void selectImage() {
        final CharSequence[] items = {getString(R.string.Take_Photo),
                getString(R.string.Choose_from_Gallery)};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.Add_Photo));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(getString(R.string.Take_Photo))) {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        // Create the File where the photo should go.
                        // If you don't do this, you may get a crash in some devices.
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException ex) {
                            // Error occurred while creating the File
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    getString(R.string.problem_saving_photo), Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        // Continue only if the File was successfully created
                        if (photoFile != null) {
                            Uri fileUri = Uri.fromFile(photoFile);
                            mCapturedImageUrl = fileUri;
                            mCurrentPhotoPath = fileUri.getPath();
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                    fileUri);
                            startActivityForResult(takePictureIntent, TAKE_PHOTO);
                        }
                    }
                    dialog.dismiss();
                } else if (items[item].equals(getString(R.string.Choose_from_Gallery))) {
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("image/*");
                        startActivityForResult(intent, CHOOSE_PHOTO);
                        dialog.dismiss();
                    } else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                            && android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore
                                .Images.Media.EXTERNAL_CONTENT_URI);

                        // Start new activity with the LOAD_IMAGE_RESULTS to handle back the
                        // results when image is picked from the Image Gallery.
                        startActivityForResult(i, LOAD_IMAGE_RESULTS);

                    }
                }
            }
        });
        builder.show();
    }


    public void pickDate(View view) {
        TextView textView = (TextView) findViewById(R.id.pickdate);
        DialogFragment newFragment = new DatePickerFragment(textView);
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void pickTime(View view) {
        TextView textView = (TextView) findViewById(R.id.picktime);
        DialogFragment newFragment = new TimePickerFragment(textView);
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void makeKnot(View view) {
        EditText titleEditText = (EditText) findViewById(R.id.addTitle);
        String title = titleEditText.getText().toString();
        EditText descEditText = (EditText) findViewById(R.id.addDesc);
        String desc = descEditText.getText().toString();
        long timeStamp = tools.getCurrentTime();
        checkBox = (CheckBox) findViewById(R.id.checkBoxadd);
        if (!checkBox.isChecked()) {
            mNewKnot.isRepeating = 0;
            mNewKnot.repeatingTime = 0;
            mPreviousKnot.isRepeating = 0;
            mPreviousKnot.repeatingTime = 0;
        } else {
            mPreviousKnot.isRepeating = 1;
            mNewKnot.isRepeating = 1;
        }
        if (type == EDIT) {
            Calendar pc = Calendar.getInstance();
            pc.setTime(new Date(mPreviousKnot.reminderTimestamp));
            year = pc.get(Calendar.YEAR);
            day = pc.get(Calendar.DAY_OF_MONTH);
            month = pc.get(Calendar.MONTH);
            hour = pc.get(Calendar.HOUR_OF_DAY);
            min = pc.get(Calendar.MINUTE);
            if (datePicked && timePicked) {
                year = DatePickerFragment.mYear;
                day = DatePickerFragment.mDay;
                month = DatePickerFragment.mMonth;
                hour = TimePickerFragment.hour;
                min = TimePickerFragment.min;
                Calendar c = Calendar.getInstance();
                c.set(year, month - 1, day, hour, min, sec);
                mPreviousKnot.reminderTimestamp = c.getTimeInMillis();
            } else if (datePicked) {
                year = DatePickerFragment.mYear;
                day = DatePickerFragment.mDay;
                month = DatePickerFragment.mMonth;
                Calendar c = Calendar.getInstance();
                c.set(year, month - 1, day, hour, min, sec);
                mPreviousKnot.reminderTimestamp = c.getTimeInMillis();

            } else if (timePicked) {
                hour = TimePickerFragment.hour;
                min = TimePickerFragment.min;
                Calendar c = Calendar.getInstance();
                c.set(year, month, day, hour, min, sec);
                mPreviousKnot.reminderTimestamp = c.getTimeInMillis();
            }
        }
        KnotitOpenHelper openHelper = KnotitOpenHelper.getInstance(this);
        SQLiteDatabase db = openHelper.getWritableDatabase();
        if (type == ADD_NEW) {
            if (datePicked && timePicked) {
                year = DatePickerFragment.mYear;
                day = DatePickerFragment.mDay;
                month = DatePickerFragment.mMonth;
                hour = TimePickerFragment.hour;
                min = TimePickerFragment.min;
                Calendar c = Calendar.getInstance();
                c.set(year, month - 1, day, hour, min, sec);
                long reminderTimestamp = c.getTimeInMillis();
                Knot knot = new Knot(title, desc, imageSource, timeStamp, reminderTimestamp,
                        mNewKnot.isRepeating, mNewKnot.repeatingTime);
                openHelper.addToDataBase(db, KnotitOpenHelper.KNOTS_TABLE_NAME, knot);
                tools.setReminder(this, knot);
                this.finish();
            } else {
                Toast t = Toast.makeText(this, getString(R.string.Choose_date_time), Toast.LENGTH_SHORT);
                t.show();
            }

        } else if (type == EDIT) {


            String timestamp[] = {String.valueOf(mPreviousKnot.timestamp)};
            db.delete(KnotitOpenHelper.KNOTS_TABLE_NAME, KnotitOpenHelper.COLUMN_TIMESTAMP + "= ?"
                    , timestamp);
            Knot knot = new Knot(title, desc, mPreviousKnot.imageSource, timeStamp,
                    mPreviousKnot.reminderTimestamp, mPreviousKnot.isRepeating,
                    mPreviousKnot.repeatingTime);
            openHelper.addToDataBase(db, KnotitOpenHelper.KNOTS_TABLE_NAME, knot);
            tools.cancelReminder(this, mPreviousKnot);
            tools.setReminder(this, knot);
            this.finish();
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHOOSE_PHOTO && data != null && data.getData() != null) {
            Uri _uri = data.getData();
            imageSource = tools.getPath(this, _uri);
            mBitmap = tools.decodeSampledBitmapFromSource(imageSource,
                    (int) tools.getWidth(getApplicationContext()),
                    (int) tools.convertDpToPixel(200, getApplicationContext()));
            mPreviousKnot.imageSource = imageSource;
            imageView.setVisibility(View.VISIBLE);
            if (mBitmap != null)
                imageView.setImageBitmap(mBitmap);
            else {
                Toast.makeText(this, getString(R.string.problem_setting_image),
                        Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == TAKE_PHOTO && resultCode == ActionBarActivity.RESULT_OK) {
            if (data != null) {
                //For Devices where camera does not store image to given path and
                // and only stores in gallery or storage framework.
                //Cut image from gallery and move to our directory
                Uri _uri = data.getData();
                ProgressDialog pDialog = new ProgressDialog(getApplicationContext());
                pDialog.show();

                imageSource = tools.getPath(this, _uri);
                mBitmap = tools.getBitmapFromSource(imageSource);
                File newFile = new File(mCurrentPhotoPath);
                FileOutputStream fos;
                try {
                    fos = new FileOutputStream(newFile);

                    // Use the compress method on the BitMap object to write image
                    // to the OutputStream
                    mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.close();
                    File del = new File(imageSource);
                    del.delete();
                    imageSource = newFile.getAbsolutePath();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                pDialog.dismiss();
            } else {
                imageSource = mCurrentPhotoPath;
                mPreviousKnot.imageSource = mCurrentPhotoPath;
                mBitmap = tools.decodeSampledBitmapFromSource(imageSource, (int)
                                tools.getWidth(getApplicationContext()),
                        (int) tools.convertDpToPixel(200, getApplicationContext()));
            }
            imageView.setVisibility(View.VISIBLE);
            if (mBitmap != null)
                imageView.setImageBitmap(mBitmap);
            else {
                Toast.makeText(this, getString(R.string.problem_setting_image),
                        Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == LOAD_IMAGE_RESULTS && resultCode == RESULT_OK && data != null) {
            // Let's read picked image data - its URI
            Uri pickedImage = data.getData();
            // Let's read picked image path using content resolver
            String[] filePath = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(pickedImage, filePath, null, null, null);
            cursor.moveToFirst();
            imageSource = cursor.getString(cursor.getColumnIndex(filePath[0]));
            mPreviousKnot.imageSource = imageSource;
            mBitmap = tools.decodeSampledBitmapFromSource(imageSource,
                    (int) tools.getWidth(getApplicationContext()),
                    (int) tools.convertDpToPixel(200, getApplicationContext()));
            imageView.setVisibility(View.VISIBLE);
            if (mBitmap != null)
                imageView.setImageBitmap(mBitmap);
            else {
                Toast.makeText(this, getString(R.string.problem_setting_image),
                        Toast.LENGTH_SHORT).show();
            }
            cursor.close();
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.image_capture_fail),
                    Toast.LENGTH_SHORT)
                    .show();
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}