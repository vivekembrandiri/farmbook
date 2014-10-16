package com.mypackage;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.utilitiespackage.CustomHttpClient;
import com.utilitiespackage.Table;

import android.app.Dialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class FindFriend extends FarmbookActivity {

	private TextView phonenumber;
	private Button backspace,clear;
	private Button[] numbers;
	private Dialog nofriend;
	private MediaPlayer mp_main=null;
	private ImageButton ok,cancel,pause;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.findfriend);

		addBarFunctions(FindFriend.this);

		mp_main = MediaPlayer.create(FindFriend.this, R.raw.find_friend);
		pause = (ImageButton)findViewById(R.id.bar_pause); 
		playMedia(mp_main, pause, R.drawable.play_button, R.drawable.pause_button, true);

		phonenumber=(TextView)findViewById(R.id.findfriend_number);

		numbers = new Button[10]; 
		numbers[0] = (Button)findViewById(R.id.findfriend_number_0);
		numbers[1] = (Button)findViewById(R.id.findfriend_number_1);
		numbers[2] = (Button)findViewById(R.id.findfriend_number_2);
		numbers[3] = (Button)findViewById(R.id.findfriend_number_3);
		numbers[4] = (Button)findViewById(R.id.findfriend_number_4);
		numbers[5] = (Button)findViewById(R.id.findfriend_number_5);
		numbers[6] = (Button)findViewById(R.id.findfriend_number_6);
		numbers[7] = (Button)findViewById(R.id.findfriend_number_7);
		numbers[8] = (Button)findViewById(R.id.findfriend_number_8);
		numbers[9] = (Button)findViewById(R.id.findfriend_number_9);
		backspace = (Button)findViewById(R.id.findfriend_number_backspace);
		clear = (Button)findViewById(R.id.findfriend_number_clear);

		for(int i=0; i<10; i++) {
			final int digit=i;
			numbers[i].setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					phonenumber.setText(phonenumber.getText().toString() + digit);
				}	
			});
		}
		backspace.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String pn=phonenumber.getText().toString();

				if(pn.length() > 0)
					phonenumber.setText(pn.substring(0, pn.length()-1));
			}	
		});
		clear.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				phonenumber.setText("");
			}	
		});


		ok=(ImageButton)findViewById(R.id.findfriend_ok);
		ok.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showProfile();
			}	
		});
		cancel=(ImageButton)findViewById(R.id.findfriend_cancel);
		cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}	
		});
	}

	@Override
	protected void onStop() {
		super.onStop();

		if(mp_main != null)
			mp_main.release();
	}

	@Override
	protected void onRestart() {
		super.onRestart();

		mp_main = MediaPlayer.create(FindFriend.this, R.raw.find_friend);
		playMedia(mp_main, pause, R.drawable.play_button, R.drawable.pause_button, false);
	}

	private void showProfile() {

		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("phoneno", getStuffApplication().getPhonenumber()));
		postParameters.add(new BasicNameValuePair("friendno", phonenumber.getText().toString()));

		try {
			String response = CustomHttpClient.executeHttpPost("findfriend.php", postParameters);
			Log.i("FindFriend","findfriend.php response = "+response);

			if(response.equals(NONE)) {
				final MediaPlayer wrong_number = MediaPlayer.create(FindFriend.this, R.raw.wrong_friend_number);
				wrong_number.start();

				nofriend = getOkDialog("Friend Not Found", "This person was not found or is already your friend! Please re-enter the phone number", FindFriend.this); 
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
				Table frienddetails = new Table(new String[]{"firstname","lastname","location"}, 1);
				frienddetails.add(0, response);

				Intent intent = new Intent(FindFriend.this, FriendProfile.class);

				intent.putExtra("friend_phoneno", phonenumber.getText().toString());
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
