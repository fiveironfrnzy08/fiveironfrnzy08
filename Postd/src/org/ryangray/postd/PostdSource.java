package org.ryangray.postd;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources.NotFoundException;
import android.widget.CheckBox;

public class PostdSource extends Activity {
	
	public String name;
	public String url;
	public int imgId;
	public int checkBoxInt;
	public CheckBox checkBox;
	public Boolean checked;
	
//	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
//	
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
//	
	
	public int getImgId() {
		return imgId;
	}

	public void setImgId(int imgId) {
		this.imgId = imgId;
	}
	
//	

	public int getCheckBoxInt() {
		return checkBoxInt;
	}

	public void setCheckBoxInt(int checkBoxInt) {
		this.checkBoxInt = checkBoxInt;
	}

//	

	public CheckBox getCheckBox() {
		return checkBox;
	}

	public void setCheckBox(CheckBox checkBox) {
		this.checkBox = checkBox;
	}
	
//	
	
	public Boolean getChecked() {
		return checked;
	}

	public void setChecked(Boolean checked) {
		this.checked = checked;
	}

//	
	
	public PostdSource(String name, String url, int imgId, int checkBoxInt, Boolean checked) {
		super();
		this.name = name;
		this.url = url;
		this.imgId = imgId;
		this.checkBoxInt = checkBoxInt;
		this.checked = checked;
	}
	
	public PostdSource(String name, int checkBoxInt) {
		super();
		this.name = name;
		this.checkBoxInt = checkBoxInt;
	}
	
	public static void createSources(SharedPreferences sharedPrefs, Context context) throws NotFoundException {
		
//		ABC News
		PostdSource abcSource = new PostdSource(
				context.getResources().getString(R.string.abc_name), 
				context.getResources().getString(R.string.url_abc), 
				R.drawable.abc, 
				R.id.abcCheck, 
				sharedPrefs.getBoolean(context.getResources().getString(R.string.abc_name), true));
		PostdMainActivity.allSources.add(abcSource);
		
//		AP
		PostdSource apSource = new PostdSource(
				context.getResources().getString(R.string.ap_name), 
				context.getResources().getString(R.string.url_ap), 
				R.drawable.ap, 
				R.id.apCheck, 
				sharedPrefs.getBoolean(context.getResources().getString(R.string.ap_name), true));
		PostdMainActivity.allSources.add(apSource);

//		BBC News
		PostdSource bbcSource = new PostdSource(
				context.getResources().getString(R.string.bbc_name), 
				context.getResources().getString(R.string.url_bbc), 
				R.drawable.bbc,
				R.id.bbcCheck, 
				sharedPrefs.getBoolean(context.getResources().getString(R.string.bbc_name), true));
		PostdMainActivity.allSources.add(bbcSource);
		
//		CBC News
		PostdSource cbcSource = new PostdSource(
				context.getResources().getString(R.string.cbc_name), 
				context.getResources().getString(R.string.url_cbc), 
				R.drawable.cbc, 
				R.id.cbcCheck, 
				sharedPrefs.getBoolean(context.getResources().getString(R.string.cbc_name), true));
		PostdMainActivity.allSources.add(cbcSource);
		
//		CBS News
		PostdSource cbsSource = new PostdSource(
				context.getResources().getString(R.string.cbs_name), 
				context.getResources().getString(R.string.url_cbs), 
				R.drawable.cbs, 
				R.id.cbsCheck, 
				sharedPrefs.getBoolean(context.getResources().getString(R.string.cbs_name), true));
		PostdMainActivity.allSources.add(cbsSource);
		
//		CNN News
		PostdSource cnnSource = new PostdSource(
				context.getResources().getString(R.string.cnn_name), 
				context.getResources().getString(R.string.url_cnn), 
				R.drawable.cnn, 
				R.id.cnnCheck, 
				sharedPrefs.getBoolean(context.getResources().getString(R.string.cnn_name), true));
		PostdMainActivity.allSources.add(cnnSource);
		
//		Fox News
		PostdSource foxSource = new PostdSource(
				context.getResources().getString(R.string.fox_name), 
				context.getResources().getString(R.string.url_fox), 
				R.drawable.fox, 
				R.id.foxCheck, 
				sharedPrefs.getBoolean(context.getResources().getString(R.string.fox_name), true));
		PostdMainActivity.allSources.add(foxSource);
		
//		Google News
		PostdSource googleSource = new PostdSource(
				context.getResources().getString(R.string.google_name), 
				context.getResources().getString(R.string.url_google), 
				R.drawable.google, 
				R.id.googleCheck, 
				sharedPrefs.getBoolean(context.getResources().getString(R.string.google_name), true));
		PostdMainActivity.allSources.add(googleSource);
		
//		NBC News
		PostdSource nbcSource = new PostdSource(
				context.getResources().getString(R.string.nbc_name), 
				context.getResources().getString(R.string.url_nbc), 
				R.drawable.nbc, 
				R.id.nbcCheck, 
				sharedPrefs.getBoolean(context.getResources().getString(R.string.nbc_name), true));
		PostdMainActivity.allSources.add(nbcSource);
		
//		Reuters
		PostdSource reutersSource = new PostdSource(
				context.getResources().getString(R.string.reuters_name), 
				context.getResources().getString(R.string.url_reuters), 
				R.drawable.reuters, 
				R.id.reutersCheck, 
				sharedPrefs.getBoolean(context.getResources().getString(R.string.reuters_name), true));
		PostdMainActivity.allSources.add(reutersSource);
		
//		Time
		PostdSource timeSource = new PostdSource(
				context.getResources().getString(R.string.time_name), 
				context.getResources().getString(R.string.url_time), 
				R.drawable.time, 
				R.id.timeCheck, 
				sharedPrefs.getBoolean(context.getResources().getString(R.string.time_name), true));
		PostdMainActivity.allSources.add(timeSource);
		
//		USA Today
		PostdSource usatodaySource = new PostdSource(
				context.getResources().getString(R.string.usatoday_name), 
				context.getResources().getString(R.string.url_usatoday), 
				R.drawable.usatoday, 
				R.id.usatodayCheck, 
				sharedPrefs.getBoolean(context.getResources().getString(R.string.usatoday_name), true));
		PostdMainActivity.allSources.add(usatodaySource);
		
//		World News
		PostdSource wnSource = new PostdSource(
				context.getResources().getString(R.string.wn_name), 
				context.getResources().getString(R.string.url_wn), 
				R.drawable.wn, 
				R.id.wnCheck, 
				sharedPrefs.getBoolean(context.getResources().getString(R.string.wn_name), true));
		PostdMainActivity.allSources.add(wnSource);
		
//		Yahoo News
		PostdSource yahooSource = new PostdSource(
				context.getResources().getString(R.string.yahoo_name), 
				context.getResources().getString(R.string.url_yahoo), 
				R.drawable.yahoo, 
				R.id.yahooCheck, 
				sharedPrefs.getBoolean(context.getResources().getString(R.string.yahoo_name), true));
		PostdMainActivity.allSources.add(yahooSource);
		
	}

