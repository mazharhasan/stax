package com.smart.taxi;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

import com.smart.taxi.activities.SplashActivity;
import com.smart.taxi.components.CFTextView;
import com.smart.taxi.utils.DownloadImagesTask;

public class BarcodeActivity extends Activity {

	private CFTextView labelUserNameBarcode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().hide();
		setContentView(R.layout.activity_barcode);
		ImageView barcodeImage = (ImageView)findViewById(R.id.imgBarcode);
		labelUserNameBarcode = (CFTextView)findViewById(R.id.labelUserNameBarcode);
		labelUserNameBarcode.setText(SplashActivity.loggedInUser.getFullName());
		try {
				String url = "https://chart.googleapis.com/chart?chs=300x300&cht=qr&chl=id-" + SplashActivity.loggedInUser.getId() + "&choe=UTF-8";
				barcodeImage.setTag(url);
				new DownloadImagesTask().execute(barcodeImage);
				//Drawable thumb_d = Drawable.createFromStream(thumb_u.openStream(), "src");
	    		//barcodeImage.setImageDrawable(thumb_d);
		}catch(Exception ex)
		{
			
		}
	}
	
	@Override
	public void onBackPressed()
	{
		finish();
	}

}
