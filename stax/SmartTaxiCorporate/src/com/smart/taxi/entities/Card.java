package com.smart.taxi.entities;

import com.google.gson.JsonObject;

public class Card {
	private int cardId;
	private String cardHolderId;
	private String cardTitle;
	private String cardNumber;
	private String cardExpiry;
	private String cardCCV;
	private String cardStatus;
	private String cardAddedOn;
	private String cardToken;
	public enum CardType {
        VISA(1), MASTER(5), AMEX(10);
        private int value;

        private CardType(int value) {
                this.value = value;
        }
	};


	public int getCardId() {
		return cardId;
	}

	public void setCardId(int cardId) {
		this.cardId = cardId;
	}

	public String getCardHolderId() {
		return cardHolderId;
	}

	public void setCardHolderId(String cardHolderId) {
		this.cardHolderId = cardHolderId;
	}

	public String getCardTitle() {
		return cardTitle;
	}

	public void setCardTitle(String cardTitle) {
		this.cardTitle = cardTitle;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public String getCardExpiry() {
		return cardExpiry;
	}

	public void setCardExpiry(String cardExpiry) {
		this.cardExpiry = cardExpiry;
	}

	public String getCardCCV() {
		return cardCCV;
	}

	public void setCardCCV(String cardCCV) {
		this.cardCCV = cardCCV;
	}

	public String getCardStatus() {
		return cardStatus;
	}

	public void setCardStatus(String cardStatus) {
		this.cardStatus = cardStatus;
	}

	public String getCardAddedOn() {
		return cardAddedOn;
	}

	public void setCardAddedOn(String cardAddedOn) {
		this.cardAddedOn = cardAddedOn;
	}

	public String getCardToken() {
		return cardToken;
	}

	public void setCardToken(String cardToken) {
		this.cardToken = cardToken;
	}
	
	public static Card parseCard(JsonObject cardJson)
	{
		Card card = new Card();
		card.setCardId(Integer.parseInt(cardJson.get("id").getAsString()));
		card.setCardHolderId(cardJson.get("user_id").getAsString());
		card.setCardTitle(cardJson.get("card_title").getAsString());
		card.setCardNumber(cardJson.get("card_number").getAsString());
		card.setCardExpiry(cardJson.get("card_expiry").getAsString());
		card.setCardCCV(cardJson.get("card_ccv").getAsString());
		card.setCardStatus(cardJson.get("status").getAsString());
		card.setCardToken(cardJson.get("token").getAsString());
		card.setCardAddedOn(cardJson.get("created").getAsString());
		//{"UserCardInformation":{"user_id":"440","card_type":"Visa","card_title":"syed qadri","card_number":"45**********2683","card_expiry":"0915","card_ccv":"336","first_name":"","last_name":"","token":"7318552101012683"}
		return card;
	}
}
