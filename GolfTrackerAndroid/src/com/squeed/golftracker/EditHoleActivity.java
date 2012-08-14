package com.squeed.golftracker;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squeed.golftracker.common.model.Course;
import com.squeed.golftracker.common.model.Hole;
import com.squeed.golftracker.common.model.PointOfInterest;
import com.squeed.golftracker.helper.DbHelper;

public class EditHoleActivity extends ListActivity {

	private Course course;
	private int currentHoleNumber = 0;
	private Hole currentHole;
	private PointOfInterest[] pois;
	private Animation inAnimation;

	private ArrayAdapter<CharSequence> adapter;
	private ListView lv;

	private DbHelper dbHelper;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_hole_page);

		// Populate hcp spinner
		adapter = new ArrayAdapter<CharSequence>(this,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		for (int a = 0; a < 18; a++)
			adapter.add(new Integer(a + 1).toString());

		// Load course...
		Long courseId = getIntent().getLongExtra("course_id", -1L);

		dbHelper = new DbHelper(this);

		// TODO there should only be one client-side instance of a course.
//		course = dbHelper.loadCourseFromDb(dbHelper.getReadableDatabase(),
//				courseId);

		updateHole();

		inAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
				android.R.anim.fade_in);
		inAnimation.setDuration(200L);

		setupButtons();

	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Long courseId = getIntent().getLongExtra("course_id", -1L);
		// TODO restore course from local storage since it might contain changes.
		// course = dbHelper.loadCourseFromDb(dbHelper.getReadableDatabase(), courseId);
		updateHole();
	}

	private void updateHole() {
		currentHole = (Hole) course.getHoles().get(currentHoleNumber);

		pois = new PointOfInterest[currentHole.getPois().size()];
		int i = 0;
		for (PointOfInterest poi : currentHole.getPois()) {
			pois[i++] = (PointOfInterest) poi;
		}

		Spinner hcpSpinner = (Spinner) findViewById(R.id.editHoleHcpFld);
		hcpSpinner.setAdapter(adapter);

		((TextView) findViewById(R.id.editHoleHoleVal)).setText(new Integer(
				currentHoleNumber+1).toString());

		// If hole (currentHole) exists on course, select values on spinners.

		if (currentHole.getHcp() != 0) {
			hcpSpinner.setSelection(currentHole.getHcp() - 1);
		}
		if (currentHole.getPar() != 0) {
			Spinner parSpinner = ((Spinner) findViewById(R.id.editHoleParFld));

			for (int a = 0; a < parSpinner.getAdapter().getCount(); a++) {
				String val = (String) parSpinner.getAdapter().getItem(a);
				if (val.equals(new Integer(currentHole.getPar()).toString())) {
					parSpinner.setSelection(a);
					break;
				}
			}
		}

		lv = getListView();
		lv.setTextFilterEnabled(true);
		
		registerForContextMenu(lv);

		EfficientAdapter efficientAdapter = new EfficientAdapter(this);
		setListAdapter(efficientAdapter);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
	    ContextMenuInfo menuInfo) {
	  if (v.getId()==android.R.id.list) {
	    //AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
	    // menu.setHeaderTitle(Countries[info.position]);
	    String[] menuItems = new String[]{"Redigera", "Radera"};
	    for (int i = 0; i<menuItems.length; i++) {
	      menu.add(Menu.NONE, i, i, menuItems[i]);
	    }
	  }
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	  AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
	  int menuItemIndex = item.getItemId();
	  final PointOfInterest poi = pois[info.position];
	  Log.i("EditHoleActivity", "Selected POI: " + poi.getTitle());
	  if(menuItemIndex == 0) {
		  // Edit POI
		  AlertDialog.Builder builder;				

			Context mContext = EditHoleActivity.this;
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
			View layout = inflater.inflate(R.layout.custom_dialog,
			                               (ViewGroup) findViewById(R.id.custom_dialog));
			
			final EditText editText = (EditText) layout.findViewById(R.id.poiNameFld);	
			editText.setText(poi.getTitle());
			
			
			
			
			builder = new AlertDialog.Builder(mContext);
			builder.setView(layout);
			final AlertDialog alertDialog = builder.create();
			
			final Button saveBtn = (Button) layout.findViewById(R.id.savePoiNameBtn);
			final Button cancelBtn = (Button) layout.findViewById(R.id.cancelPoiNameBtn);
			
			saveBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					//dbHelper.updatePoiName(dbHelper.getWritableDatabase(), poi.getId(), editText.getText().toString().trim(), poi.getCreatedBy(), UserAgent.OWNER_ID);
					poi.setTitle(editText.getText().toString().trim());
					alertDialog.dismiss();
					Toast.makeText(
							EditHoleActivity.this,
							"Namn uppdaterat! Inte",
							Toast.LENGTH_SHORT).show();
				}
			});
			
			cancelBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					alertDialog.dismiss();
				}
			});
			
			alertDialog.show();
			
			
	  } else if(menuItemIndex == 1) {
		  // Delete POI
		  currentHole.getPois().remove(poi.getType());
		  
		  // TODO figure out some cool way to store client-side changes until sync-time
		  //dbHelper.deletePoi(dbHelper.getWritableDatabase(), poi, UserAgent.OWNER_ID);
	  }	  
	  
	  updateHole();
	  return true;
	}

	private void setupButtons() {

		((Button) findViewById(R.id.editHoleSaveBtn1))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						// Get hcp and idx values, save to correct hole.
						Spinner hcpSpinner = (Spinner) findViewById(R.id.editHoleHcpFld);
						Spinner parSpinner = ((Spinner) findViewById(R.id.editHoleParFld));

						// TODO - save Par and Hcp values directly to database on change, remove save button?
						
						currentHole.setHcp(Integer
								.parseInt((String) hcpSpinner.getSelectedItem()));
						currentHole.setPar(Integer.parseInt((String) parSpinner
								.getSelectedItem()));
						// TODO add code that keeps client-side changes until sync
//						dbHelper.updateHole(dbHelper.getWritableDatabase(),
//								currentHole);
						Toast.makeText(
								EditHoleActivity.this,
								"Hålet sparat!",
								Toast.LENGTH_SHORT).show();
					}
				});

		((Button) findViewById(R.id.editHoleForwardButton1))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						currentHoleNumber++;
						if (currentHoleNumber+1 > course.getHoles().size()) {
							currentHoleNumber = course.getHoles().size() - 1;
							Toast.makeText(
									EditHoleActivity.this,
									"Du är redan på hål "
											+ (course.getHoles().size()),
									Toast.LENGTH_SHORT).show();
						}

						((ViewGroup) findViewById(android.R.id.content))
								.getChildAt(0).startAnimation(inAnimation);

						updateHole();
					}

				});

		((Button) findViewById(R.id.editHoleBackButton1))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						currentHoleNumber--;
						if (currentHoleNumber < 0) {
							currentHoleNumber = 0;
							Toast.makeText(EditHoleActivity.this,
									"Du är redan på hål 1", Toast.LENGTH_SHORT)
									.show();
						}

						((ViewGroup) findViewById(android.R.id.content))
								.getChildAt(0).startAnimation(inAnimation);
						updateHole();
					}
				});
		
		((Button) findViewById(R.id.editHoleNewPoiBtn))
		.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(EditHoleActivity.this, NewPoiActivity.class);
				i.putExtra("hole_id", currentHole.getId());
				startActivityForResult(i, 666);
			}
		});
		
		((Button) findViewById(R.id.showMapBtn2))
		.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
				
				
					Intent i = new Intent(EditHoleActivity.this, EditHoleMapActivity.class);
					i.putExtra("hole", currentHole);
					i.putExtra("course", course);
					startActivity(i);
