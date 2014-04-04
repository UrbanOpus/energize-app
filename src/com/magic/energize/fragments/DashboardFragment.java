package com.magic.energize.fragments;

import com.magic.energize.R;
import com.magic.energize.StorageHelper;
import com.magic.energize.R.layout;
import com.magic.energize.ui.MainActivity;
import com.magic.energize.ui.ReadMeterActivity;

import android.support.v4.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DashboardFragment extends Fragment {
	private final String  TAG = MainActivity.UNIVERSAL_TAG + "DashboardFragment";
	private static View rootView;
	SharedPreferences prefs;
	public DashboardFragment(){}
	
   @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
	    if (rootView != null) {
	        ViewGroup parent = (ViewGroup) rootView.getParent();
	        if (parent != null)
	            parent.removeView(rootView);
	    }
	    try {
	    	rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);
	    } catch (InflateException e) {
	        /* map is already there, just return view as it is */
	    }
        prefs = getActivity().getApplicationContext().getSharedPreferences(getString(R.string.prefs_id), MainActivity.MODE_PRIVATE);
        if(!prefs.contains(getActivity().getString(R.string.base_reading))) {
    		setupNewDashboard();
    	} else {
    		 // load data into dashboard items if possible.
    		long base_reading_date = prefs.getLong(getActivity().getString(R.string.base_reading), 0);
    		StorageHelper db = new StorageHelper(getActivity());
    		Bundle reading = db.getMeterReadingFromDate(base_reading_date);
    		if(reading == null) {
    			Log.wtf(TAG, "AHHHH messed up data getting");
    			setupNewDashboard();
    		} else
    			setupExistingDashboard(reading);
    		
    	}
        return rootView;
    }
   
//   @Override
//   public void onDestroyView() {
//       super.onDestroyView();
//       DashboardFragment f = (DashboardFragment) getFragmentManager()
//    		   						.findFragmentByTag(getActivity().getString(R.string.dashboard_tag));
//       if (f != null) 
//           getFragmentManager().beginTransaction().remove(f).commit();
//   }
   
   public void setupNewDashboard() {
	   LinearLayout getBaseReading = (LinearLayout) rootView.findViewById(R.id.dashboard_top_left);
		getBaseReading.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((MainActivity)getActivity()).takeBaseReading(arg0);
				
			}
			
		});
   }
   
	public void setupExistingDashboard(Bundle reading) {
	   // Base reading
	   TextView tl_title = (TextView) rootView.findViewById(R.id.dashboard_tl_title);
	   tl_title.setText("Base Reading");
	   TextView tl_content = (TextView) rootView.findViewById(R.id.dashboard_tl_content);
	   tl_content.setText("Total GJ : " + reading.getDouble("total_gj") + 
				"\nTotal Cubic Meters: " + reading.getInt("cubic_meters"));
	   tl_content.setTextSize(18);
		
	   // last reading
	   if(prefs.contains(getActivity().getString(R.string.last_reading))) {
		   String last_reading = (String)DateUtils.getRelativeTimeSpanString(
				   prefs.getLong(getActivity().getString(R.string.last_reading), 0));
		   TextView tr_title = (TextView) rootView.findViewById(R.id.dashboard_tr_title);
		   tr_title.setText("Last Reading");
		   TextView tr_content = (TextView) rootView.findViewById(R.id.dashboard_tr_content);
		   tr_content.setText(last_reading);
	   }
	}
   
   

}
