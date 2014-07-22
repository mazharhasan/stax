package com.smart.taxi.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.smart.taxi.entities.CustomHttpException;
import com.smart.taxi.entities.CustomHttpResponse;
import com.smart.taxi.interfaces.HttpResponseListener;

public class BaseFragment extends Fragment implements OnClickListener,
		HttpResponseListener {
	protected View rootView;
	public BaseFragment() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		/*View rootView = inflater.inflate(R.layout.activity_testing, container, false);
		//list = (ListView)findViewById(R.id.list);
		String[] labels = {"Welcome","To","A", "List", "Item"};
		ListAdapter data = new CustomArrayAdapter(getActivity(), labels);
		setListAdapter(data);*/
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onResponse(CustomHttpResponse object) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onException(CustomHttpException exception) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

	}

}
