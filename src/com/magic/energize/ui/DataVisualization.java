package com.magic.energize.ui;

import java.util.Calendar;
import java.util.Locale;


import com.magic.energize.R;

public class DataVisualization {
	public static final float NULL_USAGE = -99.0f;
	private static final float DIFFERENCE_TRESHOLD = 3.0f;
	private final int _dataImageViewId;
	private final int _dataDateViewId;
	private final int _dataGraphViewId;
	
	private float _usage;
	private float _avgUsage;
	private Calendar _date;
	
	public DataVisualization(int imgView, int dateView, int graphView, Calendar date) {
		_dataImageViewId = imgView;
		_dataDateViewId = dateView;
		_dataGraphViewId = graphView;
		
		_usage = NULL_USAGE;
		_avgUsage = NULL_USAGE;
		
		_date = date;
	}
	
	public void nextMonth() {
		_date.add(Calendar.MONTH, 1);
	}
	
	public void previousMonth() {
		_date.add(Calendar.MONTH, -1);
	}
	
	public void setMonth(Calendar date) {
		_date = date;
	}
	
	public void setUsage(float usage) {
		_usage = usage;
	}
	
	public void setAvgUsage(float avgUsage) {
		_avgUsage = avgUsage;
	}
	
//	public void setImageViewId(int id) {
//		_dataImageViewId = id;
//	}
//	
//	public void setDateViewId(int id) {
//		_dataDateViewId = id;
//	}
//	
//	public void setGraphViewId(int id) {
//		_dataGraphViewId = id;
//	}
	
	public int getImageResourceId() {
		float delta = this._usage - this._avgUsage;
		if(Math.abs(delta) < DIFFERENCE_TRESHOLD) {
			// use nutral image
			return R.drawable.vis_avg;
		} else if(delta > 0) {
			// use poor image
			return R.drawable.vis_poor;
		} else {
			// use good image
			return R.drawable.vis_good;
		}
	}
	
	public float getUsage() {
		return _usage;
	}
	
	public float getAvgUsage() {
		return _avgUsage;
	}
	
	public int getImageViewId() {
		return _dataImageViewId;
	}
	
	public int getDateViewId() {
		return _dataDateViewId;
	}
	
	public int getGraphViewId() {
		return _dataGraphViewId;
	}
	
	public String getDateTxt() {
		return _date.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US).toUpperCase() + " " + _date.get(Calendar.YEAR);
	}
}
