package com.squeed.golftracker;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ViewSwitcher;

import com.squeed.golftracker.common.model.Club;
import com.squeed.golftracker.common.model.Hole;
import com.squeed.golftracker.common.model.HoleScore;
import com.squeed.golftracker.helper.DbHelper;
import com.squeed.golftracker.helper.StaticDataStore;
import com.squeed.ui.CustomSpinnerView;
import com.squeed.ui.MyListItem;
import com.squeed.ui.OnCustomSpinnerSelectListener;

public class HoleScoreActivity extends Activity {
	
	private Hole currentHole;
	private Round currentRound;
	private int currentScore;
	private ViewSwitcher switcher;
	
	private HoleScore dto;
	
	Bitmap[] icons;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hole_score);
		switcher = (ViewSwitcher) findViewById(R.id.scoreSwitcher);
		
		this.currentHole = (Hole) getIntent().getSerializableExtra("currentHole");
		this.currentRound = (Round) getIntent().getSerializableExtra("currentRound");
		this.currentScore = currentHole.getPar();
		final DbHelper db = new DbHelper(this);
		
//		final CustomSpinnerView clubCsv = (CustomSpinnerView) findViewById(R.id.clubSpinner);
//		List<MyListItem> l = new ArrayList<MyListItem>();
//		
//		Bitmap[] icons = new Bitmap[5];
		icons = new Bitmap[8];
		icons[4] = BitmapFactory.decodeResource(getResources(), R.drawable.wedge);
		icons[3] = BitmapFactory.decodeResource(getResources(), R.drawable.iron);
		icons[2] = BitmapFactory.decodeResource(getResources(), R.drawable.hybrid);
		icons[1] = BitmapFactory.decodeResource(getResources(), R.drawable.fwood);
		icons[0] = BitmapFactory.decodeResource(getResources(), R.drawable.driver);
		
		icons[5] = BitmapFactory.decodeResource(getResources(), R.drawable.draw);
		icons[6] = BitmapFactory.decodeResource(getResources(), R.drawable.straight);
		icons[7] = BitmapFactory.decodeResource(getResources(), R.drawable.fade);
//		
//		List<Club> clubs = db.getClubs(db.getReadableDatabase());
//		for(Club c : clubs) {
//			
//			l.add(new MyListItem(c.getId(), c.getLongName(), null, icons[c.getIconId()]));
//		}
//		clubCsv.setItems(l.toArray(new MyListItem[]{}));
//		clubCsv.setSelectedIndex(0);
		
		final CustomSpinnerView puttsSpinner = (CustomSpinnerView) findViewById(R.id.puttsSpinner);
		puttsSpinner.setItems(StaticDataStore.getPutts(currentHole.getPar(), currentScore));
		
		final CustomSpinnerView resultSpinner = (CustomSpinnerView) findViewById(R.id.resultSpinner);
		resultSpinner.setItems(StaticDataStore.getHoleResult(currentHole.getPar()));
		resultSpinner.setOnCustomSpinnerSelectListener(new OnCustomSpinnerSelectListener() {
		
			
			
			@Override
			public void onSelect(MyListItem item) {
				puttsSpinner.setItems(StaticDataStore.getPutts(currentHole.getPar(), item.getId().intValue()));
				puttsSpinner.setSelectedIndex(0);
			}
		});
		
		
		
		
		
		((Button) findViewById(R.id.nextBtn)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dto = new HoleScore(-1L, -1L, new Date(), currentHole.getId(),
						currentRound.getId(), resultSpinner.getSelectedItemId().intValue(), 
						puttsSpinner.getSelectedItemId().intValue(), null, null, null, null, -1L);
				Log.i("HoleScoreActivity", "Change view!");
				switcher.showNext();
				
				// Clubs...	
				final CustomSpinnerView clubSpinner = (CustomSpinnerView) findViewById(R.id.clubSpinnr);
				List<Club> clubs = db.getClubs(db.getReadableDatabase());
				List<MyListItem> l = new ArrayList<MyListItem>();
				for(Club c : clubs) {
					
					l.add(new MyListItem(c.getId(), c.getLongName(), null, icons[c.getIconId()]));
				}
				clubSpinner.setItems(l.toArray(new MyListItem[]{}));
				clubSpinner.setSelectedIndex(0);
				clubSpinner.invalidate();
				clubSpinner.forceLayout();
				
				// Bunker shots
				final CustomSpinnerView bunkerSpinner = (CustomSpinnerView) findViewById(R.id.bunkerShotsSpinner);
				bunkerSpinner.setItems(StaticDataStore.getNumbers(0, 10));
				bunkerSpinner.invalidate();
				bunkerSpinner.forceLayout();
				
				// Penalties
				final CustomSpinnerView penaltiesSpinner = (CustomSpinnerView) findViewById(R.id.penaltySpinner);
				penaltiesSpinner.setItems(StaticDataStore.getNumbers(0, 10));
				penaltiesSpinner.forceLayout();
			}
		});
	}
}
