package com.magic.energize;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.EditText;

public class SettingsActivity extends FragmentActivity {
	private final String TAG = "Energize.SettingsActivity";
	SharedPreferences prefs;
	private boolean keyboard_open;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
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
    	EditText gas_id = (EditText)dialog.findViewById(R.id.gas_account_password);
    	EditText gas_password = (EditText)dialog.findViewById(R.id.gas_account_id);
    	Button confirm = (Button)dialog.findViewById(R.id.buttonConnectGasAccount);
    	confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				
				// TODO: do something with id and password
				dialog.cancel();
				
		}});
    	dialog.show();
	}
	
	public void connectElectricityAccount(View v) {
		final Dialog dialog = new Dialog(this);
    	dialog.setContentView(R.layout.electricity_modal);
    	EditText gas_id = (EditText)dialog.findViewById(R.id.electricity_account_password);
    	EditText gas_password = (EditText)dialog.findViewById(R.id.electricity_account_id);
    	Button confirm = (Button)dialog.findViewById(R.id.buttonConnectElecAccount);
    	confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				
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
}
