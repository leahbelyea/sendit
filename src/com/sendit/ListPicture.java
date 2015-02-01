package com.sendit;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ListPicture extends ImageView {

	public ListPicture(Context context) {
		super(context);
	}
	
	public ListPicture(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public int position;

}
