package com.squeed.golftracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.squeed.golftracker.entity.PointOfInterestDTO;
import com.squeed.golftracker.helper.DbHelper;
import com.squeed.golftracker.helper.UserAgent;

public class NewPoiActivity extends Activity {

	private DbHelper dbHelper;
	private Long holeId;
	private LocationManager locMgr;
	private Location gpsLocation = null;

	private static final int YELLOW_TEE = 0;
	private static final int RED_TEE = 1;
	private static final int GREEN = 2;
	private static final int BUNKER = 3;
	private static final int OTHER = 4;
	
	private int selectedTypeId = -1;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_poi_page);
		
		Double lat = getIntent().getDoubleExtra("lat", -1);
		Double lon = getIntent().getDoubleExtra("lon", -1);
		if(lat != null && lat != -1 && lon != null && lon != -1) {
			// We already have coords... log this.
			Log.i("NewPoiActivity", "ENTER - onCreate(). We got lon/lat: " + lon + " / " + lat);
			((ProgressBar) findViewById(R.id.progressBar1)).setVisibility(ProgressBar.INVISIBLE);
			((TextView) findViewById(R.id.latValue)).setText(Double.toString(lat));
			((TextView) findViewById(R.id.longValue)).setText(Double.toString(lon));
			
			gpsLocation = new Location("");
			gpsLocation.setLatitude(lat);
			gpsLocation.setLongitude(lon);
		} else {
			
			// If no location were supplied, get a fix from the GPS service.
			locMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			locMgr.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);
			((ProgressBar) findViewById(R.id.progressBar1)).setIndeterminate(true);
		}
		
		holeId = getIntent().getLongExtra("hole_id", -1L);
		setupButtons();
		
		dbHelper = new DbHelper(this);
	}
	
	LocationListener locationListener = new LocationListener() {
		public void onLocationChanged(Location location) {			
			Log.i("onLocationChanged", "Location updated...");
			((ProgressBar) findViewById(R.id.progressBar1)).setVisibility(ProgressBar.GONE);
			gpsLocation = location;
			((TextView) findViewById(R.id.latValue)).setText(Double.toString(location.getLatitude()));
			((TextView) findViewById(R.id.longValue)).setText(Double.toString(location.getLongitude()));
		}

		public void onProviderDisabled(String provider) {
			
		}

		public void onProviderEnabled(String provider) {
			
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			
		}
	};

	private void setupButtons() {
		((Button) findViewById(R.id.button1)).setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				resetForm();
				selectedTypeId = YELLOW_TEE;
				showSaveBtn();
			}
		});
		
		((Button) findViewById(R.id.button2)).setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				resetForm();
				selectedTypeId = RED_TEE;
				showSaveBtn();
			}
		});
		
		// Green. When clicked, show front/mid/back radio group.
		((Button) findViewById(R.id.button3)).setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				resetForm();
				((RadioGroup) findViewById(R.id.greenPointRadioGrp)).setVisibility(RadioGroup.VISIBLE);
				selectedTypeId = GREEN;
				// TODO only show save btn when user has selected green poi position.
				showSaveBtn();
			}
		});
		
		// Bunker. When clicked, show fw/green radio group.
		((Button) findViewById(R.id.button4)).setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				resetForm();
				Log.i("NewPoiActivity", "ENTER - Bunker listener");
				((RadioGroup) findViewById(R.id.bunkerTypeRadioGrp)).setVisibility(RadioGroup.VISIBLE);
				selectedTypeId = BUNKER;
			}
		});
		
		((RadioButton) findViewById(R.id.bunkerTypeFw)).setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				((RadioGroup) findViewById(R.id.greenBunkerPosRadioGrp)).setVisibility(RadioGroup.GONE);
				((RadioGroup) findViewById(R.id.fwBunkerPosRadioGrp)).setVisibility(RadioGroup.VISIBLE);
				showSaveBtn();
			}
		});
		
		((RadioButton) findViewById(R.id.bunkerTypeGreen)).setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				((RadioGroup) findViewById(R.id.fwBunkerPosRadioGrp)).setVisibility(RadioGroup.GONE);
				((RadioGroup) findViewById(R.id.greenBunkerPosRadioGrp)).setVisibility(RadioGroup.VISIBLE);
				showSaveBtn();
			}
		});
		
		// Save button...
		((Button) findViewById(R.id.poiSaveBtn)).setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				//((Button) findViewById(R.id.poiSaveBtn)).setVisibility(RadioGroup.GONE);
				if(!hasCoordinate()) {
					return;
				}
				
				switch(selectedTypeId) {
					case YELLOW_TEE:						
						savePoiAndFinish("yt");
						break;
					case RED_TEE:
						savePoiAndFinish("rt");
						break;
					case GREEN:
						int checkedRadioButtonId = ((RadioGroup) findViewById(R.id.greenPointRadioGrp)).getCheckedRadioButtonId();
						RadioButton rb = (RadioButton) findViewById(checkedRadioButtonId);
						String tag = (String) rb.getTag();
						savePoiAndFinish(tag);
						break;
					case BUNKER:
						int bunkerTypeRadioButtonId = ((RadioGroup) findViewById(R.id.bunkerTypeRadioGrp)).getCheckedRadioButtonId();
						if(bunkerTypeRadioButtonId == R.id.bunkerTypeFw) {
							// Fairway
							int fwBunkerPositionRadioButtonId = ((RadioGroup) findViewById(R.id.fwBunkerPosRadioGrp)).getCheckedRadioButtonId();
							RadioButton bunkerRb = (RadioButton) findViewById(fwBunkerPositionRadioButtonId);
							String bunkerTag = (String) bunkerRb.getTag();
							savePoiAndFinish(bunkerTag);
						}
						if(bunkerTypeRadioButtonId == R.id.bunkerTypeGreen) {
							// Green
							int greenBunkerPositionRadioButtonId = ((RadioGroup) findViewById(R.id.greenBunkerPosRadioGrp)).getCheckedRadioButtonId();
							RadioButton greenBunkerRb = (RadioButton) findViewById(greenBunkerPositionRadioButtonId);
							String bunkerTag = (String) greenBunkerRb.getTag();
							savePoiAndFinish(bunkerTag);
						}
						break;
				}
			}

			
		});
			
	}
	
	private void resetForm() {
		((Button) findViewById(R.id.poiSaveBtn)).setVisibility(Button.INVISIBLE);
		((RadioGroup) findViewById(R.id.fwBunkerPosRadioGrp)).setVisibility(RadioGroup.GONE);
		((RadioGroup) findViewById(R.id.greenBunkerPosRadioGrp)).setVisibility(RadioGroup.GONE);
		((RadioGroup) findViewById(R.id.bunkerTypeRadioGrp)).setVisibility(RadioGroup.GONE);
		((RadioGroup) findViewById(R.id.greenPointRadioGrp)).setVisibility(RadioGroup.GONE);
		
		// Reset all radiobutton selections
		
		((RadioButton) findViewById(R.id.radio0)).setChecked(false);
		((RadioButton) findViewById(R.id.radio1)).setChecked(false);
		((RadioButton) findViewById(R.id.radio2)).setChecked(false);
		
		((RadioButton) findViewById(R.id.bunkerTypeFw)).setChecked(false);
		((RadioButton) findViewById(R.id.bunkerTypeGreen)).setChecked(false);
		
		((RadioButton) findViewById(R.id.leftRadioBtn)).setChecked(false);
		((RadioButton) findViewById(R.id.leftRadioBtn2)).setChecked(false);
		((RadioButton) findViewById(R.id.rightRadioBtn)).setChecked(false);
		((RadioButton) findViewById(R.id.rightRadioBtn2)).setChecked(false);
	
		((RadioButton) findViewById(R.id.frontRadioBtn)).setChecked(false);
		((RadioButton) findViewById(R.id.backRadioBtn)).setChecked(false);
	}
	
	private void showSaveBtn() {
		((Button) findViewById(R.id.poiSaveBtn)).setVisibility(Button.VISIBLE);
	}
	
	private void savePoiAndFinish(String tag) {
		Long id = dbHelper.createNewPoi(dbHelper.getWritableDatabase(), holeId, gpsLocation, tag, getTypeText(selectedTypeId, tag), UserAgent.OWNER_ID);
		PointOfInterestDTO poi = new PointOfInterestDTO(id, tag, getTypeText(selectedTypeId, tag), gpsLocation.getLatitude(), gpsLocation.getLongitude());
		
		Intent data = new Intent();
		data.putExtra("poi", poi);
		NewPoiActivity.this.setResult(Activity.RESULT_OK, data);
		NewPoiActivity.this.finish();
	}
	
	private String getTypeText(int typeCode, String type) {
		if(typeCode == YELLOW_TEE) {
			return "Gul tee";
		}
		if(typeCode == RED_TEE) {
			return "Röd tee";
		}
		
		if(typeCode == GREEN) {
			if(type.equals("fg")) {
				return "Framkant green";
			}
			if(type.equals("mg")) {
				return "Mitten green";
			}
			if(type.equals("bg")) {
				return "Bakkant green";
			}
		}
		if(typeCode == BUNKER) {
			if(type.equals("fbl")) {
				return "Vänster fairwaybunker";
			}
			if(type.equals("fbr")) {
				return "Höger fairwaybunker";
			}
			if(type.equals("gbf")) {
				return "Framförliggande greenbunker";
			}
			if(type.equals("gbl")) {
				return "Vänster greenbunker";
			}
			if(type.equals("gbr")) {
				return "Höger greenbunker";
			}
			if(type.equals("gbb")) {
				return "Bakomliggande greenbunker";
			}
		}
		return "---";
	}
	
	private boolean hasCoordinate() {
		if(gpsLocation == null) {
			Toast.makeText(
					NewPoiActivity.this,
					"Ingen GPS-koordinat låst ännu, försök strax igen",
					Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}
}
