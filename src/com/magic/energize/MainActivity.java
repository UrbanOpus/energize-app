package com.magic.energize;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
 
interface HTTPClientListener {
	void onRequestCompleted(String method, String result);
}
public class MainActivity extends Activity {
	
	//RestAdapter restAdapter;
	//EnergizeService service;
	SharedPreferences prefs;
	public static boolean unregistered_user = true;
	private final String TAG = "Energize.MainActivity";
	private String energizeUrl = "http://192.168.0.13:3030/api";
	
	//** UI Declarations **/
	//private ProgressDialog mDialog;
	private InputMethodManager imm;
	
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
        prefs = getApplicationContext().getSharedPreferences("com.magic.urbanopis.energize", MODE_PRIVATE);
        imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        
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
        	if(prefs.getString("token", null) == null) {
        		Intent myIntent = new Intent(this, LoginActivity.class);
        		startActivity(myIntent);
            } else {
            	unregistered_user = false;
            	displayView(0);
            }
            // on first time display view for first nav item
            
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
            return true;
        case R.id.action_logout:
        	SharedPreferences.Editor editor =  prefs.edit();
        	editor.clear();
        	editor.commit();
        	Intent myIntent = new Intent(this, LoginActivity.class);
    		startActivity(myIntent);
        	return true;
        case R.id.action_read_meter:
        	displayView(-3);
        	return true;
        default:
            return super.onOptionsItemSelected(item);
        }
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
    
    /** Called when the user touches the register button */
//	public void registerUser(View view) {
//		// hide keyboard
//		if(imm.isAcceptingText())
//			imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
//	    
//	    // get form data
//		String fname = ((EditText)findViewById(R.id.first_name)).getText().toString();
//		String lname = ((EditText)findViewById(R.id.last_name)).getText().toString();
//		String email = ((EditText)findViewById(R.id.email_address)).getText().toString();
//		String password = ((EditText)findViewById(R.id.password)).getText().toString();
//		String password_confirm = ((EditText)findViewById(R.id.password_confirm)).getText().toString();
//		
//		if(password.equals(password_confirm)) {
//			
//			//TODO: confirm password
//			Bundle new_user = new Bundle();
//			new_user.putString("fname", fname);
//			new_user.putString("lname", lname);
//			new_user.putString("email", email);
//			// store SHA-256 hash of password in DB.
//			new_user.putString("password", hashSHA(password));
//			
//			/**
//			 *  Call HTTPClient.POST to handle POST
//			 *  - Url, data, callback_id, HTTPClientListener
//			 */
//			HTTPClient.POST(energizeUrl + "/register", new_user, "post-register", this);
//			mDialog = ProgressDialog.show(MainActivity.this, "", 
//	                "Registering. One moment please...", true);
//			
//		}
//	}
//	
//	public void loginUser(View v) {
//		// hide keyboard
//		if(imm.isAcceptingText())
//			imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
//		
//		// get post data
//		String email = ((EditText)findViewById(R.id.login_email)).getText().toString();
//		String password = ((EditText)findViewById(R.id.login_password)).getText().toString();
//		Bundle user_login = new Bundle();
//		user_login.putString("email", email);
//		user_login.putString("password", hashSHA(password));
//		HTTPClient.POST(energizeUrl + "/login", user_login, "post-login", this);
//		mDialog = ProgressDialog.show(MainActivity.this, "", 
//                "Loging in. One moment please...", true);
//	}
//	
//	/*public void goToRegister(View view) {
//		displayView(-2);
//	}*/
//			
//	
//	@Override
//	public void onRequestCompleted(String method, String result) {
//		if(mDialog != null && mDialog.isShowing()) {
//			mDialog.hide();
//		}
//
//		if(result.equals("ERROR")) {
//			Log.e(TAG, "Web Request Failed");
//			//TODO: Handle connection refused/connection error
//			return;
//		}
//		// try to parse response as json
//		JSONObject json;
//		try {
//			json = new JSONObject(result);
//		} catch (JSONException e) {
//			Log.e(TAG, "Failed get json from web result:");
//			Log.e(TAG, result);
//			e.printStackTrace();
//			return;
//		}
//		// Check if there was a sever error, if so handle
//		String error;
//		try {
//			error = json.getString("error");
//			Log.e(TAG, "Server Error : " + error);
//			if(method.equals("post-register")) {
//				TextView errTxt = (TextView)findViewById(R.id.error_registration);
//				errTxt.setText(error);
//			} else if (method.equals("post-login")) {
//				TextView errTxt = (TextView)findViewById(R.id.error_login);
//				errTxt.setText(error);
//			}
//			
//			return;
//		} catch (JSONException err) {
//			Log.d(TAG, "No Server error found in response");
//		}
//		
//		// If no error, continue on and execute proper action
//		if(method.equals("post-register")) {
//			Log.d(TAG, result);
//			JSONObject user;
//			try {
//				user = json.getJSONObject("user");
//				initializeUser(user);
//			} catch (JSONException e) {
//				Log.e(TAG, "Failed get user json from post-register");
//				e.printStackTrace();
//				return;
//			}
//		} else if(method.equals("post-login")) {
//			Log.d(TAG, result);
//			JSONObject user;
//			try {
//				user = json.getJSONObject("user");
//				initializeUser(user);
//			} catch (JSONException e) {
//				Log.e(TAG, "Failed get user json from post-login");
//				e.printStackTrace();
//				return;
//			}
//		} else {
//			Log.w(TAG, "Error: task method not recognized");
//		}
//		
//	}
//	
//	// Sets up user preferences from user object and navigates to home screen
//	public void initializeUser(JSONObject user) {
//		try{
//			if(user != null) {
//				SharedPreferences.Editor editor =  prefs.edit();
//	        	editor.putString("token", user.getString("token"));
//	        	editor.putString("fname", user.getString("fname"));
//	        	editor.putString("lname", user.getString("lname"));
//	        	editor.putString("email", user.getString("email"));
//	        	editor.commit();
//				displayView(0);
//			} else {
//				Log.e(TAG, "Failed get json from post-register");
//			}
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			Log.w(TAG, "Unable to parse user data from JSON.");
//			e.printStackTrace();
//		}
//	}
//	
//	public String hashSHA(String password) {
//		MessageDigest md;
//		try {
//			md = MessageDigest.getInstance("SHA-256");
//		} catch (NoSuchAlgorithmException e) {
//			// won't get here as long as we're using SHA-256
//			return null;
//		}
//		// Turn password to hash using sha-256
//        md.update(password.getBytes());
//        byte byteData[] = md.digest();
//        //convert the byte to hex format
//        StringBuffer sb = new StringBuffer();
//        for (int i = 0; i < byteData.length; i++) {
//         sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
//        }
// 
//        return sb.toString();
//	}
 
}
