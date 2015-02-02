package com.sendit;

import java.io.File;
import java.util.Locale;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sendit.R;
import android.net.Uri;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class ViewRoute extends Activity {
	
	String previousActivity;
	Route route;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);
		setContentView(R.layout.activity_view_route);

		// Get bundled info
		route = (Route)getIntent().getExtras().get("route");
		previousActivity = getIntent().getExtras().getString("from");
		
		// Get views to populate
		final ScrollView sv_main = (ScrollView)findViewById(R.id.view_route_sv_main);
		LinearLayout ll_main = (LinearLayout)findViewById(R.id.view_route_ll_main);
		LinearLayout ll_name = (LinearLayout)findViewById(R.id.view_route_ll_name);
		TextView tv_name = (TextView)findViewById(R.id.view_route_tv_name);
		TextView tv_grade = (TextView)findViewById(R.id.view_route_tv_grade);
		RelativeLayout rl_photo = (RelativeLayout)findViewById(R.id.view_route_rl_photo);
		ImageView iv_photo = (ImageView)findViewById(R.id.view_route_iv_photo);
		RelativeLayout rl_location_container = (RelativeLayout)findViewById(R.id.view_route_rl_location_container);
		TextView tv_location_icon = (TextView)findViewById(R.id.view_route_tv_location_icon);
		LinearLayout ll_location_details = (LinearLayout)findViewById(R.id.view_route_ll_location_details);
		TextView tv_wall = (TextView)findViewById(R.id.view_route_tv_wall);
		TextView tv_crag = (TextView)findViewById(R.id.view_route_tv_crag);
		RelativeLayout rl_map_container = (RelativeLayout)findViewById(R.id.view_route_rl_map_container);
		MapFragment mf_map = ((MapFragment) getFragmentManager().findFragmentById(R.id.view_route_mf_map));
		ImageView iv_map = (ImageView)findViewById(R.id.view_route_iv_map);
		TextView tv_open_map = (TextView)findViewById(R.id.view_route_tv_open_map);
		LinearLayout ll_date = (LinearLayout)findViewById(R.id.view_route_ll_date);
		TextView tv_date_icon = (TextView)findViewById(R.id.view_route_tv_date_icon);
		TextView tv_date = (TextView)findViewById(R.id.view_route_tv_date);
		LinearLayout ll_send_type = (LinearLayout)findViewById(R.id.view_route_ll_send_type);
		TextView tv_mountain_icon = (TextView)findViewById(R.id.view_route_tv_mountain_icon);
		TextView tv_send_type = (TextView)findViewById(R.id.view_route_tv_send_type);
		LinearLayout ll_notes = (LinearLayout)findViewById(R.id.view_route_ll_notes);
		TextView tv_notes_icon = (TextView)findViewById(R.id.view_route_tv_notes_icon);
		TextView tv_notes = (TextView)findViewById(R.id.view_route_tv_notes);
		
		// Set fonts
		Typeface belligerent = FontLoader.getFont("belligerent", this);
		Typeface fontawesome = FontLoader.getFont("fontawesome", this);
		Typeface sendit = FontLoader.getFont("sendit", this);
		
		tv_location_icon.setTypeface(fontawesome);
		tv_date_icon.setTypeface(fontawesome);
		tv_notes_icon.setTypeface(fontawesome);
		tv_mountain_icon.setTypeface(sendit);
		tv_name.setTypeface(belligerent);

		// Name & grade
		tv_name.setText(route.getName());
		if (!route.getGrade().equals("")) {
			tv_grade.setText(route.getGrade());
		}
		else {
			ll_name.removeView(tv_grade);
		}
		
		// Photo
        if (!route.getPhotoPath().equals("")) {
	    	File file = new File(route.getPhotoPath());
	        Bitmap bitmap = BitmapDecoder.decodeSampledBitmapFromFile(file.getAbsolutePath(), 1000, 700);
	        bitmap = BitmapDecoder.correctImageRotation(bitmap, route.getPhotoPath());
	    	iv_photo.setImageBitmap(bitmap);
	    	rl_photo.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_VIEW);
					intent.setDataAndType(Uri.parse("file://" + route.getPhotoPath()), "image/*");
					System.out.println(Uri.parse(route.getPhotoPath()));
					System.out.println(route.getPhotoPath());
					startActivity(intent);
				}
	    		
	    	});
        }
        else {
        	ll_main.removeView(rl_photo);
        }	
        
        // Rating
        LinearLayout ll_rating = (LinearLayout)findViewById(R.id.view_route_ll_rating);
		for(int i=0; i<((ViewGroup)ll_rating).getChildCount(); i++) {
		    TextView nextChild = (TextView)((ViewGroup)ll_rating).getChildAt(i);
			nextChild.setTypeface(fontawesome);
		    if (i+1 <= route.getRating()){
				nextChild.setText(R.string.icon_star);
		    }
		}		
		
		// Location
		if (route.getWall().equals("") && route.getCrag().equals("") && route.getLocation() == null) {
			ll_main.removeView(rl_location_container);
		}
		else {
			if (!route.getWall().equals("")) {
				tv_wall.setText(route.getWall());
			}
			else {
				ll_location_details.removeView(tv_wall);
			}
			if (!route.getCrag().equals("")) {
				tv_crag.setText(route.getCrag());
			}
			else {
				ll_location_details.removeView(tv_crag);
			}
			if (route.getLocation() != null) {
				LocationLoader ll = new LocationLoader(ll_location_details, this);
				ll.execute(route.getLocation());

				GoogleMap map = mf_map.getMap();
				map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
				LatLng location = new LatLng(route.getLocation().getLatitude(), route.getLocation().getLongitude());
				
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(location , 10));
				map.addMarker(new MarkerOptions()
				        .position(location)
				        .title(route.getName())
				        .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker)));
				map.getUiSettings().setTiltGesturesEnabled(false);
				map.getUiSettings().setRotateGesturesEnabled(false);
			}
			else {
				rl_location_container.removeView(rl_map_container);
				rl_location_container.removeView(tv_open_map);
			}
		}
		
		iv_map.setOnTouchListener(new View.OnTouchListener() {

		    @Override
		    public boolean onTouch(View v, MotionEvent event) {
		        int action = event.getAction();
		        switch (action) {
		           case MotionEvent.ACTION_DOWN:
		                // Disallow ScrollView to intercept touch events.
		                sv_main.requestDisallowInterceptTouchEvent(true);
		                // Disable touch on transparent view
		                return false;

		           case MotionEvent.ACTION_UP:
		                // Allow ScrollView to intercept touch events.
		                sv_main.requestDisallowInterceptTouchEvent(false);
		                return true;

		           case MotionEvent.ACTION_MOVE:
		                sv_main.requestDisallowInterceptTouchEvent(true);
		                return false;

		           default: 
		                return true;
		        }   
		    }
		});
		
		// Set click listener for map - open in Google Map
		tv_open_map.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				String uri = String.format(Locale.ENGLISH, "geo:%f,%f?q=%f,%f(%s)", route.getLocation().getLatitude(), route.getLocation().getLongitude(),route.getLocation().getLatitude(), route.getLocation().getLongitude(), route.getName());
				Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
				startActivity(i);
			}
		});
		
		// Date, send type & notes	
		if (route.getDate() != 0) {
			tv_date.setText(Route.dateToString(route.getDate(), "MMMM d, yyyy"));
		}
		else {
			ll_main.removeView(ll_date);
		}
		
		if (!route.getSendType().equals("")) {
			tv_send_type.setText(route.getSendType());
		}
		else {
			ll_main.removeView(ll_send_type);
		}
		
		if (!route.getNotes().equals("")) {
			tv_notes.setText(route.getNotes());
		}
		else {
			((LinearLayout)tv_notes.getParent()).removeView(tv_notes);
			ll_main.removeView(ll_notes);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_route, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.view_route_menu_item_edit:
				Intent i_edit = new Intent(this, EditRoute.class);
				Bundle b = new Bundle();
				b.putParcelable("route", route);
				i_edit.putExtras(b);
				i_edit.putExtra("action", "edit");
				startActivity(i_edit);
	            return true;
	        case R.id.view_route_menu_item_delete:
	        	confirmDeletion();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	private void confirmDeletion() {
		AlertDialog alertDialog = new AlertDialog.Builder(ViewRoute.this).create();
	    alertDialog.setTitle("Are you sure you want to delete this route?");
	    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Delete", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
				new RoutesDbHelper(ViewRoute.this).deleteRoute(route);
				Toast.makeText(getApplicationContext(), "This route has been successfully removed!", Toast.LENGTH_LONG).show();
				Intent i_delete = new Intent(ViewRoute.this, ViewRoutes.class);
				startActivity(i_delete);
	        }
	    });
	    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	        	//Do nothing
	        }
	    });
	    alertDialog.show();	
	}
	
	@Override
	public void onBackPressed()
	{
	    if (previousActivity.equals("EditRoute")) {
	    	Intent i = new Intent(this, ViewRoutes.class);
	    	startActivity(i);
	    }
	    else {
	    	super.onBackPressed();
	    }
	}
}
