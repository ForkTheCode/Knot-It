package utilities;

import models.Knot;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class KnotitOpenHelper extends SQLiteOpenHelper {
	
	private static KnotitOpenHelper mInstance = null;
	public static final String DATABASE_NAME = "knots_data";
	public static final int VERSION = 2;
	public static final String KNOTS_TABLE_NAME = "knots";
	public static final String ARCHIVED_KNOTS_TABLE_NAME = "archived_knots";
	public static final String TRASH_KNOTS_TABLE_NAME = "trash_knots";
	public static final String COLUMN_KNOT_ID = "id";
	public static final String COLUMN_TITLE = "title";
	public static final String COLUMN_DESC = "description";
	public static final String COLUMN_IMAGE_SOURCE = "image_source";
	public static final String COLUMN_TIMESTAMP = "timestamp";
	public static final String COLUMN_REMINDER_TIMESTAMP = "reminderTimeStamp";
    public static final String COLUMN_REMINDER_REPEATING = "reminderRepeating";
    public static final String COLUMN_REMINDER_REPEATING_TIME = "reminderRepeatingTime";


	Context context;
	public static KnotitOpenHelper getInstance(Context ctx) {
	    if (mInstance == null) {
	      mInstance = new KnotitOpenHelper(ctx.getApplicationContext());
	    }
	    return mInstance;
	  }
	private KnotitOpenHelper(Context context) {
		super(context,DATABASE_NAME, null, VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + KNOTS_TABLE_NAME + " ( " 
				+ COLUMN_KNOT_ID + " INTEGER PRIMARY KEY, " 
				+ COLUMN_TITLE + " TEXT, " 
				+ COLUMN_DESC + " TEXT, " 
				+ COLUMN_TIMESTAMP + " INTEGER, "
				+ COLUMN_REMINDER_TIMESTAMP + " INTEGER, "
                + COLUMN_REMINDER_REPEATING + " INTEGER DEFAULT 0, "
                + COLUMN_REMINDER_REPEATING_TIME + " INTEGER DEFAULT 0, "
				+ COLUMN_IMAGE_SOURCE + " TEXT)");
		db.execSQL("CREATE TABLE " + ARCHIVED_KNOTS_TABLE_NAME + " ( " 
				+ COLUMN_KNOT_ID + " INTEGER PRIMARY KEY, " 
				+ COLUMN_TITLE + " TEXT, " 
				+ COLUMN_DESC + " TEXT, " 
				+ COLUMN_TIMESTAMP + " INTEGER, "
				+ COLUMN_REMINDER_TIMESTAMP + " INTEGER, "
                + COLUMN_REMINDER_REPEATING + " INTEGER DEFAULT 0, "
                + COLUMN_REMINDER_REPEATING_TIME + " INTEGER DEFAULT 0, "
				+ COLUMN_IMAGE_SOURCE + " TEXT)");
		db.execSQL("CREATE TABLE " + TRASH_KNOTS_TABLE_NAME + " ( " 
				+ COLUMN_KNOT_ID + " INTEGER PRIMARY KEY, " 
				+ COLUMN_TITLE + " TEXT, " 
				+ COLUMN_DESC + " TEXT, " 
				+ COLUMN_TIMESTAMP + " INTEGER, "
				+ COLUMN_REMINDER_TIMESTAMP + " INTEGER, "
                + COLUMN_REMINDER_REPEATING + " INTEGER DEFAULT 0, "
                + COLUMN_REMINDER_REPEATING_TIME + " INTEGER DEFAULT 0, "
				+ COLUMN_IMAGE_SOURCE + " TEXT)");
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("ALTER TABLE " + KNOTS_TABLE_NAME + " ADD COLUMN " + COLUMN_REMINDER_REPEATING + " INTEGER DEFAULT 0" );
        db.execSQL("ALTER TABLE " + KNOTS_TABLE_NAME + " ADD COLUMN " + COLUMN_REMINDER_REPEATING_TIME + " INTEGER DEFAULT 0" );
        db.execSQL("ALTER TABLE " + ARCHIVED_KNOTS_TABLE_NAME + " ADD COLUMN " + COLUMN_REMINDER_REPEATING + " INTEGER DEFAULT 0" );
        db.execSQL("ALTER TABLE " + ARCHIVED_KNOTS_TABLE_NAME + " ADD COLUMN " + COLUMN_REMINDER_REPEATING_TIME + " INTEGER DEFAULT 0" );
        db.execSQL("ALTER TABLE " + TRASH_KNOTS_TABLE_NAME + " ADD COLUMN " + COLUMN_REMINDER_REPEATING + " INTEGER DEFAULT 0" );
        db.execSQL("ALTER TABLE " + TRASH_KNOTS_TABLE_NAME + " ADD COLUMN " + COLUMN_REMINDER_REPEATING_TIME + " INTEGER DEFAULT 0" );

	}
	
	public void addToDataBase(SQLiteDatabase db,String tablename,Knot knot){
		ContentValues values = new ContentValues();
		values.put(COLUMN_TITLE, knot.title);
		values.put(COLUMN_DESC, knot.description);
		values.put(COLUMN_REMINDER_TIMESTAMP, knot.reminderTimestamp);
		values.put(COLUMN_TIMESTAMP, knot.timestamp);
		values.put(COLUMN_IMAGE_SOURCE, knot.imageSource);
        values.put(COLUMN_REMINDER_REPEATING,knot.isRepeating);
        values.put(COLUMN_REMINDER_REPEATING_TIME,knot.repeatingTime);
		db.insert(tablename, null, values);
	}

}
