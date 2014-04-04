package com.magic.energize.ui;

import java.util.ArrayList;
import java.util.Date;

import com.magic.energize.R;
import com.magic.energize.StorageHelper;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.OnWheelClickedListener;
import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.NumericWheelAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class ReadMeterActivity extends Activity {
	private final String TAG = MainActivity.UNIVERSAL_TAG + ".ReadMeterActivity";
	
	SharedPreferences prefs;
	
	//Meter Settings
	private String utilityType;
	private String meterType;
	private String meterUnits;
	
	private static final double cubicFeetToMeters = 0.0283168;
	private static final double cubicMetersToGJ = 0.03921568627;
	private String METRIC;
	private String IMPERIAL;
	private String DIGITAL;
	private String ANALOG;
	
	//Meter UI
	private ImageView img;
	private SeekBar seekBar;
	//private TextView meterValue;
	
	// Value changed flag
	private boolean valueChanged = false;

	// Value scrolled flag
	private boolean valueScrolled = false;
	
	//Value of each wheel
	private int[] readingValues;
	/*
	private double firstValue;
	private double secondValue;
	private double thirdValue;
	private double fourthValue;
	private double fifthValue;*/
	
	//Layout for each mini-meter for analog dials
	int[] mini_meter_ids;
	ArrayList<LinearLayout> mini_meters;
	
	int[] wheel_view_ids;
	ArrayList<WheelView> wheel_views;
	/*LinearLayout mini_meter1;
	LinearLayout mini_meter2;
	LinearLayout mini_meter3;
	LinearLayout mini_meter4;
	LinearLayout mini_meter5;*/
	
	private int current_dial;
	private int dial_value;
	private int rotate_value;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		prefs = getApplicationContext().getSharedPreferences(getString(R.string.prefs_id), MODE_PRIVATE);
		// get settings for meter
		Intent intent = getIntent();
		utilityType = intent.getStringExtra("utility_type");
		meterType = intent.getStringExtra("meter_type");
		meterUnits = intent.getStringExtra("meter_units");
		
		METRIC = getResources().getStringArray(R.array.meter_units)[0];
		IMPERIAL = getResources().getStringArray(R.array.meter_units)[1];
		
		DIGITAL = getResources().getStringArray(R.array.meter_types)[0];
		ANALOG = getResources().getStringArray(R.array.meter_types)[1];
		
		readingValues = new int[5];
		current_dial = 1;
		
		//Find meter type for view
		if(meterType.equals(ANALOG)) {
			loadAnalogMeter();
		} else if(meterType.equals(DIGITAL)) {
			loadDigitalMeter();
		} // other meter types here...?
	}
	
	public void loadDigitalMeter() {
		setContentView(R.layout.read_meter_digital);
		if(meterUnits.equals(METRIC)) {
			// Metric units
			wheel_view_ids = new int[5];
		} else if (meterUnits.equals(IMPERIAL)) {
			// Imperial units
			wheel_view_ids = new int[4];
		}
		wheel_view_ids[0] = R.id.first;
		wheel_view_ids[1] = R.id.second;
		wheel_view_ids[2] = R.id.third;
		wheel_view_ids[3] = R.id.fourth;
		if(meterUnits.equals(METRIC))
			wheel_view_ids[4] = R.id.fifth;
		else {
			WheelView extra_wheel = (WheelView) findViewById(R.id.fifth);
			extra_wheel.setVisibility(View.GONE);
			LinearLayout wheel_layout = (LinearLayout) findViewById(R.id.wheel_view_layout);
			wheel_layout.setWeightSum(4);
		}
		
		OnWheelChangedListener wheelListener = new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				if (!valueScrolled) {
					valueChanged = true;
					//do something
					for(int i = 0; i < wheel_view_ids.length; i++) {
						if(wheel.getId() == wheel_view_ids[i])
							readingValues[i] = Integer.parseInt(((NumericWheelAdapter)wheel.getViewAdapter()).getItemText(newValue).toString());
					}
					valueChanged = false;
					printReadingValues();
				}
			}
		};
		OnWheelClickedListener click = new OnWheelClickedListener() {
			@Override
			public void onItemClicked(WheelView wheel, int itemIndex) {
				wheel.setCurrentItem(itemIndex, true);
			}
		};

		OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
			@Override
			public void onScrollingStarted(WheelView wheel) {
				valueScrolled = true;
			}
			@Override
			public void onScrollingFinished(WheelView wheel) {
				valueScrolled = false;
			}
		};
		
		// Add listeners
		for(int i = 0; i < wheel_view_ids.length; i++) {
			final WheelView wv = (WheelView) findViewById(wheel_view_ids[i]);
			wv.setViewAdapter(new NumericWheelAdapter(this));
			wv.setCyclic(true);
			wv.setCurrentItem(0);
			addChangingListener(wv);
			wv.addChangingListener(wheelListener);
			wv.addClickingListener(click);
			wv.addScrollingListener(scrollListener);
			//wheel_views.add((LinearLayout) findViewById(wheel_view_ids[i]));
		}
			
		

		/*final WheelView second = (WheelView) findViewById(R.id.second);
		second.setViewAdapter(new NumericWheelAdapter(this));
		second.setCyclic(true);
		
		final WheelView third = (WheelView) findViewById(R.id.third);
		third.setViewAdapter(new NumericWheelAdapter(this));
		third.setCyclic(true);
		
		final WheelView fourth = (WheelView) findViewById(R.id.fourth);
		fourth.setViewAdapter(new NumericWheelAdapter(this));
		fourth.setCyclic(true);
		
		final WheelView fifth = (WheelView) findViewById(R.id.fifth);
		fifth.setViewAdapter(new NumericWheelAdapter(this));
		fifth.setCyclic(true);


		first.setCurrentItem(0);
		second.setCurrentItem(0);
		third.setCurrentItem(0);
		fourth.setCurrentItem(0);
		fifth.setCurrentItem(0);


		// add listeners
		addChangingListener(first, "first");
		addChangingListener(second, "second");
		addChangingListener(third, "thrid");
		addChangingListener(fourth, "fourth");
		addChangingListener(fifth, "fifth");*/

	}
	
	public void loadAnalogMeter() {
		setContentView(R.layout.read_meter_analog);
		mini_meters = new ArrayList<LinearLayout>();
		seekBar = (SeekBar) findViewById(R.id.analog_dial_setter);
		seekBar.setMax(360);
		img = (ImageView) findViewById(R.id.meter_dial);
		if(meterUnits.equals(METRIC)) {
			// Metric units
			mini_meter_ids = new int[5];
		} else if (meterUnits.equals(IMPERIAL)) {
			// Imperial units
			mini_meter_ids = new int[4];
		}
		mini_meter_ids[0] = R.id.mini_meter1;
		mini_meter_ids[1] = R.id.mini_meter2;
		mini_meter_ids[2] = R.id.mini_meter3;
		mini_meter_ids[3] = R.id.mini_meter4;
		if(meterUnits.equals(METRIC))
			mini_meter_ids[4] = R.id.mini_meter5;
		else {
			LinearLayout extra_mini_meter = (LinearLayout) findViewById(R.id.mini_meter5);
			extra_mini_meter.setVisibility(View.GONE);
			LinearLayout mini_meter_layout = (LinearLayout) findViewById(R.id.mini_meter_layout);
			mini_meter_layout.setWeightSum(4);
		}
		for(int i = 0; i < mini_meter_ids.length; i++)
			mini_meters.add((LinearLayout) findViewById(mini_meter_ids[i]));
		// get mini meter layouts
		/*mini_meter1 = (LinearLayout) findViewById(R.id.mini_meter1);
		mini_meter2 = (LinearLayout) findViewById(R.id.mini_meter2);
		mini_meter3 = (LinearLayout) findViewById(R.id.mini_meter3);
		mini_meter4 = (LinearLayout) findViewById(R.id.mini_meter4);
		mini_meter5 = (LinearLayout) findViewById(R.id.mini_meter5);*/
		
		current_dial = 1;
		dial_value = 0;
		rotate_value = 0;
		setMiniDialBackground(current_dial);
		
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			int progress = 0;
			@Override
			public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
				doRotate(progress, progresValue);
				progress = progresValue;
				rotate_value = progress;
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// Do something here, 
				//if you want to do anything at the start of
				// touching the seekbar
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// Display the value in textview
				//meterValue.setText(progress);
			}
		});
		//doRotate();
	}
	
	private void setMiniDialBackground(int dial) {
		if(current_dial == dial)
			mini_meters.get(dial - 1).setBackgroundColor(
					getResources().getColor(R.color.mini_meter_background_selected));
		else 
			mini_meters.get(dial - 1).setBackgroundColor(
					getResources().getColor(R.color.mini_meter_background));
		/*switch(dial) {
		case 1:
			if(current_dial == 1)
				mini_meter1.setBackgroundColor(
						getResources().getColor(R.color.mini_meter_background_selected));
			else
				mini_meter1.setBackgroundColor(
						getResources().getColor(R.color.mini_meter_background));
			break;
		case 2:
			if(current_dial == 2)
				mini_meter2.setBackgroundColor(
						getResources().getColor(R.color.mini_meter_background_selected));
			else
				mini_meter2.setBackgroundColor(
						getResources().getColor(R.color.mini_meter_background));
			break;
		case 3:
			if(current_dial == 3)
				mini_meter3.setBackgroundColor(
						getResources().getColor(R.color.mini_meter_background_selected));
			else
				mini_meter3.setBackgroundColor(
						getResources().getColor(R.color.mini_meter_background));
			break;
		case 4:
			if(current_dial == 4)
				mini_meter4.setBackgroundColor(
						getResources().getColor(R.color.mini_meter_background_selected));
			else
				mini_meter4.setBackgroundColor(
						getResources().getColor(R.color.mini_meter_background));
			break;
		case 5:
			if(current_dial == 5)
				mini_meter5.setBackgroundColor(
						getResources().getColor(R.color.mini_meter_background_selected));
			else
				mini_meter5.setBackgroundColor(
						getResources().getColor(R.color.mini_meter_background));
			break;
		}*/
	}
	
	/*private void setMiniDialBackground() {
		for(int i=1; i < 6; i++) {
			setMiniDialBackground(i);
		}
	}*/

	/**
	 * Adds changing listener for wheel that updates the wheel label
	 * @param wheel the wheel
	 */
	private void addChangingListener(final WheelView wheel) {
		wheel.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				for(int i = 0; i < wheel_view_ids.length; i++) {
					if(wheel.getId() == wheel_view_ids[i])
						readingValues[i] = Integer.parseInt(((NumericWheelAdapter)wheel.getViewAdapter())
								.getItemText(newValue).toString());
				}
				/*if(wheel.getId() == R.id.first) {
					readingValues[0] = Integer.parseInt(((NumericWheelAdapter)wheel.getViewAdapter()).getItemText(newValue).toString());
				} else if(wheel.getId() == R.id.second) {
					readingValues[1] = Integer.parseInt(((NumericWheelAdapter)wheel.getViewAdapter()).getItemText(newValue).toString());
				} else if(wheel.getId() == R.id.third) {
					readingValues[2] = Integer.parseInt(((NumericWheelAdapter)wheel.getViewAdapter()).getItemText(newValue).toString());
				} else if(wheel.getId() == R.id.fourth) {
					readingValues[3] = Integer.parseInt(((NumericWheelAdapter)wheel.getViewAdapter()).getItemText(newValue).toString());
				} else if(wheel.getId() == R.id.fifth) {
					readingValues[4] = Integer.parseInt(((NumericWheelAdapter)wheel.getViewAdapter()).getItemText(newValue).toString());
				}*/
				valueChanged = false;
				printReadingValues();
			}
		});
	}
	
	// Called when Analog meter is rotated.
	public void doRotate(int oldVal, int newVal) {
		final class DialAnimation implements Runnable {
			int oldValue;
			int newValue;
			public DialAnimation(int oldVal, int newVal) {
				oldValue = oldVal;
				newValue = newVal;
			}
			@Override
			public void run() {
				try {
					RotateAnimation rotateAnimation = new RotateAnimation(
							oldValue, newValue,
							Animation.RELATIVE_TO_SELF, 0.5f,
							Animation.RELATIVE_TO_SELF, 0.5f);
					rotateAnimation.setInterpolator(new LinearInterpolator());
					rotateAnimation.setDuration(1);
					rotateAnimation.setFillAfter(true);
					img.startAnimation(rotateAnimation);
					// if dial is close to a number
					/*if(newValue%10 > 8 || newValue%10 < 2) {
						set flag for close meter value?
					}*/
					dial_value = newValue;
					TextView meterValue = (TextView) findViewById(R.id.analog_meter_value);
					int actual_val;
					if(dial_value/36 == 10) {
						actual_val = 0;
					} else {
						actual_val = dial_value/36;
					}
					meterValue.setText(actual_val + "");
 				} catch (Exception e) {

				}
				
			}
			
		}
		runOnUiThread(new DialAnimation(oldVal, newVal));
	}
	
	public void cancelReading(View view) {
		super.onBackPressed();
	}
	
	public void onAnalogNext(View view) {
		readingValues[current_dial - 1] = dial_value;
		if(current_dial > 0 && current_dial < mini_meters.size()) {
			current_dial++;
			setMiniDialBackground(current_dial - 1);
			setMiniDialBackground(current_dial);
			if(current_dial == 5) {
				Button nextButton = (Button) findViewById(R.id.analog_reading_next);
				nextButton.setText("FINISH");
			} else if (current_dial > 1) {
				Button prevButton = (Button) findViewById(R.id.analog_reading_prev);
				prevButton.setVisibility(View.VISIBLE);
				Log.d(TAG, "Setting prev button visible");
			}
			seekBar.setProgress(readingValues[current_dial-1]);
		} else if (current_dial == mini_meters.size()) {
			submitReading(null);
		}
	}
	
	public void onAnalogPrev(View view) {
		if(current_dial == mini_meters.size()) {
			Button nextButton = (Button) findViewById(R.id.analog_reading_next);
			nextButton.setText("NEXT");
		}
		current_dial--;
		setMiniDialBackground(current_dial + 1);
		setMiniDialBackground(current_dial);
		seekBar.setProgress(readingValues[current_dial-1]);
		if(current_dial == 1) {
			Button prevButton = (Button) findViewById(R.id.analog_reading_prev);
			prevButton.setVisibility(View.INVISIBLE);
		}
	}
	
	public void submitReading(View view) {
		//TODO: Submit reading and send to server?
		Log.d(TAG, "Submit Values :");
		printReadingValues();
		String title = "Meter Reading: " + getReading() + " ";
		if(meterUnits.equals(IMPERIAL))
			title += "cubic feet.";
		else
			title += "cubic meters.";
		String text = "Total Gigajouls Consumed: " + GJforReading(convertedReading());
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
		alertBuilder.setMessage(text).setTitle(title)
	       .setCancelable(false)
	       .setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	                dialog.cancel();
	                long when = new Date().getTime();
	                // Submit Reading
	        		StorageHelper db = new StorageHelper(ReadMeterActivity.this);
	        		db.addMeterReading(convertedReading(), when);
	        		// Add meter reading to preferences
	        		SharedPreferences.Editor editor =  prefs.edit();
	        		if(!prefs.contains(getString(R.string.base_reading)))
	        			editor.putLong(getString(R.string.base_reading), when);
		    		editor.putLong(getString(R.string.last_reading), when);
		    		editor.commit();
	                // Go back to main activity
	            	Intent i = new Intent(ReadMeterActivity.this, MainActivity.class);
	            	i.putExtra("data-synced", false);
	        		startActivity(i);
	           }
	       })
	       .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	                dialog.cancel();
	           }
	       });
		AlertDialog alert = alertBuilder.create();
		alert.show();
	}
	
	public static double GJforReading(int cubicMeters) {
		double reading = (double)cubicMeters;//convertedReading();
		double gj = reading*cubicMetersToGJ;
		
		// cut off at 2 decimal places
		double roundedGJ = ((double)((int)(gj*100.0)))/100;
		return roundedGJ;
	}
	
	// Calculates the current m^3 of gass used from current values.
	private int convertedReading() {
		int reading = getReading();
		if(meterUnits.equals(IMPERIAL)) {
			return (int)(((double)reading)*cubicFeetToMeters);
		}
		return reading;
	}
	
	private int getReading() {
		int reading = 0;
		String reading_total = "";
		for(int i=0; i<readingValues.length; i++) {
			int actual_val = 0;
			if(readingValues[i]/36 == 10) {
				actual_val = 0;
			} else {
				actual_val = readingValues[i]/36;
			}
			reading_total = reading_total + actual_val;
		}
		reading = Integer.parseInt(reading_total);
		if(meterUnits.equals(IMPERIAL)) {
			reading = reading*1000;
		}
		return reading;
	}
	
	private void printReadingValues() {
		Log.d(TAG, "Reading Values:");
		for(int i=0; i<readingValues.length; i++)
			Log.d(TAG, "Vaule " + (i+1) + ": " + readingValues[i]);
	}
}
