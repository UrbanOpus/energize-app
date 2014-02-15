package com.magic.energize;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class EnergySavingsFragment extends Fragment {
	public EnergySavingsFragment(){}
	
	   @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
	  
	        View rootView = inflater.inflate(R.layout.fragment_savings, container, false);
	          
	        return rootView;
	    }
}
