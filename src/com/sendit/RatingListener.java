package com.sendit;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.sendit.R;

public class RatingListener implements OnClickListener {
	
	int star_id;

	public RatingListener(int star_id) {
		this.star_id = star_id;
	}


	@Override
	public void onClick(View view) {

		TextView star_view;
		LinearLayout ll_rating;
		int num_stars;
		
		star_view = (TextView)view;
		ll_rating = (LinearLayout)star_view.getParent();
		num_stars = getNumStars(ll_rating);
		
		emptyStars(ll_rating);
		
		if (star_id == 1 && num_stars == 1) {
			ll_rating.setTag(0);
			return;
		}
		
		for(int i=0; i<((ViewGroup)ll_rating).getChildCount(); i++) {
		    TextView nextChild = (TextView)((ViewGroup)ll_rating).getChildAt(i);
			nextChild.setText(R.string.icon_star_o);
		}
		
		for(int i=0; i<((ViewGroup)ll_rating).getChildCount(); i++) {
		    TextView nextChild = (TextView)((ViewGroup)ll_rating).getChildAt(i);
		    if (i+1 <= star_id){
				nextChild.setText(R.string.icon_star);
		    }
		}

		ll_rating.setTag(star_id);
		
	}
	
	public void emptyStars(LinearLayout ll_rating) {
		
		for(int i=0; i<((ViewGroup)ll_rating).getChildCount(); ++i) {
		    TextView nextChild = (TextView)((ViewGroup)ll_rating).getChildAt(i);
			nextChild.setText(R.string.icon_star_o);
		}
		
	}
	
	public int getNumStars(LinearLayout ll_rating) {
		
		int num_stars = 0;
		String star_full_contents;
		boolean star_full;
		
		for(int i=0; i<((ViewGroup)ll_rating).getChildCount(); ++i) {
		    TextView nextChild = (TextView)((ViewGroup)ll_rating).getChildAt(i);
		    star_full_contents = nextChild.getContext().getResources().getString(R.string.icon_star);
			star_full = (nextChild.getText().equals(star_full_contents));
		    if (star_full) {
		    	num_stars += 1;
		    }
		}
		return num_stars;
	}

}
