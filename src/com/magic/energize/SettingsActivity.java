package com.magic.energize;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
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
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SettingsActivity extends FragmentActivity implements HTTPClientListener {
	private final String TAG = MainActivity.UNIVERSAL_TAG + ".SettingsActivity";
	SharedPreferences prefs;
	private boolean keyboard_open;
	private ProgressDialog mDialog;
	AlertDialog.Builder alertBuilder;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		alertBuilder = new AlertDialog.Builder(this);
		prefs = getApplicationContext().getSharedPreferences(getString(R.string.prefs_id), MODE_PRIVATE);
		final View activityRootView = findViewById(R.id.frame_settings_container);
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
		
		goToHouseholdSettings();
	}
	
	private void goToHouseholdSettings() {
		HouseholdSettingsFragment fragment = new HouseholdSettingsFragment();
		loadFragment(fragment, "household-settings-fragment");
	}
	
	private void goToConnectAccounts() {
		ConnectAccountsFragment fragment = new ConnectAccountsFragment();
		loadFragment(fragment, "connect_accounts-fragment");
	}
	
	
	private void loadFragment(android.support.v4.app.Fragment fragment, String tag) {
	    FragmentManager fm = getSupportFragmentManager();
	    FragmentTransaction ft = fm.beginTransaction();
	    ft.replace(R.id.frame_settings_container, fragment, tag);
	    //ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
	    ft.addToBackStack(null);
	    ft.commitAllowingStateLoss();
	}
	
	public void connectGasAccount(View v) {
		final Dialog dialog = new Dialog(this);
    	dialog.setContentView(R.layout.gas_modal);
    	Button confirm = (Button)dialog.findViewById(R.id.buttonConnectGasAccount);
    	confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				EditText gas_id = (EditText)dialog.findViewById(R.id.gas_account_id);
		    	EditText gas_password = (EditText)dialog.findViewById(R.id.gas_account_password);
		    	if(prefs.getString("token", null) != null) {
		    		Bundle data = new Bundle();
		    		data.putString("token", prefs.getString("token", null));
		    		data.putString("accountid", gas_id.getText().toString());
		    		data.putString("pass", gas_password.getText().toString());
		    		HTTPClient.POST(getString(R.string.energize_api_url) + "/add_gas", data, HTTPClient.ADD_GAS_ACCOUNT_ID, SettingsActivity.this);
		    		// Add gas account details to preferences
		    		SharedPreferences.Editor editor =  prefs.edit();
		    		editor.putString("gas-id", gas_id.getText().toString());
		    		editor.putString("gas-pass", gas_password.getText().toString());
		    		editor.commit();
		    		mDialog = ProgressDialog.show(SettingsActivity.this, "", 
			                "Registering account. One moment please...", true);
		    	} else {
		    		showDialog("Unable to register account, user not recognized.");
		    	}
				// TODO: do something with id and password
				dialog.cancel();
				
		}});
    	dialog.show();
	}
	
	public void connectElectricityAccount(View v) {
		final Dialog dialog = new Dialog(this);
    	dialog.setContentView(R.layout.electricity_modal);
    	Button confirm = (Button)dialog.findViewById(R.id.buttonConnectElecAccount);
    	confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				EditText elec_id = (EditText)dialog.findViewById(R.id.electricity_account_id);
		    	EditText elec_password = (EditText)dialog.findViewById(R.id.electricity_account_password);
		    	if(prefs.getString("token", null) != null) {
		    		Bundle data = new Bundle();
		    		data.putString("token", prefs.getString("token", null));
		    		data.putString("accountid", elec_id.getText().toString());
		    		data.putString("pass", elec_password.getText().toString());
		    		HTTPClient.POST(getString(R.string.energize_api_url) + "/add_electric", data, HTTPClient.ADD_ELECTRIC_ACCOUNT_ID, SettingsActivity.this);
		    		// Add gas account details to preferences
		    		SharedPreferences.Editor editor =  prefs.edit();
		    		editor.putString("electric-id", elec_id.getText().toString());
		    		editor.putString("electric-pass", elec_password.getText().toString());
		    		editor.commit();
		    		mDialog = ProgressDialog.show(SettingsActivity.this, "", 
			                "Registering account. One moment please...", true);
		    	} else {
		    		showDialog("Unable to register account, user not recognized.");
		    	}
				// TODO: do something with id and password
				dialog.cancel();
				
		}});
    	dialog.show();
	}
	
	public void householdNext(View v) {
		// Change top button colors
		Button step1 = (Button) findViewById(R.id.step1_button);
		Button step2 = (Button) findViewById(R.id.step2_button);
		step1.setBackgroundResource(R.drawable.round_button_unselected);
		step2.setBackgroundResource(R.drawable.round_button_selected);
		
		goToConnectAccounts();
	}
	
	public void accountsNext(View v) {
		// Go back to main activity
    	Intent myIntent = new Intent(this, MainActivity.class);
    	myIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(myIntent);
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
			/*if(method.equals(HTTPClient.REGISTER_ID)) {
				TextView errTxt = (TextView)findViewById(R.id.error_registration);
				errTxt.setText(error);
			} else if (method.equals(HTTPClient.LOGIN_ID)) {
				TextView errTxt = (TextView)findViewById(R.id.error_login);
				errTxt.setText(error);
			}*/
			
			return;
		} catch (JSONException err) {
			Log.d(TAG, "No Server error found in response");
		}
		
		// TODO: Handle callbacks!
		
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
}
