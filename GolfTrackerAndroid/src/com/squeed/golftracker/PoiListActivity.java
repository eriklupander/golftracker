package com.squeed.golftracker;

import android.app.ListActivity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squeed.golftracker.entity.HoleDTO;
import com.squeed.golftracker.entity.PointOfInterestDTO;
import com.squeed.golftracker.helper.LongLatConverter;

public class PoiListActivity extends ListActivity {

	private HoleDTO hole;
	private Location lastLocation;
	private PointOfInterestDTO[] pois;
	private LocationManager locMgr;
	private ListView lv;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	  setContentView(R.layout.poi_list_page);
	  hole = (HoleDTO) getIntent().getSerializableExtra("hole");
	 
	  pois = new PointOfInterestDTO[hole.getPois().size()];
	  int i = 0;
	  for(PointOfInterestDTO poi : hole.getPois()) {
		  pois[i++] = (PointOfInterestDTO) poi;
	  }
	  
	  ((TextView) findViewById(R.id.poi_list_header_text)).setText("Hål " + hole.getNumber() + " Par " + hole.getPar() + " Hcp " + hole.getId());
	  ((Button) findViewById(R.id.poi_list_refresh_button)).setOnClickListener(new OnClickListener() {
		
		public void onClick(View v) {
			((LocationManager) getSystemService(Context.LOCATION_SERVICE)).requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);
		}
	  });
	  
	  lv = getListView();
	  lv.setTextFilterEnabled(true);
	 
	  EfficientAdapter efficientAdapter = new EfficientAdapter(this);
	  setListAdapter(efficientAdapter);

	  
	  
	  //	  int[] colors = {0, 0xFFFF0000, 0}; // red for the example
//	  lv.setDivider(new GradientDrawable(Orientation.RIGHT_LEFT, colors));
//	  lv.setDividerHeight(1);

	  locMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	  locMgr.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);
	}
	
	LocationListener locationListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			lastLocation = location;
			PoiListActivity.this.setListAdapter(new EfficientAdapter(PoiListActivity.this));
		}

		public void onProviderDisabled(String arg0) {
			
		}

		public void onProviderEnabled(String provider) {
			
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			
		}
	};
	
	private int[] colors = new int[] { 0x00000000, 0x33000000 };
	
	class EfficientAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private Bitmap mIcon1;
        private Bitmap mIcon2;
        private Bitmap mIcon3;
        private Bitmap mIcon4;

        public EfficientAdapter(Context context) {
            // Cache the LayoutInflate to avoid asking for a new one each time.
            mInflater = LayoutInflater.from(context);
            
            mIcon1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.green_icon);
            mIcon2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.bunker_icon);
            mIcon3 = BitmapFactory.decodeResource(context.getResources(), R.drawable.yel_tee_icon);
            mIcon4 = BitmapFactory.decodeResource(context.getResources(), R.drawable.red_tee_icon);
        }
        
        public int getCount() {
            return hole.getPois().size();
        }
        
        public Object getItem(int position) {
            return pois[position];
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
                convertView = mInflater.inflate(R.layout.poi_list_item, null);

                // Creates a ViewHolder and store references to the two children views
                // we want to bind data to.
                holder = new ViewHolder();
                holder.name = (TextView) convertView.findViewById(R.id.poi_name);
                holder.distance = (TextView) convertView.findViewById(R.id.poi_distance);
                
                convertView.setVerticalFadingEdgeEnabled(true);
               
                holder.icon = (ImageView) convertView.findViewById(R.id.poi_icon);

                convertView.setTag(holder);
            } else {
                // Get the ViewHolder back to get fast access to the TextView
                // and the ImageView.
                holder = (ViewHolder) convertView.getTag();
            }
            
            // Bind the data efficiently with the holder.
            holder.id = ((PointOfInterestDTO) getItem(position)).getId();
            holder.name.setText( ((PointOfInterestDTO) getItem(position)).getName());
            //holder.type.setText( ((PointOfInterest) getItem(position)).getType());
            if(lastLocation != null) {
            	holder.distance.setText( LongLatConverter.getDistance(lastLocation, (PointOfInterestDTO) getItem(position)) + " m");
            }
            String type = ((PointOfInterestDTO) getItem(position)).getType();
            if(type.equals("fg") || type.equals("mg") || type.equals("bg")) {
            	holder.icon.setImageBitmap(mIcon1);
            } else if(isBunker(type)) {
            	holder.icon.setImageBitmap(mIcon2);
            } else if(type.equals("yt")){
            	holder.icon.setImageBitmap(mIcon3);
            } else if(type.equals("rt")){
                holder.icon.setImageBitmap(mIcon4);
            }
            holder.icon = (ImageView) convertView.findViewById(R.id.poi_icon);
            
            convertView.setBackgroundColor(colors[position % colors.length]);
                       
            return convertView;
        }

		

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
			locMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			locMgr.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);
			break;
		case 2:
			PoiListActivity.this.finish();
			break;
		}
		return true;
	}
	
}
