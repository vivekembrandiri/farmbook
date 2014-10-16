package com.mypackage;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.utilitiespackage.CustomHttpClient;
import com.utilitiespackage.Table;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Profile extends FarmbookActivity {

	private TextView phoneno_textview,username_textview,location_textview,posts_textview,comments_textview;
	private ImageView profilepic_imageview;
	private GridView layout;
	private String[] friendlist;
	private int no_friends;
	private Table frienddetails,details;
	private boolean[] present;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile);

		profilepic_imageview = (ImageView)findViewById(R.id.profile_profilepic);
		profilepic_imageview.setImageBitmap(getStuffApplication().getProfilepic());
		username_textview = (TextView)findViewById(R.id.profile_name);
		username_textview.setText(getStuffApplication().getFirstname()+" "+getStuffApplication().getLastname());
		location_textview = (TextView)findViewById(R.id.profile_location);
		location_textview.setText(getStuffApplication().getLocation());
		phoneno_textview = (TextView)findViewById(R.id.profile_phoneno);
		phoneno_textview.setText(getStuffApplication().getPhonenumber());

		posts_textview = (TextView)findViewById(R.id.profile_posts);
		comments_textview = (TextView)findViewById(R.id.profile_comments);

		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("phoneno", getStuffApplication().getPhonenumber()));

		try {
			String response = CustomHttpClient.executeHttpPost("profile.php", postParameters);	
			Log.i("Profile","profile.php response = "+response);

			details = new Table(new String[]{"friends","posts","comments"}, 1);
			details.add(0, response);

			friendlist = details.get(0, "friends").split("\\/");
			no_friends = friendlist.length;
			Log.i("Profile","Number of friends = "+no_friends);

			setUpViews();

		} catch (Exception e) {
			Log.e("Profile",e.getMessage());
			Toast.makeText(getApplicationContext(), "Error connecting to server!", Toast.LENGTH_LONG).show();
			finish();
		}
	}

	public void setUpViews() {
		
		addBarFunctions(Profile.this);
		
		frienddetails = new Table(new String[]{"firstname","lastname","location","sex"}, no_friends);
		present = new boolean[no_friends];

		layout = (GridView)findViewById(R.id.profile_friendlist);
		layout.setAdapter(new ImageAdapter(this, friendlist));

		layout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				int i=position;

				if(!present[i]) {
					ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
					postParameters.add(new BasicNameValuePair("phoneno", friendlist[i]));

					try {
						String response = CustomHttpClient.executeHttpPost("check.php", postParameters);	
						Log.i("Profile","check.php response = "+response);

						frienddetails.add(i, response);
						Log.i("Profile","Friend details = "+frienddetails.show(i));
						present[i] = true;

					} catch (Exception e) {
						Log.e("Profile",e.getMessage());
						Toast.makeText(getApplicationContext(), "Error connecting to server!", Toast.LENGTH_LONG).show();
						finish();
					}
				}

				if(present[i]) {
					Intent intent = new Intent(Profile.this, FriendProfile.class);

					intent.putExtra("friend_phoneno", friendlist[i]);
					intent.putExtra("friend_firstname", frienddetails.get(i, "firstname"));
					intent.putExtra("friend_lastname", frienddetails.get(i, "lastname")); 
					intent.putExtra("friend_location", frienddetails.get(i, "location"));
					intent.putExtra("view_type", "view");
					startActivity(intent);
				}
			}
		});

		posts_textview.setText(details.get(0, "posts"));
		comments_textview.setText(details.get(0, "comments"));
	}
}
