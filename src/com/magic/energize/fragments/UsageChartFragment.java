package com.magic.energize.fragments;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;

/**
 * A {@link Fragment} used to switch between tabs.
 */
public class UsageChartFragment extends Fragment {

  private View _viewRoot;

  //
  // Exposed Members
  //
  private class DrawView extends View {
      Paint paint = new Paint();

      public DrawView(Context context) {
          super(context);
          paint.setColor(Color.BLACK);
      }
      public DrawView(Context context, AttributeSet attrs) {
          super(context, attrs);
          paint.setColor(Color.BLACK);
      }
      public DrawView(Context context, AttributeSet attrs, int defStyle) {
          super(context, attrs, defStyle);
          paint.setColor(Color.BLACK);
      }

      @Override
      protected void onDraw(Canvas canvas) {
          super.onDraw(canvas);
            canvas.drawRect(50, 50, 100, 300, paint); 
            canvas.drawRect(300, 300, 300, 300, paint); 
      }

      @Override 
      protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
          super.onMeasure(widthMeasureSpec, heightMeasureSpec);

          int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
          int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
          this.setMeasuredDimension(parentWidth, parentHeight);
      }
  }   

 
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//    _viewRoot = inflater.inflate(R.layout.fragment_tabs, null);
//    return _viewRoot;
	  super.onCreateView(inflater, container, savedInstanceState);
      return new DrawView(this.getActivity());
 
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    setRetainInstance(true);

   
  }

}