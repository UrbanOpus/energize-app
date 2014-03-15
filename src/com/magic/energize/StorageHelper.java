package com.magic.energize;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



public class StorageHelper extends SQLiteOpenHelper {
	
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
    private static final String KEY_DATE = "date";
    private static final String KEY_GJ_CONSUMED = "consumption"; //Total GigaJules consumed
    private static final String KEY_READING = "reading"; //meter reading normalized to m^3
    
    private static final String DATA_TABLE_CREATE =
                "CREATE TABLE " + TABLE_DATA + " (" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_DATE + " INTEGER, " +
                KEY_GJ_CONSUMED + " DOUBLE, " +
                KEY_READING + " INTEGER);";

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
		SQLiteDatabase db = this.getWritableDatabase();
	 
	    ContentValues values = new ContentValues();
	    values.put(KEY_DATE, date); // Date of meter reading.
	    values.put(KEY_READING, cubicMeters); // Cubic meters on gas meter.
	    values.put(KEY_GJ_CONSUMED, gj);
	 
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