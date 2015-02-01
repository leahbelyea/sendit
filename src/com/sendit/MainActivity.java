package com.sendit;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.sendit.R;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		
		// Set font types
		Typeface font_awesome = FontLoader.getFont("fontawesome", this);
		Typeface title_font = FontLoader.getFont("belligerent", this);
		
		TextView tv_title = (TextView)findViewById(R.id.main_tv_title);
		tv_title.setTypeface(title_font);

		TextView tv_plus = (TextView)findViewById(R.id.main_tv_plus);
		TextView tv_bars = (TextView)findViewById(R.id.main_tv_bars);
		tv_plus.setTypeface(font_awesome);
		tv_bars.setTypeface(font_awesome);
		
		// Set onClick Listeners
		LinearLayout ll_add = (LinearLayout)findViewById(R.id.main_ll_add);
		ll_add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				Intent i = new Intent(view.getContext(), EditRoute.class);
				i.putExtra("action", "add");
				startActivity(i);
			}
			
		});
		
		LinearLayout ll_view = (LinearLayout)findViewById(R.id.main_ll_view);
		ll_view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				Intent i = new Intent(view.getContext(), ViewRoutes.class);
				startActivity(i);
			}
			
		});
		
	}
	
	@Override
	public void onBackPressed()
	{
	    // Do nothing
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
