package com.sendit;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import com.sendit.R;

public class FilterDialog extends DialogFragment {
	
	String filter = "";
	ArrayList<String> filterArgs = new ArrayList<String>();
    OnFilterBuiltListener mListener;
    HashMap<String, String[]> filterFields = new HashMap<String, String[]>();

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    LayoutInflater inflater = getActivity().getLayoutInflater();
	    View view = inflater.inflate(R.layout.filter_builder, null);
	    builder.setView(view).setTitle("Filter These Results");

	    final CheckBox cb_grade = (CheckBox)view.findViewById(R.id.filter_builder_cb_grade);
	    final CheckBox cb_rating = (CheckBox)view.findViewById(R.id.filter_builder_cb_rating);
	    final CheckBox cb_date = (CheckBox)view.findViewById(R.id.filter_builder_cb_date);
	    final CheckBox cb_send_type = (CheckBox)view.findViewById(R.id.filter_builder_cb_send_type);
	    final CheckBox cb_keyword = (CheckBox)view.findViewById(R.id.filter_builder_cb_keyword);

		final Spinner sp_grade_operator = (Spinner)view.findViewById(R.id.filter_builder_sp_grade_operator);
		ArrayAdapter<CharSequence> grade_operator_adapter = ArrayAdapter.createFromResource(view.getContext(), R.array.operators, R.layout.filter_spinner_layout);
		grade_operator_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp_grade_operator.setAdapter(grade_operator_adapter);
		
		final Spinner sp_grade = (Spinner)view.findViewById(R.id.filter_builder_sp_grade);
		ArrayAdapter<CharSequence> grade_adapter = ArrayAdapter.createFromResource(view.getContext(), R.array.grade_numbers, R.layout.filter_spinner_layout);
		grade_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp_grade.setAdapter(grade_adapter);

		final Spinner sp_rating_operator = (Spinner)view.findViewById(R.id.filter_builder_sp_rating_operator);
		ArrayAdapter<CharSequence> rating_operator_adapter = ArrayAdapter.createFromResource(view.getContext(), R.array.operators, R.layout.filter_spinner_layout);
		rating_operator_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp_rating_operator.setAdapter(rating_operator_adapter);
		
		final Spinner sp_rating = (Spinner)view.findViewById(R.id.filter_builder_sp_rating);
		ArrayAdapter<CharSequence> rating_adapter = ArrayAdapter.createFromResource(view.getContext(), R.array.ratings, R.layout.filter_spinner_layout);
		rating_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp_rating.setAdapter(rating_adapter);
		
		final Spinner sp_date_operator = (Spinner)view.findViewById(R.id.filter_builder_sp_date_operator);
		ArrayAdapter<CharSequence> date_operator_adapter = ArrayAdapter.createFromResource(view.getContext(), R.array.operators, R.layout.filter_spinner_layout);
		date_operator_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp_date_operator.setAdapter(date_operator_adapter);
		
		final Spinner sp_send_type = (Spinner)view.findViewById(R.id.filter_builder_sp_send_type);
		ArrayAdapter<CharSequence> send_type_adapter = ArrayAdapter.createFromResource(view.getContext(), R.array.send_types, R.layout.filter_spinner_layout);
		send_type_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp_send_type.setAdapter(send_type_adapter);

		final EditText et_date = (EditText)view.findViewById(R.id.filter_builder_et_date);
		final EditText et_keyword = (EditText)view.findViewById(R.id.filter_builder_et_keyword);
		
		// Set up datepicker
		final Calendar date = Calendar.getInstance();
		final DatePickerDialog.OnDateSetListener date_listener = new DatePickerDialog.OnDateSetListener() {
		    @Override
		    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		        date.set(Calendar.YEAR, year);
		        date.set(Calendar.MONTH, monthOfYear);
		        date.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		        updateET(year, monthOfYear, dayOfMonth);
		        
				CheckBox checkbox = (CheckBox)((LinearLayout)et_date.getParent()).getChildAt(0);
				checkbox.setChecked(true);
		    }

