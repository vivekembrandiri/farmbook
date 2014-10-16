package com.mypackage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.utilitiespackage.CustomHttpClient;
import com.utilitiespackage.Table;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Question extends FarmbookActivity {

	private LayoutInflater inflater;
	private LinearLayout layout;

	private Vector<Bitmap> profilepic_bitmaps;
	private Vector<String> phonenos_bitmaps;

	private RelativeLayout post,comments[];

	private ImageView post_profilepic_imageview,post_image_imageview;
	private TextView post_phoneno_textview,post_text_textview,post_timestamp_textview;
	private ImageButton post_audio_imagebutton;
	private MediaPlayer post_mp;

	private ImageView[] profilepics,images;
	private TextView[] phonenos,texts,timestamps;
	private ImageButton[] audiobuttons;
	private MediaPlayer mp[];
	private Button newcomment;

	private String post_phoneno,post_image,post_audio,post_text,post_timestamp,post_queryid;
	private String commentlist[]=null;
	private Table commentdetails;
	private int no_comments=0;

	private MediaPlayer mp_main=null;
	private ImageButton pause;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wall);

		Bundle extras = getIntent().getExtras();
		if(extras !=null) {
			post_phoneno = extras.getString("phoneno");
			post_image = extras.getString("image");
			post_audio = extras.getString("audio");
			post_text = extras.getString("text");
			post_timestamp = extras.getString("timestamp");
			post_queryid = extras.getString("queryid");
		}

		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("queryid", post_queryid));

		try {
			String response = CustomHttpClient.executeHttpPost("comments.php", postParameters);	
			Log.i("Question","comments.php response = "+response);

			if(!response.equals(NONE)) {
				commentlist = response.split("\\/");
				no_comments = commentlist.length;
			}
			Log.i("Question","Number of queries = "+no_comments);

		} catch (Exception e) {
			Log.e("Question",e.getMessage());
			Toast.makeText(getApplicationContext(), "Error connecting to server!", Toast.LENGTH_LONG).show();
			finish();
		}

		inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		try {
			setUpViews();
		} catch(Exception e) {
			Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_LONG).show();
			finish();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		
		if(mp_main != null)
			mp_main.release();
		
		if(!post_audio.equals(""))
			post_mp.release();
		
		for(int i=0; i<no_comments; i++)
			if(!commentdetails.get(i, "audio").equals("") && mp[i]!=null)
				mp[i].release();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		finish();
	}

	private void setUpViews() {
		
		addBarFunctions(Question.this);
		
		mp_main = MediaPlayer.create(Question.this, R.raw.comment);
		pause=(ImageButton)findViewById(R.id.bar_pause);
		playMedia(mp_main, pause, R.drawable.play_button, R.drawable.pause_button, true);

		commentdetails = new Table(new String[]{"userlevel","phoneno","timestamp","image","audio","text"}, no_comments);

		phonenos_bitmaps = new Vector<String>();
		profilepic_bitmaps = new Vector<Bitmap>();

		layout = (LinearLayout)findViewById(R.id.wall_linearlayout);

		post = (RelativeLayout)inflater.inflate(R.layout.comment, null);
		post.setBackgroundResource(R.drawable.border_blue);

		post_profilepic_imageview = (ImageView)post.findViewById(R.id.comment_profilepic);
		post_profilepic_imageview.setImageBitmap(CustomHttpClient.downloadImage(post_phoneno+"/profilepic.jpg"));

		post_phoneno_textview = (TextView)post.findViewById(R.id.comment_phoneno);
		post_phoneno_textview.setText(post_phoneno);

		post_text_textview = (TextView)post.findViewById(R.id.comment_text);
		post_text_textview.setText(post_text);

		post_timestamp_textview = (TextView)post.findViewById(R.id.comment_timestamp);
		post_timestamp_textview.setText(post_timestamp);

		post_image_imageview = (ImageView)post.findViewById(R.id.comment_image);
		if(!post_image.equals(""))
			post_image_imageview.setImageBitmap(CustomHttpClient.downloadImage(post_phoneno+"/"+post_image));
		else
			post_image_imageview.setVisibility(View.GONE);
			
		post_audio_imagebutton = (ImageButton)post.findViewById(R.id.comment_audio);

		if(!post_audio.equals("")) {

			post_mp = playAudio(post_phoneno+"/"+post_audio);
			
			if(post_mp == null)
				Toast.makeText(getApplicationContext(), "No Audio!", Toast.LENGTH_SHORT).show();
			
			else {
				try {
					post_mp.prepare();
				} catch (IllegalStateException e) {
					Log.e("Question", e.getMessage());
				} catch (IOException e) {
					Log.e("Question", e.getMessage());
				}
				playMedia(post_mp, post_audio_imagebutton, R.drawable.play_audio, R.drawable.pause_button, false);
			}
		}
		else
			post_audio_imagebutton.setVisibility(View.GONE);
		layout.addView(post);

		TextView tv = new TextView(this); 
		tv.setBackgroundColor(Color.TRANSPARENT);
		tv.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 10));
		layout.addView(tv);

		comments = new RelativeLayout[no_comments];

		profilepics = new ImageView[no_comments];
		phonenos = new TextView[no_comments];
		texts = new TextView[no_comments];
		timestamps = new TextView[no_comments];
		images = new ImageView[no_comments];
		audiobuttons = new ImageButton[no_comments];

		mp = new MediaPlayer[no_comments];
		int loc=0;

		for(int i=0; i<no_comments; i++) {

			commentdetails.add(i, commentlist[i]);
			Log.i("Wall",commentdetails.show(i));
		}
		commentdetails.sort(Table.ASCENDING);

		String ul="",pn="";
		for(int i=0; i<no_comments; i++) {
			ul=commentdetails.get(i, "userlevel");

			if(ul.equals(FARMER))
				pn=commentdetails.get(i, "phoneno");
			else
				pn="center";

			if(ul.equals(FARMER)) {
				if(phonenos_bitmaps.indexOf(pn) == -1) {
					phonenos_bitmaps.add(pn);
					/*Bitmap comment_bitmap = CustomHttpClient.downloadImage(pn+"/profilepic.jpg");
					if(comment_bitmap == null)
						comment_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.profile_man);
					profilepic_bitmaps.add(comment_bitmap);*/
					profilepic_bitmaps.add(CustomHttpClient.downloadImage(pn+"/profilepic.jpg"));
				}
				loc=phonenos_bitmaps.indexOf(pn);
			}

			comments[i] = (RelativeLayout)inflater.inflate(R.layout.comment, null);

			if(ul.equals(FARMER))
				comments[i].setBackgroundResource(R.drawable.border_darkgreen);
			else
				comments[i].setBackgroundResource(R.drawable.border_gray);

			profilepics[i] = (ImageView)comments[i].findViewById(R.id.comment_profilepic);
			if(ul.equals(FARMER))
				profilepics[i].setImageBitmap(profilepic_bitmaps.get(loc));
			else
				profilepics[i].setImageResource(R.drawable.center);

			images[i] = (ImageView)comments[i].findViewById(R.id.comment_image);
			if(!commentdetails.get(i, "image").equals("")) {
				
				/*Bitmap commentimage_bitmap = CustomHttpClient.downloadImage(pn+"/"+commentdetails.get(i, "image"));
				if(commentimage_bitmap == null)
					commentimage_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.profile_man);
				images[i].setImageBitmap(commentimage_bitmap);*/
				images[i].setImageBitmap(CustomHttpClient.downloadImage(pn+"/"+commentdetails.get(i, "image")));
			}
			else
				images[i].setVisibility(View.GONE);

			phonenos[i] = (TextView)comments[i].findViewById(R.id.comment_phoneno);
			if(ul.equals(FARMER))
				phonenos[i].setText(pn);
			else
				phonenos[i].setText("CENTER");

			texts[i] = (TextView)comments[i].findViewById(R.id.comment_text);
			texts[i].setText(commentdetails.get(i, "text"));

			timestamps[i] = (TextView)comments[i].findViewById(R.id.comment_timestamp);
			timestamps[i].setText(commentdetails.get(i, "timestamp"));

			audiobuttons[i] = (ImageButton)comments[i].findViewById(R.id.comment_audio);

			if(!commentdetails.get(i, "audio").equals("")) {
				mp[i] = playAudio(pn+"/"+commentdetails.get(i, "audio"));

				try {
					mp[i].prepare();
				} catch (IllegalStateException e) {
					Log.e("Question", e.getMessage());
				} catch (IOException e) {
					Log.e("Question", e.getMessage());
				}
				playMedia(mp[i], audiobuttons[i], R.drawable.play_audio, R.drawable.pause_button, false);
			}
			else
				audiobuttons[i].setVisibility(View.GONE);

			layout.addView(comments[i]);

			TextView txv = new TextView(this); 
			txv.setBackgroundColor(Color.TRANSPARENT);
			txv.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 7));
			layout.addView(txv);
		}

		newcomment = new Button(this);
		newcomment.setText("New Comment");
		newcomment.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		newcomment.setBackgroundResource(R.drawable.border_green);
		newcomment.setGravity(Gravity.CENTER_HORIZONTAL);
		newcomment.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Question.this, CreatePost.class);
				intent.putExtra("type", COMMENT);
				intent.putExtra("postid", post_queryid);
				intent.putExtra("postphoneno", post_phoneno);
				startActivity(intent);
			}
		});
		layout.addView(newcomment);
	}
}
