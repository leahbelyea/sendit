package com.sendit;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import com.sendit.R;

public class RouteListAdapter extends CursorAdapter {
	
	Cursor cursor;

	public RouteListAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
		this.cursor = c;
	}
    
	@Override
	public void bindView(View view, Context context, Cursor cursor) {

		TextView tv_route_name = (TextView)view.findViewById(R.id.route_list_item_tv_route_name);
		TextView tv_wall_crag = (TextView)view.findViewById(R.id.route_list_item_tv_wall_crag);
		TextView tv_date = (TextView)view.findViewById(R.id.route_list_item_tv_date);
		ListPicture iv_picture = (ListPicture)view.findViewById(R.id.route_list_item_iv_picture);
		
		String name = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(1)));
		String wall = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(5)));
		String crag = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(4)));
		Long date = cursor.getLong(cursor.getColumnIndex(cursor.getColumnName(7)));
		String photo_path = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(2)));
		
		tv_route_name.setText(name);
		if (!wall.equals("") && !crag.equals("")){
			tv_wall_crag.setText(wall + ", " + crag);
		}
		else if (!wall.equals("")) {
			tv_wall_crag.setText(wall);
		}
		else if (!crag.equals("")) {
			tv_wall_crag.setText(crag);
		}
		else {
			tv_wall_crag.setText("");
		}
		if (date != 0) {
			tv_date.setText(Route.dateToString(date, Route.parcelable_format));
		}
		else {
			tv_date.setText("");
		}
		
        Typeface fontawesome = FontLoader.getFont("fontawesome", view.getContext());    
        TextView tv_photo_icon = (TextView)view.findViewById(R.id.route_list_item_tv_photo_icon);
        tv_photo_icon.setTypeface(fontawesome);

		iv_picture.setImageDrawable(null);
		tv_photo_icon.setText("");
		if (photo_path.equals("")) {
			tv_photo_icon.setText(R.string.icon_camera);
		}
        iv_picture.position = cursor.getPosition();
		AsyncBitmap ab = new AsyncBitmap(tv_photo_icon, iv_picture, cursor.getPosition());
		ab.execute(photo_path);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View retView = inflater.inflate(R.layout.route_list_item, parent, false);
        return retView;
        
	}
	
	public Route getRoute(int position) {
		Cursor cursor = getCursor();
		if (cursor != null && cursor.moveToPosition(position)) {
			Route route = new Route(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), Route.stringToLocation(cursor.getString(6)), cursor.getLong(7), cursor.getString(8), Integer.parseInt(cursor.getString(9)), cursor.getString(10));
			return route;
		}
		
		return null;
	}

}
