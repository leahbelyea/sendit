package com.sendit;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.net.Uri;
import android.os.Environment;
import android.util.Log;

public class MediaFileHelper {

	public static String photo_path = null;
	
	// Create Uri from Media File
	public static Uri getOutputMediaFileUri(){
	      return Uri.fromFile(getOutputMediaFile());
	}
	
	// Create Media File for Saving Photo 
	private static File getOutputMediaFile() {
	    // To be safe, you should check that the SDCard is mounted
	    // using Environment.getExternalStorageState() before doing this.

	    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "SendIt");
	    // This location works best if you want the created images to be shared
	    // between applications and persist after your app has been uninstalled.

	    // Create the storage directory if it does not exist
	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            Log.d("SendIt", "failed to create directory");
	            return null;
	        }
	    }

	    // Create a media file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    File mediaFile;
        photo_path = mediaStorageDir.getPath() + File.separator + "IMG_"+ timeStamp + ".jpg";
        mediaFile = new File(photo_path);
        
	    return mediaFile;
	}

}
