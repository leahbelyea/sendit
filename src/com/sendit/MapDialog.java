package com.sendit;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sendit.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.EditText;

public class MapDialog extends DialogFragment implements ConnectionCallbacks, OnConnectionFailedListener {

	Location selected_location = null;
	EditText et_location;
	GoogleApiClient mGoogleApiClient;
	LatLng userLocation = new LatLng(45.457083, -66.316777);
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
        et_location = (EditText)getActivity().findViewById(R.id.edit_route_et_location);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
	    // Get the layout inflater
	    LayoutInflater inflater = getActivity().getLayoutInflater();
	    
	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout
	    final View view = inflater.inflate(R.layout.map_selector, null);
	    builder.setView(view);
        builder.setTitle("Tap and hold to select a location");
        
        // Get map and set listener
		MapFragment mapFragment = ((MapFragment) getFragmentManager().findFragmentById(R.id.map_selector_mf_map));
		final GoogleMap map = mapFragment.getMap();
		map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
		map.getUiSettings().setRotateGesturesEnabled(false);
		if (!et_location.getText().toString().equals("")) {
			selected_location = Route.stringToLocation(et_location.getText().toString());
			LatLng location_coords = new LatLng(selected_location.getLatitude(), selected_location.getLongitude());
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(location_coords , 10));
			map.addMarker(new MarkerOptions()
		        .position(location_coords)
		        .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker)));
		}
		else {
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 10));
		}
		
		map.setOnMapLongClickListener(new OnMapLongClickListener() { 
			
			@Override
			public void onMapLongClick(LatLng point) {
				selected_location = Route.stringToLocation(String.valueOf(point.latitude) + ", " + String.valueOf(point.longitude));
				map.clear();
                map.addMarker(new MarkerOptions()
			        .position(new LatLng(point.latitude, point.longitude))
			        .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker)));
                Vibrator vb = (Vibrator)view.getContext().getSystemService(Context.VIBRATOR_SERVICE);
                vb.vibrate(200);
                view.playSoundEffect(SoundEffectConstants.CLICK);
			}
        });
        
	    builder.setPositiveButton("Use this Location", new DialogInterface.OnClickListener() {
	    	@Override
	    	public void onClick(DialogInterface dialog, int id) {
                String location_text = Route.locationToString(selected_location);
                et_location.setText(location_text);
	    	}
	    });
        
		return builder.create();
	}
	
	@Override
	public void onDestroyView() {
		Fragment fragment = (getFragmentManager().findFragmentById(R.id.map_selector_mf_map));
		FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
		ft.remove(fragment);
		ft.commit();
		super.onDestroyView();
	}

	@Override
	public void onConnected(Bundle connectionHint) {
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
        	userLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        }
	}

	@Override
	public void onConnectionSuspended(int cause) {
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
	}

}
