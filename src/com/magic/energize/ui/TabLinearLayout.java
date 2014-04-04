package com.magic.energize.ui;

import com.magic.energize.R;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.Animation;
import android.widget.LinearLayout;

public class TabLinearLayout extends LinearLayout
{
    private Context _context;

    public TabLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        _context = context;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec+((int)_context.getResources().getDimension(R.dimen.tab_offset_vis)), heightMeasureSpec);
    }
    
    @Override
    protected void onAnimationEnd() {
        super.onAnimationEnd();
        //Functionality here
        Log.d("TabLinearLayout", "Animation End");
        MainActivity.get.animationEnded();
    }
}