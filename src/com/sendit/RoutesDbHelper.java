package com.sendit;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RoutesDbHelper extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "routes_db";
	private static final String TABLE_NAME = "routes";

	private static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME + " (" +
			"_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
			"name TEXT, " +
			"photo_path TEXT, " +
			"grade TEXT, " +
			"crag TEXT, " +
			"wall TEXT, " +
			"location TEXT, " +
			"date INTEGER, " +
			"send_type TEXT, " +
			"rating INTEGER, " +
			"notes TEXT);";
	
	public RoutesDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}
	
	public void addRoute(Route route) {
		
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put("name", route.getName());
		values.put("photo_path", route.getPhotoPath());
		values.put("grade", route.getGrade());
		values.put("crag", route.getCrag());
		values.put("wall", route.getWall());
		values.put("location", Route.locationToString(route.getLocation()));
		values.put("date", route.getDate());
		values.put("send_type", route.getSendType());
		values.put("rating", route.getRating());
		values.put("notes", route.getNotes());
		
	    db.insert(TABLE_NAME, null, values);
	    db.close(); 
	}
	
	public int updateRoute(Route route) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put("name", route.getName());
		values.put("photo_path", route.getPhotoPath());
		values.put("grade", route.getGrade());
		values.put("crag", route.getCrag());
		values.put("wall", route.getWall());
		values.put("location", Route.locationToString(route.getLocation()));
		values.put("date", route.getDate());
		values.put("send_type", route.getSendType());
		values.put("rating", route.getRating());
		values.put("notes", route.getNotes());
		
	    int retval = db.update(TABLE_NAME, values, "_id=?", new String[] { String.valueOf(route.getId()) });
	    db.close(); 
	    return retval;
	}
	
	public void deleteRoute(Route route) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_NAME, "_id=?",new String[] { String.valueOf(route.getId()) });
		db.close();
	}

}
