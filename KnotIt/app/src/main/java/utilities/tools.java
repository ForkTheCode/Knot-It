package utilities;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import com.forkthecode.knotit.R;
import models.Knot;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.DisplayMetrics;

public class tools {

		public static Bitmap getBitmapFromSource(String Source){
			//Get full size Bitmap Image
            if(Source == null){
				return null;
			}
			 Bitmap bm;
             BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
             bm = BitmapFactory.decodeFile(Source, bitmapOptions);
             return bm;
		}

		
		public static long getCurrentTime(){
            //Get Current Time
			return System.currentTimeMillis();
		}
		
		public static ArrayList<Knot> getKnotsArrayList(Context context,String tableName){
            //Get Knots From Table
			ArrayList<Knot> list = new ArrayList<Knot>();
			KnotitOpenHelper openHelper =  KnotitOpenHelper.getInstance(context);
			SQLiteDatabase db = openHelper.getReadableDatabase();
            SharedPreferences prefs = context.getSharedPreferences("sort",Context.MODE_PRIVATE);
            //Checking Sorting Option
            String sortBy = prefs.getString("sort_by",KnotitOpenHelper.COLUMN_REMINDER_TIMESTAMP);
                Cursor c = db.query(tableName, null, null, null, null, null, sortBy);
                while (c.moveToNext()) {
                    String title = c.getString(c.getColumnIndex(KnotitOpenHelper.COLUMN_TITLE));
                    String des = c.getString(c.getColumnIndex(KnotitOpenHelper.COLUMN_DESC));
                    String imageSource = c.getString(c.getColumnIndex(
                            KnotitOpenHelper.COLUMN_IMAGE_SOURCE));
                    long timestamp = c.getLong(c.getColumnIndex(KnotitOpenHelper.COLUMN_TIMESTAMP));
                    long reminderTimestamp = c.getLong(c.getColumnIndex(
                            KnotitOpenHelper.COLUMN_REMINDER_TIMESTAMP));
                    long repeatingReminderTime = c.getLong(c.getColumnIndex(
                            KnotitOpenHelper.COLUMN_REMINDER_REPEATING_TIME));
                    int isRepeating = c.getInt(c.getColumnIndex(
                            KnotitOpenHelper.COLUMN_REMINDER_REPEATING));
                    while (getCurrentTime() > reminderTimestamp && isRepeating == 1) {
                       //If knot is repeating then change the reminder time to the next occurrence
                       // time.
                        reminderTimestamp += repeatingReminderTime;
                    }
                    Knot knot = new Knot(title, des, imageSource, timestamp, reminderTimestamp,
                            isRepeating, repeatingReminderTime);
                    list.add(knot);
                }
                c.close();
            //Checking If sort by option is set to Upcoming
            if(sortBy.equals(KnotitOpenHelper.COLUMN_REMINDER_TIMESTAMP)){
                //Making 2 lists one with upcoming knots and second with knots which are not active.
               ArrayList<Knot> list1 = new ArrayList<>();
                ArrayList<Knot> list2 = new ArrayList<>();
                Long currentTime = getCurrentTime();
                for(int i = 0;i<list.size();i++){
                    Knot knot = list.get(i);
                    if(knot.reminderTimestamp > currentTime){
                        list1.add(knot);
                    }
                    else{
                        list2.add(knot);
                    }
                }
                Comparator<Knot> knotComparator = new Comparator<Knot>() {
                    @Override
                    public int compare(Knot lhs, Knot rhs) {
                        Long a = lhs.reminderTimestamp;
                        Long b = rhs.reminderTimestamp;
                        return a.compareTo(b);
                    }
                };
                //Sorting and merging these lists to create a single list with knots sorted
                //such that upcoming knots are shown first and then knots which are knot active.
                Collections.sort(list1,knotComparator);
                Collections.sort(list2,knotComparator);
                list.clear();
                list.addAll(list1);
                list.addAll(list2);
            }
			return list;
		}
		
		public static float convertDpToPixel(float dp, Context context){
		    Resources resources = context.getResources();
		    DisplayMetrics metrics = resources.getDisplayMetrics();
            return dp * (metrics.densityDpi / 160f);
		}
		
		public static float getWidth(Context context){
			Resources resources = context.getResources();
		    DisplayMetrics metrics = resources.getDisplayMetrics();
		    return metrics.widthPixels;
		}

		
		public static Bitmap decodeSampledBitmapFromSource(String image_source,
		        int reqWidth, int reqHeight) {
			if(image_source==null){
				return null;
			}
		    // First decode with inJustDecodeBounds=true to check dimensions
		    final BitmapFactory.Options options = new BitmapFactory.Options();
		    options.inJustDecodeBounds = true;
		    BitmapFactory.decodeFile(image_source, options);

		    // Calculate inSampleSize
		    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		    // Decode bitmap with inSampleSize set
		    options.inJustDecodeBounds = false;
		    return BitmapFactory.decodeFile(image_source, options);
		}
		
