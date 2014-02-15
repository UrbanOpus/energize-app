package com.magic.energize;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SavingTipsFragment extends Fragment {
	public SavingTipsFragment(){}
	
	   @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
	  
	        View rootView = inflater.inflate(R.layout.fragment_saving_tips, container, false);
	          
	        return rootView;
	    }

}
