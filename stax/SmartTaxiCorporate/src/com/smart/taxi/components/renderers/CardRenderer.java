package com.smart.taxi.components.renderers;

import com.smart.taxi.components.CFTextView;
import com.smart.taxi.entities.Card;
import com.smarttaxi.client.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class CardRenderer extends FrameLayout {
	
	private Card card;
	CFTextView txtCardNumber;
	public CardRenderer(Context context) {
		super(context);
		LayoutInflater layoutInflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.listitem_cards, this);
	}
	
	public synchronized void setData(Card data)
	{
		((CFTextView)findViewById(R.id.txtCardNumber)).setText(data.getCardNumber());
		if(data.getCardNumber().startsWith("4"))
		{
			((ImageView)findViewById(R.id.imgCardType)).setImageResource(R.drawable.visa_curved_32px);
		}else if(data.getCardNumber().startsWith("5")){
			((ImageView)findViewById(R.id.imgCardType)).setImageResource(R.drawable.mastercard_curved_32px);
		}else {
			((ImageView)findViewById(R.id.imgCardType)).setImageResource(R.drawable.american_express_curved_32px);
		}
	}
	
	public Card getCardData()
	{
		return card;
	}

}