		public static int calculateInSampleSize(
	            BitmapFactory.Options options, int reqWidth, int reqHeight) {
	    // Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;

	    if (height > reqHeight || width > reqWidth) {

	        final int halfHeight = height / 2;
	        final int halfWidth = width / 2;

	        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
	        // height and width larger than the requested height and width.
	        while ((halfHeight / inSampleSize) > reqHeight
	                && (halfWidth / inSampleSize) > reqWidth) {
	            inSampleSize *= 2;
	        }
	    }

	    return inSampleSize;
	}
		
		public static String epochToTime(Context context,long epoch){
			Locale locale = context.getResources().getConfiguration().locale;
			Date updateDate = new Date(epoch);
		    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm",locale);
		    return format.format(updateDate);
			
		}
		

    public static void setReminder(Context context, Knot knot) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("title", knot.title);
        intent.putExtra("description", knot.description);
        intent.putExtra("image_path", knot.imageSource);
        intent.putExtra("timestamp", knot.timestamp);
        intent.putExtra("reminder_timestamp", knot.reminderTimestamp);
        intent.putExtra("isRepeating", knot.isRepeating);
        intent.putExtra("repeating_time", knot.repeatingTime);
        PendingIntent reminderIntent = PendingIntent.getBroadcast(context, (int) knot.timestamp,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if(knot.isRepeating == 1) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, knot.reminderTimestamp,
                    knot.repeatingTime, reminderIntent);
        }
        else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, knot.reminderTimestamp, reminderIntent);
        }
    }
		
