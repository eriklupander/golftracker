package com.squeed.golftracker;

import java.util.List;

import com.squeed.golftracker.R;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.squeed.golftracker.entity.CourseDTO;
import com.squeed.golftracker.helper.DbHelper;

/**
 * Lets the user select the course to play.
 * @author Erik
 *
 */
public class CourseListActivity extends ListActivity {

	private List<CourseDTO> allCourses;
	private ListView lv;
	private String term;
	
	DbHelper dbHelper;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	  setContentView(R.layout.course_list_page);
	  
	  EditText searchFld = ((EditText) findViewById(R.id.searchCourseFld));
	  searchFld.setOnKeyListener(new OnKeyListener() {

		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if(event.getAction() == KeyEvent.ACTION_UP)
				search(v);
			return false;
		}
	  });
	  
	  searchFld.setOnEditorActionListener(new OnEditorActionListener() {
		
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			search(v);
			return false;
		}
	});
	  
	  // Create a new course if the desired one doesn't exist.
	  Button b = (Button) findViewById(R.id.newCourseBtn);
		
		b.setOnClickListener(
				new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent(CourseListActivity.this, NewCourseActivity.class);
				startActivity(intent);
			}
			
		});
	  
	  dbHelper = new DbHelper(this);
	  allCourses = dbHelper.loadCourses(dbHelper.getReadableDatabase(), term);
	  
	  
	 
	  lv = getListView();
	  lv.setTextFilterEnabled(true);
	  lv.requestFocus();
	 
	  EfficientAdapter efficientAdapter = new EfficientAdapter(this);
	  setListAdapter(efficientAdapter);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		lv.requestFocus();
	}
	
	private void search(View v) {
		term = ((EditText) v).getText().toString();
		Log.i("CourseListActivity", term);
		allCourses = dbHelper.loadCourses(dbHelper.getReadableDatabase(), term);
		CourseListActivity.this.setListAdapter(new EfficientAdapter(CourseListActivity.this));
	}
	
	private int[] colors = new int[] { 0x00000000, 0x33000000 };
	
	class EfficientAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private Bitmap mIcon1;
        

        public EfficientAdapter(Context context) {
            // Cache the LayoutInflate to avoid asking for a new one each time.
            mInflater = LayoutInflater.from(context);            
            mIcon1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.green_icon);
          
        }
        
        public int getCount() {
            return allCourses.size();
        }
        
        public Object getItem(int position) {
            return allCourses.get(position);
        }
       
        public long getItemId(int position) {
            return position;
        }
        
        public View getView(int position, View convertView, ViewGroup parent) {
            // A ViewHolder keeps references to children views to avoid unneccessary calls
            // to findViewById() on each row.
            ViewHolder holder;

            // When convertView is not null, we can reuse it directly, there is no need
            // to reinflate it. We only inflate a new View when the convertView supplied
            // by ListView is null.
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.course_list_item, null);
                convertView.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						Log.d("CourseListActivity", "Clicked on course!");
						Long courseId = ((ViewHolder) v.getTag()).id;
						Intent i = new Intent(CourseListActivity.this, SelectTeeActivity.class);
						i.putExtra("courseId", courseId);
						startActivity(i);
					}
                	
                });

                // Creates a ViewHolder and store references to the two children views
                // we want to bind data to.
                holder = new ViewHolder();
                holder.name = (TextView) convertView.findViewById(R.id.course_name);
                holder.icon = (ImageView) convertView.findViewById(R.id.course_icon);
               
                convertView.setVerticalFadingEdgeEnabled(true);

                convertView.setTag(holder);
            } else {
                // Get the ViewHolder back to get fast access to the TextView
                // and the ImageView.
                holder = (ViewHolder) convertView.getTag();
            }
            
            // Bind the data efficiently with the holder.
            holder.id = ((CourseDTO) getItem(position)).getId();
            holder.name.setText( ((CourseDTO) getItem(position)).getName());
            holder.icon.setImageBitmap(mIcon1);
           
            convertView.setBackgroundColor(colors[position % colors.length]);
                       
            return convertView;
        }

        class ViewHolder {
            Long id;
			TextView name;
			ImageView icon;
        }
        
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, 1, 1, R.string.refreshLbl);
		menu.add(Menu.NONE, 2, 2, R.string.backLbl);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case 1:
			
			break;
		case 2:
			CourseListActivity.this.finish();
			break;
		}
		return true;
	}
	
}
