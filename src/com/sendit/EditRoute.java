package com.sendit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;
import com.sendit.R;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class EditRoute extends Activity {
	
	String action;
	DatePickerDialog.OnDateSetListener date_listener;
	EditText et_name;
	ImageView iv_photo;
	EditText et_grade;
	EditText et_crag;
	EditText et_wall;
	EditText et_location;
	EditText et_date;
	Spinner sp_send_type;
	LinearLayout ll_rating;
	EditText et_notes;
	TextView submit_btn;
	Route route = null;
	private final int SELECT_PHOTO = 1;
	TextView tv_photo_icon;
	TextView tv_remove_photo_icon;
	Uri fileUri;
	String path_to_photo = null;
	int grade_number;
	String grade_modifier;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 
		setContentView(R.layout.activity_edit_route);

		// Get input views
		et_name = (EditText)findViewById(R.id.edit_route_et_name);
		iv_photo = (ImageView)findViewById(R.id.edit_route_iv_photo);
		et_grade = (EditText)findViewById(R.id.edit_route_et_grade);
		et_crag = (EditText)findViewById(R.id.edit_route_et_crag);
		et_wall = (EditText)findViewById(R.id.edit_route_et_wall);
		et_location = (EditText)findViewById(R.id.edit_route_et_location);
		et_date = (EditText)findViewById(R.id.edit_route_et_date);
		sp_send_type = (Spinner)findViewById(R.id.edit_route_sp_send_type);
		ll_rating = (LinearLayout)findViewById(R.id.edit_route_ll_rating);
		et_notes = (EditText)findViewById(R.id.edit_route_et_notes);
		submit_btn = (TextView)findViewById(R.id.edit_route_tv_submit);
		
		// Get action and set activity title
		action = getIntent().getStringExtra("action");
		String action_title = null;
		if (action.equals("add")) {
			action_title = "ADD NEW ROUTE";
		}
		else if (action.equals("edit")) {
			action_title = "EDIT ROUTE";
			submit_btn.setText("Update Route!");
		}
		
		TextView tv_action_title = (TextView)findViewById(R.id.edit_route_tv_action_title);
		tv_action_title.setText(action_title);
		
		// Set font types
		Typeface fontawesome = FontLoader.getFont("fontawesome", this);
		Typeface belligerent = FontLoader.getFont("belligerent", this);

		TextView tv_star_1 = (TextView)findViewById(R.id.edit_route_tv_star_1);
		tv_star_1.setTypeface(fontawesome);

		TextView tv_star_2 = (TextView)findViewById(R.id.edit_route_tv_star_2);
		tv_star_2.setTypeface(fontawesome);

		TextView tv_star_3 = (TextView)findViewById(R.id.edit_route_tv_star_3);
		tv_star_3.setTypeface(fontawesome);

		TextView tv_star_4 = (TextView)findViewById(R.id.edit_route_tv_star_4);
		tv_star_4.setTypeface(fontawesome);

		TextView tv_star_5 = (TextView)findViewById(R.id.edit_route_tv_star_5);
		tv_star_5.setTypeface(fontawesome);
		
		tv_remove_photo_icon = (TextView)findViewById(R.id.edit_route_tv_remove_photo_icon);
		tv_remove_photo_icon.setTypeface(fontawesome);
		
		tv_photo_icon = (TextView)findViewById(R.id.edit_route_tv_photo_icon);
		tv_photo_icon.setTypeface(fontawesome);
		
		tv_action_title.setTypeface(belligerent);
		
		// Set up photo selector
		iv_photo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent pickIntent = new Intent();
				pickIntent.setType("image/*");
				pickIntent.setAction(Intent.ACTION_PICK);
				
				Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				fileUri = MediaFileHelper.getOutputMediaFileUri(); // create a file to save the image
			    takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
			    
				String pickTitle = "Take new photo or select from gallery";
				Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
				chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {takePhotoIntent});
			    startActivityForResult(chooserIntent, SELECT_PHOTO);

			}
		});
		
		// Set up grade selection dialog
		et_grade.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				DialogFragment newFragment = new GradeDialog();
			    newFragment.show(getFragmentManager(), "grade");
			}
		});
		
		// Set up GPS location dialog
		et_location.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				DialogFragment newFragment = new LocationDialog();
			    newFragment.show(getFragmentManager(), "location");
			}
		});
		
		// Set up datepicker
		final Calendar date = Calendar.getInstance();
		date_listener = new DatePickerDialog.OnDateSetListener() {
		    @Override
		    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		        date.set(Calendar.YEAR, year);
		        date.set(Calendar.MONTH, monthOfYear);
		        date.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		        updateET(year, monthOfYear, dayOfMonth);
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

		// Set up send type spinner
		ArrayAdapter<CharSequence> send_type_adapter = ArrayAdapter.createFromResource(this, R.array.send_types, R.layout.send_type_layout);
		send_type_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp_send_type.setAdapter(send_type_adapter);
		
		// Set up ratings
		ll_rating.setTag(0);
		tv_star_1.setOnClickListener(new RatingListener(1));
		tv_star_2.setOnClickListener(new RatingListener(2));
		tv_star_3.setOnClickListener(new RatingListener(3));
		tv_star_4.setOnClickListener(new RatingListener(4));
		tv_star_5.setOnClickListener(new RatingListener(5));
		
		// Recreate photo and rating fields if device orientation was changed
	    if (savedInstanceState != null)
	    {
	        int saved_rating = savedInstanceState.getInt("rating");
			for(int i=0; i<((ViewGroup)ll_rating).getChildCount(); i++) {
			    TextView nextChild = (TextView)((ViewGroup)ll_rating).getChildAt(i);
			    if (i+1 <= saved_rating){
					nextChild.setText(R.string.icon_star);
			    }
			}
			ll_rating.setTag(saved_rating);
			
	        String saved_photo_path = savedInstanceState.getString("photo_path");
	        if (saved_photo_path != null) {
		    	File file = new File(saved_photo_path);
		        Bitmap bitmap = BitmapDecoder.decodeSampledBitmapFromFile(file.getAbsolutePath(), 1000, 700);
		    	iv_photo.setImageBitmap(bitmap);
                tv_photo_icon.setText("");
    	        path_to_photo = saved_photo_path;
	        }
	    }
	    
		// If editing existing route, pre-populate fields
		if (action.equals("edit")) {
			route = (Route)getIntent().getExtras().get("route");
			et_name.setText(route.getName());
			et_grade.setText(route.getGrade());
			et_crag.setText(route.getCrag());
			et_wall.setText(route.getWall());
			et_location.setText(Route.locationToString(route.getLocation()));
			et_date.setText(Route.dateToString(route.getDate(), "dd/MM/yyyy"));
			sp_send_type.setSelection(send_type_adapter.getPosition(route.getSendType()));
			et_notes.setText(route.getNotes());
			
	        if (!route.getPhotoPath().equals("")) {
		    	File file = new File(route.getPhotoPath());
		        Bitmap bitmap = BitmapDecoder.decodeSampledBitmapFromFile(file.getAbsolutePath(), 1000, 700);
                bitmap = BitmapDecoder.correctImageRotation(bitmap, route.getPhotoPath());
		    	iv_photo.setImageBitmap(bitmap);
                tv_photo_icon.setText("");
                addPhotoRemove();
	        }
	        path_to_photo = route.getPhotoPath();
	        
			for(int i=0; i<((ViewGroup)ll_rating).getChildCount(); i++) {
			    TextView nextChild = (TextView)((ViewGroup)ll_rating).getChildAt(i);
			    if (i+1 <= route.getRating()){
					nextChild.setText(R.string.icon_star);
			    }
			}
			
			ll_rating.setTag(route.getRating());
		}
		
		// On submit, create Route
		submit_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				String name = et_name.getText().toString();
				
				if (name.equals("")) {
					showRouteMissingDialog(view);
					return;
				}
				
				String photo_path = path_to_photo;
				photo_path = (photo_path == null) ? "" : photo_path;
				String crag = et_crag.getText().toString();
				String wall = et_wall.getText().toString();
				Location location = Route.stringToLocation(et_location.getText().toString());
				Long date = Route.dateToNum(et_date.getText().toString(), Route.parcelable_format);
				String send_type = sp_send_type.getSelectedItem().toString();
				send_type = (send_type.equals("Send Type")) ? "" : send_type;
				int rating = (Integer)ll_rating.getTag();
				String notes = et_notes.getText().toString();
				
				RoutesDbHelper db = new RoutesDbHelper(view.getContext());
				
				if (action.equals("edit")) {
					Route updated_route = new Route(route.getId(), name, photo_path, grade_number, grade_modifier, crag, wall, location, date, send_type, rating, notes);
					db.updateRoute(updated_route);
					Toast.makeText(view.getContext(), (CharSequence)"Your route has been successfully updated!", Toast.LENGTH_LONG).show();
					
					Intent i_edit = new Intent(view.getContext(), ViewRoute.class);
					Bundle b = new Bundle();
					b.putString("from", "EditRoute");
					b.putParcelable("route", updated_route);
					i_edit.putExtras(b);
					startActivity(i_edit);
				}
				else if (action.equals("add")) {
					Route new_route = new Route(name, photo_path, grade_number, grade_modifier, crag, wall, location, date, send_type, rating, notes);
					db.addRoute(new_route);
					Toast.makeText(view.getContext(), (CharSequence)"Your route has been successfully added!", Toast.LENGTH_LONG).show();	
					Intent i_add = new Intent(view.getContext(), MainActivity.class);
					startActivity(i_add);
				}

			}

		});
	}
	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { 
        super.onActivityResult(requestCode, resultCode, data);
        
        if(requestCode == SELECT_PHOTO && resultCode == RESULT_OK) {
        	if (data == null) { // Photo taken with camera
	        	path_to_photo = MediaFileHelper.photo_path;
	        	File file = new File(path_to_photo);
	            Bitmap bitmap = BitmapDecoder.decodeSampledBitmapFromFile(file.getAbsolutePath(), 1000, 700);
	        	tv_photo_icon.setText("");
                addPhotoRemove();
	        	iv_photo.setImageBitmap(bitmap);
	        	galleryAddPic(path_to_photo);
        	}
        	else { // Photo selected from gallery
	            try {
	                final Uri imageUri = data.getData();
	                path_to_photo = getRealPathFromURI(imageUri);
	                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
	                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
	                selectedImage = BitmapDecoder.correctImageRotation(selectedImage, path_to_photo);
	                tv_photo_icon.setText("");
	                addPhotoRemove();
	                iv_photo.setImageBitmap(selectedImage);
	            } catch (FileNotFoundException e) {
	                e.printStackTrace();
	            }
        	}
        	
        }
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_route, menu);
		return true;
	}
	
	private void showRouteMissingDialog(View view) {
		AlertDialog alertDialog = new AlertDialog.Builder(view.getContext()).create();
        alertDialog.setTitle("Route Name Missing");
        alertDialog.setMessage("Hey, you need to enter a route name before proceeding!");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Okay, got it!", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                	ScrollView sv = (ScrollView)findViewById(R.id.edit_route_sv);
                	sv.scrollTo(0, sv.getTop());
                	et_name.requestFocus();
                }
        });
        alertDialog.show();	
	}
	
	private String getRealPathFromURI(Uri contentUri) {
	    String[] proj = { MediaStore.Images.Media.DATA };
	    CursorLoader loader = new CursorLoader(this, contentUri, proj, null, null, null);
	    Cursor cursor = loader.loadInBackground();
	    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	    cursor.moveToFirst();
	    return cursor.getString(column_index);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
	    super.onSaveInstanceState(outState);
	    outState.putInt("rating", (Integer)ll_rating.getTag());;
	    outState.putString("photo_path", path_to_photo);;
	}
	
	private void galleryAddPic(String photo_path) {
	    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
	    File f = new File(photo_path);
	    Uri contentUri = Uri.fromFile(f);
	    mediaScanIntent.setData(contentUri);
	    this.sendBroadcast(mediaScanIntent);
	}
	
	private void addPhotoRemove() {
        tv_remove_photo_icon.setVisibility(View.VISIBLE);
        tv_remove_photo_icon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				iv_photo.setImageBitmap(null);
				path_to_photo = null;
		        tv_remove_photo_icon.setVisibility(View.INVISIBLE);
	        	tv_photo_icon.setText(R.string.icon_camera);
			}
        	
        });
	}
	
	public void setGrade(int gn, String gm) {
		grade_number = gn;
		grade_modifier = gm;
	}

}
