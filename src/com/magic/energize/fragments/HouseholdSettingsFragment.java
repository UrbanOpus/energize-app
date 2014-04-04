package com.magic.energize.fragments;

import com.magic.energize.R;
import com.magic.energize.R.layout;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HouseholdSettingsFragment extends Fragment {
	public HouseholdSettingsFragment(){}
	
	   @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
	  
	        View rootView = inflater.inflate(R.layout.fragment_household_settings, container, false);
	          
	        return rootView;
	    }

}
