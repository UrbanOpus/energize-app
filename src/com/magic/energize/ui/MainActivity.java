package com.magic.energize.ui;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.magic.energize.R;
import com.magic.energize.HTTPClient;
import com.magic.energize.NavDrawerItem;
import com.magic.energize.StorageHelper;

import com.magic.energize.fragments.CommunityFragment;
import com.magic.energize.fragments.DashboardFragment;
import com.magic.energize.fragments.EnergySavingsFragment;
import com.magic.energize.fragments.EnergyUsageFragment;
import com.magic.energize.fragments.ReadMeterFragment;
import com.magic.energize.fragments.SafetyTipsFragment;
import com.magic.energize.fragments.SavingTipsFragment;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
 
public class MainActivity extends FragmentActivity {
	
	/** Set to false to make login not required **/
	private final boolean FORCE_LOGIN = true;

	SharedPreferences prefs;
	public static boolean unregistered_user = true;
	public static final String UNIVERSAL_TAG = "Energize"; //getString(R.string.unversal_log_tag);
	private final String TAG = UNIVERSAL_TAG + ".MainActivity";
	
	public static MainActivity get;
	
	//** UI Declarations **/
	//private ProgressDialog mDialog;
	private InputMethodManager imm;
	
	//Meter Settings
	private String utilityType;
	private String meterType;
	private String meterUnits;
	
	private Toast toast;
    private long lastBackPressTime = 0;
	
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
 
    // nav drawer title
    private CharSequence mDrawerTitle;
 
    // used to store app title
    private CharSequence mTitle;
 
    // slide menu items
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;
 
    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getApplicationContext().getSharedPreferences(getString(R.string.prefs_id), MODE_PRIVATE);
        imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        
        if(prefs.getString(getString(R.string.login_token), null) == null && FORCE_LOGIN) {
    		Intent myIntent = new Intent(this, LoginActivity.class);
    		startActivity(myIntent);
        } else {
        	unregistered_user = false;
        	setContentView(R.layout.activity_main);
        }
        
        mTitle = mDrawerTitle = "\t" + getString(R.string.app_title);
 
        // load slide menu items
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
 
