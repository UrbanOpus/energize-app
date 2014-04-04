package com.magic.energize.fragments;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import com.magic.energize.R;
import com.magic.energize.SimpleTabDefinition;
import com.magic.energize.TabDefinition;
import com.magic.energize.ui.DataVisualization;
import com.magic.energize.ui.OnSwipeTouchListener;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

public class EnergyUsageFragment extends Fragment implements OnTabChangeListener, AnimationListener {
	
	/**
	 * Constants
	 */
	private final TabDefinition[] TAB_DEFINITIONS = new TabDefinition[] {
		new SimpleTabDefinition(R.id.tab1, R.layout.tab_view1, 
			R.string.tab_title_1, R.id.tabTitle1, new Fragment()),
		new SimpleTabDefinition(R.id.tab2, R.layout.tab_view2, 
			R.string.tab_title_2, R.id.tabTitle2, new Fragment()),
	};

	/**
	 * Fields
	 */
	private static View rootView;
	private TabHost _tabHost;
	
	private OnSwipeTouchListener onVisSwipeListener;
	private OnSwipeTouchListener onChartSwipeListener;
	
	private Animation slideRight;
	private Animation slideLeft;
	
	private LinearLayout visView;
	private LinearLayout chartView;
	
	private DataVisualization[] visDisplay;
	private HashMap<String, Bundle> usageData;
	
	private boolean animation = false;
	private boolean slidingRight = false;
	
	public EnergyUsageFragment(){}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
		if (rootView != null) {
			ViewGroup parent = (ViewGroup) rootView.getParent();
			if (parent != null)
				parent.removeView(rootView);
		}
		try {
			rootView = inflater.inflate(R.layout.fragment_usage, container, false);
		} catch (InflateException e) {
			/* map is already there, just return view as it is */
		}
		
	    _tabHost = (TabHost)rootView.findViewById(android.R.id.tabhost);
	    _tabHost.setup();

	    for (TabDefinition tab : TAB_DEFINITIONS) {
	    	_tabHost.addTab(createTab(inflater, _tabHost, rootView, tab));
	    }
	    
	    visView = (LinearLayout) rootView.findViewById(R.id.tab_slide1);
	    chartView = (LinearLayout) rootView.findViewById(R.id.tab2);
	    
