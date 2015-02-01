package com.sendit;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.sendit.R;

public class LocationLoader extends AsyncTask<Location, Void, String[]> {
	
	LinearLayout parent;
	Context context;

	public LocationLoader(LinearLayout parent, Context context) {
		this.parent = parent;
		this.context = context;
	}
	
	@Override
	protected String[] doInBackground(Location... params) {
		Location location = params[0];
		Double latitude = location.getLatitude();
		Double longitude = location.getLongitude();
	    Geocoder geocoder = new Geocoder(context, Locale.getDefault());
	    List<Address> addresses = null;
	    try {
	        addresses = geocoder.getFromLocation(latitude, longitude, 1);
	    } catch (IOException e) {
	    }
	    if (addresses != null && !addresses.isEmpty()) {
	        return new String[] {addresses.get(0).getAdminArea(), addresses.get(0).getCountryName()};
	    }
	    return null;
	}

	protected void onPostExecute(String[] location) {

		if (location != null) {
			String state = location[0];
			String country = location[1];
			TextView tv = (TextView)parent.findViewById(R.id.view_route_tv_location);
//			TextView tv = new TextView(parent.getContext());
//			tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
//			tv.setTextSize(25);
			tv.setText(state + ", " + country);
			LayoutParams params = tv.getLayoutParams();
			params.height = LayoutParams.MATCH_PARENT;
		}
	}
	
}
