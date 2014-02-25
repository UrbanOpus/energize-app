package com.magic.energize;


import android.content.SharedPreferences;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class RegistrationFragment extends Fragment {
	private String TAG = "Energize.RegistrationFragment";
	public RegistrationFragment(){}
	
	SharedPreferences prefs;
	//private View mRootView;
	
	
   @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
  
        View rootView = inflater.inflate(R.layout.fragment_registration, container, false);
        Log.d(TAG, "Creating registration view");
        /*Button b = (Button)rootView.findViewById(R.id.button_register);
        b.setOnClickListener(this);
        
        Gson gson = new GsonBuilder()
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        .registerTypeAdapter(Date.class, new DateTypeAdapter())
        .create();
        
		restAdapter = new RestAdapter.Builder()
		  .setServer("http://192.168.0.13:3030/api") // The base API endpoint.
		  .setConverter(new GsonConverter(gson))
		  .setErrorHandler(new ErrorHandler() {
			  @Override 
			  public Throwable handleError(RetrofitError cause) {
				  Log.e(TAG, "Retrofit Error");
				return cause;  
			  }
		  })
		  .setLogLevel(RestAdapter.LogLevel.FULL)
		  .build();
		service = restAdapter.create(EnergizeService.class);*/
        
		//mRootView = rootView;
        return rootView;
    }
   
   /** Called when the user touches the register button */
	/*private void registerUser(View view) {
		
		String fname = ((EditText)mRootView.findViewById(R.id.first_name)).getText().toString();
		String lname = ((EditText)mRootView.findViewById(R.id.last_name)).getText().toString();
		String email = ((EditText)mRootView.findViewById(R.id.email_address)).getText().toString();
		String password = ((EditText)mRootView.findViewById(R.id.password)).getText().toString();
		String password_confirm = ((EditText)mRootView.findViewById(R.id.password_confirm)).getText().toString();
		
		//TODO: confirm password
		Bundle new_user = new Bundle();
		new_user.putString("fname", fname);
		new_user.putString("lname", lname);
		new_user.putString("email", email);
		new_user.putString("password", password);
		
		/*Callback<Bundle> callback = new Callback<Bundle>() {
		    @Override
		    public void success(Bundle json, Response response) {
		    	try {
		    	InputStreamReader is=new InputStreamReader(response.getBody().in());
		    	BufferedReader br=new BufferedReader(is);
		    	String read = null;
		    	StringBuffer sb = new StringBuffer(read);
		    	while((read = br.readLine()) != null) {
		    	    sb.append(read);
		    	}
		    	String responseBody = sb.toString();
		    	Log.d(TAG, json.toString());
		    	Log.d(TAG, responseBody);
		    	Log.d(TAG, "App Token: " + json.getString("app-token"));
		    	} catch (Exception e){
		    		Log.d(TAG, e.toString());
		    	}
		    }

		    @Override
		    public void failure(RetrofitError retrofitError) {
		    	 retrofitError.fillInStackTrace();
		    }
		};
		try {
			service.registerUser(new_user, callback);
		} catch (RetrofitError err) {
			Log.e(TAG, err.getMessage());
		}
		
	}*/

	/*@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.button_register:
			registerUser(v);
		}
		
	}*/
   
   
}
