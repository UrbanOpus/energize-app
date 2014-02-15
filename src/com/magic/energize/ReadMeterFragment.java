package com.magic.energize;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ReadMeterFragment extends Fragment {
	// Meter Picker?
	//https://github.com/novak/numpicker-demo/
	public ReadMeterFragment(){}
	
	   @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
	  
	        View rootView = inflater.inflate(R.layout.fragment_read_meter, container, false);
	          
	        return rootView;
	    }

}
