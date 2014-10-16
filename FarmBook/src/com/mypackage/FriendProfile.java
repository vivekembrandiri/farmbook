package com.mypackage;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.utilitiespackage.CustomHttpClient;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class FriendProfile extends FarmbookActivity {
	
	private String friend_phoneno,friend_firstname,friend_lastname,friend_location,view_type;
	private ImageButton add,cancel,back;
	private ImageView friend_profilepic_imageview;
	private TextView friend_phoneno_textview,friend_username_textview,friend_location_textview;
	private MediaPlayer mp_main=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friendprofile);
		
		addBarFunctions(FriendProfile.this);
		
		Bundle extras = getIntent().getExtras();
		if(extras !=null) {
			friend_phoneno = extras.getString("friend_phoneno");
			friend_firstname = extras.getString("friend_firstname");
			friend_lastname = extras.getString("friend_lastname");
			friend_location = extras.getString("friend_location");
			view_type = extras.getString("view_type");
		}
		
		friend_profilepic_imageview = (ImageView)findViewById(R.id.friendprofile_profilepic);
		
		/*Bitmap bitmap = CustomHttpClient.downloadImage(friend_phoneno+"/profilepic.jpg");
		if(bitmap == null)
			bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.profile_man);
		friend_profilepic_imageview.setImageBitmap(bitmap);*/
		friend_profilepic_imageview.setImageBitmap(CustomHttpClient.downloadImage(friend_phoneno+"/profilepic.jpg"));
		
		friend_username_textview = (TextView)findViewById(R.id.friendprofile_name);
		friend_username_textview.setText(friend_firstname+" "+friend_lastname);
		friend_location_textview = (TextView)findViewById(R.id.friendprofile_location);
		friend_location_textview.setText(friend_location);
		friend_phoneno_textview = (TextView)findViewById(R.id.friendprofile_phoneno);
		friend_phoneno_textview.setText(friend_phoneno);
		
		add = (ImageButton)findViewById(R.id.friendprofile_add);
		cancel = (ImageButton)findViewById(R.id.friendprofile_cancel);
		back = (ImageButton)findViewById(R.id.friendprofile_back);
		
		if(view_type.equals("add")) {			
			mp_main = MediaPlayer.create(FriendProfile.this, R.raw.connect_friend);
			mp_main.start();
			
	        add.setOnClickListener(new View.OnClickListener() {
	
	            @Override
	            public void onClick(View v) {
	                add();
	                finish();
	            }
	        });
	        cancel.setOnClickListener(new View.OnClickListener() {
	
	            @Override
	            public void onClick(View v) {
	                finish();
	            }	
	        });
	        
	        back.setVisibility(View.GONE);
		}
        
		else {
			
			add.setVisibility(View.GONE);
			cancel.setVisibility(View.GONE);
			
			back.setOnClickListener(new View.OnClickListener() {

	            @Override
	            public void onClick(View v) {
	                finish();
	            }	
	        });
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		if(mp_main != null)
			mp_main.release();
	}

	private void add() {
		try {
			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			postParameters.add(new BasicNameValuePair("phoneno", getStuffApplication().getPhonenumber()));
			postParameters.add(new BasicNameValuePair("friends", friend_phoneno));
			
			CustomHttpClient.executeHttpPost("addfriends.php", postParameters);	
			Toast.makeText(getApplicationContext(), "Friend added", Toast.LENGTH_LONG).show();				
    	} catch (Exception e) {
    		Log.e("FriendProfile",e.toString());
    		Toast.makeText(getApplicationContext(), "Error connecting to server!", Toast.LENGTH_LONG).show();
			finish();
    	}
	}	
}
