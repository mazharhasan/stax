package com.smarttaxi.driver.activities;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.smarttaxi.driver.R;
import com.smarttaxi.driver.BAL.Driver;
import com.smarttaxi.driver.constants.APIConstants;
import com.smarttaxi.driver.database.SqliteHelper;
import com.smarttaxi.driver.entities.Cab;
import com.smarttaxi.driver.entities.CustomHttpException;
import com.smarttaxi.driver.entities.CustomHttpResponse;
import com.smarttaxi.driver.entities.User;
import com.smarttaxi.driver.helpers.LoaderHelper;
import com.smarttaxi.driver.interfaces.HttpResponseListener;
import com.smarttaxi.driver.model.FactoryModel;
import com.smarttaxi.driver.model.WebServiceModel;
import com.smarttaxi.driver.preferences.PreferencesHandler;
import com.smarttaxi.driver.utils.AppToast;
import com.smarttaxi.driver.utils.Applog;
import com.smarttaxi.driver.utils.Utils;

public class Cabs extends Activity implements HttpResponseListener {
	private ArrayList<Cab> cabsList;
	private String driverID;
	private long userID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_cabs_list);
		Intent intent = this.getIntent();
		Bundle extras = intent.getExtras();
		if (extras == null || extras.containsKey("driver_id") == false)
			finish();
		driverID = extras.getString("driver_id");
		Applog.Debug("Cab - Driver ID : " + driverID);
		init();
		loadCabsList();

	}

	private void init() {
		
		try
		{
		((Button) findViewById(R.id.bttAddNewCab))
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(Cabs.this, CabForm.class);
						intent.putExtra("driver_id", driverID);
						startActivityForResult(intent, 1);
					}
				});
		}
		catch(Throwable t)
		{
			Applog.Debug("Cab - Driver ID : " + t.getMessage() );
			
		}
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {
				FactoryModel.setCabsList(null);
				loadCabsList();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void loadCabsList() {
		/**TODO: On CAB Assign, if the Cab List is need to be refreshed from server Than assign cabLst= null**/
		cabsList = FactoryModel.getCabsList();
		if (Utils.isEmptyOrNull(cabsList)) {
			LoaderHelper.showLoader(this, "Loading...", "");
			SqliteHelper sqliteHelper = new SqliteHelper(this);
			PreferencesHandler preferencesHandler = new PreferencesHandler(this);
			userID = preferencesHandler.getUserID();
			User user = sqliteHelper.getUser(userID);
			String cabId = user != null ? user.getCabID() : "";
			Applog.Debug("cabID " + cabId);
			WebServiceModel.getMyCabs(cabId, driverID, this);
		} else
			bindCab(cabsList);
	}

	@Override
	public void onResponse(CustomHttpResponse object) {
		if (object.getStatusCode() == APIConstants.SUCESS_CODE) {
			if (object.getMethodName().equalsIgnoreCase(
					APIConstants.METHOD_DRIVER_CABS)) {
				ArrayList<Cab> list = (ArrayList<Cab>) object.getResponse();
				if (list != null) {
					FactoryModel.setCabsList(list);
					bindCab(list);
				}
				LoaderHelper.hideLoader();
			} else if (object.getMethodName().equalsIgnoreCase(
					APIConstants.METHOD_ASSIGN_CAB_TO_DRIVER)) {
				Cab cab = (Cab) object.getResponse();
				LoaderHelper.hideLoader();
				if (cab == null)
					AppToast.ShowToast(this, "An error occured.");
				else {
					AppToast.ShowToast(this, "Cab assigned successfully.");
					SqliteHelper sqliteHelper = new SqliteHelper(this);
					sqliteHelper.updateDriverInfo(cab.getId(), userID);
					resetCabsList(cab);
					loadCabsList();
				}
			}
		}
	}

	private void resetCabsList(Cab cab) {
		if(cab == null) return;
		
		if(!Utils.isEmptyOrNull(FactoryModel.getCabsList())){
			ArrayList<Cab> list = FactoryModel.getCabsList();
			for (Cab _cab : list) {
				_cab.isActive(cab.getId().equalsIgnoreCase(_cab.getId()));
			}
			FactoryModel.setCabsList(list);
		}	
	}

	private void bindCab(ArrayList<Cab> list) {
		((ListView) findViewById(R.id.listViewCabs))
				.setAdapter(new cabsAdapter(this, list, R.layout.cell_cab_item));
	}

	@Override
	public void onException(CustomHttpException exception) {
		// TODO Auto-generated method stub

	}

	private class cabsAdapter extends ArrayAdapter<Cab> {
		private ArrayList<Cab> list;
		private Activity context;
		private int layoutId;

		public cabsAdapter(Activity context, ArrayList<Cab> list, int layoutId) {
			super(context, R.id.txtName, list);

			this.layoutId = layoutId;
			this.list = list;
			this.context = context;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			viewHolder holder = null;
			Cab cab = getItem(position);
			if (row == null) {
				LayoutInflater inflater = (context).getLayoutInflater();
				row = inflater.inflate(layoutId, parent, false);

				holder = new viewHolder();
				holder.txtName = (TextView) row.findViewById(R.id.txtName);
				holder.status = (ImageView) row.findViewById(R.id.imgStatus);
				holder.layout = (RelativeLayout) row.findViewById(R.id.layout);
				row.setTag(holder);
			} else
				holder = (viewHolder) row.getTag();
			holder.txtName.setText(cab.getCabNo());
			if (cab.isActive()) {
				holder.status.setImageDrawable(getResources().getDrawable(
						R.drawable.ic_launcher));
			}
			holder.layout.setTag(cab);
			holder.layout.setOnClickListener(onClickListner);
			return row;
		}

		@Override
		public Cab getItem(int position) {
			return list.get(position);
		}

		OnClickListener onClickListner = new OnClickListener() {
			@Override
			public void onClick(View v) {
				Cab cab = (Cab) v.getTag();
				if (cab.isActive() == false) {
					activateCab(cab);
				}
			}
		};

	}

	static class viewHolder {
		TextView txtName;
		ImageView status;
		RelativeLayout layout;
	}

	protected void activateCab(Cab cab) {
		LoaderHelper.showLoader(this, "Loading...", "");
		// assign_driver_to_cab.json
		// driver_user_id
		// cab_id
		// type = assign/add
		WebServiceModel.assignCab(cab.getId(), driverID, "assign", this);
	}
}
