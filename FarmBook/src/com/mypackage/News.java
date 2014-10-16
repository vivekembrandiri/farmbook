package com.mypackage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import org.apache.http.NameValuePair;

import com.utilitiespackage.CustomHttpClient;
import com.utilitiespackage.Table;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class News extends FarmbookActivity {

	private LayoutInflater inflater;
	private LinearLayout layout;

	private RelativeLayout news[];

	private ImageView[] pics,images;
	private TextView[] texts,timestamps;
	private ImageButton[] audiobuttons;
	private MediaPlayer mp[];

	private String newslist[]=null;
	private Table newsdetails;
	private int no_news=0;

	private Dialog nonews;
	private MediaPlayer mp_main=null;
	private ImageButton pause;

	private Hashtable<String, Integer> backgrounds,icons; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wall);

		try {
			String response = CustomHttpClient.executeHttpPost("getnews.php", new ArrayList<NameValuePair>());	
			Log.i("Notifications","getnews.php response = "+response);

			if(!response.equals(NONE)) {
				newslist = response.split("\\/");
				no_news = newslist.length;
			}
			Log.i("Notifications","Number of news = "+no_news);

		} catch (Exception e) {
			Log.e("Notifications",e.getMessage());
			Toast.makeText(getApplicationContext(), "Error connecting to server!", Toast.LENGTH_LONG).show();
			finish();
		}

		if(no_news==0) {
			final MediaPlayer mp_nonews = MediaPlayer.create(News.this, R.raw.no_news);
			mp_nonews.start();
			
			nonews = getOkDialog("No News", "There is no news to be displayed", News.this);
			ImageButton nonews_ok = (ImageButton)nonews.findViewById(R.id.okdialog_ok);
			nonews_ok.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mp_nonews.release();
					finish();
				}
			});
			nonews.show();
		}
		else {	
			inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			try {
				setUpViews();
			} catch(Exception e) {
				Toast.makeText(getApplicationContext(), "Error in News screen!", Toast.LENGTH_LONG).show();
				finish();
			}
		}
	}

	@Override
	protected void onStop() {
		super.onStop();

		if(mp_main != null)
			mp_main.release();

		for(int i=0; i<no_news; i++)
			if(!newsdetails.get(i, "audio").equals("") && mp[i]!=null)
				mp[i].release();
	}

	private void setUpViews() {

		addBarFunctions(News.this);

		mp_main = MediaPlayer.create(News.this, R.raw.news);
		pause=(ImageButton)findViewById(R.id.bar_pause);
		playMedia(mp_main, pause, R.drawable.play_button, R.drawable.pause_button, true);

		backgrounds = new Hashtable<String,Integer>();
		icons = new Hashtable<String,Integer>();

		backgrounds.put("Seeds",R.drawable.border_blue);
		icons.put("Seeds",R.drawable.seeds);

		backgrounds.put("Fertilizers",R.drawable.border_green);
		icons.put("Fertilizers",R.drawable.fertilizer);

		backgrounds.put("Equipment",R.drawable.border_red);
		icons.put("Equipment",R.drawable.equipment);

		backgrounds.put("Yojanas",R.drawable.border_gray);
		icons.put("Yojanas",R.drawable.yojna);

		backgrounds.put("Sale",R.drawable.border_cyan);
		icons.put("Sale",R.drawable.sale_crops);

		backgrounds.put("Land",R.drawable.border_cyan);
		icons.put("Land",R.drawable.sale_land);

		newsdetails = new Table(new String[]{"category","timestamp","image","audio","text"}, no_news);

		layout = (LinearLayout)findViewById(R.id.wall_linearlayout);
		news = new RelativeLayout[no_news];

		pics = new ImageView[no_news];
		texts = new TextView[no_news];
		timestamps = new TextView[no_news];
		images = new ImageView[no_news];
		audiobuttons = new ImageButton[no_news];

		mp = new MediaPlayer[no_news];

		for(int i=0; i<no_news; i++) {

			newsdetails.add(i, newslist[i]);
			Log.i("Wall",newsdetails.show(i));
		}
		newsdetails.sort(Table.DESCENDING);

		String category="";
		for(int i=0; i<no_news; i++) {
			category=newsdetails.get(i, "category");

			news[i] = (RelativeLayout)inflater.inflate(R.layout.news, null);
			news[i].setBackgroundResource(backgrounds.get(category));

			pics[i] = (ImageView)news[i].findViewById(R.id.news_pic);
			pics[i].setImageResource(icons.get(category));

			images[i] = (ImageView)news[i].findViewById(R.id.news_image);
			if(!newsdetails.get(i, "image").equals(""))
				images[i].setImageBitmap(CustomHttpClient.downloadImage("center/"+newsdetails.get(i, "image")));
			else
				images[i].setVisibility(View.GONE);

			texts[i] = (TextView)news[i].findViewById(R.id.news_text);
			texts[i].setText(newsdetails.get(i, "text"));

			timestamps[i] = (TextView)news[i].findViewById(R.id.news_timestamp);
			timestamps[i].setText(newsdetails.get(i, "timestamp"));

			audiobuttons[i] = (ImageButton)news[i].findViewById(R.id.news_audio);

			if(!newsdetails.get(i, "audio").equals("")) {
				mp[i] = playAudio("center/"+newsdetails.get(i, "audio"));

				if(mp[i] == null)
					Toast.makeText(getApplicationContext(), "No Audio!", Toast.LENGTH_SHORT).show();

				else {
					try {
						mp[i].prepare();
					} catch (IllegalStateException e) {
						Log.e("Question", e.getMessage());
					} catch (IOException e) {
						Log.e("Question", e.getMessage());
					}
					playMedia(mp[i], audiobuttons[i], R.drawable.play_audio, R.drawable.pause_button, false);
				}
			}
			else
				audiobuttons[i].setVisibility(View.GONE);

			layout.addView(news[i]);

			TextView txv = new TextView(this); 
			txv.setBackgroundColor(Color.TRANSPARENT);
			txv.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 7));
			layout.addView(txv);
		}
	}
}
