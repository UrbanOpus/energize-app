package com.magic.energize.fragments;

import com.magic.energize.R;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ReadMeterFragment extends Fragment {
	private static View rootView;
	// Meter Picker?
	//https://github.com/novak/numpicker-demo/
	public ReadMeterFragment(){}
	
	   @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
		    if (rootView != null) {
		        ViewGroup parent = (ViewGroup) rootView.getParent();
		        if (parent != null)
		            parent.removeView(rootView);
		    }
		    try {
		    	rootView = inflater.inflate(R.layout.fragment_read_meter, container, false);
		    } catch (InflateException e) {
		        /* map is already there, just return view as it is */
		    }
	        return rootView;
	    }
	   
	   @Override
	   public void onDestroyView() {
	       super.onDestroyView();
	       ReadMeterFragment f = (ReadMeterFragment) getFragmentManager()
	    		   						.findFragmentByTag(getActivity().getString(R.string.meter_tag));
	       if (f != null) 
	           getFragmentManager().beginTransaction().remove(f).commit();
	   }

}