//				} else {
//					Toast.makeText(EditHoleActivity.this, "Hålet saknar GPS-koordinater för gul tee och mitten green, kan ej visa.", Toast.LENGTH_SHORT).show();
//				}
			}
		});
	}
	
	
	/**
	 * When the "create new poi" activity returns, put the new POI into the object graph.
	 * (It has already been saved to the hole in the DB)
	 */
	@Override
	protected void onActivityResult(int reqCode, int resCode, Intent data) {
		Log.i("EditHoleActivity", "ENTER - onActivityResult: " + reqCode + " / " + resCode);
		if(reqCode == 666 && data != null && data.getSerializableExtra("poi") != null) {
			PointOfInterest poi = (PointOfInterest) data.getSerializableExtra("poi");
			currentHole.getPois().add(poi);
			
			updateHole();
		}
		if(reqCode == 777 && data != null && data.getSerializableExtra("hole_number") != null) {
			Integer holeNumber = data.getIntExtra("hole_number", 1);
			currentHoleNumber = holeNumber - 1;
			updateHole();
		}
	}

	class EfficientAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private Bitmap mIcon1;
		private Bitmap mIcon2;
		private Bitmap mIcon3;
		private Bitmap mIcon4;

		public EfficientAdapter(Context context) {
			// Cache the LayoutInflate to avoid asking for a new one each time.
			mInflater = LayoutInflater.from(context);

			mIcon1 = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.green_icon);
			mIcon2 = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.bunker_icon);
			mIcon3 = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.yel_tee_icon);
			mIcon4 = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.red_tee_icon);
		}
		
		
		public int getCount() {
			return currentHole.getPois().size();
		}

		public Object getItem(int position) {
			return pois[position];
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			// A ViewHolder keeps references to children views to avoid
			// unneccessary calls
			// to findViewById() on each row.
			ViewHolder holder;

			// When convertView is not null, we can reuse it directly, there is
			// no need
			// to reinflate it. We only inflate a new View when the convertView
			// supplied
			// by ListView is null.
			if (convertView == null) {
				convertView = mInflater.inflate(
						R.layout.edit_hole_poi_list_item, null);

				// Creates a ViewHolder and store references to the two children
				// views
				// we want to bind data to.
				holder = new ViewHolder();
				holder.name = (TextView) convertView
						.findViewById(R.id.poi_name);

				convertView.setVerticalFadingEdgeEnabled(true);

				holder.icon = (ImageView) convertView
						.findViewById(R.id.poi_icon);

				convertView.setTag(holder);
			} else {
				// Get the ViewHolder back to get fast access to the TextView
				// and the ImageView.
				holder = (ViewHolder) convertView.getTag();
			}

			// Bind the data efficiently with the holder.
			holder.id = ((PointOfInterest) getItem(position)).getId();
			holder.name
					.setText(((PointOfInterest) getItem(position)).getTitle());

			String type = ((PointOfInterest) getItem(position)).getType().getName();
			if (type.equals("fg") || type.equals("mg") || type.equals("bg")) {
				holder.icon.setImageBitmap(mIcon1);
			} else if (isBunker(type)) {
				holder.icon.setImageBitmap(mIcon2);
			} else if (type.equals("yt")) {
				holder.icon.setImageBitmap(mIcon3);
			} else if (type.equals("rt")) {
				holder.icon.setImageBitmap(mIcon4);
			}
			holder.icon = (ImageView) convertView.findViewById(R.id.poi_icon);

			convertView.setBackgroundColor(colors[position % colors.length]);

			return convertView;
		}
		
		private int[] colors = new int[] { 0x00000000, 0x33000000 };

		class ViewHolder {
			Long id;
			TextView name;
			TextView type;
			TextView distance;
			ImageView icon;
		}

	}
	
	private boolean isBunker(String type) {
		return type.equals("fbl") || type.equals("fbr")|| type.equals("gbf") || type.equals("gbl") || type.equals("gbr") || type.equals("gbb");
	}
}