			private void updateET(int year, int monthOfYear, int dayOfMonth) {
				String date_string = String.format("%02d/%02d/%d", dayOfMonth, monthOfYear+1, year);
				et_date.setText(date_string);
			}
		};
		
		et_date.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				
				new DatePickerDialog(view.getContext(), date_listener, date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH)).show();
			}
			
		});
		
		// When item in row is clicked, check checkbox of that row
		sp_grade_operator.setOnItemSelectedListener(new FilterSpinnerListener());
		sp_grade.setOnItemSelectedListener(new FilterSpinnerListener());
		sp_rating_operator.setOnItemSelectedListener(new FilterSpinnerListener());
		sp_rating.setOnItemSelectedListener(new FilterSpinnerListener());
		sp_date_operator.setOnItemSelectedListener(new FilterSpinnerListener());
		sp_send_type.setOnItemSelectedListener(new FilterSpinnerListener());
		et_keyword.setOnClickListener(new FilterListener());
		
	    // Add filter button
	    builder.setPositiveButton("Filter!", new DialogInterface.OnClickListener() {
	    	@Override
	    	public void onClick(DialogInterface dialog, int id) {

	    	}
	    });
		
	    // Add remove filter button
	    builder.setNegativeButton("Remove All Filters", new DialogInterface.OnClickListener() {
	    	@Override
	    	public void onClick(DialogInterface dialog, int id) {
	    	    mListener.onFilterBuilt("", new String[0]);
	    	}
	    });
	    
	    final Dialog d = builder.create();
	    d.setOnShowListener(new DialogInterface.OnShowListener() {

	        @Override
	        public void onShow(DialogInterface dialog) {

	            Button b = ((AlertDialog) d).getButton(AlertDialog.BUTTON_POSITIVE);
	            b.setOnClickListener(new View.OnClickListener() {

	                @Override
	                public void onClick(View view) {
	    	    		
	    	    		if (!cb_grade.isChecked() && !cb_rating.isChecked() && !cb_date.isChecked() && !cb_send_type.isChecked() && !cb_keyword.isChecked()) {
	    	    			Toast toast = Toast.makeText(getActivity(), (CharSequence)"You did not select any filters to apply.", Toast.LENGTH_SHORT);
	    	    			toast.setGravity(Gravity.CENTER|Gravity.CENTER, 0, 0);
	    	    			toast.show();
	    	    			return;
	    	    		}

	    	    	    if (cb_grade.isChecked()) {
	    	    	    	String[] grade_numbers = sp_grade.getSelectedItem().toString().split("\\.");
	    	    	    	filterFields.put("grade_number" + sp_grade_operator.getSelectedItem().toString() + "?", new String[] {grade_numbers[1]});
	    	    	    }
	    	    	    if (cb_rating.isChecked()) {
	    	    	    	filterFields.put("rating" + sp_rating_operator.getSelectedItem().toString() + "?", new String[] {sp_rating.getSelectedItem().toString()});
	    	    	    }
	    	    	    if (cb_date.isChecked()) {
	    	    	    	filterFields.put("date" + sp_date_operator.getSelectedItem().toString() + "?", new String[] {String.valueOf(Route.dateToNum(et_date.getText().toString(), "dd/MM/yyyy"))});
	    	    	    }
	    	    	    if (cb_send_type.isChecked() && !sp_send_type.getSelectedItem().toString().equals("Send Type")) {
	    	    	    	filterFields.put("send_type=?", new String[] {sp_send_type.getSelectedItem().toString()});
	    	    	    }
	    	    	    if (cb_keyword.isChecked()) {
	    	    	    	String keyword = "%" + et_keyword.getText().toString() + "%";
	    	    	    	filterFields.put("(name LIKE ? OR notes LIKE ? OR crag LIKE ? OR wall LIKE ?)", new String[] {keyword, keyword, keyword, keyword});
	    	    	    }
	    	    	    
	    	    	    Iterator<Entry<String, String[]>> it = filterFields.entrySet().iterator();
	    	    	    while (it.hasNext()) {
	    	    	        Map.Entry<String, String[]> field = (Map.Entry<String, String[]>)it.next();
	    	    	        filter = filter + field.getKey() + " AND ";
	    	    	        for (int i=0; i<field.getValue().length; i++) {
	    	    	        	filterArgs.add(field.getValue()[i]);
	    	    	        }
	    	    	        it.remove();
	    	    	    }
	    	    	    // Remove trailing " AND "
	    	    	    if (!filter.equals("")) {
	    	    	    	filter = filter.substring(0, filter.length()-5);
	    	    	    }
	    	    	    
	    	    	    mListener.onFilterBuilt(filter, filterArgs.toArray(new String[0]));
	                    d.dismiss();
	                }
	            });
	        }
	    });
	    
	    // Override checked boxes set onItemSelected
	    cb_rating.setChecked(false);
	    cb_date.setChecked(false);
	    cb_send_type.setChecked(false);
	    
	    // Hide keyboard
	    d.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
	    return d;
	}
	
	public static interface OnFilterBuiltListener {
	    public abstract void onFilterBuilt(String filter, String[] filterArgs);
	}

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFilterBuiltListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnFilterBuiltListener");
        }
    }
    
    public class FilterSpinnerListener implements OnItemSelectedListener {
    	
    	int count = 0;

		@Override
		public void onItemSelected(AdapterView<?> v, View arg1, int arg2, long arg3) {
			if (count > 0) {
				CheckBox checkbox = (CheckBox)((LinearLayout)v.getParent()).getChildAt(0);
				checkbox.setChecked(true);
			}
			count++;
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// Do nothing
		}
    }
    
    public class FilterListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			CheckBox checkbox = (CheckBox)((LinearLayout)v.getParent()).getChildAt(0);
			checkbox.setChecked(true);
		}
    }
}
