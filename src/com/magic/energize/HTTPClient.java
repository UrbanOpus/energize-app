package com.magic.energize;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

public class HTTPClient {
	
	private static final String TAG = "Energize.HttpClient";
	//private static final 
	private static ArrayList<AsyncTask> clientTaskList = new ArrayList<AsyncTask>();
	
	public static void POST(String url, Bundle data, String return_id, HTTPClientListener listener) {
		data.putString("post-url", url);
		data.putString("post-method", return_id);
		HttpPOSTAsyncTask task = new HttpPOSTAsyncTask(listener);
		clientTaskList.add(task);
		task.execute(data);
	}
	
	public static void GET(String url, String return_id, HTTPClientListener listener) {
		HttpGETAsyncTask task = new HttpGETAsyncTask(listener, return_id);
		clientTaskList.add(task);
		task.execute(url, return_id);
	}
	
	public static void shutDown() {
		for(AsyncTask task : clientTaskList) {
			if(task!=null && !task.isCancelled()) {
				task.cancel(true);
			}
			clientTaskList.remove(task);
		}
	}
	
	public static boolean isNetworkAvailable(Context activity) {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
	private static class HttpGETAsyncTask extends AsyncTask<String, Void, String> {
    	private HTTPClientListener listener;
    	private String method;
    	
    	public HttpGETAsyncTask(HTTPClientListener listener, String callback_id){
            this.listener=listener;
            method = callback_id;
            if(!isNetworkAvailable((Context)listener)) {
            	listener.onRequestCompleted("no-connection", "ERROR");
            	this.cancel(true);
            }
        }

		@Override
        protected String doInBackground(String... urls) {
        	method = urls[1];
            return execute_GET(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
        	clientTaskList.remove(this);
        	if(!isCancelled()) {
        		listener.onRequestCompleted(method, result);
        	} else {
        		return;
        	}
       }
	}
	
    private static class HttpPOSTAsyncTask extends AsyncTask<Bundle, Void, String> {
    	private HTTPClientListener listener;
    	private String method;
    	
    	public HttpPOSTAsyncTask(HTTPClientListener listener){
            this.listener=listener;
            if(!isNetworkAvailable((Context)listener)) {
            	listener.onRequestCompleted("no-connection", "ERROR");
            	this.cancel(true);
            }
        }
    	
        @Override
        protected String doInBackground(Bundle... vals) {
        	String url = vals[0].getString("post-url");
        	method = vals[0].getString("post-method");
        	vals[0].remove("post-url");
        	vals[0].remove("post-method");
            try {
				return execute_POST(new URI(url), vals[0]);
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            return null;
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
        	clientTaskList.remove(this);
        	if(!isCancelled()) {
        		listener.onRequestCompleted(method, result);
        	} else {
        		return;
        	}
       }
    }
    
    private static String execute_GET(String url){
        InputStream inputStream = null;
        String result = "";
        try {
 
            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();
 
            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
 
            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();
 
            // convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "ERROR";
            
            inputStream.close();
 
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
            result = "ERROR";
        }
 
        return result;
    }
    
    private static String execute_POST(URI uri, Bundle postData) {
        InputStream inputStream = null;
        String result = "";
        try {
        	// create POST
        	HttpPost post = new HttpPost(uri);
    		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();;
    		Set<String> keys = postData.keySet();
    	    for (String key : keys) {
    	    	nameValuePairs.add(new BasicNameValuePair(key, (String)postData.get(key)));
    	    }
    	    post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
 
            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();
            
            // make POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(post);
 
            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();
 
            // convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "ERROR";
            
            inputStream.close();
 
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
            result = "ERROR";
        }
 
        return result;
    }
    
    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;
 
        inputStream.close();
        return result;
 
    }

}
