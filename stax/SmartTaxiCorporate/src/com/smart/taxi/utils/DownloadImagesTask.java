package com.smart.taxi.utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;

public class DownloadImagesTask extends AsyncTask<ImageView, Void, Drawable> {

	ImageView imageView = null;

	@Override
	protected Drawable doInBackground(ImageView... imageViews) {
	    this.imageView = imageViews[0];
	    return download_Image((String)imageView.getTag());
	}

	@Override
	protected void onPostExecute(Drawable result) {
	    imageView.setImageDrawable(result);
	}


	private Drawable download_Image(String url) {
		URL thumb_u;
		Drawable thumb_d;
		try {
			thumb_u = new URL(url);
			thumb_d = Drawable.createFromStream(thumb_u.openStream(), "src");
			return thumb_d;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}