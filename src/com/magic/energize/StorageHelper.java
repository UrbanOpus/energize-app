package com.magic.energize;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;



public class StorageHelper extends SQLiteOpenHelper {
	public static final String TAG = "Energize.StorageHelper";
	public static final double DEFAULT_CONVERSION_FACTOR = 0.038;
	
	// All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
 
    // Database Name
    private static final String DATABASE_NAME = "dataManager";
 
    // Data table name
    private static final String TABLE_DATA = "data";
 
    // Data Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_DATE = "date"; //date for data
    private static final String KEY_IS_METER_READING = "is_reading"; // true if data is from user reading
    private static final String KEY_START_DATE = "start_date"; //start date from billing data
    private static final String KEY_END_DATE = "end_date"; //end date for billing data
    private static final String KEY_TOTAL_GJ = "total_gj"; //Total GigaJules consumed
    private static final String KEY_CONSUMED_GJ = "consumed_gj"; // Relative GJ consumed, must be accompanied by start/end date
    private static final String KEY_METER_READING = "meter_reading"; //meter reading normalized to m^3
    private static final String KEY_AVG_TEMP = "average_temp"; // avg temp from bill
    
    private static final String DATA_TABLE_CREATE =
                "CREATE TABLE " + TABLE_DATA + " (" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_DATE + " INTEGER, " +
                KEY_IS_METER_READING + " BOOLEAN, " +
                KEY_START_DATE + " INTEGER, " +
                KEY_END_DATE + " INTEGER, " +
                KEY_TOTAL_GJ + " DOUBLE, " +
                KEY_CONSUMED_GJ + " DOUBLE, " +
                KEY_METER_READING + " INTEGER, " + 
                KEY_AVG_TEMP + "DOUBLE);";

    StorageHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATA_TABLE_CREATE);
    }

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
	   // Adding new contact
	public void addMeterReading(int cubicMeters, long date) {
		double gj = ((double)cubicMeters) * DEFAULT_CONVERSION_FACTOR;
		SQLiteDatabase db = getWritableDatabase();
	 
	    ContentValues values = new ContentValues();
	    values.put(KEY_DATE, date); // Date of meter reading.
	    values.put(KEY_IS_METER_READING, true);
	    values.put(KEY_CONSUMED_GJ, gj);
	    values.put(KEY_METER_READING, cubicMeters); // Cubic meters on gas meter.
	    
	    Log.d(TAG, "Adding meter reading to storage.");
	    Log.d(TAG, values.toString());
	    // Inserting Row
	    db.insert(TABLE_DATA, null, values);
	    db.close(); // Closing database connection
	}
	
	public void getConsumptionBetween(long dateStart, long dateEnd) {
		//TODO: Get GJ consumed between the two given dates.
	}
	
	public void restorDatabaseFromData() {
		//TODO restore database with data from server (e.g person changed phones)
	}
}