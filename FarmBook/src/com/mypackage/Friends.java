package com.mypackage;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

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
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mypackage.FarmbookActivity;
import com.utilitiespackage.CustomHttpClient;
import com.utilitiespackage.Table;

public class Friends extends FarmbookActivity {

	private LinearLayout layout;
	private RelativeLayout friends[]=null;
	private ImageView profilepics[];
	private TextView texts[];
	private String friendlist[]=null;
	private int no_friends=0,no_added=0;
	private boolean friendsel[];
	private Table frienddetails;

	private MediaPlayer mp_main=null;
	private ImageButton pause,addfriend=null;
	private Dialog nofriends,confirmselection;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friends);

		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("phoneno", getStuffApplication().getPhonenumber()));

		try {
			String response = CustomHttpClient.executeHttpPost("viewfriends.php", postParameters);	
			Log.i("Friends","viewfriends.php response = "+response);

			if(!response.equals(NONE)) {
				friendlist = response.split("\\/");
				no_friends = friendlist.length;
			}
			Log.i("Friends","Number of friends = "+no_friends);

		} catch (Exception e) {
			Log.e("Friends",e.toString());
			Toast.makeText(getApplicationContext(), "Error connecting to server!", Toast.LENGTH_LONG).show();
			finish();
		}

		if(no_friends==0) {
			final MediaPlayer mp_nofriends = MediaPlayer.create(Friends.this, R.raw.no_suggestion);
			mp_nofriends.start();

			nofriends = getOkDialog("No Friends","You have added all the people in your locality as friends", Friends.this);
			ImageButton nofriends_ok = (ImageButton)nofriends.findViewById(R.id.okdialog_ok);
			nofriends_ok.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mp_nofriends.release();
					finish();
				}
			});
			nofriends.show();
		}
		else {	
			try {
				setUpViews();
			} catch(Exception e) {
				Toast.makeText(getApplicationContext(), "Error in Friends!", Toast.LENGTH_LONG).show();
				finish();
			}
		}
	}

	@Override
	protected void onStop() {
		super.onStop();

		if(mp_main != null)
			mp_main.release();
	}

	private void setUpViews() {

		addBarFunctions(Friends.this);

		mp_main = MediaPlayer.create(Friends.this, R.raw.add_friend);
		pause = (ImageButton)findViewById(R.id.bar_pause);
		playMedia(mp_main, pause, R.drawable.play_button, R.drawable.pause_button, true);

		layout = (LinearLayout)findViewById(R.id.friends_linearlayout);
		friends = new RelativeLayout[no_friends];
		profilepics = new ImageView[no_friends];
		texts = new TextView[no_friends];

		friendsel = new boolean[no_friends];
		frienddetails = new Table(new String[]{"phoneno","firstname","lastname"}, no_friends);

		final LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		for(int i=0; i<no_friends; i++) {
			friendsel[i] = false;
			frienddetails.add(i, friendlist[i]);

			friends[i] = (RelativeLayout)inflater.inflate(R.layout.friendbutton, null);
			friends[i].setId(i);
			friends[i].setBackgroundResource(R.drawable.border_gray);

			profilepics[i] = (ImageView)friends[i].findViewById(R.id.friendbutton_profilepic);
			profilepics[i].setImageBitmap(CustomHttpClient.downloadImage(frienddetails.get(i, "phoneno")+"/profilepic.jpg"));

			texts[i] = (TextView)friends[i].findViewById(R.id.friendbutton_text);
			texts[i].setText(frienddetails.get(i, "firstname")+" "+frienddetails.get(i, "lastname")+"\n"+frienddetails.get(i, "phoneno"));
			friends[i].setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					int i=v.getId();

					friendsel[i] = !friendsel[i];

					if(friendsel[i]) {
						friends[i].setBackgroundResource(R.drawable.border_cyan);
						no_added++;
						Log.i("Friends","i = "+i+" no_added = "+no_added);
					}
					else {
						friends[i].setBackgroundResource(R.drawable.border_gray);
						no_added--;
						Log.i("Friends","i = "+i+" no_added = "+no_added);
					}
				}
			});

			layout.addView(friends[i]);

			TextView tv = new TextView(this); 
			tv.setBackgroundColor(Color.TRANSPARENT);
			tv.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 7));
			layout.addView(tv);
		}

		addfriend = new ImageButton(this);
		
		addfriend.setImageResource(R.drawable.tick_icon);
		addfriend.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		addfriend.setBackgroundResource(R.drawable.border_green);
		addfriend.setScaleType(ScaleType.CENTER_INSIDE);
		addfriend.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				confirm();
			}
		});
		layout.addView(addfriend);
	}

	private void confirm() {
		final MediaPlayer mp_confirm = MediaPlayer.create(Friends.this, R.raw.confirmation);
		mp_confirm.start();

		confirmselection = getOkCancelDialog("Confirm", "You have selected "+no_added+" friend(s). Continue?", Friends.this);	
		ImageButton confirmselection_ok = (ImageButton)confirmselection.findViewById(R.id.okcanceldialog_ok);
		confirmselection_ok.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(no_added>0)
					add();

				mp_confirm.stop();
				mp_confirm.release();
				finish();
			}
		});
		ImageButton confirmselection_cancel = (ImageButton)confirmselection.findViewById(R.id.okcanceldialog_cancel);
		confirmselection_cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mp_confirm.stop();
				mp_confirm.release();
				confirmselection.cancel();
			}
		});
		confirmselection.show();
	}

	private void add() {
		String addedfriends="";

		for(int i=0; i<no_friends; i++)
			if(friendsel[i])
				addedfriends += frienddetails.get(i, "phoneno")+"/";

		addedfriends = addedfriends.substring(0, addedfriends.length()-1);
		Log.i("Friends","Friends added = "+addedfriends);

		try {
			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			postParameters.add(new BasicNameValuePair("phoneno", getStuffApplication().getPhonenumber()));
			postParameters.add(new BasicNameValuePair("friends", addedfriends));

			CustomHttpClient.executeHttpPost("addfriends.php", postParameters);	
			Toast.makeText(getApplicationContext(), no_added+" friend(s) added", Toast.LENGTH_LONG).show();				
		} catch (Exception e) {
			Log.e("Friends",e.getMessage());
			Toast.makeText(getApplicationContext(), "Error connecting to server!", Toast.LENGTH_LONG).show();
			finish();
		}
	}
}

