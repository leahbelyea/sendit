package com.sendit;

import java.io.File;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.TextView;

public class AsyncBitmap extends AsyncTask<String, Void, Bitmap> {
	
	TextView photo_icon;
	ListPicture picture;
	int position;
	
	AsyncBitmap(TextView photo_icon, ListPicture picture, int position) {
		this.photo_icon = photo_icon;
		this.picture = picture;
		this.position = position;
	}

	@Override
	protected Bitmap doInBackground(String... params) {
		Bitmap retval = null;
		String photo_path = params[0];
		if (!photo_path.equals("")) {
			File file = new File(photo_path);
			retval = BitmapDecoder.decodeSampledBitmapFromFile(file.getAbsolutePath(), 1000, 700);
			retval = BitmapDecoder.correctImageRotation(retval, photo_path);
		}
		return retval;
	}
	
	protected void onPostExecute(Bitmap bp){
		if (picture.position == position){
			picture.setImageBitmap(bp);
		}
	}

}
