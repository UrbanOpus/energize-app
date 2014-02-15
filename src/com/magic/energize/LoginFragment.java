package com.magic.energize;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class LoginFragment extends Fragment {
	public LoginFragment(){}
	
	   @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
	  
	        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
	          
	        return rootView;
	    }
}
