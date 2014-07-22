package com.smarttaxi.driver.helpers;

import java.io.InputStream;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

public class DownloadImageAsync extends AsyncTask<String, Void, Bitmap> {

	ImageView bmImage;
	Activity activity;

	public DownloadImageAsync(ImageView bmImage, Activity activity) {

		this.bmImage = bmImage;
		this.activity = activity;

	}

	protected Bitmap doInBackground(String... urls) {
		String urldisplay = urls[0];
		Bitmap mIcon11 = null;
		try {
			InputStream in = new java.net.URL(urldisplay).openStream();
			mIcon11 = BitmapFactory.decodeStream(in);
		} catch (Exception e) {
			Log.e("Error", e.getMessage());
			e.printStackTrace();
		}
		return mIcon11;
	}

	protected void onPostExecute(Bitmap result) {
		this.bmImage.setImageBitmap(result);
	}

}
