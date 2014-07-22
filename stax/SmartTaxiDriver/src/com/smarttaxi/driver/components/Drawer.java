package com.smarttaxi.driver.components;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import com.smarttaxi.driver.R;

public class Drawer extends RelativeLayout  {
	
	
	private Context context;

	public Drawer(Context context) {
	    
		super(context);
	    this.context = context;
	    //mInflater =LayoutInflater.from(getContext());
	}/*

	public static RelativeLayout layoutDrawer;
	private static LayoutInflater mInflater;
	public static LetMenuAdaptr leftMenuAdapter;
	public static ListView listViewDrawer;
	
	public static View v;
	private Context context;

	public Drawer(Context context) {
	    
		super(context);
	    this.context = context;
	    //mInflater =LayoutInflater.from(getContext());
	}

	public Drawer(Context context, AttributeSet attrs) {
	    super(context, attrs);
	}

	public Drawer(Context context, AttributeSet attrs, int defStyle) {
	    super(context, attrs, defStyle);
	}

	public static void showDrawer(Context ctx) {

		
		
		LayoutInflater mInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		v = mInflater.inflate(R.layout.activity_main, null);
		
		
		leftMenuAdapter = new LetMenuAdaptr(ctx);
		layoutDrawer = (RelativeLayout) v.findViewById(R.id.layoutDrawer);
		listViewDrawer = (ListView) v.findViewById(R.id.listViewDrawer);
		

		// flipAdapter = new F lipViewAdapter();
		// vf.onInterceptTouchEvent(MotionEvent ev);
		listViewDrawer.setAdapter(leftMenuAdapter);

		
	}

*/}
