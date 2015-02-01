package com.sendit;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class SquareImage extends ImageView {

    public SquareImage(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
    
    public SquareImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub

    }

    public SquareImage(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub

    }
    
	@Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

}
