package com.magic.energize.ui;

import com.magic.energize.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;

public class GraphView extends LinearLayout {
	
	private Context _context;
	Paint paint = new Paint();

    public GraphView(Context context, AttributeSet attrs) {
    	super(context, attrs);
        _context = context;
    }
    
    private void init(AttributeSet attrs) { 
        TypedArray a=getContext().obtainStyledAttributes(
             attrs,
             R.styleable.GraphView);

        //Use a
        Log.i("test",a.getString(
             R.styleable.GraphView_android_text));
        Log.i("test",""+a.getColor(
             R.styleable.GraphView_android_textColor, Color.BLACK));
        Log.i("test",a.getString(
             R.styleable.GraphView_extraInformation));

        //Don't forget this
        a.recycle();
    }
    
    /*@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
       // Try for a width based on our minimum
       int minw = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();
       int w = resolveSizeAndState(minw, widthMeasureSpec, 1);

       // Whatever the width ends up being, ask for a height that would let the pie
       // get as big as it can
       int minh = MeasureSpec.getSize(w) - (int)mTextWidth + getPaddingBottom() + getPaddingTop();
       int h = resolveSizeAndState(MeasureSpec.getSize(w) - (int)mTextWidth, heightMeasureSpec, 0);

       setMeasuredDimension(w, h);
    }*/

    @Override
    public void onDraw(Canvas canvas) {
    	Log.d("Draw", "Canvas onDraw");
        paint.setColor(Color.BLACK);
//        Rect r = new Rect();
//        canvas.drawRect(400, 200, 200, 400, paint); 
//        canvas.drawRect(300,300, 300, 300, paint);
        //paint.setStrokeWidth(3);
//        canvas.drawRect(30, 30, 80, 80, paint);
//        paint.setStrokeWidth(0);
        paint.setColor(Color.CYAN);
        canvas.drawRect(33, 60, 77, 77, paint );
        paint.setColor(Color.YELLOW);
        canvas.drawRect(33, 33, 77, 60, paint );
        super.onDraw(canvas);
    }

}