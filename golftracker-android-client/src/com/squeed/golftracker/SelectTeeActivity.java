package com.squeed.golftracker;

import java.util.Calendar;

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
import com.squeed.golftracker.common.model.Round;
import com.squeed.golftracker.common.model.RoundScore;
import com.squeed.golftracker.common.model.TeeType;
import com.squeed.golftracker.rest.RestClient;

public class SelectTeeActivity extends ListActivity {
	
	private long courseId;
	private Course course;
	private ListView lv;
	
	private GolfTrackerAppContext appState;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	  this.appState = ((GolfTrackerAppContext)getApplicationContext());

	  setContentView(R.layout.select_tee_page);
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
						Intent i = new Intent(SelectTeeActivity.this, PlayingGolfActivity.class);
						
						// HERE: Create the "Round" session object.
						Round round = new Round();
						round.setCourse(course);
						round.setDate(Calendar.getInstance());
						
						RoundScore roundScore = new RoundScore();
						roundScore.setTeeType(((ViewHolder) v.getTag()).teeType);
						roundScore.setUser(appState.getLoggedInUser());
						round.getRoundScore().add(roundScore);
						
						appState.setCurrentRound(round);
						
						// TODO Validate so we have all requisite info on the round object.
						
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
            holder.id = ((TeeType) getItem(position)).getId();
            holder.name.setText( ((TeeType) getItem(position)).getName());
            holder.distance.setText( "N/A");
            holder.teeType = (TeeType) getItem(position);
            
            //holder.icon = (ImageView) convertView.findViewById(R.id.poi_icon);
            
            convertView.setBackgroundColor(colors[position % colors.length]);
                       
            return convertView;
        }

        private int[] colors = new int[] { 0x00000000, 0x33000000 };

        class ViewHolder {
            Long id;
			TextView name;            
            TextView distance;
            TeeType teeType;
         //   ImageView icon;
        }
        
	}
}
