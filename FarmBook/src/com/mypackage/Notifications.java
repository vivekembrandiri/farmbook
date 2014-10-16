package com.mypackage;

import java.util.ArrayList;
import java.util.Hashtable;
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

public class Notifications extends FarmbookActivity {
	
	private String[] notificationlist;
	private int no_notifications;
	private Dialog nonotifications,nofriend;
	private LayoutInflater inflater;
	private Table notificationdetails;
	private LinearLayout layout;
	private RelativeLayout[] notifications;
	private ImageView[] images;
	private TextView[] descriptions,timestamps;
	
	private Vector<Bitmap> profilepic_bitmaps;
	private Vector<String> phonenos_bitmaps;
	
	private Hashtable<String, Integer> backgrounds,icons; 
	private Hashtable<String, Class<?>> targets; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wall);
		
		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("phoneno", getStuffApplication().getPhonenumber()));

		try {
			String response = CustomHttpClient.executeHttpPost("notification.php", postParameters);	
			Log.i("Notifications","notification.php response = "+response);

			if(!response.equals(NONE)) {
				notificationlist = response.split("\\/");
				no_notifications = notificationlist.length;
			}
			Log.i("Notifications","Number of notifications = "+no_notifications);

		} catch (Exception e) {
			Log.e("Notifications",e.getMessage());
			Toast.makeText(getApplicationContext(), "Error connecting to server!", Toast.LENGTH_LONG).show();
			finish();
		}

		if(no_notifications==0) {
			
			final MediaPlayer mp_nonotifications = MediaPlayer.create(Notifications.this, R.raw.no_notification);
			mp_nonotifications.start();
			
			nonotifications = getOkDialog("No Notifications", "There are no notifications to be displayed", Notifications.this);
			ImageButton nonotifications_ok = (ImageButton)nonotifications.findViewById(R.id.okdialog_ok);
			nonotifications_ok.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mp_nonotifications.release();
					finish();
				}
			});
			nonotifications.show();
		}
		else {	
			inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			try {
				setUpViews();
			} catch(Exception e) {
				Toast.makeText(getApplicationContext(), "Error in Notifcations screen!", Toast.LENGTH_LONG).show();
				finish();
			}
		}
	}

	private void setUpViews() {
		
		addBarFunctions(Notifications.this);
		
		backgrounds = new Hashtable<String,Integer>();
		icons = new Hashtable<String,Integer>();
		targets = new Hashtable<String,Class<?>>();
		
		backgrounds.put("farmer_comment",R.drawable.border_darkgreen);
		icons.put("farmer_comment",-1);
		targets.put("farmer_comment",Wall.class);
		
		backgrounds.put("center_comment",R.drawable.border_gray);
		icons.put("center_comment",R.drawable.center);
		targets.put("center_comment",Wall.class);
		
		backgrounds.put("new_user",R.drawable.border_red);
		icons.put("new_user",-1);
		targets.put("new_user", Friends.class);
		
		backgrounds.put("friend",R.drawable.border_darkgreen);
		icons.put("friend",-1);
		targets.put("friend", FindFriend.class);
		
		backgrounds.put("news",R.drawable.border_cyan);
		icons.put("news",R.drawable.news_icon);
		targets.put("news", News.class);
		
		notificationdetails = new Table(new String[]{"type","description","timestamp","target"}, no_notifications);
		
		layout = (LinearLayout)findViewById(R.id.wall_linearlayout);
		notifications = new RelativeLayout[no_notifications];
		
		phonenos_bitmaps = new Vector<String>();
		profilepic_bitmaps = new Vector<Bitmap>();
		
		images = new ImageView[no_notifications];
		descriptions = new TextView[no_notifications];
		timestamps = new TextView[no_notifications];
		
		for(int i=0; i<no_notifications; i++) {

			notificationdetails.add(i, notificationlist[i]);
			Log.i("Notifications",notificationdetails.show(i));
		}
		notificationdetails.sort(Table.DESCENDING);
		
		int loc=0;
		for(int i=0; i<no_notifications; i++) {
			
			final String type=notificationdetails.get(i, "type");
			final String pn=notificationdetails.get(i, "target");
			if(phonenos_bitmaps.indexOf(pn) == -1) {
				phonenos_bitmaps.add(pn);
				
				profilepic_bitmaps.add(CustomHttpClient.downloadImage(pn+"/profilepic.jpg"));
			}
			loc=phonenos_bitmaps.indexOf(pn);
			
			notifications[i] = (RelativeLayout)inflater.inflate(R.layout.notification, null);
			notifications[i].setBackgroundResource(backgrounds.get(type));
			
			images[i] = (ImageView)notifications[i].findViewById(R.id.notification_pic);
			if(icons.get(type) == -1)
				images[i].setImageBitmap(profilepic_bitmaps.get(loc));
			else
				images[i].setImageResource(icons.get(type));

			descriptions[i] = (TextView)notifications[i].findViewById(R.id.notification_description);
			descriptions[i].setText(notificationdetails.get(i, "description"));

			timestamps[i] = (TextView)notifications[i].findViewById(R.id.notification_timestamp);
			timestamps[i].setText(notificationdetails.get(i, "timestamp"));
			
			notifications[i].setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(type.equals("friend")) {
						showProfile(pn);
					} else {
						Intent intent = new Intent(Notifications.this, targets.get(type));
						startActivity(intent);
					}
				}
			});
			
			layout.addView(notifications[i]);
			
			TextView tv = new TextView(this); 
			tv.setBackgroundColor(Color.TRANSPARENT);
			tv.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 7));
			layout.addView(tv);
		}
	}
	
	private void showProfile(String friendno) {

		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("phoneno", getStuffApplication().getPhonenumber()));
		postParameters.add(new BasicNameValuePair("friendno", friendno));
		
		try {
			String response = CustomHttpClient.executeHttpPost("findfriend.php", postParameters);
			Log.i("FindFriend","findfriend.php response = "+response);

			if(response.equals(NONE)) {
				final MediaPlayer wrong_number = MediaPlayer.create(Notifications.this, R.raw.wrong_friend_number);
				wrong_number.start();

				nofriend = getOkDialog("Friend Not Found", "This person was not found or is already your friend! Please re-enter the phone number", Notifications.this); 
				ImageButton nofriend_ok = (ImageButton)nofriend.findViewById(R.id.okdialog_ok);
				nofriend_ok.setOnClickListener(new View.OnClickListener() {
					
							@Override
							public void onClick(View v) {
								wrong_number.release();
								nofriend.cancel();
							}
						});
				nofriend.show();
			}
			else {
				Table frienddetails = new Table(new String[]{"firstname","lastname","location"}, 0);
				frienddetails.add(0, response);

				Intent intent = new Intent(Notifications.this, FriendProfile.class);

				intent.putExtra("friend_phoneno", friendno);
				intent.putExtra("friend_firstname", frienddetails.get(0, "firstname"));
				intent.putExtra("friend_lastname", frienddetails.get(0, "lastname")); 
				intent.putExtra("friend_location", frienddetails.get(0, "location"));
				intent.putExtra("view_type", "add");
				startActivity(intent);
			}	

		} catch (Exception e) {
			Log.e("FindFriend",e.toString());
			Toast.makeText(getApplicationContext(), "Error connecting to server!", Toast.LENGTH_LONG).show();
			finish();
		}
	}
}
