package com.smarttaxi.client;

import java.util.ArrayList;
import java.util.List;

import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.smarttaxi.client.R;
import com.smart.taxi.activities.BaseActivity;
import com.smart.taxi.activities.SplashActivity;
import com.smart.taxi.components.renderers.CardRenderer;
import com.smart.taxi.components.renderers.TripHistoryRenderer;
import com.smart.taxi.entities.Card;
import com.smart.taxi.entities.CustomHttpException;
import com.smart.taxi.entities.CustomHttpResponse;
import com.smart.taxi.entities.Journey;
import com.smart.taxi.fragments.BaseFragment;
import com.smart.taxi.interfaces.HttpResponseListener;
import com.smart.taxi.utils.NetworkAvailability;

public class ListCardsActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_cards);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new CardsListFragment()).commit();
		}
	}
	
	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class CardsListFragment extends ListFragment
	{
		View rootView;
		private Button btnAddCard;

		
		public CardsListFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			rootView = inflater.inflate(R.layout.fragment_list_cards, container, false);
			initUI();
			loadCards();
			return rootView;
		}
		
		private void initUI() {
			 btnAddCard = (Button) rootView.findViewById(R.id.btnAddAnotherCard);
			 btnAddCard.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Log.e("in", "click");
				}
				 
			 });
		}

		@Override
		public void onResume()
		{
			super.onResume();
			if(rootView != null)
				loadCards();
		}

		private void loadCards() {
			if(SplashActivity.isLoggedIn() && SplashActivity.loggedInUser.hasCards())
			{
				ListAdapter cards = new CardsAdapter(getActivity(), SplashActivity.loggedInUser.getUserCards());
				setListAdapter(cards);
			}else{
				
			}
			
		}
		
		
		private static class CardsAdapter extends ArrayAdapter<Card> {

	        /**
	         * @param demos An array containing the details of the demos to be displayed.
	         */
	        public CardsAdapter(Context context, List<Card> cards) {
	        	super(context, R.id.txtCardNumber);
	        	this.addAll(cards);
	            //super(context, R.layout.feature, R.id.title, labels);
	        }
	        
	        @Override
	        public View getView(int position, View convertView, ViewGroup parent) {
	        	CardRenderer renderer;
	            if (convertView instanceof CardRenderer) {
	            	renderer = (CardRenderer) convertView;
	            } else {
	            	renderer = new CardRenderer(getContext());
	            }
	            Card card= getItem(position);
	            renderer.setData(card);
	            

	            return renderer;
	        }
	        
	    }

		
	}

}
