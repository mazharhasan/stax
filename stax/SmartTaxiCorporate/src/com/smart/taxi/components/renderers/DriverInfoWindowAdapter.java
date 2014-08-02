package com.smart.taxi.components.renderers;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;
import com.smarttaxi.client.R;
import com.smart.taxi.utils.Utils;

public class DriverInfoWindowAdapter implements InfoWindowAdapter {

	private View mWindow;
	private View mContents;

	public DriverInfoWindowAdapter(Activity activity) {
		mWindow = activity.getLayoutInflater().inflate(R.layout.layout_info_window, null);
        mContents = activity.getLayoutInflater().inflate(R.layout.layout_info_window, null);
	}

	@Override
	public View getInfoContents(Marker marker) {
		if(Utils.isEmptyOrNull(marker.getTitle()))
			return null;
		render(marker, mContents);
        return mContents;
	}

	@Override
	public View getInfoWindow(Marker marker) {
		if(Utils.isEmptyOrNull(marker.getTitle()))
			return null;
		render(marker, mWindow);
        return mWindow;
	}
	
	private void render(Marker marker, View view) {
        ((ImageView) view.findViewById(R.id.badge)).setImageResource(R.drawable.default_profile_image);

        String title = marker.getTitle();
        TextView titleUi = ((TextView) view.findViewById(R.id.title));
        if (title != null) {
            // Spannable string allows us to edit the formatting of the text.
            titleUi.setText(title);
        } else {
            titleUi.setText("");
        }

        String snippet = marker.getSnippet();
        TextView snippetUi = ((TextView) view.findViewById(R.id.snippet));
        snippetUi.setText(snippet);
    }

}
