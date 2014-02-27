package com.magic.energize;

import com.magic.energize.R;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.OnWheelClickedListener;
import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class ReadMeterActivity extends Activity {
	private final String TAG = "Energize.ReadMeterActivity";
	
	//Meter Settings
	private String utilityType;
	private String meterType;
	private String meterUnits;
	
	// Value changed flag
	private boolean valueChanged = false;

	// Value scrolled flag
	private boolean valueScrolled = false;
	
	//Value of each wheel
	private double firstValue;
	private double secondValue;
	private double thirdValue;
	private double fourthValue;
	private double fifthValue;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.read_meter_layout);
		
		// get settings for meter
		Intent intent = getIntent();
		utilityType = intent.getStringExtra("utility_type");
		meterType = intent.getStringExtra("meter_type");
		meterUnits = intent.getStringExtra("meter_units");
		

		final WheelView first = (WheelView) findViewById(R.id.first);
		first.setViewAdapter(new IncrementWheelAdapter(this));
		first.setCyclic(true);

		final WheelView second = (WheelView) findViewById(R.id.second);
		second.setViewAdapter(new IncrementWheelAdapter(this));
		second.setCyclic(true);
		
		final WheelView third = (WheelView) findViewById(R.id.third);
		third.setViewAdapter(new IncrementWheelAdapter(this));
		third.setCyclic(true);
		
		final WheelView fourth = (WheelView) findViewById(R.id.fourth);
		fourth.setViewAdapter(new IncrementWheelAdapter(this));
		fourth.setCyclic(true);
		
		final WheelView fifth = (WheelView) findViewById(R.id.fifth);
		fifth.setViewAdapter(new IncrementWheelAdapter(this));
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
		addChangingListener(fifth, "fifth");

		OnWheelChangedListener wheelListener = new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				if (!valueScrolled) {
					valueChanged = true;
					//do something
					if(wheel.getId() == R.id.first) {
						firstValue = ((IncrementWheelAdapter)wheel.getViewAdapter()).getItemValue(newValue);
					} else if(wheel.getId() == R.id.second) {
						secondValue = ((IncrementWheelAdapter)wheel.getViewAdapter()).getItemValue(newValue);
					} else if(wheel.getId() == R.id.third) {
						thirdValue = ((IncrementWheelAdapter)wheel.getViewAdapter()).getItemValue(newValue);
					} else if(wheel.getId() == R.id.fourth) {
						fourthValue = ((IncrementWheelAdapter)wheel.getViewAdapter()).getItemValue(newValue);
					} else if(wheel.getId() == R.id.fifth) {
						fifthValue = ((IncrementWheelAdapter)wheel.getViewAdapter()).getItemValue(newValue);
					}
					valueChanged = false;
					Log.d(TAG, "Values :" + firstValue + ",  " + secondValue + ",  " + thirdValue + ",  " + fourthValue + ",  " + fifthValue);
				}
			}
		};
		first.addChangingListener(wheelListener);
		second.addChangingListener(wheelListener);
		third.addChangingListener(wheelListener);
		fourth.addChangingListener(wheelListener);
		fifth.addChangingListener(wheelListener);

		OnWheelClickedListener click = new OnWheelClickedListener() {
			@Override
			public void onItemClicked(WheelView wheel, int itemIndex) {
				wheel.setCurrentItem(itemIndex, true);
			}
		};
		first.addClickingListener(click);
		second.addClickingListener(click);
		third.addClickingListener(click);
		fourth.addClickingListener(click);
		fifth.addClickingListener(click);

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

		first.addScrollingListener(scrollListener);
		second.addScrollingListener(scrollListener);
		third.addScrollingListener(scrollListener);
		fourth.addScrollingListener(scrollListener);
		fifth.addScrollingListener(scrollListener);

	}

	/**
	 * Adds changing listener for wheel that updates the wheel label
	 * @param wheel the wheel
	 * @param label the wheel label
	 */
	private void addChangingListener(final WheelView wheel, final String label) {
		wheel.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				if(wheel.getId() == R.id.first) {
					firstValue = ((IncrementWheelAdapter)wheel.getViewAdapter()).getItemValue(newValue);
				} else if(wheel.getId() == R.id.second) {
					secondValue = ((IncrementWheelAdapter)wheel.getViewAdapter()).getItemValue(newValue);
				} else if(wheel.getId() == R.id.third) {
					thirdValue = ((IncrementWheelAdapter)wheel.getViewAdapter()).getItemValue(newValue);
				} else if(wheel.getId() == R.id.fourth) {
					fourthValue = ((IncrementWheelAdapter)wheel.getViewAdapter()).getItemValue(newValue);
				} else if(wheel.getId() == R.id.fifth) {
					fifthValue = ((IncrementWheelAdapter)wheel.getViewAdapter()).getItemValue(newValue);
				}
				valueChanged = false;
				Log.d(TAG, "Values :" + firstValue + ",  " + secondValue + ",  " + thirdValue + ",  " + fourthValue + ",  " + fifthValue);
			}
		});
	}
	
	public void cancelReading(View view) {
		super.onBackPressed();
	}
	
	public void submitReading(View view) {
		//TODO: Submit reading and send to server?
		Log.d(TAG, "Submit Values :" + firstValue + ",  " + secondValue + ",  " + thirdValue + ",  " + fourthValue + ",  " + fifthValue);
		super.onBackPressed();
	}
}
