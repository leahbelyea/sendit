package com.sendit;

import java.util.HashMap;
import com.sendit.FilterDialog.OnFilterBuiltListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sendit.R;
import android.location.Location;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

public class ViewRoutes extends Activity implements LoaderManager.LoaderCallbacks<Cursor>, OnFilterBuiltListener {
	
	SQLiteDatabase db;
	ViewSwitcher switcher = null;
	RouteListAdapter adapter;
	String filter = null;
	String[] filterArgs = null;
	int boundsCounter;
	LatLngBounds.Builder bounds;
	int maxZoom = 15;
	Boolean mapDrawn = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);
		LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflator.inflate(R.layout.actionbar, null);
		actionBar.setCustomView(view);

		setContentView(R.layout.activity_view_routes);
		
		// Action Bar Tabs
	    final TextView tv_list = (TextView)findViewById(R.id.actionbar_tv_list);
	    final TextView tv_map = (TextView)findViewById(R.id.actionbar_tv_map);
	    tv_list.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				switcher.setDisplayedChild(0);
				tv_map.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_not_selected));
				tv_list.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_selected));
			}
	    	
	    });
	    tv_map.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				switcher.setDisplayedChild(1);
				tv_list.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_not_selected));
				tv_map.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_selected));
			}
	    	
	    });
		
	    
	    // View Switcher
		switcher = (ViewSwitcher)findViewById(R.id.view_routes_vs);
	
		// Adapter
		ListView lv_routes = (ListView)findViewById(R.id.view_routes_lv_routes);
		adapter = new RouteListAdapter(this, null, false);
		lv_routes.setAdapter(adapter);
		getLoaderManager().initLoader(0, null, this);
		
		// List On Click Listener
		lv_routes.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				adapter = (RouteListAdapter)parent.getAdapter();
				Route route = adapter.getRoute(position);
				Intent i = new Intent(view.getContext(), ViewRoute.class);
				Bundle b = new Bundle();
				b.putString("from", "ViewRoutes");
				b.putParcelable("route", route);
				i.putExtras(b);
				startActivity(i);
			}
		});
	}
	
	@Override
	public void onContentChanged() {
	    super.onContentChanged();
	    // When content is added, remove default "no routes yet!" text
	    TextView tv_empty = (TextView)findViewById(R.id.view_routes_tv_empty);
	    ListView list = (ListView)findViewById(R.id.view_routes_lv_routes);
	    list.setEmptyView(tv_empty);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_routes, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.view_routes_menu_item_filter:    	
				DialogFragment newFragment = new FilterDialog();
			    newFragment.show(getFragmentManager(), "filter");
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
	public void onBackPressed()
	{
	    Intent i = new Intent(this, MainActivity.class);
	    startActivity(i);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
	    CursorLoader cursorLoader = new CursorLoader(this, RouteContentProvider.CONTENT_URI, null, filter, filterArgs, "date DESC");
	    return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if(adapter!=null && cursor!=null) {
			adapter.swapCursor(cursor);
			GoogleMap map = populateGoogleMap(cursor);
			if (mapDrawn) {
				setMapBoundsZoom(map);
			}
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		if(adapter!=null) {
			adapter.swapCursor(null);
		}
	}

	@Override
	public void onFilterBuilt(String filter, String[] filterArgs) {
    	this.filter = filter;
    	this.filterArgs = filterArgs;
		getLoaderManager().restartLoader(0, null, this);
	}

	private GoogleMap populateGoogleMap(final Cursor cursor) {
		boundsCounter = 0;
		MapFragment mapFragment = ((MapFragment) getFragmentManager().findFragmentById(R.id.view_routes_mf_map));
		final GoogleMap map = mapFragment.getMap();
		map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
		map.getUiSettings().setRotateGesturesEnabled(false);
		map.clear();
		bounds = new LatLngBounds.Builder();
		final HashMap<Marker, Integer> markerList = new HashMap<Marker, Integer>();
		if (cursor.moveToFirst()) {
			while (cursor.isAfterLast() == false) {
			    Location location = (Route.stringToLocation(cursor.getString(6)));
			    if (location != null) {
				    LatLng location_coords = new LatLng(location.getLatitude(), location.getLongitude());
				    String name = cursor.getString(1);
				    Marker marker = map.addMarker(new MarkerOptions()
				        .position(location_coords)
				        .title(name)
				        .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker))
				    );
					bounds.include(new LatLng(location.getLatitude(), location.getLongitude()));
					boundsCounter++;
					markerList.put(marker, cursor.getPosition());
			    }
			    cursor.moveToNext();
			}
		}
		
		map.setOnCameraChangeListener(new OnCameraChangeListener() {

		    @Override
		    public void onCameraChange(CameraPosition arg0) {
		    	setMapBoundsZoom(map);
		        // Remove listener to prevent position reset on camera move.
		        map.setOnCameraChangeListener(null);
		        mapDrawn = true;
		    }
		});
			
		map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
	        @Override
	        public void onInfoWindowClick(Marker marker) {
		        cursor.moveToPosition(markerList.get(marker));
		        Route route = new Route(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), Route.stringToLocation(cursor.getString(6)), cursor.getLong(7), cursor.getString(8), Integer.parseInt(cursor.getString(9)), cursor.getString(10));
		        Intent i = new Intent(ViewRoutes.this, ViewRoute.class);
		        Bundle b = new Bundle();
				b.putString("from", "ViewRoutes");
		        b.putParcelable("route", route);
		        i.putExtras(b);
		        startActivity(i);
	        }
		});
		return map;
	}
	
	private void setMapBoundsZoom(GoogleMap map) {
        // Move camera.
    	if (boundsCounter > 0) {
    		map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 200));
    	}
    	// If map is zoomed in too far, zoom out to a maximum value
    	int currentZoom = (int) map.getCameraPosition().zoom;
    	if (currentZoom > maxZoom) {
    		map.moveCamera(CameraUpdateFactory.zoomTo(maxZoom));
    	}
	}
}
