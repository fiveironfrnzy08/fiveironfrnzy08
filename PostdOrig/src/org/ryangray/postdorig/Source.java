package org.ryangray.postdorig;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources.NotFoundException;
import android.widget.CheckBox;

public class Source extends Activity {
	
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
	
	public Source(String name, String url, int imgId, int checkBoxInt, Boolean checked) {
		super();
		this.name = name;
		this.url = url;
		this.imgId = imgId;
		this.checkBoxInt = checkBoxInt;
		this.checked = checked;
	}
	
	public Source(String name, int checkBoxInt) {
		super();
		this.name = name;
		this.checkBoxInt = checkBoxInt;
	}
	
	public static void createSources(SharedPreferences sharedPrefs, Context context) throws NotFoundException {
		
//		AP
		Source apSource = new Source(
				context.getResources().getString(R.string.ap_name), 
				context.getResources().getString(R.string.url_ap), 
				R.drawable.ap, 
				R.id.apCheck, 
				sharedPrefs.getBoolean(context.getResources().getString(R.string.ap_name), true));
		MainActivity.allSources.add(apSource);
		
//		ABC News
		Source abcSource = new Source(
				context.getResources().getString(R.string.abc_name), 
				context.getResources().getString(R.string.url_abc), 
				R.drawable.abc, 
				R.id.abcCheck, 
				sharedPrefs.getBoolean(context.getResources().getString(R.string.abc_name), true));
		MainActivity.allSources.add(abcSource);

//		BBC News
		Source bbcSource = new Source(
				context.getResources().getString(R.string.bbc_name), 
				context.getResources().getString(R.string.url_bbc), 
				R.drawable.bbc,
				R.id.bbcCheck, 
				sharedPrefs.getBoolean(context.getResources().getString(R.string.bbc_name), true));
		MainActivity.allSources.add(bbcSource);
		
//		CBC News
		Source cbcSource = new Source(
				context.getResources().getString(R.string.cbc_name), 
				context.getResources().getString(R.string.url_cbc), 
				R.drawable.cbc, 
				R.id.cbcCheck, 
				sharedPrefs.getBoolean(context.getResources().getString(R.string.cbc_name), true));
		MainActivity.allSources.add(cbcSource);
		
//		CBS News
		Source cbsSource = new Source(
				context.getResources().getString(R.string.cbs_name), 
				context.getResources().getString(R.string.url_cbs), 
				R.drawable.cbs, 
				R.id.cbsCheck, 
				sharedPrefs.getBoolean(context.getResources().getString(R.string.cbs_name), true));
		MainActivity.allSources.add(cbsSource);
		
//		CNN News
		Source cnnSource = new Source(
				context.getResources().getString(R.string.cnn_name), 
				context.getResources().getString(R.string.url_cnn), 
				R.drawable.cnn, 
				R.id.cnnCheck, 
				sharedPrefs.getBoolean(context.getResources().getString(R.string.cnn_name), true));
		MainActivity.allSources.add(cnnSource);
		
//		Fox News
		Source foxSource = new Source(
				context.getResources().getString(R.string.fox_name), 
				context.getResources().getString(R.string.url_fox), 
				R.drawable.fox, 
				R.id.foxCheck, 
				sharedPrefs.getBoolean(context.getResources().getString(R.string.fox_name), true));
		MainActivity.allSources.add(foxSource);
		
//		Google News
		Source googleSource = new Source(
				context.getResources().getString(R.string.google_name), 
				context.getResources().getString(R.string.url_google), 
				R.drawable.google, 
				R.id.googleCheck, 
				sharedPrefs.getBoolean(context.getResources().getString(R.string.google_name), true));
		MainActivity.allSources.add(googleSource);
		
//		NBC News
		Source nbcSource = new Source(
				context.getResources().getString(R.string.nbc_name), 
				context.getResources().getString(R.string.url_nbc), 
				R.drawable.nbc, 
				R.id.nbcCheck, 
				sharedPrefs.getBoolean(context.getResources().getString(R.string.nbc_name), true));
		MainActivity.allSources.add(nbcSource);
		
//		Reuters
		Source reutersSource = new Source(
				context.getResources().getString(R.string.reuters_name), 
				context.getResources().getString(R.string.url_reuters), 
				R.drawable.reuters, 
				R.id.reutersCheck, 
				sharedPrefs.getBoolean(context.getResources().getString(R.string.reuters_name), true));
		MainActivity.allSources.add(reutersSource);
		
//		Time
		Source timeSource = new Source(
				context.getResources().getString(R.string.time_name), 
				context.getResources().getString(R.string.url_time), 
				R.drawable.time, 
				R.id.timeCheck, 
				sharedPrefs.getBoolean(context.getResources().getString(R.string.time_name), true));
		MainActivity.allSources.add(timeSource);
		
//		USA Today
		Source usatodaySource = new Source(
				context.getResources().getString(R.string.usatoday_name), 
				context.getResources().getString(R.string.url_usatoday), 
				R.drawable.usatoday, 
				R.id.usatodayCheck, 
				sharedPrefs.getBoolean(context.getResources().getString(R.string.usatoday_name), true));
		MainActivity.allSources.add(usatodaySource);
		
//		World News
		Source wnSource = new Source(
				context.getResources().getString(R.string.wn_name), 
				context.getResources().getString(R.string.url_wn), 
				R.drawable.wn, 
				R.id.wnCheck, 
				sharedPrefs.getBoolean(context.getResources().getString(R.string.wn_name), true));
		MainActivity.allSources.add(wnSource);
		
//		Yahoo News
		Source yahooSource = new Source(
				context.getResources().getString(R.string.yahoo_name), 
				context.getResources().getString(R.string.url_yahoo), 
				R.drawable.yahoo, 
				R.id.yahooCheck, 
				sharedPrefs.getBoolean(context.getResources().getString(R.string.yahoo_name), true));
		MainActivity.allSources.add(yahooSource);
		
	}

