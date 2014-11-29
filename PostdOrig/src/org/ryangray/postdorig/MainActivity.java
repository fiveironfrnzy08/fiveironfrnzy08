package org.ryangray.postdorig;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
//import org.ryangray.my.top.news.R;

@TargetApi(9)
public class MainActivity extends Activity {

	public static int activities = 1;
	public static ArrayList<Source> allSources = new ArrayList<Source>();
	public static final String PREFS_CHECK_BOOL = "all_check_bool";
	private ImageView loadingPngIV = null;
	public static TextView progressTextView = null;
	private AnimationDrawable loadingPng = null;
	private final int visible = 0;
	private final int gone = 8;
	
    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity); 
        
        loadingPngIV = (ImageView) findViewById(R.id.loadingImageView);
        loadingPngIV.setBackgroundResource(R.drawable.loading_png);
        loadingPng = (AnimationDrawable) loadingPngIV.getBackground();
        loadingPngIV.setVisibility(gone);
        
        progressTextView = (TextView) findViewById(R.id.progressTextView);
        
        Button newsButton = (Button) findViewById(R.id.goToNews);
        newsButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				loadingPngIV.setVisibility(visible);
				loadingPng.start();
				allSources.clear();

		    	SharedPreferences sharedPrefs = getSharedPreferences(PREFS_CHECK_BOOL, 0);
		        sharedPrefs = getSharedPreferences(PREFS_CHECK_BOOL, 0);
				Source.createSources(sharedPrefs, MainActivity.this);
				
		    	GetHeadlines headline = new GetHeadlines();
		    	new GetFeedAsync<Object>().execute(MainActivity.this, headline);
			}
        });
        
        
        Button prefButton = (Button) findViewById(R.id.setPreferences);
        prefButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				loadingPng.setVisible(true, true);
				allSources.clear();
				startActivity(new Intent(MainActivity.this, SelectSources.class));
				finish();
			}
		});
        
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_activity_menu, menu);
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
	
	protected void onStop(){
	      super.onStop();

	      SharedPreferences sharedPrefs = getSharedPreferences(PREFS_CHECK_BOOL, 0);
	      sharedPrefs = getSharedPreferences(PREFS_CHECK_BOOL, 0);
	      SharedPreferences.Editor editor = sharedPrefs.edit();
	      for (Source source : allSources) {
	    	  editor.putBoolean(source.getName().toString(), source.getChecked());
	      }
	      editor.commit();
	}
}