        // nav drawer icons from resources
        navMenuIcons = getResources()
                .obtainTypedArray(R.array.nav_drawer_icons);
 
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);
 
        navDrawerItems = new ArrayList<NavDrawerItem>();
 
        // adding nav drawer items to array
        // Home
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
        // Find People
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
        // Photos
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
        // Communities, Will add a counter here
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1), true, "22"));
        // Pages
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
        // What's hot, We  will add a counter here
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1), true, "50+"));
         
 
        // Recycle the typed array
        navMenuIcons.recycle();
 
        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());
 
        // setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems);
        mDrawerList.setAdapter(adapter);
 
        // enabling action bar app icon and behaving it as toggle button
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
 
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, //nav menu toggle icon
                R.string.app_title, // nav drawer open - description for accessibility
                R.string.app_title // nav drawer close - description for accessibility
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }
 
            public void onDrawerOpened(View drawerView) {
            	//TODO: Add Icon Before title in Action Bar?
//            	ActionBar actionBar = getActionBar();
//            	actionBar.setIcon(R.drawable.ic_drawer);
            	getActionBar().setTitle(mDrawerTitle);
                
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
 
        if (savedInstanceState == null) {
        	// on first time display view for first nav item
        	displayView(0);
        }
        
        get = this;
     
    }
    
    @Override  
    protected void onDestroy() {
    	super.onDestroy();
    	Log.w(TAG, "Destroying main activity, sutting down HTTPClient.");
    	HTTPClient.shutDown();
    }
    
    @Override
    public void onBackPressed() {
    	FragmentManager fm = getSupportFragmentManager();
	    if(fm.getBackStackEntryCount() > 0) {
	    	fm.popBackStack();
	    } else {
	    	if (this.lastBackPressTime < System.currentTimeMillis() - 4000) {
	    		toast = Toast.makeText(this, "Press back again to close this app", Toast.LENGTH_LONG);
	    		toast.show();
	    		this.lastBackPressTime = System.currentTimeMillis();
	    	} else {
		        if (toast != null)
		        	toast.cancel();
		        
			        Intent intent = new Intent(Intent.ACTION_MAIN);
			        intent.addCategory(Intent.CATEGORY_HOME);
			        startActivity(intent);
		    }
	    }
    }
    
 
    /**
     * Slide menu item click listener
     * */
    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long id) {
            // display view for selected nav drawer item
            displayView(position);
        }
    }
 
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
 
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
        switch (item.getItemId()) {
        case R.id.action_settings:
        	startSettingsActivity();
            return true;
        case R.id.action_logout:
        	AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        	alertBuilder.setTitle("Logout?")
        		.setMessage("Are you sure you wish to logout? This will delete all app data on this device.")
	 	       	.setCancelable(true)
	 	       	.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	 	       		public void onClick(DialogInterface dialog, int id) {
		 	       		SharedPreferences.Editor editor =  prefs.edit();
		 	        	editor.clear();
		 	        	editor.commit();
		 	        	StorageHelper storage = new StorageHelper(MainActivity.this);
		 	        	storage.removeAll();
		 	        	Intent longinIntent = new Intent(MainActivity.this, LoginActivity.class);
		 	    		startActivity(longinIntent);
	 	       			dialog.cancel();
	 	       		}
	 	       	});
	 		AlertDialog alert = alertBuilder.create();
	 		alert.show();
        	return true;
        case R.id.action_read_meter:
        	takeMeterReading();
        	return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    public void takeMeterReading() {
    	Dialog dialog = new Dialog(this);
    	dialog.setTitle("TAKE METER READING");
    	dialog.setContentView(R.layout.meter_settings);
    	Spinner spin1 = (Spinner)dialog.findViewById(R.id.spinner1);
    	Spinner spin2 = (Spinner)dialog.findViewById(R.id.spinner2);
    	Spinner spin3 = (Spinner)dialog.findViewById(R.id.spinner3);
    	Button confirm = (Button)dialog.findViewById(R.id.buttonStartMeterRead);
    	spin1.setOnItemSelectedListener(new OnItemSelectedListener() {
    		
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				String[] utility_types = getResources().getStringArray(R.array.utility_types);;
				utilityType = utility_types[arg2];
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}});
    	spin2.setOnItemSelectedListener(new OnItemSelectedListener() {
    		
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				String[] meter_types = getResources().getStringArray(R.array.meter_types);;
				meterType = meter_types[arg2];
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}});
    	spin3.setOnItemSelectedListener(new OnItemSelectedListener() {
    		
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				String[] meter_units = getResources().getStringArray(R.array.meter_units);;
				meterUnits = meter_units[arg2];
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}});
    	confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				startMeterActivity();
				
			}});
    	dialog.show();
    }
    
    public void startMeterActivity() {
    	Intent meterIntent = new Intent(this, ReadMeterActivity.class);
    	meterIntent.putExtra("utility_type", utilityType);
    	meterIntent.putExtra("meter_type", meterType);
    	meterIntent.putExtra("meter_units", meterUnits);
		startActivity(meterIntent);
    }
    
    public void startSettingsActivity() {
    	Intent settingsIntent = new Intent(this, SettingsActivity.class);
		startActivity(settingsIntent);
    }
    
    public void collectBillBaseReading(final Dialog dialog) {
    	final Dialog baseReading = new Dialog(MainActivity.this);
    	baseReading.setContentView(R.layout.base_bill_modal);
    	final DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

            // when dialog box is closed, below method will be called.
            public void onDateSet(DatePicker view, int selectedYear,
                    int selectedMonth, int selectedDay) {
                String year1 = String.valueOf(selectedYear);
                String month1 = String.valueOf(selectedMonth + 1);
                String day1 = String.valueOf(selectedDay);
                TextView dateView = (TextView) baseReading.findViewById(R.id.reading_date_txt);
                dateView.setText(day1 + "/" + month1 + "/" + year1);

            }
        };
        final Calendar c = Calendar.getInstance();
        final DatePickerDialog datePicker = new DatePickerDialog(this,
                R.style.AppTheme, datePickerListener,
                c.get(Calendar.YEAR), 
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH));
    	TextView billDate = (TextView) baseReading.findViewById(R.id.reading_date_txt);
    	billDate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				datePicker.show();
			}
    	});
    	Button ok = (Button) baseReading.findViewById(R.id.from_bill_ok);
    	Button cancel = (Button) baseReading.findViewById(R.id.from_bill_cancel);
    	ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				TextView dateView = (TextView) baseReading.findViewById(R.id.reading_date_txt);
				String date = (String) dateView.getText();
				String[] dates = date.split("/");
				Date d = new Date();
				//TODO: fix deprecated time
				d.setYear(Integer.parseInt(dates[2]));
				d.setMonth(Integer.parseInt(dates[1]));
				d.setDate(Integer.parseInt(dates[0]));
				EditText meterReading = (EditText) baseReading.findViewById(R.id.present_reading_txt);
				String reading = meterReading.getText().toString();
				EditText conversionFactor = (EditText) baseReading.findViewById(R.id.conversion_factor);
				float cFactor = Float.parseFloat(conversionFactor.getText().toString());
				Log.d(TAG, "Base Reading : " + d.getTime() + ", " + reading + ", " + cFactor);
				StorageHelper db = new StorageHelper(getApplicationContext());
        		db.addMeterReading(Integer.parseInt(reading), d.getTime());
        		long last_reading = d.getTime();
        		SharedPreferences.Editor editor =  prefs.edit();
            	editor.putLong(getString(R.string.base_reading), last_reading);
            	editor.putLong(getString(R.string.last_reading), last_reading);
            	editor.putFloat(getString(R.string.conversion_factor), cFactor);
            	editor.commit();
				dialog.cancel();
				baseReading.cancel();
				displayView(0);
			}
    		
    	});
    	cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				baseReading.cancel();
			}
    	});
    	baseReading.show();
    }
    
 
    /***
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }
 
    /**
     * Diplaying fragment view for selected nav drawer list item
     * */
    private void displayView(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        String fragment_tag = null;
        int fragment_icon = 0;
        switch (position) {
        case 0:
            fragment = new DashboardFragment();
            fragment_tag = getString(R.string.dashboard_tag);
            fragment_icon = R.drawable.ic_dashboard;
            break;
        case 1:
//            Intent myIntent = new Intent(this, DrawRectActivity.class);
//            startActivity(myIntent); 
            fragment = new EnergyUsageFragment();
            fragment_tag = getString(R.string.usage_tag);
            fragment_icon = R.drawable.ic_usage;
            break;
        case 2:
            fragment = new EnergySavingsFragment();
            fragment_tag = getString(R.string.savings_tag);
            fragment_icon = R.drawable.ic_savings;
            break;
        case 3:
            fragment = new CommunityFragment();
            fragment_tag = getString(R.string.community_tag);
            fragment_icon = R.drawable.ic_community;
            break;
        case 4:
            fragment = new SafetyTipsFragment();
            fragment_tag = getString(R.string.safety_tag);
            fragment_icon = R.drawable.ic_tips_safety;
            break;
        case 5:
            fragment = new SavingTipsFragment();
            fragment_tag = getString(R.string.saving_tag);
            fragment_icon = R.drawable.ic_tips_saving;
            break;
        /*case -1:
        	fragment = new LoginFragment();
        	break;
        case -2:
        	fragment = new RegistrationFragment();
        	break;*/
        case -3:
        	fragment = new ReadMeterFragment();
        	fragment_tag = getString(R.string.meter_tag);
        	break;
        default:
            break;
        }
 
        if (fragment != null) {
        	loadFragment(fragment, fragment_tag);
 
            // update selected item and title, then close the drawer
            if(position >= 0) {
	            mDrawerList.setItemChecked(position, true);
	            mDrawerList.setSelection(position);
	            setTitle(navMenuTitles[position]);
	            mDrawerLayout.closeDrawer(mDrawerList);
	            getActionBar().setIcon(fragment_icon);
            } else {
            	mDrawerLayout.closeDrawer(mDrawerList);
            	//mDrawerList.setClickable(false);
            	//TODO: no allow drawer to be used
            }
        } else {
            // error in creating fragment
            Log.e(TAG, "Error in creating fragment");
        }
    }
    
	private void loadFragment(android.support.v4.app.Fragment fragment, String tag) {
	    FragmentManager fm = getSupportFragmentManager();
	    FragmentTransaction ft = fm.beginTransaction();
	    ft.replace(R.id.frame_container, fragment, tag);
	    //ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
	    ft.addToBackStack(null);

	    ft.commitAllowingStateLoss();
	}
 
    @Override
    public void setTitle(CharSequence title) {
        mTitle = "\t" + title;
        getActionBar().setTitle(mTitle);
    }
    
    public void takeBaseReading(View view) {
    	final Dialog dialog = new Dialog(this);
    	dialog.setContentView(R.layout.base_reading_modal);
    	dialog.setTitle(getString(R.string.base_reading_title));
    	Button from_bill = (Button)dialog.findViewById(R.id.base_reading_from_bill);
    	from_bill.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				collectBillBaseReading(dialog);
			}
		});
    	Button take_reading = (Button)dialog.findViewById(R.id.base_reading_from_meter);
    	take_reading.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				takeMeterReading();
			}
    	});
    	dialog.show();
    }
    
    public void goToReminder(View view) {
    	
    }
    
    public void goToSavings(View view) {
		displayView(2);
	}
    public void goToUsage(View view) {
		displayView(1);
	}
    public void goToCommunity(View view) {
		displayView(3);
	}
 
    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */
 
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        if(!unregistered_user) { 
        	mDrawerToggle.syncState();
        }
    }
 
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        if(!unregistered_user) { 
        	mDrawerToggle.onConfigurationChanged(newConfig);
        }
    }
    
    public void animationEnded() {
    	EnergyUsageFragment f = (EnergyUsageFragment) 
    			getSupportFragmentManager().findFragmentByTag(getString(R.string.usage_tag));
    	f.onAnimationEnd(null);
    }
}
