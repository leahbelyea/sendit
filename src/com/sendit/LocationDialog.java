package com.sendit;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.EditText;
import com.sendit.R;

public class LocationDialog extends DialogFragment {
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Set Location");
        builder.setNegativeButton("Use Current Location", new DialogInterface.OnClickListener() {
        	public void onClick(DialogInterface dialog, int id) {
                LocationManager locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
                EditText et_location = (EditText)getActivity().findViewById(R.id.edit_route_et_location);
                Criteria c = new Criteria();
                String provider = locationManager.getBestProvider(c, false);
                Location location = locationManager.getLastKnownLocation(provider);
                String location_text = "Cannot access GPS";
                if (location != null) {
                	location_text = Route.locationToString(location);
                }
                et_location.setText(location_text);
            }
        });
        builder.setPositiveButton("Choose Location from Map", new DialogInterface.OnClickListener() {
        	public void onClick(DialogInterface dialog, int id) {
				DialogFragment newFragment = new MapDialog();
			    newFragment.show(getFragmentManager(), "map");
            }
        });
	    
	    return builder.create();
	}

}
