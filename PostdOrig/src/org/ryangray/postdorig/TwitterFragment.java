package org.ryangray.postdorig;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class TwitterFragment extends DialogFragment {
	
//	int mNum;
	EditText tweetText;
	TextView charCount;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int style = DialogFragment.STYLE_NO_TITLE, theme = 0;
        setStyle(style, theme);
    }
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tweet_dialog, container, false);	        
        
     // Watch for button clicks.
        tweetText = (EditText)v.findViewById(R.id.tweetText);
        tweetText.setText("\n" + NewsActivity.twitterArticleURL);
        
        charCount = (TextView) v.findViewById(R.id.charCount);
        charCount.setText("" + (140 - tweetText.getText().length()));
        
        tweetText.addTextChangedListener(new TextWatcher() {
			
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				charCount.setText("" + (140 - tweetText.getText().length()));
				
			}
			
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
        

     // Watch for button clicks.
        Button sendTweet = (Button)v.findViewById(R.id.sendTweet);
        sendTweet.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	if (Integer.parseInt((String)charCount.getText()) < 0) {
            		Context context = getActivity().getApplicationContext();
					Toast.makeText(context, "Tweet is too long", Toast.LENGTH_SHORT).show();
					
				} else if (Integer.parseInt((String)charCount.getText()) >= 0) {
					String message = tweetText.getText().toString();
	            	new TwitterTweet().execute(message);
	            	dismiss();
	            	synchronized (TwitterFrag.lock) {
	            		TwitterFrag.lock.notify();
        				Log.i("RyanDebug", "Notifying");
	        		}
	            	Log.i("RyanDebug", "Notifying...");
//	            	v.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
//	            	v.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
				}
            }
        });
        
        // Watch for button clicks.
        Button tweetCancel = (Button)v.findViewById(R.id.tweetCancel);
        tweetCancel.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	dismiss();
            	synchronized (TwitterFrag.lock) {
            		TwitterFrag.lock.notify();
    				Log.i("RyanDebug", "Notifying");
        		}
            	Log.i("RyanDebug", "Notifying...");
//            	v.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
//            	v.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
            }

        });

        return v;
    }
    
}