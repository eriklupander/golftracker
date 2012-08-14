package com.squeed.golftracker;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.squeed.golftracker.common.model.Course;
import com.squeed.golftracker.common.model.Tee;
import com.squeed.golftracker.rest.RestClient;

public class SelectTeeActivity extends ListActivity {
	
	private long courseId;
	private Course course;
	private ListView lv;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	  setContentView(R.layout.poi_list_page);
	  courseId = getIntent().getLongExtra("courseId", -1L);
	  
	 // DbHelper dbHelper = new DbHelper(this);
	  
	  course = new RestClient().getCourse(courseId); //dbHelper.loadCourseFromDb(dbHelper.getReadableDatabase(), courseId);
	  
	  lv = getListView();
	  lv.setTextFilterEnabled(true);
	 
	  EfficientAdapter efficientAdapter = new EfficientAdapter(this);
	  setListAdapter(efficientAdapter);
	}
	
	
	class EfficientAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        //private Bitmap mIcon1;
        
        public EfficientAdapter(Context context) {
            // Cache the LayoutInflate to avoid asking for a new one each time.
            mInflater = LayoutInflater.from(context);
            
           // mIcon1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.con);           
        }
        
        public int getCount() {
            return course.getTees().size();
        }
        
        public Object getItem(int position) {
            return course.getTees().get(position);
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
                convertView = mInflater.inflate(R.layout.tee_list_item, null);
                convertView.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						Log.d("SelectTeeActivity", "Clicked on course!");
						Long teeId = ((ViewHolder) v.getTag()).id;
						Intent i = new Intent(SelectTeeActivity.this, GolfTrackerActivity.class);
						i.putExtra("teeId", teeId);
						i.putExtra("courseId", courseId);
						startActivity(i);
					}
                	
                });
                // Creates a ViewHolder and store references to the two children views
                // we want to bind data to.
                holder = new ViewHolder();
                holder.name = (TextView) convertView.findViewById(R.id.tee_name);
                holder.distance = (TextView) convertView.findViewById(R.id.tee_distance);
                
                convertView.setVerticalFadingEdgeEnabled(true);
               
               // holder.icon = (ImageView) convertView.findViewById(R.id.poi_icon);

                convertView.setTag(holder);
            } else {
                // Get the ViewHolder back to get fast access to the TextView
                // and the ImageView.
                holder = (ViewHolder) convertView.getTag();
            }
            
            // Bind the data efficiently with the holder.
            holder.id = ((Tee) getItem(position)).getId();
            holder.name.setText( ((Tee) getItem(position)).getTeeType().getName());
            holder.distance.setText( "N/A");
           
            
            //holder.icon = (ImageView) convertView.findViewById(R.id.poi_icon);
            
            convertView.setBackgroundColor(colors[position % colors.length]);
                       
            return convertView;
        }

        private int[] colors = new int[] { 0x00000000, 0x33000000 };

        class ViewHolder {
            Long id;
			TextView name;            
            TextView distance;
         //   ImageView icon;
        }
        
	}
}
