package com.smart.taxi.components.renderers;

import java.util.ArrayList;
import java.util.List;

import ch.boye.httpclientandroidlib.message.BasicNameValuePair;

import com.smart.taxi.activities.CustomHttpClass;
import com.smart.taxi.activities.PaymentOptionsFragment.CardsAdapter;
import com.smart.taxi.activities.SplashActivity;
import com.smart.taxi.components.CFTextView;
import com.smart.taxi.constants.APIConstants;
import com.smart.taxi.entities.Card;
import com.smart.taxi.entities.CustomHttpException;
import com.smart.taxi.entities.CustomHttpResponse;
import com.smart.taxi.helpers.LoaderHelper;
import com.smart.taxi.interfaces.HttpResponseListener;
import com.smart.taxi.preferences.PreferencesHandler;
import com.smart.taxi.utils.CommonUtilities;
import com.smarttaxi.client.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.view.View;

public class CardRenderer extends FrameLayout implements View.OnClickListener, HttpResponseListener {
	
	private Card card;
	CFTextView txtCardNumber;
	
	public CardsAdapter parentAdapter;
	public CardRenderer(Context context) {
		super(context);
		LayoutInflater layoutInflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.listitem_cards, this);
	}
	
	public synchronized void setData(Card data)
	{
		card = data;
		((CFTextView)findViewById(R.id.txtCardNumber)).setText(data.getCardNumber());
		if(data.getCardNumber().startsWith("4"))
		{
			((ImageView)findViewById(R.id.imgCardType)).setImageResource(R.drawable.visa_curved_32px);
		}else if(data.getCardNumber().startsWith("5")){
			((ImageView)findViewById(R.id.imgCardType)).setImageResource(R.drawable.mastercard_curved_32px);
		}else {
			((ImageView)findViewById(R.id.imgCardType)).setImageResource(R.drawable.american_express_curved_32px);
		}
		((ImageButton)findViewById(R.id.btnDeleteCards)).setOnClickListener(this);
	}
	
	public Card getCardData()
	{
		return card;
	}

	@Override
	public void onClick(View v) {
		
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				getContext());
 
			// set title
			alertDialogBuilder.setTitle("Remove " + card.getCardNumber() + "?");
 
			// set dialog message
			alertDialogBuilder
				.setMessage("Are you sure you want to remove this cards? This action is not reversable.")
				.setCancelable(false)
				.setPositiveButton("Yes,  remove this card!", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						// if this button is clicked, close
						// current activity
						Log.e(card.getCardHolderId(), card.getCardToken());
						List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
						params.add(new BasicNameValuePair("user_id",card.getCardHolderId()));
						params.add(new BasicNameValuePair("card_number",card.getCardToken()));
						LoaderHelper.showLoader(parentAdapter.activity, "Deleting,  please wait...", "");
						CustomHttpClass.runPostService(CardRenderer.this, APIConstants.METHOD_POST_DELETE_CARD, params, false, false);
					}
				  })
				.setNegativeButton("Nope",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						// if this button is clicked, just close
						// the dialog box and do nothing
						dialog.cancel();
					}
				});
 
				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();
 
				// show it
				alertDialog.show();
		
		
		
	}

	@Override
	public void onResponse(CustomHttpResponse object) {
		LoaderHelper.hideLoaderSafe();
		if(object.getStatusCode() == 0)
		{
			for(int i = 0; i < SplashActivity.loggedInUser.getUserCards().size(); i++)
			{
				if(SplashActivity.loggedInUser.getUserCards().get(i).getCardToken().equals(card.getCardToken()))
				{
					SplashActivity.loggedInUser.getUserCards().remove(i);
					PreferencesHandler pf = new PreferencesHandler(getContext());
					pf.setUserLoggedIn();
					if(parentAdapter != null)
					{
						parentAdapter.remove(card);
					}
					if(!SplashActivity.loggedInUser.hasCards())
					{
						SplashActivity.splashActivity.startAddCardsActivity();
						parentAdapter.activity.finish();
					}
					break;
				}
			}
		}else{
			CommonUtilities.displayAlert(getContext(), "Unable to delete this card at the moment. Please try again.", "Failed", "Ok", "", false);
		}
	}

	@Override
	public void onException(CustomHttpException exception) {
		// TODO Auto-generated method stub
		LoaderHelper.hideLoaderSafe();
		CommonUtilities.displayAlert(getContext(), "Unable to delete this card at the moment. Please try again.", "Failed", "Ok", "", false);
	}

}
