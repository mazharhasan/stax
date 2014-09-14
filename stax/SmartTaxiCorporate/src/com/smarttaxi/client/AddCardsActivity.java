package com.smarttaxi.client;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ch.boye.httpclientandroidlib.message.BasicNameValuePair;

import com.google.android.gms.internal.cc;
import com.google.gson.JsonObject;
import com.smarttaxi.client.R;
import com.smart.taxi.activities.BaseActivity;
import com.smart.taxi.activities.CustomHttpClass;
import com.smart.taxi.activities.SplashActivity;
import com.smart.taxi.components.CFEditText;
import com.smart.taxi.constants.APIConstants;
import com.smart.taxi.entities.Card;
import com.smart.taxi.entities.CustomHttpException;
import com.smart.taxi.entities.CustomHttpResponse;
import com.smart.taxi.fragments.BaseFragment;
import com.smart.taxi.helpers.LoaderHelper;
import com.smart.taxi.preferences.PreferencesHandler;
import com.smart.taxi.utils.CommonUtilities;
import com.smart.taxi.utils.CreditCardValidator;
import com.smart.taxi.utils.Utils;
import com.smarttaxi.watcher.CreditCardWatcher;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.os.Build;

public class AddCardsActivity extends BaseActivity {

	public static final int CARD_ADD_REQUEST = 999;
	public static final int CARD_RESULT_SUCCESS = 0;
	private boolean isFirstCard = false;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_cards);

		Bundle extras = getIntent().getExtras();
		isFirstCard = (extras != null && extras.get(APIConstants.IS_FIRST_CARD) != null)?true:false;
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
			.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}
	
	@Override
	public void onBackPressed()
	{
		if(!isFirstCard)
		{
			setResult(-1);
			super.onBackPressed();
		}
	}


	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends BaseFragment {

		CFEditText txtCardTitle;
		CFEditText txtCardNumber;
		CFEditText txtCardMonth;
		CFEditText txtCardYear;
		CFEditText txtCardCCV;
		Button btnAddCard;
		private Card newCard;
		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			rootView = inflater.inflate(R.layout.fragment_add_cards,
					container, false);
			initUI();
			return rootView;
		}

		private void initUI() {
			txtCardTitle = (CFEditText) rootView.findViewById(R.id.txtCardTitle);
			txtCardNumber = (CFEditText) rootView.findViewById(R.id.txtCardNumber);
			txtCardNumber.addTextChangedListener(new CreditCardWatcher());
			txtCardMonth = (CFEditText) rootView.findViewById(R.id.txtCardMonth);
			txtCardYear = (CFEditText) rootView.findViewById(R.id.txtCardYear);
			txtCardCCV = (CFEditText) rootView.findViewById(R.id.txtCCV);
			btnAddCard = (Button) rootView.findViewById(R.id.btnAddCard);
			btnAddCard.setOnClickListener(this);
		}

		public void onClick(View v) {
			if(isValidForm())
			{
				String ccnum = txtCardNumber.getText().toString().replace(" ", "");
				String cardType = "Visa";
				String expiry = txtCardMonth.getText().toString().concat(txtCardYear.getText().toString());
				try{
					cardType = CreditCardValidator.getCardName(CreditCardValidator.getCardID(ccnum));
				}catch(Exception ex)
				{
					return;
				}
				newCard = new Card();
				newCard.setCardTitle(txtCardTitle.getText().toString());
				newCard.setCardNumber(ccnum);
				newCard.setCardHolderId(SplashActivity.loggedInUser.getId());
				newCard.setCardExpiry(expiry);
				newCard.setCardCCV(txtCardCCV.getText().toString());
				//user_id,card_type,card_title,card_number,card_ccv,card_expiry
				List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
				params.add(new BasicNameValuePair("user_id", newCard.getCardHolderId()));
				params.add(new BasicNameValuePair("card_type", cardType));
				params.add(new BasicNameValuePair("card_title", newCard.getCardTitle()));
				params.add(new BasicNameValuePair("card_number", newCard.getCardNumber()));
				params.add(new BasicNameValuePair("card_expiry", newCard.getCardExpiry()));
				params.add(new BasicNameValuePair("card_ccv", newCard.getCardCCV()));
				btnAddCard.setOnClickListener(null);
				LoaderHelper.showLoader(getActivity(), "Validating card...", "");
				CommonUtilities.hideSoftKeyboard(getActivity());
				CustomHttpClass.runPostService(this, APIConstants.METHOD_POST_ADD_CARD, params, true, false);
				Log.e("Validation", "valid card");
			}
		}

		@SuppressLint("SimpleDateFormat")
		private boolean isValidForm() {

			CFEditText[] controls = {txtCardTitle, txtCardNumber, txtCardMonth, txtCardYear, txtCardCCV};
			for(int i = 0; i < controls.length; i++)
			{
				if(Utils.isEmptyOrNull(controls[i].getText().toString()))
				{
					controls[i].setError("This field can not be empty");
					controls[i].requestFocus();
					return false;
				}
			}

			String ccnum = txtCardNumber.getText().toString().replace(" ", "");
			//if(ccnum.length() < 13 || ccnum.length() > 16)

			SimpleDateFormat ft = new SimpleDateFormat("yy");
			int currentYear = Integer.valueOf(ft.format(new Date()));
			SimpleDateFormat ftm = new SimpleDateFormat("MM");
			int currentMonth = Integer.valueOf(ftm.format(new Date()));
			//Log.e("Card type",CreditCardValidator.getCardName(CreditCardValidator.getCardID(ccnum)));

			if(ccnum.length() < 13)
			{
				txtCardNumber.setError("Please provide your credit card number");
				txtCardNumber.requestFocus();
				return false;
			}else if(Integer.valueOf(txtCardMonth.getText().toString()) < 0 
					||
					Integer.valueOf(txtCardMonth.getText().toString()) > 12
					)
			{

				txtCardMonth.setError("Invalid value in month.");
				txtCardMonth.requestFocus();
				return false;
			}else if(Integer.valueOf(txtCardYear.getText().toString()) < currentYear)
			{
				txtCardYear.setError("Invalid value in year.");
				txtCardYear.requestFocus();
				return false;
			}else if(txtCardCCV.getText().length() < 3
					||
					Integer.valueOf(txtCardCCV.getText().toString()) < 0
					||
					Integer.valueOf(txtCardCCV.getText().toString()) > 999
					)
			{
				txtCardCCV.setError("Invalid CCV value.");
				txtCardCCV.requestFocus();
				return false;
			}
			if(ccnum.length() >= 13 && ccnum.length() <= 16){
				try{
					if(!CreditCardValidator.validCC(ccnum))
					{
						txtCardNumber.setError("Please provide your credit card number");
						txtCardNumber.requestFocus();
						return false;
					}else{
						return true;
					}
				}catch(Exception ex)
				{
					txtCardNumber.setError("Please provide your credit card number");
					txtCardNumber.requestFocus();
					return false;
				}
			}else{
				return false;
			}

		}
		
		@Override
		public void onResponse(CustomHttpResponse object) {
			btnAddCard.setOnClickListener(this);
			LoaderHelper.hideLoaderSafe();
			if(object.getMethodName() == APIConstants.METHOD_POST_ADD_CARD)
			{
				if(object.getStatusCode() == 0)
				{//{"UserCardInformation":{"user_id":"440","card_type":"Visa","card_title":"syed qadri","card_number":"45**********2683","card_expiry":"0915","card_ccv":"336","first_name":"","last_name":"","token":"7318552101012683"}
					if(newCard != null)
					{
						JsonObject cardsJson = CustomHttpClass.getJsonObjectFromBody(object.getRawJson(), "UserCardInformation");
						if(cardsJson != null && cardsJson.has("token"))
						{
							String token = cardsJson.get("token").getAsString();
							if(SplashActivity.loggedInUser.hasCards())
							{
								for(int i = 0; i < SplashActivity.loggedInUser.getUserCards().size(); i++)
								{
									if(SplashActivity.loggedInUser.getUserCards().get(i).getCardToken().equals(token))
									{
										Toast.makeText(getActivity(), "Card already exists.", Toast.LENGTH_LONG).show();
										getActivity().finish();
										return;
									}
								}
							}
							newCard.setCardToken(token);
							newCard.setCardNumber(cardsJson.get("card_number").getAsString());
							SplashActivity.loggedInUser.getUserCards().add(newCard);
							new PreferencesHandler(getActivity()).setUserLoggedIn();
							getActivity().setResult(CARD_RESULT_SUCCESS);
							getActivity().finish();
						}
					}
				}else if(object.getStatusCode() == 1084)
				{
					CommonUtilities.displayAlert(getActivity(), "The card information you have entered is not valid,  please try again", "", "Retry", "", false);
				}
			}
		}
		
		@Override
		public void onException(CustomHttpException exception) 
		{
			CommonUtilities.displayAlert(getActivity(), "The card information you have entered is not valid,  please try again", "", "Retry", "", false);
		}

	}
}
