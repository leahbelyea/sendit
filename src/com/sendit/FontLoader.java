package com.sendit;

import java.util.HashMap;
import java.util.Map;
import android.content.Context;
import android.graphics.Typeface;

public class FontLoader {
	
	private static Boolean fonts_loaded = false;
	private static HashMap<String, String> font_files = new HashMap<String, String>();
	private static HashMap<String, Typeface> fonts = new HashMap<String, Typeface>();

	public static Typeface getFont(String font_name, Context context) {
		
		if(!fonts_loaded) {
			loadFonts(context);
		}
		
		return fonts.get(font_name);
		
	}
	
	public static void loadFonts(Context context) {
		font_files.put("fontawesome", "fontawesome.ttf");
		font_files.put("belligerent", "belligerent.ttf");
		font_files.put("sendit", "sendit.ttf");
		
		for (Map.Entry<String, String> font : font_files.entrySet()) {
			fonts.put((String)font.getKey(), Typeface.createFromAsset(context.getAssets(), (String)font.getValue()));
		}
		
		fonts_loaded = true;
	    
	}

}
