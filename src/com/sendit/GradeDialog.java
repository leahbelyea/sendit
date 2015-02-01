package com.sendit;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import com.sendit.R;

public class GradeDialog extends DialogFragment {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		final Spinner sp_grade_number;
		final Spinner sp_grade_qualifier;
		
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    // Get the layout inflater
	    LayoutInflater inflater = getActivity().getLayoutInflater();
	    
	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout
	    View view = inflater.inflate(R.layout.grade_selector, null);
	    builder.setView(view).setTitle("Set Grade");
	    
		sp_grade_number = (Spinner)view.findViewById(R.id.grade_selector_sp_grade_number);
		ArrayAdapter<CharSequence> grade_number_adapter = ArrayAdapter.createFromResource(view.getContext(), R.array.grade_numbers, R.layout.grade_spinner_layout);
		grade_number_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp_grade_number.setAdapter(grade_number_adapter);
		
		sp_grade_qualifier = (Spinner)view.findViewById(R.id.grade_selector_sp_grade_qualifier);
		ArrayAdapter<CharSequence> grade_qualifier_adapter = ArrayAdapter.createFromResource(view.getContext(), R.array.grade_qualifiers, R.layout.grade_spinner_layout);
		grade_qualifier_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp_grade_qualifier.setAdapter(grade_qualifier_adapter);
		
	    // Add action button
	    builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
	    	@Override
	    	public void onClick(DialogInterface dialog, int id) {
	    		String grade_number = sp_grade_number.getSelectedItem().toString();
	    		String grade_qualifier = sp_grade_qualifier.getSelectedItem().toString();
	    		String grade = String.format("%s%s", grade_number, grade_qualifier);
	    		
	    		EditText et_grade = (EditText)getActivity().findViewById(R.id.edit_route_et_grade);
	    		et_grade.setText(grade);
	    	}
	    });
	    
	    return builder.create();
	}

}
