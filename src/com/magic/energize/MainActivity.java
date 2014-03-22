package com.magic.energize;


import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
 
interface HTTPClientListener {
	void onRequestCompleted(String method, String result);
}
public class MainActivity extends FragmentActivity {
	
	/** Set to false to make login not required **/
	private final boolean FORCE_LOGIN = true;

	SharedPreferences prefs;
	public static boolean unregistered_user = true;
	public static final String UNIVERSAL_TAG = "Energize"; //getString(R.string.unversal_log_tag);
	private final String TAG = UNIVERSAL_TAG + ".MainActivity";
	
	//** UI Declarations **/
	//private ProgressDialog mDialog;
	private InputMethodManager imm;
	
	//Meter Settings
	private String utilityType;
	private String meterType;
	private String meterUnits;
	
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
        
        if(prefs.getString("token", null) == null && FORCE_LOGIN) {
    		Intent myIntent = new Intent(this, LoginActivity.class);
    		startActivity(myIntent);
        } else {
        	unregistered_user = false;
        }
        
        setContentView(R.layout.activity_main);
        
        mTitle = mDrawerTitle = getTitle();
 
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
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
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
     
    }
    
    @Override  
    protected void onDestroy() {
    	super.onDestroy();
    	Log.w(TAG, "Destroying main activity, sutting down HTTPClient.");
    	HTTPClient.shutDown();
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
        	SharedPreferences.Editor editor =  prefs.edit();
        	editor.clear();
        	editor.commit();
        	Intent longinIntent = new Intent(this, LoginActivity.class);
    		startActivity(longinIntent);
        	return true;
        case R.id.action_read_meter:
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
        	return true;
        default:
            return super.onOptionsItemSelected(item);
        }
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
        switch (position) {
        case 0:
            fragment = new DashboardFragment();
            break;
        case 1:
//            Intent myIntent = new Intent(this, DrawRectActivity.class);
//            startActivity(myIntent); 
            fragment = new EnergyUsageFragment();
            break;
        case 2:
            fragment = new EnergySavingsFragment();
            break;
        case 3:
            fragment = new CommunityFragment();
            break;
        case 4:
            fragment = new SafetyTipsFragment();
            break;
        case 5:
            fragment = new SavingTipsFragment();
            break;
        /*case -1:
        	fragment = new LoginFragment();
        	break;
        case -2:
        	fragment = new RegistrationFragment();
        	break;*/
        case -3:
        	fragment = new ReadMeterFragment();
        	break;
        default:
            break;
        }
 
        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();
 
            // update selected item and title, then close the drawer
            if(position >= 0) {
	            mDrawerList.setItemChecked(position, true);
	            mDrawerList.setSelection(position);
	            setTitle(navMenuTitles[position]);
	            mDrawerLayout.closeDrawer(mDrawerList);
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
 
    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
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
}
