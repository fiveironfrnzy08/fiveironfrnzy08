package org.ryangray.postdorig;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

@TargetApi(9)
public class SelectSources extends Activity implements OnClickListener{
	
	public static final String PREFS_CHECK_BOOL = "all_check_bool";
	private ImageView loadingPngIV = null;
	private TextView loadingTV = null;
	private AnimationDrawable loadingPng = null;
	private final int visible = 0;
	private final int gone = 8;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_sources);
        
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        
	    loadingPngIV = (ImageView) findViewById(R.id.loadingImageView);
        loadingPngIV.setBackgroundResource(R.drawable.loading_png);
        loadingPng = (AnimationDrawable) loadingPngIV.getBackground();
        loadingPngIV.setVisibility(gone);
        loadingTV = (TextView) findViewById(R.id.progressTextView);
        
        MainActivity.allSources.clear();
    	SharedPreferences sharedPrefs = getSharedPreferences(PREFS_CHECK_BOOL, 0);
        Source.createSources(sharedPrefs, this);
        
        if (MainActivity.allSources != null) {
        	sharedPrefs = getSharedPreferences(PREFS_CHECK_BOOL, 0);
        	int checkBoxInt;
        	CheckBox checkBox;
            for (Source source : MainActivity.allSources) {
            	checkBoxInt = source.getCheckBoxInt();
            	checkBox = (CheckBox) findViewById(checkBoxInt);
            	String name = source.getName();
            	checkBox.setChecked(sharedPrefs.getBoolean(name, false));
    		}
		}
        
        Button checkAll = (Button) findViewById(R.id.checkAll);
        checkAll.setOnClickListener(this);
        Button uncheckAll = (Button) findViewById(R.id.uncheckAll);
        uncheckAll.setOnClickListener(this);
        Button toBottom = (Button) findViewById(R.id.toBottom);
        toBottom.setOnClickListener(this);
        Button toTop = (Button) findViewById(R.id.toTop);
        toTop.setOnClickListener(this);
        Button showNews = (Button) findViewById(R.id.goToNews);
        showNews.setOnClickListener(this);
        
	}
	
		public void onClick(View v) {
			if(v.getId() == R.id.checkAll){
				for (Source source : MainActivity.allSources) {
					CheckBox checkBox = (CheckBox) findViewById(source.getCheckBoxInt());
					checkBox.setChecked(true);
				}
			} else if (v.getId() == R.id.uncheckAll) {
				for (Source source : MainActivity.allSources) {
					CheckBox checkBox = (CheckBox) findViewById(source.getCheckBoxInt());
					checkBox.setChecked(false);
				}
			} else if (v.getId() == R.id.toBottom) {
				ScrollView scroll = (ScrollView) findViewById(R.id.scrollView1);
				scroll.fullScroll(ScrollView.FOCUS_DOWN);
			} else if (v.getId() == R.id.toTop) {
				ScrollView scroll = (ScrollView) findViewById(R.id.scrollView1);
				scroll.fullScroll(ScrollView.FOCUS_UP); 
			} else if (v.getId() == R.id.goToNews) {
				loadingTV.setVisibility(visible);
				loadingPngIV.setVisibility(visible);
				loadingPng.start();
				goToNews();
			} else {
				Log.d("RyanDebug", "onClick in SelectSources couldn't resolve v.getId() to find the button pressed");
			}
		}
	
		public void goToNews() {
			MainActivity.allSources.clear();
			SharedPreferences sharedPrefs = getSharedPreferences(PREFS_CHECK_BOOL, 0);
			SharedPreferences.Editor editor = sharedPrefs.edit();
			
			Source.createSources(SelectSources.this);
			for (Source source : MainActivity.allSources) {
				CheckBox checkBox = (CheckBox) findViewById(source.getCheckBoxInt());
				editor.putBoolean(source.getName().toString(), checkBox.isChecked());
			}

			editor.commit();
			
			sharedPrefs = getSharedPreferences(PREFS_CHECK_BOOL, 0);
			MainActivity.allSources.clear();
			Source.createSources(sharedPrefs, this);
			
			GetHeadlines headline = new GetHeadlines();
	    	new GetFeedAsync<Object>().execute(SelectSources.this, headline);
	    	MainActivity.activities++;
		}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.select_sources_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.exitOption) {
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		loadingPngIV.setVisibility(gone);
	}

}
