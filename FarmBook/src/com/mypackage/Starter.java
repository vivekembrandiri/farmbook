package com.mypackage;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.utilitiespackage.CustomHttpClient;
import com.utilitiespackage.Table;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class Starter extends FarmbookActivity {
	private static final int REGISTER_CODE = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		start();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		finish();
	}

	private void start() {
		if(!numberExists()) {
			Intent intent = new Intent(Starter.this, Register.class);
			startActivityForResult(intent, REGISTER_CODE);
		}
		else {
			Intent intent = new Intent(Starter.this, Home.class);
			startActivity(intent);
		}
	}

	@Override 
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if(resultCode == Activity.RESULT_OK) {
			Log.i("Starter", "Reqcode = "+requestCode+" ResultCode = "+resultCode+" reqd = "+Activity.RESULT_OK);
			Intent intent = new Intent(Starter.this, Home.class);
			startActivity(intent);
		}
	}

	private boolean numberExists() {

		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("phoneno", getStuffApplication().getPhonenumber()));

		try {
			Log.i("Starter","Running check");
			String response = CustomHttpClient.executeHttpPost("check.php", postParameters);
			Log.i("Starter","check.php response = "+response);

			if(response.equals(NONE)) {
				return false;
			}

			Table details = new Table(new String[]{"firstname","lastname","location","sex"}, 1);
			details.add(0, response);
			getStuffApplication().setFirstname(details.get(0, "firstname"));
			getStuffApplication().setLastname(details.get(0, "lastname"));
			getStuffApplication().setLocation(details.get(0, "location"));
			getStuffApplication().setSex(details.get(0, "sex"));
		} catch (Exception e) {
			Log.e("MyProjectActivity",e.toString());
			Toast.makeText(getApplicationContext(), "Error connecting to server!", Toast.LENGTH_LONG).show();
			finish();
		}

		return true;
	}
}
