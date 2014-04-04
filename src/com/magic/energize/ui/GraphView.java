package com.magic.energize.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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

    @Override
    public void onDraw(Canvas canvas) {
    	Log.d("Draw", "Canvas onDraw");
        paint.setColor(Color.BLACK);
        canvas.drawRect(20, 20, 80, 80, paint); 
        canvas.drawRect(0, 0, 90, 90, paint);
        //paint.setStrokeWidth(3);
//        canvas.drawRect(30, 30, 80, 80, paint);
//        paint.setStrokeWidth(0);
//        paint.setColor(Color.CYAN);
//        canvas.drawRect(33, 60, 77, 77, paint );
//        paint.setColor(Color.YELLOW);
//        canvas.drawRect(33, 33, 77, 60, paint );
        super.onDraw(canvas);
    }

}