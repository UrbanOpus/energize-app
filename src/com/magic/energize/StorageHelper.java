package com.magic.energize;

import com.magic.energize.ui.MainActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;



public class StorageHelper extends SQLiteOpenHelper {
	public static final String TAG = MainActivity.UNIVERSAL_TAG + ".StorageHelper";
	public static final float DEFAULT_CONVERSION_FACTOR = 0.038f;
	
	// All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
 
    // Database Name
    private static final String DATABASE_NAME = "dataManager";
 
    // Data table names
    private static final String METER_DATA_TABLE = "meter_data";
    private static final String BILLING_DATA_TABLE = "bill_data";
 
    // Data Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_DATE = "date"; //date for data
    private static final String KEY_METER_READING = "meter_reading"; //meter reading normalized to m^3
    private static final String KEY_TOTAL_GJ = "total_gj"; //Total GigaJules consumed, only store if conversion factor is set
    
    private static final String KEY_START_DATE = "start_date"; //start date from billing data
    private static final String KEY_END_DATE = "end_date"; //end date for billing data
    private static final String KEY_CONSUMED_GJ = "consumed_gj"; // Relative GJ consumed, must be accompanied by start/end date
    private static final String KEY_AVG_TEMP = "average_temp"; // avg temp from bill or entered by user
    
    private static final String METER_DATA_TABLE_CREATE =
                "CREATE TABLE " + METER_DATA_TABLE + " (" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_DATE + " INTEGER NOT NULL, " +
                KEY_METER_READING + " INTEGER NOT NULL, " + 
                KEY_TOTAL_GJ + " REAL, " +
                KEY_AVG_TEMP + "DOUBLE);";
    
    private static final String BILLING_DATA_TABLE_CREATE = 
    		 	"CREATE TABLE " + BILLING_DATA_TABLE + " (" +
	            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
	            KEY_START_DATE + " INTEGER NOT NULL, " +
	            KEY_END_DATE + " INTEGER NOT NULL, " +
	            KEY_CONSUMED_GJ + " REAL NOT NULL, " +
	            KEY_AVG_TEMP + "DOUBLE);";
    
    SharedPreferences prefs;
    private Context _context;
    
    public StorageHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        _context = context;
        prefs = context.getSharedPreferences(_context.getString(R.string.prefs_id), Context.MODE_PRIVATE);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(METER_DATA_TABLE_CREATE);
        db.execSQL(BILLING_DATA_TABLE_CREATE);
    }

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
	   // Adding new contact
	public void addMeterReading(int cubicMeters, long date) {
		SQLiteDatabase db = getWritableDatabase();
	 
	    ContentValues values = new ContentValues();
	    values.put(KEY_DATE, date); // Date of meter reading.
	    values.put(KEY_METER_READING, cubicMeters); // Cubic meters on gas meter.
	    // Add total gj if conversion factor has been set
		if(prefs.contains(_context.getString(R.string.conversion_factor))) {
			float cFactor = prefs.getFloat(_context.getString(R.string.conversion_factor), DEFAULT_CONVERSION_FACTOR);
			float total_gj = ((float)cubicMeters)*cFactor;
			values.put(KEY_TOTAL_GJ, total_gj);
		}
	    
	    Log.d(TAG, "Adding meter reading to storage.");
	    Log.d(TAG, values.toString());
	    // Inserting Row
	    db.insert(METER_DATA_TABLE, null, values);
	    db.close(); // Closing database connection
	}
	
	public Bundle getMeterReadingFromDate(long date) {
		Log.d(TAG, "Getting meter reading for date : " + date);
		SQLiteDatabase db = getWritableDatabase();
		Cursor base_readings = db.query(METER_DATA_TABLE, null, KEY_DATE + "=?", new String[]{date+""}, null, null, null);
		if(base_readings.getCount() > 0) {
			base_readings.moveToFirst();
			int meter_read = Integer.parseInt(base_readings.getString(base_readings.getColumnIndex(KEY_METER_READING)));
			float total_gj;
			if(base_readings.isNull(base_readings.getColumnIndex(KEY_TOTAL_GJ))) {
				if(prefs.contains(_context.getString(R.string.conversion_factor))) {
					float cFactor = prefs.getFloat(_context.getString(R.string.conversion_factor), DEFAULT_CONVERSION_FACTOR);
					total_gj = ((float)meter_read)*cFactor;
				} else {
					total_gj = ((float)meter_read)*DEFAULT_CONVERSION_FACTOR;
				}
			} else {
				total_gj = base_readings.getFloat(base_readings.getColumnIndex(KEY_TOTAL_GJ));
			}
				
			Bundle reading = new Bundle();
			reading.putDouble("total_gj", total_gj);
			reading.putInt("cubic_meters", meter_read);
			return reading;
		} else 
			return null;
	}
	
	public void getConsumptionBetween(long dateStart, long dateEnd) {
		//TODO: Get GJ consumed between the two given dates.
	}
	
	public void restorDatabaseFromData() {
		//TODO restore database with data from server (e.g person changed phones)
	}
	
	/**
	 * Remove all user data from database.
	 */
	public void removeAll()
	{
	    SQLiteDatabase db = getWritableDatabase(); 
	    db.delete(METER_DATA_TABLE, null, null);
	    db.delete(BILLING_DATA_TABLE, null, null);
	    db.close();
	}
}