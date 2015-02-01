package com.sendit;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class RouteContentProvider extends ContentProvider {
	
    private RoutesDbHelper routesDbHelper;
    private SQLiteDatabase db;
    
    // URI Matching
    private static final int ROUTES = 1;
    private static final int ROUTE_ID = 2;
    private static final String AUTHORITY = "com.sendit.provider";
    private static final String BASE_PATH = "routes";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);
    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
      sURIMatcher.addURI(AUTHORITY, BASE_PATH, ROUTES);
      sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", ROUTE_ID);
    }

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
	    int uriType = sURIMatcher.match(uri);
	    db = routesDbHelper.getWritableDatabase();
	    int rowsDeleted = 0;
	    switch (uriType) {
	    case ROUTES:
	      rowsDeleted = db.delete("routes", selection,
	          selectionArgs);
	      break;
	    case ROUTE_ID:
	      String id = uri.getLastPathSegment();
	      if (TextUtils.isEmpty(selection)) {
	        rowsDeleted = db.delete("routes","_id=" + id, null);
	      } else {
	        rowsDeleted = db.delete("routes", "_id=" + id + " and " + selection, selectionArgs);
	      }
	      break;
	    default:
	      throw new IllegalArgumentException("Unknown URI: " + uri);
	    }
	    getContext().getContentResolver().notifyChange(uri, null);
	    return rowsDeleted;
	}

	@Override
	public String getType(Uri arg0) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
	    int uriType = sURIMatcher.match(uri);
	    db = routesDbHelper.getWritableDatabase();
	    long id = 0;
	    switch (uriType) {
	    case ROUTES:
	      id = db.insert("routes", null, values);
	      break;
	    default:
	      throw new IllegalArgumentException("Unknown URI: " + uri);
	    }
	    getContext().getContentResolver().notifyChange(uri, null);
	    return Uri.parse(BASE_PATH + "/" + id);
	}

	@Override
	public boolean onCreate() {
        routesDbHelper = new RoutesDbHelper(getContext());
        return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

	    SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

	    // Set the table
	    queryBuilder.setTables("routes"	);
	    
	    int uriType = sURIMatcher.match(uri);
	    switch (uriType) {
	    case ROUTES:
	      break;
	    case ROUTE_ID:
	      // adding the ID to the original query
	      queryBuilder.appendWhere("_id=" + uri.getLastPathSegment());
	      break;
	    default:
	      throw new IllegalArgumentException("Unknown URI: " + uri);
	    }

	    SQLiteDatabase db = routesDbHelper.getWritableDatabase();
	    Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
	    // make sure that potential listeners are getting notified
	    cursor.setNotificationUri(getContext().getContentResolver(), uri);

	    return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
	    int uriType = sURIMatcher.match(uri);
	    db = routesDbHelper.getWritableDatabase();
	    int rowsUpdated = 0;
	    switch (uriType) {
	    case ROUTES:
	      rowsUpdated = db.update("routes", values, selection, selectionArgs);
	      break;
	    case ROUTE_ID:
	      String id = uri.getLastPathSegment();
	      if (TextUtils.isEmpty(selection)) {
	        rowsUpdated = db.update("routes", values, "_id=" + id, null);
	      }
	      else {
	        rowsUpdated = db.update("routes", values, "_id=" + id + " and " + selection, selectionArgs);
	      }
	      break;
	    default:
	      throw new IllegalArgumentException("Unknown URI: " + uri);
	    }
	    getContext().getContentResolver().notifyChange(uri, null);
	    return rowsUpdated;
	}
}
