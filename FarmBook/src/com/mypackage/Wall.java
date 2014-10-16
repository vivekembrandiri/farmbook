package com.mypackage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.utilitiespackage.CustomHttpClient;
import com.utilitiespackage.Table;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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

public class Wall extends FarmbookActivity {
	
	private LayoutInflater inflater;
	private LinearLayout layout;
	private RelativeLayout questions[]=null;
	
	private Vector<Bitmap> profilepic_bitmaps;
	private Vector<String> phonenos_bitmaps;
 	
	private ImageView[] profilepics,images;
	private TextView[] phonenos,texts,timestamps;
	private ImageButton[] audiobuttons,openbuttons;
	private MediaPlayer mp[];
	private Dialog noqueries;

	private String querylist[]=null;
	private Table querydetails;
	private int no_queries=0;
	
	private MediaPlayer mp_main=null;
	private ImageButton pause;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wall);
		
		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("phoneno", getStuffApplication().getPhonenumber()));

		try {
			String response = CustomHttpClient.executeHttpPost("wall.php", postParameters);	
			Log.i("Wall","wall.php response = "+response);

			if(!response.equals(NONE)) {
				querylist = response.split("\\/");
				no_queries = querylist.length;
			}
			Log.i("Wall","Number of queries = "+no_queries);

		} catch (Exception e) {
			Log.e("Wall",e.getMessage());
			Toast.makeText(getApplicationContext(), "Error connecting to server!", Toast.LENGTH_LONG).show();
			finish();
		}

		if(no_queries==0) {
			
			final MediaPlayer mp_noqueries = MediaPlayer.create(Wall.this, R.raw.no_posts);
			mp_noqueries.start();
			
			noqueries = getOkDialog("No Posts", "There are no posts to be displayed", Wall.this);
			ImageButton noqueries_ok = (ImageButton)noqueries.findViewById(R.id.okdialog_ok);
			noqueries_ok.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mp_noqueries.release();
					finish();
				}
			});
			noqueries.show();
		}
		else {	
			inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			try { 
				setUpViews();
			} catch(Exception e) {
				Toast.makeText(getApplicationContext(), "Error in Wall!", Toast.LENGTH_LONG).show();
				finish();
			}
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		if(mp_main !=null)
			mp_main.release();
		
		for(int i=0; i<no_queries; i++)
			if(!querydetails.get(i, "audio").equals("") && mp[i]!=null)
				mp[i].release();
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		mp_main = MediaPlayer.create(Wall.this, R.raw.wall_audio);
		playMedia(mp_main, pause, R.drawable.play_button, R.drawable.pause_button, false);
		
		for(int i=0; i<no_queries; i++)
			if(!querydetails.get(i, "audio").equals("")) {
				
				mp[i] = playAudio(querydetails.get(i, "phoneno")+"/"+querydetails.get(i, "audio"));
				
				if(mp[i] == null)
					Toast.makeText(getApplicationContext(), "No Audio!", Toast.LENGTH_SHORT).show();
				
				else {
					try {
						mp[i].prepare();
					} catch (IllegalStateException e) {
						Log.e("Wall", e.getMessage());
					} catch (IOException e) {
						Log.e("Wall", e.getMessage());
					}
					playMedia(mp[i], audiobuttons[i], R.drawable.play_audio, R.drawable.pause_button, false);
				}
			}
	}
	
	private void setUpViews() {
		
		addBarFunctions(Wall.this);
		
		mp_main = MediaPlayer.create(Wall.this, R.raw.wall_audio);
		pause=(ImageButton)findViewById(R.id.bar_pause);
		playMedia(mp_main, pause, R.drawable.play_button, R.drawable.pause_button, true);
		
		querydetails = new Table(new String[]{"queryid","phoneno","timestamp","image","audio","text"}, no_queries);
		
		phonenos_bitmaps = new Vector<String>();
		profilepic_bitmaps = new Vector<Bitmap>();
		
		layout = (LinearLayout)findViewById(R.id.wall_linearlayout);
		questions = new RelativeLayout[no_queries];
		
		profilepics = new ImageView[no_queries];
		phonenos = new TextView[no_queries];
		texts = new TextView[no_queries];
		timestamps = new TextView[no_queries];
		images = new ImageView[no_queries];
		audiobuttons = new ImageButton[no_queries];
		openbuttons = new ImageButton[no_queries];
		
		mp = new MediaPlayer[no_queries];
		
		for(int i=0; i<no_queries; i++) {

			querydetails.add(i, querylist[i]);
			Log.i("Wall",querydetails.show(i));
		}
		querydetails.sort(Table.DESCENDING);
		
		int loc=0;
		String pn="";
		for(int i=0; i<no_queries; i++) {
			
			pn=querydetails.get(i, "phoneno");
			if(phonenos_bitmaps.indexOf(pn) == -1) {
				phonenos_bitmaps.add(pn);
				
				/*Bitmap post_bitmap = CustomHttpClient.downloadImage(pn+"/profilepic.jpg");
				if(post_bitmap == null)
					post_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.profile_man);
				profilepic_bitmaps.add(post_bitmap);*/
				profilepic_bitmaps.add(CustomHttpClient.downloadImage(pn+"/profilepic.jpg"));
			}
			loc=phonenos_bitmaps.indexOf(pn);
			
			questions[i] = (RelativeLayout)inflater.inflate(R.layout.post, null);
			questions[i].setBackgroundResource(R.drawable.border_blue);
			
			profilepics[i] = (ImageView)questions[i].findViewById(R.id.post_profilepic);
			profilepics[i].setImageBitmap(profilepic_bitmaps.get(loc));
			
			images[i] = (ImageView)questions[i].findViewById(R.id.post_image);
			if(!querydetails.get(i, "image").equals("")) {
				
				/*Bitmap postimage_bitmap = CustomHttpClient.downloadImage(pn+"/"+querydetails.get(i, "image"));
				if(postimage_bitmap == null)
					postimage_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.profile_man);
				images[i].setImageBitmap(postimage_bitmap);*/
				images[i].setImageBitmap(CustomHttpClient.downloadImage(pn+"/"+querydetails.get(i, "image")));
			} else
				images[i].setVisibility(View.GONE);

			phonenos[i] = (TextView)questions[i].findViewById(R.id.post_phoneno);
			phonenos[i].setText(pn);

			texts[i] = (TextView)questions[i].findViewById(R.id.post_text);
			texts[i].setText(querydetails.get(i, "text"));

			timestamps[i] = (TextView)questions[i].findViewById(R.id.post_timestamp);
			timestamps[i].setText(querydetails.get(i, "timestamp"));

			audiobuttons[i] = (ImageButton)questions[i].findViewById(R.id.post_audio);

			if(!querydetails.get(i, "audio").equals("")) {
				mp[i] = playAudio(pn+"/"+querydetails.get(i, "audio"));
				audiobuttons[i].setImageResource(R.drawable.play_audio);
				
				try {
					mp[i].prepare();
				} catch (IllegalStateException e) {
					Log.e("Wall", e.getMessage());
				} catch (IOException e) {
					Log.e("Wall", e.getMessage());
				}
				
				playMedia(mp[i], audiobuttons[i], R.drawable.play_audio, R.drawable.pause_button, false);
			}
			else
				audiobuttons[i].setVisibility(View.GONE);

			openbuttons[i] = (ImageButton)questions[i].findViewById(R.id.post_expand);
			openbuttons[i].setId(i);

			openbuttons[i].setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					int i=v.getId();	

					Intent intent = new Intent(Wall.this, Question.class);
					intent.putExtra("phoneno", querydetails.get(i, "phoneno"));
					intent.putExtra("image", querydetails.get(i, "image"));
					intent.putExtra("audio", querydetails.get(i, "audio"));
					intent.putExtra("text", querydetails.get(i, "text"));
					intent.putExtra("queryid", querydetails.get(i, "queryid"));
					intent.putExtra("timestamp", querydetails.get(i, "timestamp"));
					startActivity(intent);
				}
			});
			layout.addView(questions[i]);

			TextView tv = new TextView(this); 
			tv.setBackgroundColor(Color.TRANSPARENT);
			tv.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 7));
			layout.addView(tv);
		}
	}
}
