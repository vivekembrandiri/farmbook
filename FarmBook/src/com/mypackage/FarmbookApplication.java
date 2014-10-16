package com.mypackage;

import com.utilitiespackage.CustomHttpClient;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.telephony.TelephonyManager;

public class FarmbookApplication extends Application {

	private String phonenumber,deviceid,firstname,lastname,location,sex="F";
	private Bitmap profilepic=null;

	@Override
	public void onCreate() {
		super.onCreate();

		TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		deviceid = tm.getDeviceId();
		phonenumber = tm.getLine1Number();
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}

	public String getPhonenumber() {
		return phonenumber;
	}

	public String getDeviceid() {
		return deviceid;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getLocation() {
		return location;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}
	
	public Bitmap getProfilepic() {
		if(profilepic == null)
			profilepic = CustomHttpClient.downloadImage(phonenumber+"/profilepic.jpg");
		if(profilepic == null)
			profilepic = BitmapFactory.decodeResource(getResources(), R.drawable.profile_man);
		return profilepic;
	}	
	
	public void setProfilepic(Bitmap profilepic) {
		this.profilepic = profilepic;
	}	
}
