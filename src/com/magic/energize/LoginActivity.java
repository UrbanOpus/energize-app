package com.magic.energize;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends FragmentActivity implements HTTPClientListener {
	private final String TAG = "Energize.LoginActivity";
	
	SharedPreferences prefs;
	private String energizeUrl = "http://128.189.80.86:3030/api";
	
	//** UI Declarations **/
	private ProgressDialog mDialog;
	AlertDialog.Builder alertBuilder;
	private InputMethodManager imm;
	private boolean keyboard_open = false;
	
	private Toast toast;
    private long lastBackPressTime = 0;
	
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		prefs = getApplicationContext().getSharedPreferences("com.magic.urbanopis.energize", MODE_PRIVATE);
		imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
		alertBuilder = new AlertDialog.Builder(this);
		
		final View activityRootView = findViewById(R.id.frame_login_container);
		activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
		    @Override
		    public void onGlobalLayout() {
		        int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
		        if (heightDiff > 100) { // if more than 100 pixels, its probably a keyboard...
		           keyboard_open = true;
		        } else {
		        	keyboard_open = false;
		        }
		     }
		});
		
		
		// Navigate to Login Screen
		goToLogin();
	}
	
    @Override  
    protected void onDestroy() {
    	super.onDestroy();
    	Log.w(TAG, "Destroying login activity, sutting down HTTPClient.");
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
			        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			        startActivity(intent);
		    }
	    }
    }
	
    /** Called when the user touches the register button */
	public void registerUser(View view) {
		// hide keyboard
		if(keyboard_open)
			imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
	    
		if(HTTPClient.isNetworkAvailable(this)) {
		    // get form data
			String fname = ((EditText)findViewById(R.id.first_name)).getText().toString();
			String lname = ((EditText)findViewById(R.id.last_name)).getText().toString();
			String email = ((EditText)findViewById(R.id.email_address)).getText().toString();
			String password = ((EditText)findViewById(R.id.password)).getText().toString();
			String password_confirm = ((EditText)findViewById(R.id.password_confirm)).getText().toString();
			
			if(password.equals(password_confirm)) {
				
				//TODO: confirm password
				Bundle new_user = new Bundle();
				new_user.putString("fname", fname);
				new_user.putString("lname", lname);
				new_user.putString("email", email);
				// store SHA-256 hash of password in DB.
				new_user.putString("password", hashSHA(password));
				
				/**
				 *  Call HTTPClient.POST to handle POST
				 *  - Url, data, callback_id, HTTPClientListener
				 */
				HTTPClient.POST(energizeUrl + "/register", new_user, "post-register", this);
				mDialog = ProgressDialog.show(LoginActivity.this, "", 
		                "Registering. One moment please...", true);
			} else {
				showDialog("Password does not match.");
				((EditText)findViewById(R.id.password)).clearComposingText();
				((EditText)findViewById(R.id.password_confirm)).clearComposingText();
			}
		} else {
			showNoConnection();
		}
	}
	
	public void loginUser(View v) {
		// hide keyboard
		if(keyboard_open)
			imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
		
		if(HTTPClient.isNetworkAvailable(this)) {
			// get post data
			String email = ((EditText)findViewById(R.id.login_email)).getText().toString();
			String password = ((EditText)findViewById(R.id.login_password)).getText().toString();
			Bundle user_login = new Bundle();
			user_login.putString("email", email);
			user_login.putString("password", hashSHA(password));
			HTTPClient.POST(energizeUrl + "/login", user_login, "post-login", this);
			mDialog = ProgressDialog.show(LoginActivity.this, "", 
	                "Loging in. One moment please...", true);
		
		} else {
			showNoConnection();
		}
	}
	
	private void showNoConnection() {
		showDialog("No internet connection, unable to contact server.");
	}
	
	private void showDialog(String text) {
		alertBuilder.setMessage(text)
	       .setCancelable(false)
	       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	                dialog.cancel();
	           }
	       });
		AlertDialog alert = alertBuilder.create();
		alert.show();
	}
	
	private void goToLogin() {
		LoginFragment fragment = new LoginFragment();
		loadFragment(fragment, "login-fragment");
		//FragmentManager fragmentManager = getFragmentManager();
//		FragmentTransaction ft = getFragmentManager().beginTransaction();
//        ft.replace(R.id.frame_login_container, fragment);
//        ft.addToBackStack("");
//        ft.commit();
	}
	
	public void goToRegister(View view) {
		RegistrationFragment fragment = new RegistrationFragment();
		loadFragment(fragment, "register-fragment");
//		FragmentManager fragmentManager = getFragmentManager();
//        fragmentManager.beginTransaction()
//                .replace(R.id.frame_login_container, fragment).commit();
	}
	
	private void loadFragment(android.support.v4.app.Fragment fragment, String tag) {
	    FragmentManager fm = getSupportFragmentManager();
	    FragmentTransaction ft = fm.beginTransaction();
	    ft.replace(R.id.frame_login_container, fragment, tag);
	    //ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
	    ft.addToBackStack(null);

	    ft.commitAllowingStateLoss();
	}
			
	
	@Override
	public void onRequestCompleted(String method, String result) {
		mDialog.cancel();
		if(result.equals("ERROR")) {
			Log.e(TAG, "Web Request Failed");
			//TODO: Handle connection refused/connection error
			
			if(method.equals("no-connection")) {
				showNoConnection();
			}
			return;
		}
		// try to parse response as json
		JSONObject json;
		try {
			json = new JSONObject(result);
		} catch (JSONException e) {
			Log.e(TAG, "Failed get json from web result:");
			Log.e(TAG, result);
			e.printStackTrace();
			return;
		}
		// Check if there was a sever error, if so handle
		String error;
		try {
			error = json.getString("error");
			Log.e(TAG, "Server Error : " + error);
			if(method.equals("post-register")) {
				TextView errTxt = (TextView)findViewById(R.id.error_registration);
				errTxt.setText(error);
			} else if (method.equals("post-login")) {
				TextView errTxt = (TextView)findViewById(R.id.error_login);
				errTxt.setText(error);
			}
			
			return;
		} catch (JSONException err) {
			Log.d(TAG, "No Server error found in response");
		}
		
		// If no error, continue on and execute proper action
		if(method.equals("post-register")) {
			Log.d(TAG, result);
			JSONObject user;
			try {
				user = json.getJSONObject("user");
				initializeUser(user);
			} catch (JSONException e) {
				Log.e(TAG, "Failed get user json from post-register");
				e.printStackTrace();
				return;
			}
		} else if(method.equals("post-login")) {
			Log.d(TAG, result);
			JSONObject user;
			try {
				user = json.getJSONObject("user");
				initializeUser(user);
			} catch (JSONException e) {
				Log.e(TAG, "Failed get user json from post-login");
				e.printStackTrace();
				return;
			}
		} else {
			Log.w(TAG, "Error: task method not recognized");
		}
		
	}
	
	// Sets up user preferences from user object and navigates to home screen
	public void initializeUser(JSONObject user) {
		try{
			if(user != null) {
				SharedPreferences.Editor editor =  prefs.edit();
	        	editor.putString("token", user.getString("token"));
	        	editor.putString("fname", user.getString("fname"));
	        	editor.putString("lname", user.getString("lname"));
	        	editor.putString("email", user.getString("email"));
	        	editor.commit();
	        	// Go back to main activity
	        	Intent myIntent = new Intent(this, MainActivity.class);
        		startActivity(myIntent);
			} else {
				Log.e(TAG, "Failed get json from post-register");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Log.w(TAG, "Unable to parse user data from JSON.");
			e.printStackTrace();
		}
	}
	
	public String hashSHA(String password) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			// won't get here as long as we're using SHA-256
			return null;
		}
		// Turn password to hash using sha-256
        md.update(password.getBytes());
        byte byteData[] = md.digest();
        //convert the byte to hex format
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
         sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
 
        return sb.toString();
	}

}