//		public static String getImagePath(Uri uri , Context context){
//			   Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
//			   cursor.moveToFirst();
//			   String document_id = cursor.getString(0);
//			   document_id = document_id.substring(document_id.lastIndexOf(":")+1);
//			   cursor.close();
//			   cursor = context.getContentResolver().query(
//			   MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//			   null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
//			   cursor.moveToFirst();
//			   String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
//			   cursor.close();
//
//			   return path;
//			}



		public static void cancelReminder(Context context, Knot previousKnot) {
			AlarmManager alarmManager = (AlarmManager)context.getSystemService(
                    Context.ALARM_SERVICE);
			Intent intent = new Intent(context,AlarmReceiver.class);
			PendingIntent reminderIntent = PendingIntent.getBroadcast(context,
                   (int)previousKnot.timestamp, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			alarmManager.cancel(reminderIntent);
			
		}
		
		public static void archive(Context context , Knot knot){
			KnotitOpenHelper openHelper = KnotitOpenHelper.getInstance(context);
			SQLiteDatabase db = openHelper.getWritableDatabase();
			cancelReminder(context, knot);
			String timestamp[] = {String.valueOf(knot.timestamp)};
            //Delete Knot From "Knots" Table and add similar Knot to Archive table.
			Cursor c = db.query(KnotitOpenHelper.KNOTS_TABLE_NAME, null,
                    KnotitOpenHelper.COLUMN_TIMESTAMP + "= ?",timestamp,null,null,null );
			if(c.moveToFirst()){
			db.delete(KnotitOpenHelper.KNOTS_TABLE_NAME, KnotitOpenHelper.COLUMN_TIMESTAMP + "= ?",
                    timestamp );
			openHelper.addToDataBase(db, KnotitOpenHelper.ARCHIVED_KNOTS_TABLE_NAME, knot);
			}
			c.close();
		}
		
		public static void unArchive(Context context , Knot knot){
			KnotitOpenHelper openHelper = KnotitOpenHelper.getInstance(context);
			SQLiteDatabase db = openHelper.getWritableDatabase();
			String timestamp[] = {String.valueOf(knot.timestamp)};
			if(getCurrentTime() < knot.reminderTimestamp)
				setReminder(context, knot);
			Cursor c = db.query(KnotitOpenHelper.ARCHIVED_KNOTS_TABLE_NAME, null,
                    KnotitOpenHelper.COLUMN_TIMESTAMP + "= ?",timestamp,null,null,null );
			if(c.moveToFirst()){
			db.delete(KnotitOpenHelper.ARCHIVED_KNOTS_TABLE_NAME,
                    KnotitOpenHelper.COLUMN_TIMESTAMP + "= ?" , timestamp );
			openHelper.addToDataBase(db, KnotitOpenHelper.KNOTS_TABLE_NAME, knot);
			}
			c.close();
			
		}
		
		public static void moveToTrashFromArchived(Context context , Knot knot){
			KnotitOpenHelper openHelper = KnotitOpenHelper.getInstance(context);
			SQLiteDatabase db = openHelper.getWritableDatabase();
			String timestamp[] = {String.valueOf(knot.timestamp)};
			Cursor c = db.query(KnotitOpenHelper.ARCHIVED_KNOTS_TABLE_NAME, null,
                    KnotitOpenHelper.COLUMN_TIMESTAMP + "= ?",timestamp,null,null,null );
			if(c.moveToFirst()){
			db.delete(KnotitOpenHelper.ARCHIVED_KNOTS_TABLE_NAME,
                    KnotitOpenHelper.COLUMN_TIMESTAMP + "= ?" , timestamp );
			openHelper.addToDataBase(db, KnotitOpenHelper.TRASH_KNOTS_TABLE_NAME, knot);
			}
			c.close();
		}
		
		public static void moveToTrash(Context context , Knot knot){
			KnotitOpenHelper openHelper = KnotitOpenHelper.getInstance(context);
			SQLiteDatabase db = openHelper.getWritableDatabase();
			cancelReminder(context, knot);
			String timestamp[] = {String.valueOf(knot.timestamp)};
			Cursor c = db.query(KnotitOpenHelper.KNOTS_TABLE_NAME, null,
                    KnotitOpenHelper.COLUMN_TIMESTAMP + "= ?",timestamp,null,null,null );
			if(c.moveToFirst()){
			db.delete(KnotitOpenHelper.KNOTS_TABLE_NAME,KnotitOpenHelper.COLUMN_TIMESTAMP + "= ?" ,
                    timestamp );
			openHelper.addToDataBase(db, KnotitOpenHelper.TRASH_KNOTS_TABLE_NAME, knot);
			}
			c.close();
		}
		
		public static void moveFromTrashToArchived(Context context , Knot knot){
			KnotitOpenHelper openHelper = KnotitOpenHelper.getInstance(context);
			SQLiteDatabase db = openHelper.getWritableDatabase();
			String timestamp[] = {String.valueOf(knot.timestamp)};
			Cursor c = db.query(KnotitOpenHelper.TRASH_KNOTS_TABLE_NAME, null,
                    KnotitOpenHelper.COLUMN_TIMESTAMP + "= ?",timestamp,null,null,null );
			if(c.moveToFirst()){
			db.delete(KnotitOpenHelper.TRASH_KNOTS_TABLE_NAME,
                    KnotitOpenHelper.COLUMN_TIMESTAMP + "= ?" , timestamp );
			openHelper.addToDataBase(db, KnotitOpenHelper.ARCHIVED_KNOTS_TABLE_NAME, knot);
			}
			c.close();
		}
		
		public static void moveFromTrashToMain(Context context ,Knot knot) {
            KnotitOpenHelper openHelper = KnotitOpenHelper.getInstance(context);
            SQLiteDatabase db = openHelper.getWritableDatabase();
            String timestamp[] = {String.valueOf(knot.timestamp)};
            Cursor c = db.query(KnotitOpenHelper.TRASH_KNOTS_TABLE_NAME, null,
                    KnotitOpenHelper.COLUMN_TIMESTAMP + "= ?",timestamp,null,null,null );
            if(c.moveToFirst()){
                db.delete(KnotitOpenHelper.TRASH_KNOTS_TABLE_NAME,
                        KnotitOpenHelper.COLUMN_TIMESTAMP + "= ?" , timestamp );
                openHelper.addToDataBase(db, KnotitOpenHelper.KNOTS_TABLE_NAME, knot);
            }
            c.close();
		}
		
		public static void permDelt(Context context,Knot knot){
			KnotitOpenHelper openHelper = KnotitOpenHelper.getInstance(context);
			SQLiteDatabase db = openHelper.getWritableDatabase();
			String timestamp[] = {String.valueOf(knot.timestamp)};
			db.delete(KnotitOpenHelper.TRASH_KNOTS_TABLE_NAME,
                    KnotitOpenHelper.COLUMN_TIMESTAMP + "= ?" , timestamp );
			
		}
		
		public static void firstRun(Context context) throws IOException{
            //Create a welcome Knot
			//Copy Image from drawable to app directory.
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.bt_il_events_conversation);
			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",context.getResources()
                    .getConfiguration().locale).format(new Date());
			String imageFileName = "JPEG_" + timeStamp + "_";
			File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

			if(!storageDir.exists()){
				storageDir.mkdirs();
			}
			File image = File.createTempFile(
					imageFileName,  /* prefix */
					".jpg",         /* suffix */
					storageDir      /* directory */
					);

		        // Save a file: path for use with ACTION_VIEW intents

			String CurrentPhotoPath = image.getAbsolutePath();
			File newFile =new File(CurrentPhotoPath);
			FileOutputStream fos;
		    	try {
		    		fos = new FileOutputStream(newFile);
		   // Use the compress method on the BitMap object to write image to the OutputStream
		    		bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
		    		fos.close();
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
            //Create a new Knot with reminder set to ring after 10 mins.
		    Knot knot = new Knot(context.getString(R.string.welcome),context.getString(R.string.welcome_message),
                    CurrentPhotoPath, getCurrentTime(), getCurrentTime() + 600000L, 0,0);
		    setReminder(context, knot);
		    KnotitOpenHelper openHelper = KnotitOpenHelper.getInstance(context);
		    SQLiteDatabase db = openHelper.getWritableDatabase();
		    openHelper.addToDataBase(db, KnotitOpenHelper.KNOTS_TABLE_NAME, knot);
			
		}
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                switch (type) {
                    case "image":
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        break;
                    case "video":
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                        break;
                    case "audio":
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                        break;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }


}
