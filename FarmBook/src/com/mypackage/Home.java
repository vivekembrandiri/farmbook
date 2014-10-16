package com.mypackage;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.utilitiespackage.CustomHttpClient;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class Home extends FarmbookActivity {

	private ImageButton button1,button2,button3,button4,button5,button6,button7,button8,button9;
	private Button notificationnumber;
	private MediaPlayer mp_newnotifications = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		setUpViews();
	}

	@Override
	protected void onStop() {
		super.onStop();
		
		if(mp_newnotifications != null) 
			mp_newnotifications.release();
	}

	private void setUpViews() {
		
		addBarFunctions(Home.this);
		
		button1 = (ImageButton)findViewById(R.id.main_button1);
		button2 = (ImageButton)findViewById(R.id.main_button2);
		button3 = (ImageButton)findViewById(R.id.main_button3);
		button4 = (ImageButton)findViewById(R.id.main_button4);
		button5 = (ImageButton)findViewById(R.id.main_button5);
		button6 = (ImageButton)findViewById(R.id.main_button6);
		button7 = (ImageButton)findViewById(R.id.main_button7);
		button8 = (ImageButton)findViewById(R.id.main_button8);
		button9 = (ImageButton)findViewById(R.id.main_button9);	
		notificationnumber = (Button)findViewById(R.id.main_notification_number);
		
		Log.i("Home","Sex = "+getStuffApplication().getSex());
		
		button1.setImageResource(R.drawable.profile_man);
		button1.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Home.this, Profile.class);
				startActivity(intent);
			}
		});
		button2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Home.this, Friends.class);
				startActivity(intent);
			}
		});
		button3.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Home.this, FindFriend.class);
				startActivity(intent);
			}
		});
		button4.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Home.this, TakePhoto.class);
				startActivity(intent);
			}
		});
		button5.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Home.this, Wall.class);
				startActivity(intent);
			}
		});
		button6.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Home.this, CreatePost.class);
				intent.putExtra("type", POST);
				intent.putExtra("postid", "0");
				intent.putExtra("postphoneno", "0");
				startActivity(intent);
			}
		});
		button7.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
				startActivity(intent);
			}
		});
		button8.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Home.this, News.class);
				startActivity(intent);
			}
		});
		
		int notifications=getNumberNotifications();
		
		if(notifications>0) {
			notificationnumber.setText(notifications+"");
			
			button9.setAnimation(animation);
			notificationnumber.setAnimation(animation);
			
			mp_newnotifications = MediaPlayer.create(Home.this, R.raw.new_notification);
			mp_newnotifications.start();
		} else {
			notificationnumber.setBackgroundColor(Color.TRANSPARENT);
		}
		
		button9.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Home.this, Notifications.class);
				startActivity(intent);
			}
		});
	}

	private int getNumberNotifications() {
		
		int notifications=0;
		
		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("phoneno", getStuffApplication().getPhonenumber()));
		
		try {
			String response = CustomHttpClient.executeHttpPost("notificationnumber.php", postParameters);	
			Log.i("Home","notificationnumber.php response = "+response);
			
			if(!response.equals(NONE))
				notifications=Integer.parseInt(response);
				
    	} catch (Exception e) {
    		Log.e("Home",e.toString());
    		Toast.makeText(getApplicationContext(), "Error connecting to server!", Toast.LENGTH_LONG).show();
			finish();
    	}
		return notifications;
	}
}