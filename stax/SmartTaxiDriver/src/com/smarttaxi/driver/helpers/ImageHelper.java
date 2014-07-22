package com.smarttaxi.driver.helpers;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.client.methods.HttpGet;
import ch.boye.httpclientandroidlib.impl.client.DefaultHttpClient;

public class ImageHelper {

	static ImageHelper imageHelper;
//	static Context context;
	Map<String, Drawable> drawableMap = null;
	
	public static ImageHelper getInstance() {
//        ImageHelper.context = context;
        if (imageHelper == null) {

                imageHelper = new ImageHelper();
                imageHelper.drawableMap = new HashMap<String, Drawable>();
        }
        return imageHelper;
}


	public void fetchDrawableOnThread(final String urlString,
			final ImageView imageView) {
		if (drawableMap.containsKey(urlString)) {
			imageView.setImageDrawable(drawableMap.get(urlString));
		}

		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message message) {
				imageView.setImageDrawable((Drawable) message.obj);
			}
		};

		Thread thread = new Thread() {
			@Override
			public void run() {
				// TODO : set imageView to a "pending" image
				Drawable drawable = fetchDrawable(urlString);
				Message message = handler.obtainMessage(1, drawable);
				handler.sendMessage(message);
			}

		};
		thread.start();
	}

	private InputStream fetch(String urlString) throws MalformedURLException,
			IOException {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet request = new HttpGet(urlString);
		HttpResponse response = httpClient.execute(request);
		return response.getEntity().getContent();
	}

	public Drawable fetchDrawable(String urlString) {
		if (drawableMap.containsKey(urlString)) {
			return drawableMap.get(urlString);
		}

		Log.d(this.getClass().getSimpleName(), "image url:" + urlString);
		try {
			InputStream is = fetch(urlString);
			Drawable drawable = Drawable.createFromStream(is, "src");

			if (drawable != null) {
				drawableMap.put(urlString, drawable);
				Log.d(this.getClass().getSimpleName(),
						"got a thumbnail drawable: " + drawable.getBounds()
								+ ", " + drawable.getIntrinsicHeight() + ","
								+ drawable.getIntrinsicWidth() + ", "
								+ drawable.getMinimumHeight() + ","
								+ drawable.getMinimumWidth());
			} else {
				Log.w(this.getClass().getSimpleName(),
						"could not get thumbnail");
			}

			return drawable;
		} catch (MalformedURLException e) {
			Log.e(this.getClass().getSimpleName(), "fetchDrawable failed", e);
			return null;
		} catch (IOException e) {
			Log.e(this.getClass().getSimpleName(), "fetchDrawable failed", e);
			return null;
		}
	}

}
