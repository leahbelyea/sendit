package com.sendit;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

public class Route implements Parcelable {
	
	private int id;
	private String name; 
	private String photo_path;
	private int grade_number;
	private String grade_modifier;
	private String crag;
	private String wall;
	private Location location;
	private Long date;
	private String send_type;
	private int rating;
	private String notes;
	public static String parcelable_format = "dd/MM/yyyy";
	
	public Route(int id, String name, String photo_path, int grade_number, String grade_modifier, String crag, String wall, Location location, Long date, String send_type, int rating, String notes) {
		this.id = id;
		this.name = name;
		this.photo_path = photo_path;
		this.grade_number = grade_number;
		this.grade_modifier = grade_modifier;
		this.crag = crag;
		this.wall = wall;
		this.location = location;
		this.date = date;
		this.send_type = send_type;
		this.rating = rating;
		this.notes = notes;
	}	
	
	public Route(String name, String photo_path, int grade_number, String grade_modifier, String crag, String wall, Location location, Long date, String send_type, int rating, String notes) {
		this.name = name;
		this.photo_path = photo_path;
		this.grade_number = grade_number;
		this.grade_modifier = grade_modifier;
		this.crag = crag;
		this.wall = wall;
		this.location = location;
		this.date = date;
		this.send_type = send_type;
		this.rating = rating;
		this.notes = notes;
	}
	
	public Route(Parcel parcel) {
		this.id = parcel.readInt();
		this.name = parcel.readString();
		this.photo_path = parcel.readString();
		this.grade_number = parcel.readInt();
		this.grade_modifier = parcel.readString();
		this.crag = parcel.readString();
		this.wall = parcel.readString();
		this.location = stringToLocation(parcel.readString());
		this.date = parcel.readLong();
		this.send_type = parcel.readString();
		this.rating = parcel.readInt();
		this.notes = parcel.readString();
	}

	public int getId() {
		return this.id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setPhotoPath(String photo_path) {
		this.photo_path = photo_path;
	}
	
	public String getPhotoPath() {
		return this.photo_path;
	}

	public void setGradeNumber(int grade_number) {
		this.grade_number = grade_number;
	}

	public void setGradeModifier(String grade_modifier) {
		this.grade_modifier = grade_modifier;
	}

	public int getGradeNumber() {
		return this.grade_number;
	}

	public String getGradeModifier() {
		return this.grade_modifier;
	}
	
	public String getGrade() {
		return "5." + this.grade_number + this.grade_modifier;
	}
	
	public void setCrag(String crag) {
		this.crag = crag;
	}
	
	public String getCrag() {
		return this.crag;
	}
	
	public void setWall(String wall) {
		this.wall = wall;
	}
	
	public String getWall() {
		return this.wall;
	}
	
	public void setLocation(Location location) {
		this.location = location;
	}
	
	public Location getLocation() {
		return this.location;
	}
	
	public void setDate(Long date) {
		this.date = date;
	}
	
	public Long getDate() {
		return this.date;
	}
	
	public void setSendType(String send_type) {
		this.send_type = send_type;
	}
	
	public String getSendType() {
		return this.send_type;
	}
	
	public void setRating(int rating) {
		this.rating = rating;
	}
	
	public int getRating() {
		return this.rating;
	}
	
	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	public String getNotes() {
		return this.notes;
	}
	
	public static Location stringToLocation(String string) {
		if (string.equals("")) {
			return null;
		}
		String[] location_string = string.split(", ");
		double latitude = Double.parseDouble(location_string[0]);
		double longitude = Double.parseDouble(location_string[1]);
		Location location = new Location("");
		location.setLatitude(latitude);
		location.setLongitude(longitude);
		return location;
	}
	
	public static String locationToString(Location location) {
		if (location == null) {
			return "";
		}
        double lng = location.getLongitude();
        double lat = location.getLatitude();
        String location_string = String.format("%f, %f", lat, lng);
        return location_string;
	}
	
	public static String dateToString(Long date_num, String format) {
		if (date_num == 0) {
			return "";
		}
		Date date = new Date();
		date.setTime(date_num);
		String date_string;
		date_string = new SimpleDateFormat(format).format(date);
		return date_string;
	}
	
	public static long dateToNum(String date_string, String format) {
		if (date_string.equals("")) {
			return 0;
		}
		Date date = null;
		try {
			date = new SimpleDateFormat(format).parse(date_string);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		long date_num = date.getTime();
		return date_num;
	}
	
	public static final Parcelable.Creator<Route> CREATOR = new Parcelable.Creator<Route>() {
		 public Route createFromParcel(Parcel in) {
			 return new Route(in);
		 }
	
		 public Route[] newArray(int size) {
		     return new Route[size];
		 }
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeInt(id);
		parcel.writeString(name);
		parcel.writeString(photo_path);
		parcel.writeInt(grade_number);
		parcel.writeString(grade_modifier);
		parcel.writeString(crag);
		parcel.writeString(wall);
		parcel.writeString(locationToString(location));
		parcel.writeLong(date);
		parcel.writeString(send_type);
		parcel.writeInt(rating);
		parcel.writeString(notes);		
	}

}