	public static void createSources(Context context) {
		
//		ABC News
		PostdSource abcSource = new PostdSource(
				context.getResources().getString(R.string.abc_name),
				R.id.abcCheck); 
		PostdMainActivity.allSources.add(abcSource);

//		AP
		PostdSource apSource = new PostdSource(
				context.getResources().getString(R.string.ap_name),
				R.id.apCheck); 
		PostdMainActivity.allSources.add(apSource);

//		BBC News
		PostdSource bbcSource = new PostdSource(
				context.getResources().getString(R.string.bbc_name),
				R.id.bbcCheck);
		PostdMainActivity.allSources.add(bbcSource);
		
//		CBC News
		PostdSource cbcSource = new PostdSource(
				context.getResources().getString(R.string.cbc_name),
				R.id.cbcCheck); 
		PostdMainActivity.allSources.add(cbcSource);
		
//		CBS News
		PostdSource cbsSource = new PostdSource(
				context.getResources().getString(R.string.cbs_name),
				R.id.cbsCheck); 
		PostdMainActivity.allSources.add(cbsSource);
		
//		CNN News
		PostdSource cnnSource = new PostdSource(
				context.getResources().getString(R.string.cnn_name),
				R.id.cnnCheck); 
		PostdMainActivity.allSources.add(cnnSource);
		
//		Fox News
		PostdSource foxSource = new PostdSource(
				context.getResources().getString(R.string.fox_name),
				R.id.foxCheck); 
		PostdMainActivity.allSources.add(foxSource);
		
//		Google News
		PostdSource googleSource = new PostdSource(
				context.getResources().getString(R.string.google_name),
				R.id.googleCheck); 
		PostdMainActivity.allSources.add(googleSource);
		
//		NBC News
		PostdSource nbcSource = new PostdSource(
				context.getResources().getString(R.string.nbc_name),
				R.id.nbcCheck); 
		PostdMainActivity.allSources.add(nbcSource);
		
//		Reuters News
		PostdSource reutersSource = new PostdSource(
				context.getResources().getString(R.string.reuters_name),
				R.id.reutersCheck); 
		PostdMainActivity.allSources.add(reutersSource);
		
//		Time News
		PostdSource timeSource = new PostdSource(
				context.getResources().getString(R.string.time_name),
				R.id.timeCheck); 
		PostdMainActivity.allSources.add(timeSource);
		
//		USA Today News
		PostdSource usatodaySource = new PostdSource(
				context.getResources().getString(R.string.usatoday_name),
				R.id.usatodayCheck); 
		PostdMainActivity.allSources.add(usatodaySource);
		
//		World News
		PostdSource wnSource = new PostdSource(
				context.getResources().getString(R.string.wn_name),
				R.id.wnCheck); 
		PostdMainActivity.allSources.add(wnSource);
		
//		Yahoo News
		PostdSource yahooSource = new PostdSource(
				context.getResources().getString(R.string.yahoo_name),
				R.id.yahooCheck); 
		PostdMainActivity.allSources.add(yahooSource);
		
	}
	
}