	    slideRight = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_right);
	    slideLeft = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_left);
	    slideRight.setAnimationListener(this);
	    slideLeft.setAnimationListener(this);
	    
	    onVisSwipeListener = new OnSwipeTouchListener(getActivity().getApplicationContext()) {
	        public void onSwipeTop() {
	            //Toast.makeText(getActivity(), "vis-top", Toast.LENGTH_SHORT).show();
	        }
	        public void onSwipeRight() {
	        	if(!animation) {
		        	slidingRight = true;
		        	animation = true;
		        	visView.startAnimation(slideRight);
	        	}
	        }
	        public void onSwipeLeft() {
	        	if(!animation) {
		        	slidingRight = false;
		        	animation = true;
		        	visView.startAnimation(slideLeft);
	        	}
	        }
	        public void onSwipeBottom() {
	            //Toast.makeText(getActivity(), "vis-bottom", Toast.LENGTH_SHORT).show();
	        }
	    };
	    
	    onChartSwipeListener = new OnSwipeTouchListener(getActivity().getApplicationContext()) {
	        public void onSwipeTop() {
	            //Toast.makeText(getActivity(), "chart-top", Toast.LENGTH_SHORT).show();
	        }
	        public void onSwipeRight() {
	        	chartMoveRight();
	            Toast.makeText(getActivity(), "chart-right", Toast.LENGTH_SHORT).show();
	        }
	        public void onSwipeLeft() {
	        	chartMoveLeft();
	            Toast.makeText(getActivity(), "chart-left", Toast.LENGTH_SHORT).show();
	        }
	        public void onSwipeBottom() {
	            //Toast.makeText(getActivity(), "chart-bottom", Toast.LENGTH_SHORT).show();
	        }
	    };
	    
	    visView.setOnTouchListener(onVisSwipeListener);
	    chartView.setOnTouchListener(onChartSwipeListener);
	    
	    usageData = new HashMap<String, Bundle>();
	    initVisDisplay();
	    
	    
		return rootView;
	   
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	    setRetainInstance(true);

	    _tabHost.setOnTabChangedListener(this);

	    if (TAB_DEFINITIONS.length > 0) {
	    	onTabChanged(TAB_DEFINITIONS[0].getId());
	    }
	}
	
	private void initVisDisplay() {
		visDisplay = new DataVisualization[5];
		//int imgView, int dateView, int graphView, Calendar date
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		// start with 3 months previous
		c.add(Calendar.MONTH, -3);
		visDisplay[0] = new DataVisualization(R.id.vis_top1, R.id.vis_mid1, R.id.vis_bot1, (Calendar)c.clone());
		c.add(Calendar.MONTH, 1);
		visDisplay[1] = new DataVisualization(R.id.vis_top2, R.id.vis_mid2, R.id.vis_bot2, (Calendar)c.clone());
		c.add(Calendar.MONTH, 1);
		visDisplay[2] = new DataVisualization(R.id.vis_top3, R.id.vis_mid3, R.id.vis_bot3, (Calendar)c.clone());
		c.add(Calendar.MONTH, 1);
		visDisplay[3] = new DataVisualization(R.id.vis_top4, R.id.vis_mid4, R.id.vis_bot4, (Calendar)c.clone());
		c.add(Calendar.MONTH, 1);
		visDisplay[4] = new DataVisualization(R.id.vis_top5, R.id.vis_mid5, R.id.vis_bot5, (Calendar)c.clone());
		
		for(int i=0; i < visDisplay.length; i++) {
			showData(visDisplay[i]);
		}
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
	}
	
	public void visMoveRight() {
		for(int i=0; i < visDisplay.length; i++) {
			visDisplay[i].previousMonth();
			showData(visDisplay[i]);
		}
	}
	
	public void visMoveLeft() {
		for(int i=0; i < visDisplay.length; i++) {
			visDisplay[i].nextMonth();
			showData(visDisplay[i]);
		}
	}
	
	public void chartMoveRight() {
		
	}
	
	public void chartMoveLeft() {
		
	}
	
	public void showData(DataVisualization visData) {
		// set date
		String visDate = visData.getDateTxt();
		TextView dateText = (TextView) rootView.findViewById(visData.getDateViewId());
		dateText.setText(visDate);
		Bundle usage;
		if(usageData.containsKey(visDate)) {
			usage = usageData.get(visDate);
		} else {
			float avgUsage = 10.0f;
			float myUsage = (float)Math.random()*20+1;
			usage = new Bundle();
			usage.putFloat("avg-usage", avgUsage);
			usage.putFloat("usage", myUsage);
			usageData.put(visDate, usage);
		}
		visData.setAvgUsage(usage.getFloat("avg-usage"));
		visData.setUsage(usage.getFloat("usage"));
		// set image
		ImageView img = (ImageView) rootView.findViewById(visData.getImageViewId());
		img.setImageResource(visData.getImageResourceId());
		
	}
	
	@Override
	public void onTabChanged(String tabId) {
		for (TabDefinition tab : TAB_DEFINITIONS) {
			if (tabId != tab.getId()) {
				continue;
			}

			updateTab(tabId, tab.getFragment(), tab.getTabContentViewId());
			return;
		}

		throw new IllegalArgumentException("The specified tab id '" + 
					tabId + "' does not exist.");
	}
	   //
	   // Internal Members
	   //
	/**
	 * Creates a {@link TabSpec} based on the specified parameters.
	 * @param inflater The {@link LayoutInflater} responsible for creating {@link View}s.
	 * @param tabHost The {@link TabHost} used to create new {@link TabSpec}s.
	 * @param root The root {@link View} for the {@link Fragment}.
	 * @param tabDefinition The {@link TabDefinition} that defines what the tab will look and act like.
	 * @return A new {@link TabSpec} instance.
	 */
	   
	private TabSpec createTab(LayoutInflater inflater, 
		TabHost tabHost, View root, TabDefinition tabDefinition) {
		ViewGroup tabsView = (ViewGroup)root.findViewById(android.R.id.tabs);
		View tabView = tabDefinition.createTabView(inflater, tabsView);

		TabSpec tabSpec = tabHost.newTabSpec(tabDefinition.getId());
		tabSpec.setIndicator(tabView);
		tabSpec.setContent(tabDefinition.getTabContentViewId());
		return tabSpec;
	}
	/**
	 * Called when switching between tabs.
	 * @param tabId The unique identifier for the tab.
	 * @param fragment The {@link Fragment} to swap in for the tab.
	 * @param containerId The layout ID for the {@link View} that houses the tab's content.
	 */
	private void updateTab(String tabId, Fragment fragment, int containerId) {
		final FragmentManager manager = getFragmentManager();
	    if (manager.findFragmentByTag(tabId) == null) {
			manager.beginTransaction()
				.replace(containerId, fragment, tabId)
				.commit();
	    }
	  }

	@Override
	public void onAnimationEnd(Animation anim) {
		if(anim == null) {
			if(slidingRight) {
				visMoveRight();
				animation = false;
			} else {
				visMoveLeft();
				animation = false;
			}
		}
//		if(anim.equals(slideLeft)) {
//			visMoveRight();
//			animation = false;
//		} else if(anim.equals(slideRight)) {
//			visMoveRight();
//			animation = false;
//		}
	}

	@Override
	public void onAnimationRepeat(Animation arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnimationStart(Animation arg0) {
		// TODO Auto-generated method stub
		
	}
}