	public static void createSources(Context context) {
		
//		AP
		Source apSource = new Source(
				context.getResources().getString(R.string.ap_name),
				R.id.apCheck); 
		MainActivity.allSources.add(apSource);
		
//		ABC News
		Source abcSource = new Source(
				context.getResources().getString(R.string.abc_name),
				R.id.abcCheck); 
		MainActivity.allSources.add(abcSource);

//		BBC News
		Source bbcSource = new Source(
				context.getResources().getString(R.string.bbc_name),
				R.id.bbcCheck);
		MainActivity.allSources.add(bbcSource);
		
//		CBC News
		Source cbcSource = new Source(
				context.getResources().getString(R.string.cbc_name),
				R.id.cbcCheck); 
		MainActivity.allSources.add(cbcSource);
		
//		CBS News
		Source cbsSource = new Source(
				context.getResources().getString(R.string.cbs_name),
				R.id.cbsCheck); 
		MainActivity.allSources.add(cbsSource);
		
//		CNN News
		Source cnnSource = new Source(
				context.getResources().getString(R.string.cnn_name),
				R.id.cnnCheck); 
		MainActivity.allSources.add(cnnSource);
		
//		Fox News
		Source foxSource = new Source(
				context.getResources().getString(R.string.fox_name),
				R.id.foxCheck); 
		MainActivity.allSources.add(foxSource);
		
//		Google News
		Source googleSource = new Source(
				context.getResources().getString(R.string.google_name),
				R.id.googleCheck); 
		MainActivity.allSources.add(googleSource);
		
//		NBC News
		Source nbcSource = new Source(
				context.getResources().getString(R.string.nbc_name),
				R.id.nbcCheck); 
		MainActivity.allSources.add(nbcSource);
		
//		Reuters News
		Source reutersSource = new Source(
				context.getResources().getString(R.string.reuters_name),
				R.id.reutersCheck); 
		MainActivity.allSources.add(reutersSource);
		
//		Time News
		Source timeSource = new Source(
				context.getResources().getString(R.string.time_name),
				R.id.timeCheck); 
		MainActivity.allSources.add(timeSource);
		
//		USA Today News
		Source usatodaySource = new Source(
				context.getResources().getString(R.string.usatoday_name),
				R.id.usatodayCheck); 
		MainActivity.allSources.add(usatodaySource);
		
//		World News
		Source wnSource = new Source(
				context.getResources().getString(R.string.wn_name),
				R.id.wnCheck); 
		MainActivity.allSources.add(wnSource);
		
//		Yahoo News
		Source yahooSource = new Source(
				context.getResources().getString(R.string.yahoo_name),
				R.id.yahooCheck); 
		MainActivity.allSources.add(yahooSource);
		
	}
	
}
