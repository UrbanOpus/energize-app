package com.magic.energize;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class EnergyUsageFragment extends Fragment {
	public EnergyUsageFragment(){}
	
	   @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
	  
	        View rootView = inflater.inflate(R.layout.fragment_usage, container, false);
	          
	        return rootView;
	    }

}
