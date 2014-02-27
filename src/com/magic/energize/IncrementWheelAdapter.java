package com.magic.energize;

import android.content.Context;
import kankan.wheel.widget.adapters.AbstractWheelTextAdapter;

public class IncrementWheelAdapter extends AbstractWheelTextAdapter {
	
	 /** The default max value */
    public static final double DEFAULT_START_VALUE = 9.5;

    /** The default start value */
    private static final double DEFAULT_MIN_VALUE = 0;
	
    /** The default increment value */
    private static final double DEFAULT_INCREMENT_VALUE = 0.5;
    
    
    // Values
    private double startValue;
    private double maxValue;
    private double incrementValue;
    
    // format
    private String format;
    
    /**
     * Constructor
     * @param context the current context
     */
	public IncrementWheelAdapter(Context context) {
		this(context, DEFAULT_MIN_VALUE, DEFAULT_START_VALUE, DEFAULT_INCREMENT_VALUE);
	}
	
    /**
     * Constructor
     * @param context the current context
     * @param minValue the wheel start value
     * @param maxValue the wheel max value
     * @param maxValue the wheel max value
     */
    public IncrementWheelAdapter(Context context, double startValue, double maxValue, double incrementValue) {
        this(context, startValue, maxValue, incrementValue, null);
    }
    
    /**
     * Constructor
     * @param context the current context
     * @param minValue the wheel start value
     * @param maxValue the wheel max value
     * @param format the format string
     */
    public IncrementWheelAdapter(Context context, double startValue, double maxValue, double incrementValue, String format) {
        super(context);
        
        this.startValue = startValue;
        this.maxValue = maxValue;
        this.incrementValue = incrementValue;
        this.format = format;
    }
    
    public double getItemValue(int index) {
    	return startValue + (index*this.incrementValue);
    }
	
    @Override
    public CharSequence getItemText(int index) {
        if (index >= 0 && index < getItemsCount()) {
            double value = startValue + (index*this.incrementValue);
            return format != null ? String.format(format, value) : Double.toString(value);
        }
        return null;
    }

    @Override
    public int getItemsCount() {
    	if(incrementValue > 0) {
    		return (int)((maxValue - startValue)/incrementValue);
    	}
    	return 0;
    }  

}
