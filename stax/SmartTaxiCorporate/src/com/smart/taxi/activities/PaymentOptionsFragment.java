package com.smart.taxi.activities;

import java.util.List;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.smart.taxi.components.renderers.CardRenderer;
import com.smart.taxi.entities.Card;
import com.smarttaxi.client.AddCardsActivity;
import com.smarttaxi.client.R;

public class PaymentOptionsFragment extends ListFragment implements OnClickListener
{
	public static String TAG = "PaymentOptions";
	View rootView;
	private Button btnAddNewCard;
	
	public PaymentOptionsFragment() {
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
		// TODO Auto-generated method stub
		btnAddNewCard = (Button)rootView.findViewById(R.id.btnAddAnotherCard);
		btnAddNewCard.setOnClickListener(this);
		
	}

	private void loadCards() {
		if(SplashActivity.isLoggedIn() && SplashActivity.loggedInUser.hasCards())
		{
			CardsAdapter cards = new CardsAdapter(getActivity(), SplashActivity.loggedInUser.getUserCards());
			cards.activity = getActivity();
			setListAdapter(cards);
		}else{
			
		}
		
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		//TripHistoryRenderer renderer = (TripHistoryRenderer) getListAdapter().getItem(position);
		//Log.e("Click message", getListAdapter().getItem(position).toString());
        //startActivity(new Intent(this, demo.activityClass));
    }
	
	public class CardsAdapter extends ArrayAdapter<Card> {

        public Activity activity;

		/**
         * @param demos An array containing the details of the demos to be displayed.
         */
        public CardsAdapter(Context context, List<Card> cards) {
        	super(context, R.id.txtPassengerName);
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
            renderer.parentAdapter = this;
            Card card= getItem(position);
            renderer.setData(card);
            return renderer;
        }
        
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		startAddCardsActivity();
	}
	
	private void startAddCardsActivity() {
		startActivityForResult(new Intent(getActivity(), AddCardsActivity.class),  999);
		getActivity().overridePendingTransition(R.anim.slide_left, R.anim.slide_left);
	}
	
	@Override
    public void onActivityResult(int requestCode,
            int resultCode, Intent data)
	{
		if(requestCode == AddCardsActivity.CARD_ADD_REQUEST)
		{
			if(resultCode == AddCardsActivity.CARD_RESULT_SUCCESS)
			{
				loadCards();
			}else{
				Toast.makeText(getActivity(), "Add card cancelled", Toast.LENGTH_LONG).show();
			}
		}
	}
}

