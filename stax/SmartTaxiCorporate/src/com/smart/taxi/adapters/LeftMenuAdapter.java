package com.smart.taxi.adapters;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.smart.taxi.R;
import com.smart.taxi.components.LeftSlideMenuItem;


public class LeftMenuAdapter extends BaseAdapter  {

	private Context context;
	public static ListView listViewDrawer;
	private LayoutInflater mInflater;
	public LeftMenuAdapter(Context ctx) {
		super();

			
		this.context = ctx;
		mInflater =LayoutInflater.from(ctx);
	}

	public LeftMenuAdapter(Context context, AttributeSet attrs) {
		super();
		this.context = context;

	}

	@Override
	public int getCount() {
		return 7;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LeftSlideMenuItem view;
		if (convertView != null) {
			view = (LeftSlideMenuItem) convertView;
		} else {
			view = new LeftSlideMenuItem(context);
		}

		switch (position) {
		case 0:
			view.setTitle("Find a ride");
			view.setIconResource(R.drawable.side_menu_my_location);	
			break;
		case 1:
			view.setTitle("Upcoming trips");
			view.setIconResource(R.drawable.side_menu_trip_upcoming_icon);
			break;
		case 2:
			view.setTitle("Trip history");
			view.setIconResource(R.drawable.side_menu_trip_upcoming_icon);
			break;
		case 3:
			view.setTitle("Profile");
			view.setIconResource(R.drawable.side_menu_profile_icon);
			
			break;
		case 4:
			view.setTitle("Change tip");
			view.setIconResource(R.drawable.side_menu_leads_icon);
			break;
		case 5:
			view.setTitle("Barcode");
			view.setIconResource(R.drawable.side_menu_profile);
			break;
		case 6:
		/*	view.setTitle("Change Taxi");
			view.setIconResource(R.drawable.side_menu_change_taxi);*/
			view.setTitle("Logout");
			view.setIconResource(R.drawable.side_menu_logout);
			break;
		case 7:
			
			break;
		default:
			break;
		}
		
		view.setId(position);
		//view.setOnClickListener(this);
		
		return view;
	}

	

}
